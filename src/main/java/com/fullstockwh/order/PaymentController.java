package com.fullstockwh.order;

import com.fullstockwh.cart.CartService;
import com.fullstockwh.cart.dto.CartResponse;
import com.fullstockwh.order.dto.OrderCreateRequest;
import com.fullstockwh.order.dto.OrderResponse;
import com.fullstockwh.order.dto.PaymentRequest;
import com.fullstockwh.order.dto.ShippingRequest;
import com.fullstockwh.user.UserEntity;
import com.fullstockwh.user.UserService;
import com.fullstockwh.user.dto.AddressCreateRequest;
import com.fullstockwh.user.dto.AddressResponse;
import com.fullstockwh.user.dto.PaymentCardCreateRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class PaymentController
{
    private final PaymentService paymentService;
    private final OrderService orderService;
    private final CartService cartService;
    private final UserService userService;

    private UserEntity getUser(UserDetails userDetails)
    {
        return userService.findByEmail(userDetails.getUsername());
    }

    @GetMapping("/checkout/shipping")
    public String shippingPage(@AuthenticationPrincipal UserDetails userDetails,
                               Model model)
    {
        if (userDetails == null) return "redirect:/login";

        CartResponse cart = cartService.getCart(getUser(userDetails));
        if (cart.getItems().isEmpty()) return "redirect:/cart";

        model.addAttribute("cart",cart);
        model.addAttribute("addresses", userService.getAddresses());
        model.addAttribute("shippingRequest", new ShippingRequest());
        return "shipping";
    }

    @PostMapping("/checkout/shipping")
    public String processShipping(@AuthenticationPrincipal UserDetails userDetails,
                                  @ModelAttribute ShippingRequest shippingRequest,
                                  HttpSession session,  RedirectAttributes redirectAttributes)
    {
        if (userDetails == null) return "redirect:/login";

        Long addressId = shippingRequest.getAddressId();

        if (addressId == null)
        {
            AddressCreateRequest newAddr = new AddressCreateRequest();
            newAddr.setAddressTitle(shippingRequest.isSaveAddress()
                    ? shippingRequest.getAddressTitle()
                    : "Order Address " + System.currentTimeMillis());
            newAddr.setCity (shippingRequest.getCity());
            newAddr.setDistrict (shippingRequest.getDistrict());
            newAddr.setNeighborhood (shippingRequest.getNeighborhood());
            newAddr.setFullAddress (shippingRequest.getFullAddress());
            newAddr.setBuildingDetails(shippingRequest.getBuildingDetails());
            newAddr.setLatitude (shippingRequest.getLatitude());
            newAddr.setLongitude (shippingRequest.getLongitude());
            newAddr.setTemporary (!shippingRequest.isSaveAddress());

            try {
                AddressResponse saved = userService.addAddress(newAddr);
                addressId = saved.getId();
            } catch (RuntimeException e) {
                redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
                return "redirect:/checkout/shipping";
            }
        }

        session.setAttribute("selectedAddressId", addressId);
        return "redirect:/checkout/payment";
    }

    @GetMapping("/checkout/payment")
    public String paymentPage(@AuthenticationPrincipal UserDetails userDetails,
                              HttpSession session,
                              Model model)
    {
        if (userDetails == null) return "redirect:/login";

        Long addressId = (Long) session.getAttribute("selectedAddressId");
        if (addressId == null) return "redirect:/checkout/shipping";

        CartResponse cart = cartService.getCart(getUser(userDetails));
        if (cart.getItems().isEmpty()) return "redirect:/cart";

        model.addAttribute("selectedAddress", userService.getAddressById(addressId));
        model.addAttribute("cart", cart);
        model.addAttribute("paymentRequest", new PaymentRequest());
        model.addAttribute("savedCards", userService.getPaymentCards());
        return "checkout";
    }

    @PostMapping("/checkout/pay")
    public String processPayment(@AuthenticationPrincipal UserDetails userDetails,
                                 @ModelAttribute PaymentRequest paymentRequest,
                                 @RequestParam(required = false) String saveCard,
                                 @RequestParam(required = false) String cardNickname,
                                 @RequestParam(required = false) String cardholderName,
                                 @RequestParam(required = false) String cardNumber,
                                 @RequestParam(required = false) String expiryDate,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes)
    {
        if (userDetails == null) return "redirect:/login";

        Long addressId = (Long) session.getAttribute("selectedAddressId");
        if (addressId == null) return "redirect:/checkout/shipping";

        UserEntity user = getUser(userDetails);

        if (!paymentService.processPayment(paymentRequest))
        {
            redirectAttributes.addFlashAttribute("paymentError",
                    "Payment failed. Please check your card details.");
            return "redirect:/checkout/payment";
        }

        if ("on".equals(saveCard) && cardholderName != null && cardNumber != null)
        {
            try
            {
                String digits = cardNumber.replaceAll("\\s", "");
                PaymentCardCreateRequest cardRequest = new PaymentCardCreateRequest();
                cardRequest.setCardNickname (cardNickname != null && !cardNickname.isBlank()
                        ? cardNickname : "My Card");
                cardRequest.setCardholderName (cardholderName);
                cardRequest.setLastFourDigits (digits.length() >= 4
                        ? digits.substring(digits.length() - 4) : digits);
                cardRequest.setExpiryDate (expiryDate);
                cardRequest.setTemporary (false);
                userService.addPaymentCard(cardRequest);
            }
            catch (RuntimeException ignored) {}
        }

        OrderCreateRequest orderRequest = new OrderCreateRequest();
        orderRequest.setAddressId(addressId);
        OrderResponse order = orderService.placeOrder(user, orderRequest);

        session.removeAttribute("selectedAddressId");

        redirectAttributes.addFlashAttribute("orderId",    order.getId());
        redirectAttributes.addFlashAttribute("orderTotal", order.getTotalPrice());
        return "redirect:/checkout/confirmation";
    }

    @GetMapping("/checkout/confirmation")
    public String confirmationPage()
    {
        return "order-confirmation";
    }
}
