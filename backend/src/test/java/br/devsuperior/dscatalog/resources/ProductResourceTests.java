package br.devsuperior.dscatalog.resources;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.devsuperior.dscatalog.dto.ProductDTO;
import br.devsuperior.dscatalog.services.CategoryService;
import br.devsuperior.dscatalog.services.ProductService;
import br.devsuperior.dscatalog.services.exceptions.DatabaseException;
import br.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import br.devsuperior.dscatalog.tests.TokenUtil;

//@WebMvcTest(ProductResource.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ProductResourceTests {

	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private TokenUtil tokenUtil;
	
	@MockBean
	private ProductService service;
	@MockBean
	private CategoryService categoryService;
	
	private ProductDTO dto;
	private PageImpl<ProductDTO> page;
	private Long existId;
	private Long nonExistId;
	private Long violatedId;
	private String username;
	private String password;
	
	@BeforeEach
	void setup() throws Exception {
		dto = new ProductDTO();
		
		// passar esses valores para não falhar nos testes de update e save retornar 422
		dto.setName("teste");
		dto.setDate(Instant.now());
		dto.setPrice(23.5);
		
		page = new PageImpl<>(List.of(dto));
		existId = 2l;
		nonExistId = 3l;
		violatedId = 4l;
		username = "maria@gmail.com";
		password = "123456";
		
		// simulando o service.save
		when(service.save(any())).thenReturn(dto);
		
		// simulando o service.findAllPaginado
		when(service.findAllPaginado(any(),any(),any())).thenReturn(page);
		
		// simulando o service.findById
		when(service.findById(existId)).thenReturn(dto);
		when(service.findById(nonExistId)).thenThrow(ResourceNotFoundException.class);
		
		// simulando o service.update
		when(service.update(eq(existId), any())).thenReturn(dto);
		when(service.update(eq(nonExistId), any())).thenThrow(ResourceNotFoundException.class);
		
		// simulando o service.delete
		doNothing().when(service).delete(existId);
		doThrow(ResourceNotFoundException.class).when(service).delete(nonExistId);
		doThrow(DatabaseException.class).when(service).delete(violatedId);
		
	}
	
	@Test
	public void saveShouldReturnDto() throws Exception {
		String jsonBody = objectMapper.writeValueAsString(dto);
		String accessToken = tokenUtil.obtainAccessToken(mockMvc, username, password);
		
		ResultActions actions = mockMvc.perform(
				post("/products")
					.content(jsonBody)
					.header("Authorization","Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
				);
		actions.andExpect(status().isCreated());
		actions.andExpect(jsonPath("$.id").hasJsonPath());
	}
	
	@Test
	public void deleteShouldDoNothingWhenIdExist() throws Exception {
		
		String accessToken = tokenUtil.obtainAccessToken(mockMvc, username, password);
		
		ResultActions actions = mockMvc.perform(
					delete("/products/{id}", existId)
						.header("Authorization","Bearer " + accessToken)
						.accept(MediaType.APPLICATION_JSON)
				);
		actions.andExpect(status().isNoContent());
	}
	
	@Test
	public void deleteShouldThrowResourceNotFoundWhenIdDoesntExist() throws Exception {
		String accessToken = tokenUtil.obtainAccessToken(mockMvc, username, password);
		
		ResultActions actions = mockMvc.perform(
					delete("/products/{id}", nonExistId)
						.header("Authorization","Bearer " + accessToken)
						.accept(MediaType.APPLICATION_JSON)
				);
		actions.andExpect(status().isNotFound());
	}
	@Test
	public void deleteShouldThrowDatabaseWhenIdViolateDBRestrictions() throws Exception {
		String accessToken = tokenUtil.obtainAccessToken(mockMvc, username, password);
		
		ResultActions actions = mockMvc.perform(
					delete("/products/{id}", violatedId)
						.header("Authorization","Bearer " + accessToken)
						.accept(MediaType.APPLICATION_JSON)
				);
		actions.andExpect(status().isBadRequest());
	}
	@Test
	public void updateShouldReturnObjectDtoWhenIdExist() throws Exception {
		String accessToken = tokenUtil.obtainAccessToken(mockMvc, username, password);
		
		String JsonBody = objectMapper.writeValueAsString(dto);
		
		ResultActions resultActions = mockMvc.perform(
				put("/products/{id}", existId)
					.header("Authorization","Bearer " + accessToken)
					.content(JsonBody)
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
		);
		resultActions.andExpect(status().isOk());
		resultActions.andExpect(jsonPath("$.id").hasJsonPath());
	}
	@Test
	public void updateShouldThrowResourceNotFoundnWhenIdDoesntExist() throws Exception {
		String accessToken = tokenUtil.obtainAccessToken(mockMvc, username, password);
		
		String JsonBody = objectMapper.writeValueAsString(dto);
		
		ResultActions resultActions = mockMvc.perform(
				put("/products/{id}", nonExistId)
					.header("Authorization","Bearer " + accessToken)
					.content(JsonBody)
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
		);
		resultActions.andExpect(status().isNotFound());
	}
	
	@Test
	public void findAllPaginadoShouldReturnAnPage() throws Exception {
		ResultActions resultActions = mockMvc.perform(get("/products").accept(MediaType.APPLICATION_JSON));
		resultActions.andExpect(status().is2xxSuccessful());
	}
	
	@Test
	public void findByIdShouldReturnObjectDtoWhenIdExist() throws Exception {
		ResultActions resultActions = mockMvc.perform(get("/products/{id}", existId).accept(MediaType.APPLICATION_JSON));
		resultActions.andExpect(status().isOk());
		resultActions.andExpect(jsonPath("$.id").hasJsonPath());
	}
	@Test
	public void findByIdShouldThrowResourceNotFoundnWhenidNotExist() throws Exception {
		ResultActions resultActions = mockMvc.perform(get("/products/{id}", nonExistId).accept(MediaType.APPLICATION_JSON));
		resultActions.andExpect(status().isNotFound());
	}
	
}
