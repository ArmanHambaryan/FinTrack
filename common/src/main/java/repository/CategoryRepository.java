package repository;

import model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

    List<Category> findByUserIdOrUserIdIsNullOrderByNameAsc(Integer userId);

    Optional<Category> findByUserIdAndNameIgnoreCase(Integer userId, String name);

    Optional<Category> findByUserIdIsNullAndNameIgnoreCase(String name);
}
