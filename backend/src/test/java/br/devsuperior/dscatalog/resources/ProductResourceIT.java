package br.devsuperior.dscatalog.resources;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.devsuperior.dscatalog.dto.ProductDTO;
import br.devsuperior.dscatalog.tests.TokenUtil;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ProductResourceIT {

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private TokenUtil tokenUtil;
	
	private Long existingId;
	private Long noExistingId;
	private Long countTotalProduct;
	private ProductDTO dto;
	
	private String username;
	private String password;
	

	@BeforeEach
	void setup() throws Exception {
		existingId = 2l;
		noExistingId = 346l;
		countTotalProduct = 25l;
		dto = new ProductDTO(null, "Bazuca Militar", "Bazucona bitelosa", 3000.0, "http://www.g1.com.br", Instant.now());
		username = "maria@gmail.com";
		password = "123456";
				
	}
	
	@Test
	public void findAllShouldReturnSortedPageWhenSortByName() throws Exception {
		ResultActions actions = mockMvc.perform(
				get("/products?page=0&size=12&sort=name,asc")
					.accept(MediaType.APPLICATION_JSON)
			);
		actions.andExpect(status().isOk());
		actions.andExpect(jsonPath("$.totalElements").value(countTotalProduct));
		actions.andExpect(jsonPath("$.content").exists());
		actions.andExpect(jsonPath("$.content[0].name").value("Macbook Pro"));
		actions.andExpect(jsonPath("$.content[1].name").value("PC Gamer"));
		actions.andExpect(jsonPath("$.content[2].name").value("PC Gamer Alfa"));
		
	}
	
	@Test
	public void updateShouldReturnObjectDtoWhenIdExist() throws Exception {
		
		String accessToken = tokenUtil.obtainAccessToken(mockMvc, username, password);
		
		String JsonBody = objectMapper.writeValueAsString(dto);
		
		ResultActions resultActions = mockMvc.perform(
				put("/products/{id}", existingId)
					.header("Authorization","Bearer " + accessToken)
					.content(JsonBody)
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
		);
		resultActions.andExpect(status().isOk());
		resultActions.andExpect(jsonPath("$.id").value(existingId));
		resultActions.andExpect(jsonPath("$.name").value("Bazuca Militar"));
	}
	
	@Test
	public void updateShouldReturnNotFoundnWhenIdDoesntExist() throws Exception {
		String accessToken = tokenUtil.obtainAccessToken(mockMvc, username, password);
		String JsonBody = objectMapper.writeValueAsString(dto);
		
		ResultActions resultActions = mockMvc.perform(
				put("/products/{id}", noExistingId)
					.header("Authorization","Bearer " + accessToken)
					.content(JsonBody)
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
		);
		resultActions.andExpect(status().isNotFound());
	}
}
