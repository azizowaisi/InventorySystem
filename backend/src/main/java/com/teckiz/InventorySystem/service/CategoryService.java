package com.teckiz.InventorySystem.service;

import com.teckiz.InventorySystem.dto.CategoryDTO;
import com.teckiz.InventorySystem.dto.Response;

public interface CategoryService {
    Response createCategory(CategoryDTO categoryDTO);
    Response getAllCategories();
    Response getCategoryById(Long id);
    Response updateCategory(Long id, CategoryDTO categoryDTO);
    Response deleteCategory(Long id);
}
