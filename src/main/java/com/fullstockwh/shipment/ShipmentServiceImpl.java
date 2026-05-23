package com.fullstockwh.shipment;

import com.fullstockwh.order.Order;
import com.fullstockwh.order.OrderRepository;
import com.fullstockwh.order.enums.OrderStatus;
import com.fullstockwh.order.order_item.OrderItem;
import com.fullstockwh.shipment.dto.ShipmentResponse;
import com.fullstockwh.shipment.dto.ShipmentTrackResponse;
import com.fullstockwh.shipment.dto.ShipOrderRequest;
import com.fullstockwh.shipment.enums.ShipmentStatus;
import com.fullstockwh.shipment.event.ShipmentCreatedEvent;
import com.fullstockwh.shipment.geocoding.GeocodingResult;
import com.fullstockwh.shipment.geocoding.GeocodingService;
import com.fullstockwh.shipment.routing.DistanceResult;
import com.fullstockwh.shipment.routing.RoutingService;
import com.fullstockwh.user.address.Address;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
class ShipmentServiceImpl implements ShipmentService
{
    private final ShipmentRepository shipmentRepository;
    private final OrderRepository orderRepository;
    private final GeocodingService geocodingService;
    private final RoutingService routingService;
    private final ApplicationEventPublisher eventPublisher;

    @Value("${warehouse.lat}")
    private Double warehouseLat;

    @Value("${warehouse.lon}")
    private Double warehouseLon;

    @Override
    @Transactional
    public ShipmentResponse shipOrder(ShipOrderRequest request)
    {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found: " + request.getOrderId()));

        if (order.getStatus() != OrderStatus.SUCCESS)
            throw new RuntimeException("Only confirmed orders can be shipped.");

        if (shipmentRepository.findByOrderId(order.getId()).isPresent())
            throw new RuntimeException("This order has already been shipped.");

        Address address = order.getShippingAddress();
        if (address == null)
            throw new RuntimeException("No shipping address found for this order.");

        double destLat = warehouseLat;
        double destLon = warehouseLon;

        if (address.getLatitude() != null && address.getLongitude() != null) {
            destLat = address.getLatitude();
            destLon = address.getLongitude();
        } else {
            Optional<GeocodingResult> geo = geocodingService.geocode(
                    address.getCity(), address.getDistrict(), address.getFullAddress());
            if (geo.isPresent()) {
                destLat = geo.get().getLatitude();
                destLon = geo.get().getLongitude();

                address.setLatitude(destLat);
                address.setLongitude(destLon);
            } else {
                log.warn("Geocoding failed, falling back to warehouse coordinates. OrderId={}", order.getId());
            }
        }


        double distanceKm = 0.0;
        Optional<DistanceResult> distance = routingService.getDistance(
                warehouseLat, warehouseLon, destLat, destLon);
        if (distance.isPresent()) {
            distanceKm = distance.get().getDistanceKm();
        } else {
            log.warn("OSRM distance calculation failed, defaulting to 0 km. OrderId={}", order.getId());
        }

        BigDecimal shippingFee = calculateFee(distanceKm);
        double carbonFootprint = calculateCarbonFootprint(distanceKm, order.getItems());


        String trackingNumber = generateTrackingNumber();


        LocalDateTime estimatedDelivery = estimateDelivery(distanceKm);


        Shipment shipment = Shipment.builder()
                .order(order)
                .trackingNumber(trackingNumber)
                .carrierName(request.getCarrierName() != null ? request.getCarrierName() : "Fullstockwh Cargo")
                .originLat(warehouseLat)
                .originLon(warehouseLon)
                .destinationLat(destLat)
                .destinationLon(destLon)
                .distanceKm(distanceKm)
                .shippingFee(shippingFee)
                .status(ShipmentStatus.SHIPPED)
                .shippedAt(LocalDateTime.now())
                .estimatedDelivery(estimatedDelivery)
                .estimatedDelivery(estimatedDelivery)
                .carbonFootprintKg(carbonFootprint)
                .build();

        shipmentRepository.save(shipment);

        order.setStatus(OrderStatus.SHIPPED);
        orderRepository.save(order);

        eventPublisher.publishEvent(new ShipmentCreatedEvent(this, shipment));

        log.info("Order shipped successfully. OrderId={}, Tracking={}", order.getId(), trackingNumber);
        return mapToResponse(shipment);
    }

    @Override
    @Transactional
    public ShipmentResponse markDelivered(Long orderId)
    {
        Shipment shipment = shipmentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Shipment record not found."));

        shipment.setStatus(ShipmentStatus.DELIVERED);
        shipment.setDeliveredAt(LocalDateTime.now());
        shipmentRepository.save(shipment);

        Order order = shipment.getOrder();
        order.setStatus(OrderStatus.DELIVERED);
        orderRepository.save(order);

        return mapToResponse(shipment);
    }

    @Override
    public ShipmentTrackResponse getTrackingInfo(Long orderId)
    {
        Shipment shipment = shipmentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Shipment information not found."));

        Order   order   = shipment.getOrder();
        Address address = order.getShippingAddress();

        return ShipmentTrackResponse.builder()
                .trackingNumber(shipment.getTrackingNumber())
                .carrierName(shipment.getCarrierName())
                .shipmentStatus(shipment.getStatus())
                .orderStatus(order.getStatus())
                .shippingFee(shipment.getShippingFee())
                .distanceKm(shipment.getDistanceKm())
                .destinationLat(shipment.getDestinationLat())
                .destinationLon(shipment.getDestinationLon())
                .destinationCity(address != null ? address.getCity() : "-")
                .destinationDistrict(address != null ? address.getDistrict() : "-")
                .orderedAt(order.getOrderDate())
                .shippedAt(shipment.getShippedAt())
                .estimatedDelivery(shipment.getEstimatedDelivery())
                .deliveredAt(shipment.getDeliveredAt())
                .build();
    }

    private String generateTrackingNumber()
    {
        String uid = UUID.randomUUID().toString().toUpperCase().replace("-", "").substring(0, 8);
        return "FSW-" + uid;
    }

    private BigDecimal calculateFee(double distanceKm)
    {
        double base = 30.0;
        double perKm = distanceKm > 200 ? 0.10 : distanceKm > 100 ? 0.15 : 0.20;
        double total = base + (distanceKm * perKm);
        return BigDecimal.valueOf(total).setScale(2, RoundingMode.HALF_UP);
    }

    private LocalDateTime estimateDelivery(double distanceKm)
    {
        int days = distanceKm < 100 ? 1 : distanceKm < 400 ? 2 : 3;
        return LocalDateTime.now().plusDays(days);
    }

    private ShipmentResponse mapToResponse(Shipment s)
    {
        Order   o = s.getOrder();
        Address a = o.getShippingAddress();

        return ShipmentResponse.builder()
                .id(s.getId())
                .orderId(o.getId())
                .trackingNumber(s.getTrackingNumber())
                .carrierName(s.getCarrierName())
                .originLat(s.getOriginLat())
                .originLon(s.getOriginLon())
                .destinationLat(s.getDestinationLat())
                .destinationLon(s.getDestinationLon())
                .distanceKm(s.getDistanceKm())
                .shippingFee(s.getShippingFee())
                .status(s.getStatus())
                .customerFullName(o.getUser().getFirstName() + " " + o.getUser().getLastName())
                .customerEmail(o.getUser().getEmail())
                .destinationCity(a != null ? a.getCity() : "-")
                .destinationDistrict(a != null ? a.getDistrict() : "-")
                .destinationFullAddress(a != null ? a.getFullAddress() : "-")
                .createdAt(s.getCreatedAt())
                .shippedAt(s.getShippedAt())
                .estimatedDelivery(s.getEstimatedDelivery())
                .deliveredAt(s.getDeliveredAt())
                .carbonFootprintKg(s.getCarbonFootprintKg())
                .build();
    }
    private double calculateCarbonFootprint(double distanceKm, List<OrderItem> items)
    {
        double totalWeightKg = items.stream()
                .mapToDouble(item -> {
                    Double unitWeight = item.getProductVariant().getUnitWeight();
                    return (unitWeight != null ? unitWeight : 0.0) * item.getQuantity();
                })
                .sum();

        double totalWeightTon = totalWeightKg / 1000.0;
        double co2 = distanceKm * totalWeightTon * 0.096;
        return Math.round(co2 * 100.0) / 100.0;
    }
}
