package ru.tokarev.service.categoryservice;

import ru.tokarev.entity.Category;

import java.util.List;

public interface CategoryService {

    Category getById(Long id);

    List<Category> getAll();

    Category createCategory(Category category);

    Category updateCategory(Long id, Category category);

    void deleteCategory(Long id);

    List<Category> createCategories(List<Category> categoryList);
}
