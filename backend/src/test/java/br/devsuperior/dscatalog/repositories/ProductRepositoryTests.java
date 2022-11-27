package br.devsuperior.dscatalog.repositories;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;

import br.devsuperior.dscatalog.entities.Product;

@DataJpaTest
public class ProductRepositoryTests {
	
	@Autowired
	private ProductRepository repository;
	
	Long idToTest;
	Long idOutOfInterval;
	
	@BeforeEach
	void setupVariablesForTests() throws Exception{
		idToTest = 2L;
		idOutOfInterval = 100l;
	}

	@Test
	public void deleteSholdDeleteObjectWhenIdExists() {
		repository.deleteById(idToTest);
		Optional<Product> result = repository.findById(idToTest);
		Assertions.assertTrue(result.isEmpty());
	}
	
	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {
		Assertions.assertThrows(EmptyResultDataAccessException.class, () -> {
			repository.deleteById(idOutOfInterval);
		});
	}
	
	@Test
	public void findByIdShouldReturnAnNomEmptyOptionalObjectWhenIdIsValid() {
		Optional<Product> result = repository.findById(idToTest);
		Assertions.assertTrue(result.isPresent());
	}
	
	@Test
	public void findByIdShouldReturnAnEmptyOptionalObjectWhenIdIsInvalid() {
		Optional<Product> result = repository.findById(idOutOfInterval);
		Assertions.assertTrue(result.isEmpty());
	}
}
