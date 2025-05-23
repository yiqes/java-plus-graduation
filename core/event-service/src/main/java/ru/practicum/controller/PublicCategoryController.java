package ru.practicum.controller;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.service.category.CategoryService;

import java.util.List;

/**
 * The type Public category controller.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/categories")
public class PublicCategoryController {
    private final CategoryService categoryService;

    /**
     * Gets categories.
     *
     * @param from the from
     * @param size the size
     * @return the categories
     */
    @GetMapping()
    List<CategoryDto> getCategories(@RequestParam(name = "from", defaultValue = "0") @PositiveOrZero int from,
                                    @RequestParam(name = "size", defaultValue = "10") @PositiveOrZero int size) {
        log.info("==> getCategories from = {}, size = {}", from, size);
        List<CategoryDto> categoriesDto = categoryService.getCategories(from, size);
        log.info("<== getCategories result: {}", categoriesDto);
        return categoriesDto;
    }

    /**
     * Gets category by id.
     *
     * @param catId the cat id
     * @return the category by id
     */
    @GetMapping("/{cat-id}")
    CategoryDto getCategoryById(@PathVariable("cat-id") Long catId) {
        log.info("==> getCategoryById = {}", catId);
        return categoryService.getCategoryById(catId);
    }

}
