package br.devsuperior.dscatalog.services;

import static org.mockito.Mockito.mockitoSession;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import br.devsuperior.dscatalog.entities.Product;
import br.devsuperior.dscatalog.repositories.ProductRepository;
import br.devsuperior.dscatalog.services.exceptions.DatabaseException;
import br.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;

@ExtendWith(SpringExtension.class)
public class ProductServiceTests {
	
	@InjectMocks
	private ProductService service;
	
	@Mock
	private ProductRepository repository;
	
	private Long existId;
	private Long nonExistId;
	private Long dependentId;
	
	private Product product;
	private PageImpl<Product> page;
	
	@BeforeEach
	void setup() throws Exception{
		existId = 1l;
		nonExistId = 1000l;
		dependentId = 4l;
		
		product = Factory
		page = new PageImpl<>(List.of(product));
		
		
		Mockito.when(repository.findAll((Pageable)ArgumentMatchers.any())).thenReturn(page);
		
		
		// comportamentos do repositório - deleteById
		Mockito.doNothing().when(repository).deleteById(existId);
		Mockito.doThrow(ResourceNotFoundException.class).when(repository).deleteById(nonExistId);
		Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId);
	}
	@Test
	public void deleteShouldThrowDatabaseExceptionWhenDependentId() {
		Assertions.assertThrows(DatabaseException.class,() -> {
			service.delete(dependentId);
		});
		
		//verifica se o método mockado é chamado no repository pelo menos 1 vez
		//OBS: innso é necessário pq delete do service chama o repository
		Mockito.verify(repository, Mockito.times(1)).deleteById(dependentId);
	}
	
	@Test
	public void deleteShouldThrowResourceExceptionWhenIdNotExists() {
		Assertions.assertThrows(ResourceNotFoundException.class,() -> {
			service.delete(nonExistId);
		});
		Mockito.verify(repository, Mockito.times(1)).deleteById(nonExistId);
	}
	
	@Test
	public void deleteShouldDoNothingWhenIdExists() {
		Assertions.assertDoesNotThrow(() -> {
			service.delete(existId);
		});
		Mockito.verify(repository, Mockito.times(1)).deleteById(existId);
	}
	

}
