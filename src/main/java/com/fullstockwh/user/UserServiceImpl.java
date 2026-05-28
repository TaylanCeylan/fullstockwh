package com.fullstockwh.user;

import com.fullstockwh.user.dto.*;
import com.fullstockwh.order.OrderRepository;
import com.fullstockwh.user.address.Address;
import com.fullstockwh.user.address.AddressRepository;
import com.fullstockwh.user.payment_card.PaymentCard;
import com.fullstockwh.user.payment_card.PaymentCardRepository;
import com.fullstockwh.user.dto.PaymentCardCreateRequest;
import com.fullstockwh.user.dto.PaymentCardDeleteRequest;
import com.fullstockwh.user.dto.PaymentCardResponse;
import com.fullstockwh.auth.enums.Role;
import com.fullstockwh.user.dto.AdminUserCreateRequest;
import com.fullstockwh.user.dto.AdminUserUpdateRequest;
import com.fullstockwh.user.dto.UserResponse;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.time.YearMonth;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
class UserServiceImpl implements UserService
{
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final OrderRepository orderRepository;
    private final PaymentCardRepository paymentCardRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    private UserEntity getCurrentUser() {
        String email = Objects.requireNonNull(
                SecurityContextHolder.getContext().getAuthentication()).getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public UserEntity findById(Long id)
    {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found! ID: " + id));
    }

    @Override
    public UserEntity findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public UserUpdateRequest getUserUpdateRequest()
    {
        String currentEmail = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();

        var dbUser = userRepository.findByEmail(currentEmail).orElseThrow(() -> new RuntimeException("User not found! Email: " + currentEmail));

        UserUpdateRequest userUpdateRequest = new UserUpdateRequest();

        userUpdateRequest.setFirstName(dbUser.getFirstName());
        userUpdateRequest.setLastName(dbUser.getLastName());
        userUpdateRequest.setBirthDate(dbUser.getBirthDate());
        return userUpdateRequest;
    }

    @Transactional
    @Override
    public void updateUser(UserUpdateRequest user)
    {
        String currentEmail = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();

        var dbUser = userRepository.findByEmail(currentEmail).orElseThrow(() -> new RuntimeException("User not found! Email: " + currentEmail));

        if (user.getFirstName() != null)
        {
            dbUser.setFirstName(user.getFirstName());
        }

        if (user.getLastName() != null)
        {
            dbUser.setLastName(user.getLastName());
        }

        if (user.getBirthDate() != null)
        {
            dbUser.setBirthDate(user.getBirthDate());
        }
    }

    @Override
    public List<AddressResponse> getAddresses() {
        return addressRepository.findByUserAndTemporaryFalse(getCurrentUser())
                .stream()
                .map(this::mapToAddressResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AddressResponse addAddress(AddressCreateRequest request) {

        boolean titleExists = addressRepository
                .existsByUserAndAddressTitleIgnoreCase(getCurrentUser(), request.getAddressTitle());
        if (request.getAddressTitle() != null && !request.getAddressTitle().isBlank() && titleExists) {
            throw new RuntimeException("You already have an address with this title.");
        }

        Address address = Address.builder()
                .user (getCurrentUser())
                .addressTitle(request.getAddressTitle())
                .buildingDetails(request.getBuildingDetails())
                .city (request.getCity())
                .district (request.getDistrict())
                .neighborhood(request.getNeighborhood())
                .fullAddress (request.getFullAddress())
                .latitude (request.getLatitude())
                .longitude (request.getLongitude())
                .isTemporary (request.isTemporary())
                .build();
        return mapToAddressResponse(addressRepository.save(address));
    }

    @Override
    @Transactional
    public void deleteAddress(AddressDeleteRequest request) {
        Address address = addressRepository
                .findByIdAndUser(request.getId(), getCurrentUser())
                .orElseThrow(() -> new RuntimeException("Address not found"));

        orderRepository.findByShippingAddressId(request.getId())
                .forEach(o -> {
                    o.setShippingAddress(null);
                    orderRepository.save(o);
                });

        addressRepository.delete(address);
    }


    @Override
    @Transactional
    public AddressResponse updateAddress(Long id, AddressUpdateRequest request) {
        Address address = addressRepository.findByIdAndUser(id, getCurrentUser())
                .orElseThrow(() -> new RuntimeException("Address not found"));

        if (request.getAddressTitle()!= null) address.setAddressTitle(request.getAddressTitle());
        if (request.getCity()!= null) address.setCity(request.getCity());
        if (request.getDistrict()!= null) address.setDistrict(request.getDistrict());
        if (request.getNeighborhood()!= null) address.setNeighborhood(request.getNeighborhood());
        if (request.getFullAddress()!= null) address.setFullAddress(request.getFullAddress());
        if (request.getBuildingDetails()!= null) address.setBuildingDetails(request.getBuildingDetails());
        if (request.getLatitude()!= null) address.setLatitude(request.getLatitude());
        if (request.getLongitude()!= null) address.setLongitude(request.getLongitude());

        return mapToAddressResponse(addressRepository.save(address));
    }

    @Override
    public AddressResponse getAddressById(Long id) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Address not found"));
        return mapToAddressResponse(address);
    }

    @Override
    public List<PaymentCardResponse> getPaymentCards() {
        return paymentCardRepository
                .findByUserAndTemporaryFalse(getCurrentUser())
                .stream()
                .map(this::mapToCardResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PaymentCardResponse addPaymentCard(PaymentCardCreateRequest request) {

        if (isCardExpired(request.getExpiryDate())) {
            throw new RuntimeException("Card is expired");
        }

        if (paymentCardRepository.existsByUserAndLastFourDigitsAndExpiryDate(
                getCurrentUser(),
                request.getLastFourDigits(),
                request.getExpiryDate())) {
            throw new RuntimeException("This card is already saved.");
        }

        PaymentCard card = PaymentCard.builder()
                .user(getCurrentUser())
                .cardNickname(request.getCardNickname())
                .cardholderName(request.getCardholderName())
                .lastFourDigits(request.getLastFourDigits())
                .expiryDate(request.getExpiryDate())
                .isTemporary(request.isTemporary())
                .build();

        return mapToCardResponse(paymentCardRepository.save(card));
    }

    @Override
    @Transactional
    public void deletePaymentCard(PaymentCardDeleteRequest request) {
        PaymentCard card = paymentCardRepository
                .findByIdAndUser(request.getId(), getCurrentUser())
                .orElseThrow(() -> new RuntimeException("Card not found"));
        paymentCardRepository.delete(card);
    }

    @Override
    public PaymentCardResponse getPaymentCardById(Long id) {
        PaymentCard card = paymentCardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Card not found"));
        return mapToCardResponse(card);
    }


    private boolean isCardExpired(String expiryDate) {
        String[] parts = expiryDate.split("/");
        int month = Integer.parseInt(parts[0]);
        int year = 2000 + Integer.parseInt(parts[1]);
        return YearMonth.of(year, month).isBefore(YearMonth.now());
    }

    private PaymentCardResponse mapToCardResponse(PaymentCard card) {
        return PaymentCardResponse.builder()
                .id (card.getId())
                .cardNickname (card.getCardNickname())
                .cardholderName(card.getCardholderName())
                .lastFourDigits(card.getLastFourDigits())
                .expiryDate (card.getExpiryDate())
                .maskedNumber ("**** **** **** " + card.getLastFourDigits())
                .build();
    }

    private AddressResponse mapToAddressResponse(Address address) {
        return AddressResponse.builder()
                .id (address.getId())
                .addressTitle(address.getAddressTitle())
                .buildingDetails(address.getBuildingDetails())
                .city (address.getCity())
                .district (address.getDistrict())
                .neighborhood(address.getNeighborhood())
                .fullAddress (address.getFullAddress())
                .latitude (address.getLatitude())
                .longitude (address.getLongitude())
                .build();
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void adminCreateUser(AdminUserCreateRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent())
            throw new RuntimeException("Email already in use");

        UserEntity user = UserEntity.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .birthDate(request.getBirthDate())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .enabled(true)
                .build();

        userRepository.save(user);
    }

    @Override
    @Transactional
    public void adminUpdateUser(Long id, AdminUserUpdateRequest request) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (request.getFirstName() != null) user.setFirstName(request.getFirstName());
        if (request.getLastName() != null) user.setLastName(request.getLastName());

        userRepository.save(user);
    }

    @Override
    @Transactional
    public void toggleUserEnabled(Long id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setEnabled(!user.isEnabled());
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getRole() == Role.CUSTOMER) {
            boolean hasOrders = orderRepository.existsByUser(user);
            if (hasOrders)
                throw new RuntimeException(
                        "This customer has order history and cannot be deleted. You can deactivate the account instead.");
        }

        userRepository.delete(user);
    }

    private UserResponse mapToUserResponse(UserEntity user) {
        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(user.getRole())
                .enabled(user.isEnabled())
                .build();
    }
}
