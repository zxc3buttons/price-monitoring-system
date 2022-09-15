package ru.tokarev.controller;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.tokarev.dto.CategoryDto;
import ru.tokarev.entity.Category;
import ru.tokarev.service.categoryservice.CategoryService;
import ru.tokarev.utils.MapperUtil;

import java.util.List;

@Slf4j
@RequestMapping("/api/categories")
@RestController
public class CategoryController {

    private final CategoryService categoryService;

    private final ModelMapper modelMapper;

    @Autowired
    public CategoryController(CategoryService categoryService, ModelMapper modelMapper) {
        this.categoryService = categoryService;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public ResponseEntity<List<CategoryDto>> getAll() {
        List<Category> categoryList = categoryService.getAll();
        List<CategoryDto> categoryDtoList = MapperUtil.convertList(categoryList, this::convertToCategoryDto);

        log.info("Response for GET request /categories with data {}", categoryDtoList);
        for(CategoryDto categoryDto: categoryDtoList) {
            log.info("{}, {}", categoryDto.getId(), categoryDto.getName());
        }

        return new ResponseEntity<>(categoryDtoList, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDto> getById(@PathVariable Long id) {
        Category category = categoryService.getById(id);
        CategoryDto categoryDto = convertToCategoryDto(category);

        log.info("Response for GET request /categories/{} with data {}", id, categoryDto.getName());

        return new ResponseEntity<>(categoryDto, HttpStatus.OK);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CategoryDto> createCategory(@RequestBody CategoryDto categoryDto) {

        log.info("POST request /categories with data {}", categoryDto.getName());

        Category category = convertToCategoryEntity(categoryDto);
        Category createdCategory = categoryService.createCategory(category);
        CategoryDto createdCategoryDto = convertToCategoryDto(createdCategory);

        log.info("Response for POST request /categories with data {}, {}",
                createdCategoryDto.getId(), createdCategoryDto.getName());

        return new ResponseEntity<>(createdCategoryDto, HttpStatus.CREATED);
    }

    @PostMapping(value = "/import", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CategoryDto>> createCategories(
            @RequestBody List<CategoryDto> categoryDtoList) {

        log.info("POST request /categories/import with data {}", categoryDtoList);
        for(CategoryDto categoryDto: categoryDtoList) {
            log.info("{}", categoryDto.getName());
        }

        List<Category> categoryList = MapperUtil.convertList(categoryDtoList, this::convertToCategoryEntity);
        List<Category> createdCategoryList = categoryService.createCategories(categoryList);
        List<CategoryDto> createdCategoryDtoList = MapperUtil.convertList(
                createdCategoryList, this::convertToCategoryDto);

        log.info("Response for POST request /categories/import with data {}", createdCategoryDtoList);
        for(CategoryDto categoryDto: categoryDtoList) {
            log.info("{}, {}", categoryDto.getId(), categoryDto.getName());
        }

        return new ResponseEntity<>(createdCategoryDtoList, HttpStatus.CREATED);
    }

    @PatchMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CategoryDto> updateCategory(@PathVariable Long id, @RequestBody CategoryDto categoryDto) {

        log.info("PATCH request /categories/{} with data {}", id, categoryDto.getName());

        Category category = convertToCategoryEntity(categoryDto);
        Category updatedCategory = categoryService.updateCategory(id, category);
        CategoryDto updatedCategoryDto = convertToCategoryDto(updatedCategory);

        log.info("Response for PATCH request /categories/{} with data {}", id, updatedCategoryDto.getName());

        return new ResponseEntity<>(updatedCategoryDto, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long id) {

        log.info("DELETE request /categories/{} with data {}", id, id);

        categoryService.deleteCategory(id);

        log.info("Response for DELETE request /categories/{} with message {}", id, "Category deleted successfully");

        return ResponseEntity.ok().body("Category deleted successfully");
    }

    private CategoryDto convertToCategoryDto(Category category) {
        return modelMapper.map(category, CategoryDto.class);
    }

    private Category convertToCategoryEntity(CategoryDto categoryDto) {
        return modelMapper.map(categoryDto, Category.class);
    }
}
