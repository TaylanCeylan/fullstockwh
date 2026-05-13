package com.fullstockwh.user.address;

import com.fullstockwh.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long>
{
    List<Address> findByUser (UserEntity user);
    Optional<Address> findByIdAndUser (Long id, UserEntity user);
    @Query("SELECT a FROM Address a WHERE a.user = :user AND a.isTemporary = false")
    List<Address> findByUserAndTemporaryFalse(@Param("user") UserEntity user);
    boolean existsByUserAndAddressTitleIgnoreCase(UserEntity user, String addressTitle);
}
