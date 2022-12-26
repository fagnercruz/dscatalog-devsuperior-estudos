package br.devsuperior.dscatalog.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import br.devsuperior.dscatalog.dto.ProductDTO;
import br.devsuperior.dscatalog.repositories.ProductRepository;
import br.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;

@SpringBootTest
@Transactional
public class ProductServiceIT {
	
	@Autowired
	private ProductService productService;
	@Autowired
	private ProductRepository productRepository;
	
	private Long existingId;
	private Long noExistingId;
	private Long countTotalProduct;
	

	@BeforeEach
	void setup() throws Exception {
		existingId = 2l;
		noExistingId = 346l;
		countTotalProduct = 25l;
	}
	
	@Test
	public void deleteShouldDeleteResourceWhenIdExist() {
		productService.delete(existingId);
		
		Assertions.assertEquals(countTotalProduct-1, productRepository.count());
	}
	
	@Test
	public void deleteShouldThrowRecourceNotFoundWhenIdDoesntExist() {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			productService.delete(noExistingId);
		});
	}
	
	@Test
	public void findAllPaginadoShouldReturnPageWhenPage0AndSize10() {
		PageRequest pageRequest = PageRequest.of(0, 10);
		Page<ProductDTO> results = productService.findAllPaginado(0l,"",pageRequest);
		
		Assertions.assertTrue(!results.isEmpty());       						  // testa se a pagina está vazia
		Assertions.assertEquals(0, results.getNumber()); 						  // testa a página
		Assertions.assertEquals(10, results.getSize()); 						  // testa a qtde por pagina
		Assertions.assertEquals(countTotalProduct, results.getTotalElements());   // testa o total de elementos da cosulta
	}
	
	@Test
	public void findAllPaginadoShouldReturnEmptyPageWhenPageOverflow() {
		PageRequest pageRequest = PageRequest.of(50, 10);
		Page<ProductDTO> results = productService.findAllPaginado(0l,"",pageRequest);
		
		Assertions.assertTrue(results.isEmpty());       						  // testa se a pagina está vazia
		
	}
	
	@Test
	public void findAllPaginadoShouldReturnSortedPageWhenSortByName() {
		PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("name"));
		Page<ProductDTO> results = productService.findAllPaginado(0l,"",pageRequest);
		
		Assertions.assertTrue(!results.isEmpty());       						  // testa se a pagina está vazia
		Assertions.assertEquals("Macbook Pro", results.getContent().get(0).getName());
		Assertions.assertEquals("PC Gamer", results.getContent().get(1).getName());
		Assertions.assertEquals("PC Gamer Alfa", results.getContent().get(2).getName());
		
	}
}
