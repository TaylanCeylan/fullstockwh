package com.fullstockwh.entity;

import com.fullstockwh.enums.Color;
import com.fullstockwh.enums.Size;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "product")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Ürün adı zorunludur")
    private String name;

    @Column(unique = true, nullable = false)
    private String sku; // Benzersiz stok kodu

    private String brand; // Marka filtrelemesi için

    @Column(length = 1000)
    private String description;

    @Min(0)
    private Double price;

    private Double discountRate; // İndirim maddesi

    @Min(0)
    private Integer stockQuantity; // Stok sayısı maddesi

    // --- Lojistik ve Karbon Maddeleri (Müşteri listesindeki o kritik kısımlar) ---
    private Double weight; // kg (Karbon emisyonu hesabı için)
    private Double width;  // cm
    private Double height; // cm
    private Double length; // cm

    @Enumerated(EnumType.STRING)
    private Color color; // Renk filtreleme maddesi

    @Enumerated(EnumType.STRING)
    private Size size; // Beden filtreleme maddesi

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<Review> reviews; // Ürüne değerlendirme maddesi

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
