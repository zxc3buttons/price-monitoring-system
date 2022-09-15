package ru.tokarev.controller;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.tokarev.dto.CategoryDto;
import ru.tokarev.dto.ProductDto;
import ru.tokarev.entity.Product;
import ru.tokarev.service.productservice.ProductService;
import ru.tokarev.utils.MapperUtil;

import java.util.List;

@Slf4j
@RequestMapping("/api/products")
@RestController
public class ProductController {

    private final ProductService productService;

    private final ModelMapper modelMapper;

    @Autowired
    public ProductController(ProductService productService, ModelMapper modelMapper) {
        this.productService = productService;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public ResponseEntity<List<ProductDto>> getAll(
            @RequestParam(name = "category", required = false) String categoryName) {

        log.info("GET request for /products with data {}", categoryName);

        List<Product> productList = productService.getAll(categoryName);
        List<ProductDto> productDtoList = MapperUtil.convertList(productList, this::convertToProductDto);

        log.info("Response for GET request for /products with data {}", productDtoList);
        for(ProductDto productDto : productDtoList) {
            log.info("id {}, name {}, categoryId {}", productDto.getId(),
                    productDto.getName(), productDto.getCategoryDto().getId());
        }

        return new ResponseEntity<>(productDtoList, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getById(@PathVariable Long id) {

        log.info("GET request for /products/{} with data {}", id, id);

        Product product = productService.getById(id);
        ProductDto productDto = convertToProductDto(product);

        log.info("Response for GET request for /products/{} with data: id {}, name {}, categoryId {} ", id,
                productDto.getId(), productDto.getName(), productDto.getCategoryDto().getId());

        return new ResponseEntity<>(productDto, HttpStatus.OK);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProductDto> createProduct(@RequestBody ProductDto productDto) {

        log.info("POST request for /products with data: name {}, categoryId {}",
                productDto.getName(), productDto.getId());

        Product product = convertToProductEntity(productDto);
        Product createdProduct = productService.createProduct(product);
        ProductDto createdProductDto = convertToProductDto(createdProduct);

        log.info("Response for POST request for /products with data: name {}, categoryId {}",
                createdProductDto.getName(), createdProductDto.getId());

        return new ResponseEntity<>(createdProductDto, HttpStatus.CREATED);
    }

    @PostMapping(value = "/import", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ProductDto>> createProducts(@RequestBody List<ProductDto> productDtoList) {

        log.info("POST request for /products/import with data {}", productDtoList);
        for(ProductDto productDto : productDtoList) {
            log.info("name {}, categoryId {}", productDto.getName(), productDto.getCategoryDto().getId());
        }

        List<Product> productList = MapperUtil.convertList(productDtoList, this::convertToProductEntity);
        List<Product> createdProductList = productService.createProducts(productList);
        List<ProductDto> createdProductDtoList = MapperUtil.convertList(createdProductList, this::convertToProductDto);

        log.info("Response for POST request for /products/import with data {}", createdProductDtoList);
        for(ProductDto productDto : createdProductDtoList) {
            log.info("id {}, name {}, categoryId {}",
                    productDto.getId(), productDto.getName(), productDto.getCategoryDto().getId());
        }

        return new ResponseEntity<>(createdProductDtoList, HttpStatus.CREATED);
    }

    @PatchMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProductDto> updateProduct(@PathVariable Long id, @RequestBody ProductDto productDto) {

        log.info("PATCH request for /products/{} with data: name {}, categoryId {}",
                id, productDto.getName(), productDto.getCategoryDto().getId());

        Product product = convertToProductEntity(productDto);
        Product updatedProduct = productService.updateProduct(id, product);
        ProductDto updatedProductDto = convertToProductDto(updatedProduct);

        log.info("Response for PATCH request for /products/{} with data: id {}, name {}, categoryId {}",
                id, updatedProductDto.getId(), updatedProductDto.getName(),
                updatedProductDto.getCategoryDto().getId());

        return new ResponseEntity<>(updatedProductDto, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {

        log.info("DELETE request for /products/{} with data {}", id, id);

        productService.deleteProduct(id);

        log.info("Response for DELETE request for /products/{} with message {}", id, "Product deleted successfully");

        return new ResponseEntity<>("Product deleted successfully", HttpStatus.OK);
    }

    private ProductDto convertToProductDto(Product product) {
        CategoryDto categoryDto = modelMapper.map(product.getCategory(), CategoryDto.class);
        ProductDto productDto = modelMapper.map(product, ProductDto.class);
        productDto.setCategoryDto(categoryDto);

        return productDto;
    }

    private Product convertToProductEntity(ProductDto productDto) {
        return modelMapper.map(productDto, Product.class);
    }
}
