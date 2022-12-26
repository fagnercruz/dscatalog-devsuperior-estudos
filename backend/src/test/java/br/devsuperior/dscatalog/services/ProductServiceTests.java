package br.devsuperior.dscatalog.services;

import static org.mockito.ArgumentMatchers.any;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import br.devsuperior.dscatalog.dto.ProductDTO;
import br.devsuperior.dscatalog.entities.Category;
import br.devsuperior.dscatalog.entities.Product;
import br.devsuperior.dscatalog.repositories.CategoryRepository;
import br.devsuperior.dscatalog.repositories.ProductRepository;
import br.devsuperior.dscatalog.services.exceptions.DatabaseException;
import br.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;

@ExtendWith(SpringExtension.class)
public class ProductServiceTests {
	
	@InjectMocks
	private ProductService service;
	
	@Mock
	private ProductRepository repository;
	@Mock
	private CategoryRepository categoryRepository;
	
	private Long existId;
	private Long nonExistId;
	private Long dependentId;
	private Long catExistId;
	private Long catNonExistId;
	
	private Product product;
	private Category category;
	private PageImpl<Product> page;
	private ProductDTO dto;
	
	@BeforeEach
	void setup() throws Exception{
		existId = 1l;
		nonExistId = 1000l;
		dependentId = 4l;
		catExistId = 3l;
		catNonExistId = 6l;
		
		product = new Product();
		category = new Category();
		dto = new ProductDTO(product);
		page = new PageImpl<>(List.of(product));
		
		// comportamento do findAllPageable
		Mockito.when(repository.findAll((Pageable) any())).thenReturn(page);

		// comportamento do findById
		Mockito.when(repository.findById(existId)).thenReturn(Optional.of(product));
		Mockito.when(repository.findById(nonExistId)).thenReturn(Optional.empty());

		Mockito.when(repository.findFiltered(any(),any(), any())).thenReturn(page);
		
		// comportamento do update + categoryPerository (devido ao metodo copyDtoToEntity)
		Mockito.when(repository.getOne(existId)).thenReturn(product);
		Mockito.when(repository.getOne(nonExistId)).thenThrow(EntityNotFoundException.class);
		Mockito.when(categoryRepository.getOne(catExistId)).thenReturn(category);
		Mockito.when(categoryRepository.getOne(catNonExistId)).thenThrow(EntityNotFoundException.class);
		
		
		// comportamento do save
		Mockito.when(repository.save(any())).thenReturn(product);
		
		
		// comportamentos do repositório - deleteById
		Mockito.doNothing().when(repository).deleteById(existId);
		Mockito.doThrow(ResourceNotFoundException.class).when(repository).deleteById(nonExistId);
		Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId);
	}
	
	/* -------- FIND ALL PAGEABLE TESTS ------------*/
	
	@Test
	public void findAllPageableShouldReturnPage() {
		Pageable pageable = PageRequest.of(0, 10);
		Page<ProductDTO> result = service.findAllPaginado(0L,"",pageable);
		Assertions.assertNotNull(result);
	
	}
	
	
	/* -------- FIND BY ID TESTS ------------*/
	@Test
	public void findByIdShouldReturnProductDTOWhenIdExists() {
		ProductDTO result = service.findById(existId);
		Assertions.assertNotNull(result);
		Mockito.verify(repository).findById(existId);
	}
	
	@Test
	public void findByIdShouldThrowResourceExceptionWhenIdNotExist() {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.findById(nonExistId);
		});
		Mockito.verify(repository).findById(nonExistId);
	}
	
	/* -------- UPDATE TESTS ------------*/
	
	@Test
	public void updateShouldReturnDTOWhenIdExists() {
		ProductDTO result = service.update(existId, dto);
		Assertions.assertNotNull(result);
		Mockito.verify(repository).getOne(existId);
	}
	@Test
	public void updateShouldThrowResourceExceptionWhenIdNotExists() {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.update(nonExistId, dto);
		});
		Mockito.verify(repository).getOne(nonExistId);
	}
	
	
	/* -------- DELETE TESTS ------------*/
	
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
