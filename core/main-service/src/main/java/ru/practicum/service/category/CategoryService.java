package ru.practicum.service.category;

import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.NewCategoryDto;

import java.util.List;

/**
 * The interface Category service.
 */
public interface CategoryService {
    /**
     * Create category dto.
     *
     * @param newCategoryDto the new category dto
     * @return the category dto
     */
    CategoryDto create(NewCategoryDto newCategoryDto);

    /**
     * Delete.
     *
     * @param id the id
     */
    void delete(Long id);

    /**
     * Update category dto.
     *
     * @param id             the id
     * @param newCategoryDto the new category dto
     * @return the category dto
     */
    CategoryDto update(Long id, NewCategoryDto newCategoryDto);

    /**
     * Gets categories.
     *
     * @param from the from
     * @param size the size
     * @return the categories
     */
    List<CategoryDto> getCategories(int from, int size);

    /**
     * Gets category by id.
     *
     * @param id the id
     * @return the category by id
     */
    CategoryDto getCategoryById(Long id);
}
