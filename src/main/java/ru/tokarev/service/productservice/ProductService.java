package ru.tokarev.service.productservice;

import ru.tokarev.entity.Product;

import java.util.List;

public interface ProductService {

    Product getById(Long id);

    List<Product> getAll(String categoryName);

    Product createProduct(Product product);

    Product updateProduct(Long id, Product product);

    void deleteProduct(Long id);

    List<Product> createProducts(List<Product> productList);
}
