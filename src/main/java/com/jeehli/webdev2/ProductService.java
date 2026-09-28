package com.jeehli.webdev2;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final ShopProperties shopProperties;

    public ProductService(ProductRepository productRepository, ShopProperties shopProperties) {
        this.productRepository = productRepository;
        this.shopProperties = shopProperties;
    }

    public List<Product> getProductsAbovePrice(double priceThreshold) {
        return productRepository.findAll().stream()
                .filter(product -> product.getPrice() > priceThreshold)
                .collect(Collectors.toList());
    }

    public String getShopName() {
        return shopProperties.getName();
    }

    public String getShopCurrency() {
        return shopProperties.getCurrency();
    }
}