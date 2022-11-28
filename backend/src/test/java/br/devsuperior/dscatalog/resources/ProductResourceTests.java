package br.devsuperior.dscatalog.resources;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import br.devsuperior.dscatalog.dto.ProductDTO;
import br.devsuperior.dscatalog.services.ProductService;

@WebMvcTest(ProductResource.class)
public class ProductResourceTests {

	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private ProductService service;
	
	private ProductDTO dto;
	private PageImpl<ProductDTO> page;
	
	@BeforeEach
	void setup() throws Exception {
		dto = new ProductDTO();
		page = new PageImpl<>(List.of(dto));
		
		when(service.findAllPaginado(any())).thenReturn(page);
	}
	
	@Test
	public void findAllPaginadoShouldReturnAnPage() throws Exception {
		 ResultActions resultActions = mockMvc.perform(get("/products").accept(MediaType.APPLICATION_JSON));
		 resultActions.andExpect(status().is2xxSuccessful());
	}
	
}
