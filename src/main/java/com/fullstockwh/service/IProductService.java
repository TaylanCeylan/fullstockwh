package com.fullstockwh.service;

import com.fullstockwh.entity.Product;
import java.util.List;


public interface IProductService
{
    Product saveProduct(Product product);
    List<Product> getAllProducts();
    Product getProductById(Long id);
    Product updateStock(Long id, Integer newQuantity); //for Warehouse manager
}
