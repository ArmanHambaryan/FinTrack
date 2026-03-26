package service.impl;

import lombok.RequiredArgsConstructor;
import model.Category;
import org.springframework.stereotype.Service;
import repository.CategoryRepository;
import service.CategoryService;

import java.util.List;
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public List<Category> getAvailableCategories(Integer userId) {
        return categoryRepository.findByUserIdOrUserIdIsNullOrderByNameAsc(userId);
    }

    @Override
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
