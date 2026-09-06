package com.besysoft.service;

import com.besysoft.common.dto.SearchCriteria;
import com.besysoft.model.product.Category;

public class CategoryService {

    public Category searchByText(SearchCriteria search) {
        for (Category category : Category.values()) {
            if (category.matches(search.getValue())) {
                return category;
            }
        }
        return Category.OTHER;
    }
}
