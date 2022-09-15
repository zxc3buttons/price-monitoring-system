package ru.tokarev.service.categoryservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tokarev.dao.categorydao.CategoryDao;
import ru.tokarev.entity.Category;
import ru.tokarev.exception.categoryexception.CategoryBadRequestException;
import ru.tokarev.exception.categoryexception.CategoryExistsException;
import ru.tokarev.exception.categoryexception.CategoryNotFoundException;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    private CategoryDao<Category> categoryDao;

    @Autowired
    public void setDao(CategoryDao<Category> categoryDao) {
        this.categoryDao = categoryDao;
        this.categoryDao.setClazz(Category.class);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public Category getById(Long id) {
        return categoryDao.findById(id).orElseThrow(
                () -> new CategoryNotFoundException("Category with this id not found")
        );
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public List<Category> getAll() {

        List<Category> categoryList = categoryDao.findAll().orElseThrow(()
                -> new CategoryNotFoundException("Categories not found"));

        if (categoryList.size() == 0) {
            throw new CategoryNotFoundException("Categories not found");
        }

        return categoryDao.findAll().orElseThrow(() -> new CategoryNotFoundException("Categories not found"));
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional
    public Category createCategory(Category category) {
        if (categoryDao.findByName(category.getName()).isPresent()) {
            throw new CategoryExistsException("Category with this name already exists");
        }

        return categoryDao.create(category).orElseThrow(CategoryBadRequestException::new);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional
    public List<Category> createCategories(List<Category> categoryList) {

        List<Category> createdCategoryList = new ArrayList<>();
        for (Category category : categoryList) {
            createdCategoryList.add(createCategory(category));
        }

        return createdCategoryList;
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional
    public Category updateCategory(Long id, Category category) {
        Category existingCategory = categoryDao.findById(id).orElseThrow(
                () -> new CategoryNotFoundException("Category with this id not found")
        );
        existingCategory.setName(category.getName());

        return categoryDao.update(existingCategory).orElseThrow(CategoryBadRequestException::new);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional
    public void deleteCategory(Long id) {

        Category existingCategory = categoryDao.findById(id).orElseThrow(
                () -> new CategoryNotFoundException("Category with this id not found")
        );

        categoryDao.deleteById(existingCategory.getId());
    }
}
