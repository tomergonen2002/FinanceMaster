package financemaster.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import financemaster.persistence.entity.Category;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
	List<Category> findByUserId(Long userId);
}
