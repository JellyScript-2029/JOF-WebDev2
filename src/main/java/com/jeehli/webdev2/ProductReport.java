package com.jeehli.webdev2;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductReport implements CommandLineRunner {

    private final ProductService productService;

    public ProductReport(ProductService productService) {
        this.productService = productService;
    }

    @Override
    public void run(String... args) {
        double threshold = 5000.0;
        List<Product> expensiveProducts = productService.getProductsAbovePrice(threshold);

        System.out.println("================================");
        System.out.println("       PRODUCT REPORT");
        System.out.println("================================");
        System.out.println("Shop: " + productService.getShopName());
        System.out.println("Currency: " + productService.getShopCurrency());
        System.out.println();
        System.out.printf("Products above %s %.0f:%n%n", productService.getShopCurrency(), threshold);

        for (Product product : expensiveProducts) {
            System.out.printf("%s - %s %.0f%n",
                    product.getName(),
                    productService.getShopCurrency(),
                    product.getPrice());
        }
        System.out.println("================================");
    }
}