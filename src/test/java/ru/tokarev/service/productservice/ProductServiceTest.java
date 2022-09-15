package ru.tokarev.service.productservice;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.tokarev.dao.categorydao.CategoryDao;
import ru.tokarev.dao.productdao.ProductDao;
import ru.tokarev.entity.Category;
import ru.tokarev.entity.Product;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductDao<Product> productDao;

    @Mock
    private CategoryDao<Category> categoryDao;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    void givenProduct_whenGetById_thenReturnProduct() {

        //arrange
        Product existingProduct = new Product(1L, "water", new Category(1L, "drinks"));
        given(productDao.findById(1L)).willReturn(Optional.of(existingProduct));

        //act
        Product product = productService.getById(1L);

        //assert
        assertEquals(product, existingProduct);
    }

    @Test
    void givenProducts_whenGetAll_thenReturnProducts() {

        //arrange
        Category waterCategory = new Category(1L, "drinks");
        Product existingProduct1 = new Product(1L, "water", waterCategory);
        Product existingProduct2 = new Product(2L, "choco", new Category(2L, "sugar"));

        given(productDao.findAll()).willReturn(Optional.of(List.of(existingProduct1, existingProduct2)));
        given(categoryDao.findByName(waterCategory.getName())).willReturn(Optional.of(waterCategory));
        given(productDao.findAllByCategory(waterCategory)).willReturn(Optional.of(List.of(existingProduct1)));

        //act
        List<Product> productList = productService.getAll(null);
        List<Product> productListWithCertainCategory = productService.getAll("drinks");

        //assert
        assertEquals(productList, List.of(existingProduct1, existingProduct2));
        assertEquals(productListWithCertainCategory, List.of(existingProduct1));
    }

    @Test
    void givenCreatedProduct_whenCreateProduct_thenReturnCreatedProduct() {

        //arrange
        Category waterCategory = new Category(1L, "drinks");
        Product productToCreate = new Product(null, "water", waterCategory);
        given(productDao.create(productToCreate)).willReturn(Optional.of(productToCreate));
        given(categoryDao.findById(1L)).willReturn(Optional.of(waterCategory));

        //act
        Product product = productService.createProduct(productToCreate);

        //assert
        assertEquals(product, productToCreate);
    }

    @Test
    void givenCreatedProducts_whenCreateProducts_thenReturnListOfCreatedProducts() {

        //arrange
        Category waterCategory = new Category(1L, "drinks");
        Product productToCreate1 = new Product(null, "water", waterCategory);
        Product productToCreate2 = new Product(null, "water", waterCategory);

        given(productDao.create(productToCreate1)).willReturn(Optional.of(productToCreate1));
        given(categoryDao.findById(1L)).willReturn(Optional.of(waterCategory));
        given(productDao.create(productToCreate2)).willReturn(Optional.of(productToCreate2));
        given(categoryDao.findById(1L)).willReturn(Optional.of(waterCategory));

        //act
        List<Product> productList = productService.createProducts(List.of(productToCreate1, productToCreate2));

        //assert
        assertEquals(productList, List.of(productToCreate1, productToCreate2));
    }

    @Test
    void givenProductToUpdate_whenUpdateProduct_thenReturnUpdatedProduct() {

        //arrange
        Category waterCategory = new Category(1L, "drinks");
        Product existingProduct = new Product(1L, "water", waterCategory);
        Product updProduct = new Product(1L, "daughter", waterCategory);

        given(productDao.findById(1L)).willReturn(Optional.of(existingProduct));
        given(categoryDao.findById(1L)).willReturn(Optional.of(waterCategory));
        given(productDao.update(existingProduct)).willReturn(Optional.of(updProduct));

        //act
        Product product = productService.updateProduct(1L, existingProduct);

        //assert
        assertEquals(product, updProduct);
    }

    @Test
    void givenProduct_whenDeleteProduct_thenNothing() {

        //arrange
        Category waterCategory = new Category(1L, "drinks");
        Product existingProduct = new Product(1L, "water", waterCategory);
        given(productDao.findById(1L)).willReturn(Optional.of(existingProduct));
        willDoNothing().given(productDao).deleteById(1L);

        //act
        productService.deleteProduct(1L);

        //assert
        verify(productDao, times(1)).deleteById(1L);
    }
}