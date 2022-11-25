package br.devsuperior.dscatalog.repositories;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import br.devsuperior.dscatalog.entities.Product;
import br.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;

@DataJpaTest
public class ProductRepositoryTests {
	
	@Autowired
	private ProductRepository repository;

	@Test
	public void deleteSholdDeleteObjectWhenIdExists() {
		
		Long idToTest = 2L;
		
		repository.deleteById(idToTest);
		
		Optional<Product> result = repository.findById(idToTest);
		
		Assertions.assertTrue(result.isEmpty());
	}
	
	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {
		Long idOutOfInterval = 100l;
		
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			repository.deleteById(idOutOfInterval);
		});
	}
}
