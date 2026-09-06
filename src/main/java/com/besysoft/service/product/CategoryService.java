package com.besysoft.service.product;

import com.besysoft.common.dto.SearchCriteria;
import com.besysoft.common.result.Result;
import com.besysoft.model.product.Category;

public class CategoryService {

    public Result<Category> searchByText(SearchCriteria search) {

        if (search == null || search.value() == null) {
            return Result.failure(
                    "El criterio de búsqueda no puede ser nulo."
            );
        }

        return findCategory(search.value());
    }

    private Result<Category> findCategory(String text) {

        for (Category category : Category.values()) {
            if (category.matches(text)) {
                return Result.success(category);
            }
        }

        return Result.failure("No se encontró una categoría para: " + text);
    }
}
