package br.devsuperior.dscatalog.services;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.devsuperior.dscatalog.dto.CategoryDTO;
import br.devsuperior.dscatalog.dto.ProductDTO;
import br.devsuperior.dscatalog.entities.Category;
import br.devsuperior.dscatalog.entities.Product;
import br.devsuperior.dscatalog.repositories.CategoryRepository;
import br.devsuperior.dscatalog.repositories.ProductRepository;
import br.devsuperior.dscatalog.services.exceptions.DatabaseException;
import br.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;

@Service
public class ProductService {
	
	@Autowired
	private ProductRepository repository;
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	@Transactional(readOnly = true)
	public Page<ProductDTO> findAllPaginado(Long categoryId, String name, Pageable pageable){
		List<Category> categories = (categoryId == 0) ? null : Arrays.asList(categoryRepository.getOne(categoryId));
		Page<Product> list = repository.findFiltered(categories, name, pageable);
		repository.findProductsWithCategories(list.getContent());
		return list.map(item -> new ProductDTO(item, item.getCategories()));
	}

	@Transactional(readOnly = true)
	public ProductDTO findById(Long id) {
		Optional<Product> obj = repository.findById(id);
		Product entity = /* obj.get(); */ obj.orElseThrow( () -> new ResourceNotFoundException("Produto não encontrado."));  
		return new ProductDTO(entity, entity.getCategories());
		
	}
	@Transactional
	public ProductDTO save(ProductDTO dto) {
				
		//Forma acadêmica (passo-a-passo)
		Product entity = new Product();
		copyDTOToEntity(dto, entity);
		entity = repository.save(entity);
		return new ProductDTO(entity); 
		
	}

	@Transactional
	public ProductDTO update(Long id, ProductDTO dto) {
		
		// Não usar o findById pq aí a operação de update vai usar 2 requisições ao banco
		// será usado uma nova abordagem para obter o objeto sem consulta ao banco
		
		try {
			Product entity =  repository.getOne(id);
			copyDTOToEntity(dto, entity);
			entity = repository.save(entity);
			return new ProductDTO(entity);
		} catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException("Id não encontrado: " + id);
		}
		
	}

	public void delete(Long id) {
		try {
			repository.deleteById(id);
		} catch (EmptyResultDataAccessException e) {
			throw new ResourceNotFoundException("Id não encontrado: " + id);
		} catch (DataIntegrityViolationException e) {
			throw new DatabaseException("Violação de integridade (Integrity voilation)");
		}
		
	}
	
	
	private void copyDTOToEntity(ProductDTO dto, Product entity) {
		entity.setName(dto.getName());
		entity.setDescription(dto.getDescription());
		entity.setDate(dto.getDate());
		entity.setImgUrl(dto.getImgUrl());
		entity.setPrice(dto.getPrice());
		
		entity.getCategories().clear();
		for(CategoryDTO catDTO : dto.getCategories()) {
			Category cat = categoryRepository.getOne(catDTO.getId());
			entity.getCategories().add(cat);
		}
	}


}
