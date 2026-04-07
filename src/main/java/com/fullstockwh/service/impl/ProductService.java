package com.fullstockwh.service.impl;

import com.fullstockwh.entity.Product;
import com.fullstockwh.repository.IProductRepository;
import com.fullstockwh.service.IProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService
{
    private final IProductRepository productRepository;

    @Override
    @Transactional
    public Product saveProduct(Product product)
    {
        return productRepository.save(product);
    }

    @Override
    public List<Product> getAllProducts()
    {
        return productRepository.findAll();
    }

    @Override
    public Product getProductById(Long id)
    {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    @Override
    @Transactional
    public Product updateStock(Long id, Integer qty)
    {
        Product product = getProductById(id);
        product.setStockQuantity(qty);
        return productRepository.save(product);
    }
}
