package com.financemaster.rest_service.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.financemaster.rest_service.persistence.entity.Category;
import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
	List<Category> findByUserId(Long userId);

	void deleteByUserId(Long userId);
}
