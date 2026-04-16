package com.example.rest.service.impl;

import lombok.RequiredArgsConstructor;
import model.Category;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import repository.CategoryRepository;
import com.example.rest.service.CategoryService;

import java.util.List;
@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "categories")
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    @Cacheable(key = "'user:' + #userId")
    public List<Category> getAvailableCategories(Integer userId) {
        return categoryRepository.findByUserIdOrUserIdIsNullOrderByNameAsc(userId);
    }

    @Override
    @CacheEvict(allEntries = true)
    public Category findOrCreate(Integer userId, String name) {
        String normalized = normalizeName(name);
        if (normalized.isBlank()) {
            throw new IllegalArgumentException("Category name is required");
        }

        if (userId != null) {
            return categoryRepository.findByUserIdAndNameIgnoreCase(userId, normalized)
                    .orElseGet(() -> categoryRepository.findByUserIdIsNullAndNameIgnoreCase(normalized)
                            .orElseGet(() -> categoryRepository.save(new Category(null, userId, normalized, null, null))));
        }

        return categoryRepository.findByUserIdIsNullAndNameIgnoreCase(normalized)
                .orElseGet(() -> categoryRepository.save(new Category(null, null, normalized, null, null)));
    }

    private String normalizeName(String name) {
        if (name == null) {
            return "";
        }
        return name.trim().replaceAll("\\s+", " ");
    }
}
