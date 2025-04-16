package ru.practicum.mapper.category;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.NewCategoryDto;
import ru.practicum.model.Category;

import java.util.List;

/**
 * The interface Category mapper.
 */
@Mapper(componentModel = "spring")
public interface CategoryMapper {

    /**
     * To category dto category dto.
     *
     * @param category the category
     * @return the category dto
     */
    CategoryDto toCategoryDto(Category category);

    /**
     * Dto to category category.
     *
     * @param newCategoryDto the new category dto
     * @return the category
     */
    @Mapping(target = "id", ignore = true)
    Category dtoToCategory(NewCategoryDto newCategoryDto);

    /**
     * To categories dto list.
     *
     * @param categories the categories
     * @return the list
     */
    List<CategoryDto> toCategoriesDto(List<Category> categories);

    /**
     * To entity category.
     *
     * @param categoryDto the category dto
     * @return the category
     */
    Category toEntity(CategoryDto categoryDto);

    /**
     * To category from category dto category.
     *
     * @param categoryDto the category dto
     * @return the category
     */
    Category toCategoryFromCategoryDto(CategoryDto categoryDto);
}
