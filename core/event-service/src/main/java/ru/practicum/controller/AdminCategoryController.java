package ru.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.NewCategoryDto;
import ru.practicum.service.category.CategoryService;

/**
 * The type Admin category controller.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/categories")
public class AdminCategoryController {
    private final CategoryService categoryService;
    private static final String PATH = "cat-id";

    /**
     * Create category dto.
     *
     * @param newCategoryDto the new category dto
     * @return the category dto
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    CategoryDto create(@Valid @RequestBody NewCategoryDto newCategoryDto) {
        log.info("==> Admin creating category: {}", newCategoryDto);
        CategoryDto categoryDto = categoryService.create(newCategoryDto);
        log.info("<== Creating category: {}", categoryDto);
        return categoryDto;
    }

    /**
     * Delete category with provided id by admin.
     *
     * @param catId the cat id
     */
    @DeleteMapping("/{cat-id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable(PATH) Long catId) {
        log.info("==> delete for catId = {}", catId);
        categoryService.delete(catId);
    }

    /**
     * Update category dto.
     *
     * @param catId          the cat id
     * @param newCategoryDto the new category dto
     * @return the category dto
     */
    @PatchMapping("/{cat-id}")
    CategoryDto update(@PathVariable(PATH) Long catId, @Valid @RequestBody NewCategoryDto newCategoryDto) {
        log.info("==> Admin updating category: {}", newCategoryDto);
        CategoryDto categoryDto = categoryService.update(catId, newCategoryDto);
        log.info("<== Admin updating user: {}", categoryDto);
        return categoryDto;
    }

}
