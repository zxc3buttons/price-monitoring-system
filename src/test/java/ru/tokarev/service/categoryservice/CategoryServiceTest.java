package ru.tokarev.service.categoryservice;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.tokarev.dao.categorydao.CategoryDao;
import ru.tokarev.entity.Category;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryDao<Category> categoryDao;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    void getById() {

        //arrange
        Category existingCategory = new Category(1L, "drinks");
        given(categoryDao.findById(1L)).willReturn(Optional.of(existingCategory));

        //act
        Category category = categoryService.getById(1L);

        //assert
        assertEquals(category, existingCategory);
    }

    @Test
    void getAll() {

        //arrange
        Category existingCategory1 = new Category(1L, "drinks");
        Category existingCategory2 = new Category(2L, "grocery");
        given(categoryDao.findAll()).willReturn(Optional.of(List.of(existingCategory1, existingCategory2)));

        //act
        List<Category> categoryList = categoryService.getAll();

        //assert
        assertEquals(categoryList, List.of(existingCategory1, existingCategory2));
    }

    @Test
    void createCategory() {

        //arrange
        Category categoryToCreate = new Category(1L, "drinks");
        given(categoryDao.create(categoryToCreate)).willReturn(Optional.of(categoryToCreate));

        //act
        Category category = categoryService.createCategory(categoryToCreate);

        //assert
        assertEquals(category, categoryToCreate);

    }

    @Test
    void updateCategory() {
        //arrange
        Category existingCategory = new Category(1L, "drinks");
        Category updatedCategory = new Category(1L, "sugar");
        given(categoryDao.findById(1L)).willReturn(Optional.of(existingCategory));
        given(categoryDao.update(existingCategory)).willReturn(Optional.of(updatedCategory));

        //act
        Category category = categoryService.updateCategory(1L, existingCategory);

        //assert
        assertEquals(category, updatedCategory);
    }

    @Test
    void deleteCategory() {
        //arrange
        Category existingCategory = new Category(1L, "drinks");
        given(categoryDao.findById(1L)).willReturn(Optional.of(existingCategory));
        willDoNothing().given(categoryDao).deleteById(1L);

        //act
        categoryService.deleteCategory(1L);

        //assert
        verify(categoryDao, times(1)).deleteById(1L);
    }
}