package com.fullstockwh.shipment;

import com.fullstockwh.shipment.enums.ShipmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, Long>
{
    Optional<Shipment> findByTrackingNumber(String trackingNumber);

    @Query("SELECT s FROM Shipment s JOIN FETCH s.order o JOIN FETCH o.user WHERE o.id = :orderId")
    Optional<Shipment> findByOrderId(@Param("orderId") Long orderId);

    @Query("SELECT s FROM Shipment s JOIN FETCH s.order o JOIN FETCH o.user " +
            "JOIN FETCH o.shippingAddress " +
            "WHERE s.shippedAt BETWEEN :start AND :end " +
            "ORDER BY s.shippedAt DESC")
    List<Shipment> findByShippedAtBetween(@Param("start") LocalDateTime start,
                                          @Param("end")   LocalDateTime end);

    @Query("SELECT COALESCE(SUM(s.distanceKm), 0) FROM Shipment s " +
            "WHERE s.shippedAt BETWEEN :start AND :end")
    Double sumDistanceKm(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COALESCE(SUM(s.shippingFee), 0) FROM Shipment s " +
            "WHERE s.shippedAt BETWEEN :start AND :end")
    Double sumShippingFee(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COALESCE(SUM(s.carbonFootprintKg), 0) FROM Shipment s " +
            "WHERE s.shippedAt BETWEEN :start AND :end")
    Double sumCarbonFootprint(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COUNT(s) FROM Shipment s WHERE s.shippedAt BETWEEN :start AND :end")
    Long countByShippedAtBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COUNT(s) FROM Shipment s WHERE s.status = :status " +
            "AND s.shippedAt BETWEEN :start AND :end")
    Long countByStatusAndPeriod(@Param("status") ShipmentStatus status,
                                @Param("start")  LocalDateTime start,
                                @Param("end")    LocalDateTime end);

    @Query(value = "SELECT TO_CHAR(shipped_at, 'YYYY-MM-DD') as day, " +
            "COALESCE(SUM(carbon_footprint_kg), 0) as co2 " +
            "FROM fullstockwh.shipments " +
            "WHERE shipped_at BETWEEN :start AND :end " +
            "GROUP BY day ORDER BY day",
            nativeQuery = true)
    List<Object[]> getDailyCo2(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT a.city, COUNT(s) FROM Shipment s " +
            "JOIN s.order o JOIN o.shippingAddress a " +
            "WHERE s.shippedAt BETWEEN :start AND :end " +
            "GROUP BY a.city ORDER BY COUNT(s) DESC")
    List<Object[]> getCityDistribution(@Param("start") LocalDateTime start,
                                       @Param("end")   LocalDateTime end);
}
