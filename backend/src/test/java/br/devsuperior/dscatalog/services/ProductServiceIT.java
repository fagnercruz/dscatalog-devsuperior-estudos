package br.devsuperior.dscatalog.services;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ProductServiceIT {
	
	@Autowired
	private ProductService productService;
	
	private Long existingId;
	private Long noExistingId;
	private Long countTotalProduct;
	

	@BeforeEach
	void setup() throws Exception {
		
	}
}
