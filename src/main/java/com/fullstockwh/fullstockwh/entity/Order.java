package com.fullstockwh.fullstockwh.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "Order")
public class Order
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "order_name")
    private String orderName;
    @Column(name = "order_date")
    private String orderDate;
}
