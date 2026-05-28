package com.fullstockwh.user;


import com.fullstockwh.user.dto.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface UserService
{
    UserEntity findById(Long id);

    UserEntity findByEmail(String email);

    AddressResponse getAddressById(Long id);

    UserUpdateRequest getUserUpdateRequest();

    void updateUser(UserUpdateRequest user);

    @PreAuthorize("hasRole('CUSTOMER')")
    List<AddressResponse> getAddresses();

    @PreAuthorize("hasRole('CUSTOMER')")
    AddressResponse addAddress (AddressCreateRequest request);

    @PreAuthorize("hasRole('CUSTOMER')")
    void deleteAddress(AddressDeleteRequest request);

    @PreAuthorize("hasRole('CUSTOMER')")
    AddressResponse updateAddress(Long id, AddressUpdateRequest request);

    @PreAuthorize("hasRole('CUSTOMER')")
    List<PaymentCardResponse> getPaymentCards();

    @PreAuthorize("hasRole('CUSTOMER')")
    PaymentCardResponse addPaymentCard(PaymentCardCreateRequest request);

    @PreAuthorize("hasRole('CUSTOMER')")
    void deletePaymentCard(PaymentCardDeleteRequest request);

    PaymentCardResponse getPaymentCardById(Long id);

    @PreAuthorize("hasRole('ADMIN')")
    List<UserResponse> getAllUsers();

    @PreAuthorize("hasRole('ADMIN')")
    void adminCreateUser(AdminUserCreateRequest request);

    @PreAuthorize("hasRole('ADMIN')")
    void adminUpdateUser(Long id, AdminUserUpdateRequest request);

    @PreAuthorize("hasRole('ADMIN')")
    void toggleUserEnabled(Long id);

    @PreAuthorize("hasRole('ADMIN')")
    void deleteUser(Long id);
}
