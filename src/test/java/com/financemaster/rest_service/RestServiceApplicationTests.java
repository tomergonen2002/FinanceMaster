package com.financemaster.rest_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.financemaster.rest_service.persistence.entity.User;
import com.financemaster.rest_service.persistence.repository.UserRepository;

@SpringBootTest
class RestServiceApplicationTests {

	@Autowired
	private UserRepository userRepository;

	@Test
	void contextLoads() {
		// Test
	}

	@Test
	void userEmailExistsAfterSave() {
		User u = new User();
		u.setEmail("test@test.com");
		userRepository.save(u);
		assertTrue(userRepository.findByEmailIgnoreCase("test@test.com").isPresent());
	}
}