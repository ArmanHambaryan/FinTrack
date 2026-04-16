package com.example.rest.service;

import model.Category;

import java.util.List;

public interface CategoryService {

    List<Category> getAvailableCategories(Integer userId);

    Category findOrCreate(Integer userId, String name);
}
