package service.impl;

import model.Category;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.CategoryRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl service;

    @Captor
    private ArgumentCaptor<Category> categoryCaptor;

    @Test
    void findOrCreateRejectsBlankNames() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.findOrCreate(1, "   ")
        );

        assertEquals("Category name is required", exception.getMessage());
    }

    @Test
    void findOrCreateNormalizesAndSavesUserCategory() {
        when(categoryRepository.findByUserIdAndNameIgnoreCase(7, "Food & Dining")).thenReturn(Optional.empty());
        when(categoryRepository.findByUserIdIsNullAndNameIgnoreCase("Food & Dining")).thenReturn(Optional.empty());

        Category created = new Category(11, 7, "Food & Dining", null, null);
        when(categoryRepository.save(org.mockito.ArgumentMatchers.any(Category.class))).thenReturn(created);

        Category result = service.findOrCreate(7, "  Food   &   Dining  ");

        verify(categoryRepository).save(categoryCaptor.capture());
        assertEquals("Food & Dining", categoryCaptor.getValue().getName());
        assertEquals(7, categoryCaptor.getValue().getUserId());
        assertEquals(created, result);
    }
}
