package com.fullstockwh.entity;

import com.fullstockwh.enums.Color;
import com.fullstockwh.enums.Size;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
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

    @NotBlank(message = "Product name is required!")
    private String name;

    @Column(unique = true)
    private String sku; // Value for Stock Keeping Unit

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    @Min(0)
    private Double price;

    private Double discountRate;

    @Min(0)
    private Integer stockQuantity;

    //Values for logistics
    private Double weight; //kg
    private Double width; //cm
    private Double height; //cm
    private Double length; //cm

    @Enumerated(EnumType.STRING)
    private Color color;

    @Enumerated(EnumType.STRING)
    private Size size;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<Review> reviews;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
