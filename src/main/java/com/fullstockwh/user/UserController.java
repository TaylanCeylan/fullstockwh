package com.fullstockwh.user;

import com.fullstockwh.user.dto.AddressDeleteRequest;
import com.fullstockwh.user.dto.AddressResponse;
import com.fullstockwh.user.dto.UserUpdateRequest;
import com.fullstockwh.user.dto.AddressCreateRequest;
import com.fullstockwh.user.dto.AddressUpdateRequest;
import com.fullstockwh.user.dto.PaymentCardCreateRequest;
import com.fullstockwh.user.dto.PaymentCardDeleteRequest;
import com.fullstockwh.user.dto.PaymentCardResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class UserController
{
    private final UserService userService;

    @GetMapping("/user")
    public String showUserProfile(Model model)
    {
        model.addAttribute("user", userService.getUserUpdateRequest());

        return "user/profile";
    }

    @PostMapping("/user/update")
    public String updateUser(@ModelAttribute("user") UserUpdateRequest user)
    {
        userService.updateUser(user);

        return "redirect:/user?success";
    }

    @GetMapping("/user/addresses")
    public String showAddresses(Model model)
    {
        model.addAttribute("addresses", userService.getAddresses());
        model.addAttribute("addressRequest", new AddressCreateRequest());
        model.addAttribute("addressUpdateRequest", new AddressUpdateRequest());
        return "user/addresses";
    }

    @PostMapping("/user/addresses/add")
    public String addAddress(@ModelAttribute AddressCreateRequest request,
                             RedirectAttributes redirectAttributes)
    {
        AddressResponse saved = userService.addAddress(request);
        redirectAttributes.addFlashAttribute("successMessage",
                "Address '" + saved.getAddressTitle() + "' saved successfully.");
        return "redirect:/user/addresses";
    }

    @PostMapping("/user/addresses/update/{id}")
    public String updateAddress(@PathVariable Long id,
                                @ModelAttribute AddressUpdateRequest request,
                                RedirectAttributes redirectAttributes)
    {
        AddressResponse updated = userService.updateAddress(id, request);
        redirectAttributes.addFlashAttribute("successMessage",
                "Address '" + updated.getAddressTitle() + "' updated successfully.");
        return "redirect:/user/addresses";
    }


    @PostMapping("/user/addresses/delete/{id}")
    public String deleteAddress(@PathVariable Long id)
    {
        AddressDeleteRequest request = new AddressDeleteRequest();
        request.setId(id);
        userService.deleteAddress(request);
        return "redirect:/user/addresses";
    }

    @GetMapping("/user/payments")
    public String showPayments(Model model)
    {
        model.addAttribute("cards", userService.getPaymentCards());
        model.addAttribute("cardCreateRequest", new PaymentCardCreateRequest());
        return "user/payments";
    }

    @PostMapping("/user/payments/add")
    public String addPaymentCard(@ModelAttribute PaymentCardCreateRequest request,
                                 RedirectAttributes redirectAttributes)
    {
        try {
            PaymentCardResponse saved = userService.addPaymentCard(request);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Card ending in " + saved.getLastFourDigits() + " saved successfully.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/user/payments";
    }

    @PostMapping("/user/payments/delete/{id}")
    public String deletePaymentCard(@PathVariable Long id,
                                    RedirectAttributes redirectAttributes)
    {
        PaymentCardDeleteRequest request = new PaymentCardDeleteRequest();
        request.setId(id);
        userService.deletePaymentCard(request);
        redirectAttributes.addFlashAttribute("successMessage", "Card removed successfully.");
        return "redirect:/user/payments";
    }
}
