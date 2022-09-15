package ru.tokarev.service.productservice;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tokarev.dao.categorydao.CategoryDao;
import ru.tokarev.dao.productdao.ProductDao;
import ru.tokarev.entity.Category;
import ru.tokarev.entity.Product;
import ru.tokarev.exception.categoryexception.CategoryNotFoundException;
import ru.tokarev.exception.productexception.ProductBadRequestException;
import ru.tokarev.exception.productexception.ProductExistsException;
import ru.tokarev.exception.productexception.ProductNotFoundException;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private ProductDao<Product> productDao;

    private CategoryDao<Category> categoryDao;

    @Autowired
    public void setMarketPlaceDao(ProductDao<Product> productDao, CategoryDao<Category> categoryDao) {
        this.productDao = productDao;
        this.productDao.setClazz(Product.class);
        this.categoryDao = categoryDao;
        this.categoryDao.setClazz(Category.class);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @Transactional
    public Product getById(Long id) {

        Product product = productDao.findById(id).orElseThrow(
                () -> new ProductNotFoundException("Product with this id not found")
        );

        product.setCategory((Category) Hibernate.unproxy(product.getCategory()));

        return product;
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @Transactional
    public List<Product> getAll(String categoryName) {

        if (categoryName == null) {

            List<Product> productList = productDao.findAll().orElseThrow(
                    () -> new ProductNotFoundException("Products not found")
            );

            if (productList.size() == 0) {
                throw new ProductNotFoundException("Products not found");
            }

            for (Product product : productList) {
                product.setCategory((Category) Hibernate.unproxy(product.getCategory()));
            }

            return productList;

        } else {
            Category category = categoryDao.findByName(categoryName).orElseThrow(
                    () -> new CategoryNotFoundException("Category doesn't exist")
            );

            List<Product> productList = productDao.findAllByCategory(category).orElseThrow(
                    () -> new ProductNotFoundException("Products not found")
            );

            if (productList.size() == 0) {
                throw new ProductNotFoundException("Products not found");
            }

            for (Product product : productList) {
                product.setCategory((Category) Hibernate.unproxy(product.getCategory()));
            }

            return productList;
        }
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @Transactional
    public Product createProduct(Product product) {
        if (productDao.findByName(product.getName()).isPresent()) {
            throw new ProductExistsException("Product with this name already exists");
        } else if (product.getCategory() == null) {
            throw new ProductBadRequestException("Category shouldn't be empty");
        } else if (product.getId() != null) {
            throw new ProductBadRequestException("Id field should be empty");
        } else if (product.getName().isEmpty() || product.getName() == null) {
            throw new ProductBadRequestException("Product name field shouldn't be empty");
        } else if (product.getCategory().getId() == null && product.getCategory().getName() == null) {
            throw new ProductBadRequestException("Category fields shouldn't be empty");
        } else if (product.getCategory().getId() != null) {
            Category category = categoryDao.findById(product.getCategory().getId()).orElseThrow(
                    () -> new CategoryNotFoundException("Category with this id doesn't exist")
            );
            product.setCategory(category);
            return productDao.create(product).orElseThrow(
                    () -> new ProductBadRequestException("Bad request")
            );
        } else {
            Category category = categoryDao.findByName(product.getCategory().getName()).orElseThrow(
                    () -> new CategoryNotFoundException("Category with this name doesn't exist")
            );
            product.setCategory(category);
            return productDao.create(product).orElseThrow(
                    () -> new ProductBadRequestException("Bad request")
            );
        }
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @Transactional
    public List<Product> createProducts(List<Product> productList) {

        List<Product> createdProductList = new ArrayList<>();
        for (Product product : productList) {
            createdProductList.add(createProduct(product));
        }

        return createdProductList;
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @Transactional
    public Product updateProduct(Long id, Product product) {

        Category category;
        Product existingProduct = productDao.findById(id).orElseThrow(
                () -> new ProductNotFoundException("Product with this id not found"));

        if (productDao.findByName(product.getName()).isPresent()) {
            throw new ProductExistsException("Product with this name already exists");
        }
        if (product.getName().isEmpty() || product.getName() == null) {
            throw new ProductBadRequestException("Product name field shouldn't be empty");
        } else if (product.getCategory() == null) {
            throw new ProductBadRequestException("Category shouldn't be empty");
        } else if (product.getCategory().getId() == null && product.getCategory().getName() == null) {
            throw new ProductBadRequestException("Category fields shouldn't be empty");
        } else if (product.getCategory().getId() != null) {
            category = categoryDao.findById(product.getCategory().getId()).orElseThrow(
                    () -> new CategoryNotFoundException("Category with this id doesn't exist")
            );
            existingProduct.setCategory(category);
        } else {
            category = categoryDao.findByName(product.getCategory().getName()).orElseThrow(
                    () -> new CategoryNotFoundException("Category with this name doesn't exist")
            );
            existingProduct.setCategory(category);
        }

        existingProduct.setName(product.getName());

        return productDao.update(existingProduct).orElseThrow(
                () -> new ProductBadRequestException("Bad request"));
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @Transactional
    public void deleteProduct(Long id) {

        Product product = productDao.findById(id).orElseThrow(
                () -> new ProductNotFoundException("Product with this id not found")
        );

        productDao.deleteById(product.getId());
    }
}
