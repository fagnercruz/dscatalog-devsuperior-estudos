package br.devsuperior.dscatalog.services;

import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.devsuperior.dscatalog.dto.ProductDTO;
import br.devsuperior.dscatalog.entities.Product;
import br.devsuperior.dscatalog.repositories.ProductRepository;
import br.devsuperior.dscatalog.services.exceptions.DatabaseException;
import br.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;

@Service
public class ProductService {
	
	@Autowired
	private ProductRepository repository;
	
	@Transactional(readOnly = true)
	public Page<ProductDTO> findAllPaginado(PageRequest pageRequest){
		Page<Product> list = repository.findAll(pageRequest);
		return list.map(item -> new ProductDTO(item));
//		return list.stream().map(item -> new ProductDTO(item)).collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public ProductDTO findById(Long id) {
		//return new ProductDTO(categoryRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada")));
		
		
		// mesma coisa acima só que destrinchada para entender melhor
		Optional<Product> obj = repository.findById(id);
		Product entity = /* obj.get(); */ obj.orElseThrow( () -> new ResourceNotFoundException("Categoria não encontrada."));  
		return new ProductDTO(entity, entity.getCategories());
		
	}
	@Transactional
	public ProductDTO save(ProductDTO dto) {
				
		//Forma acadêmica (passo-a-passo)
		Product entity = new Product();
		//entity.setName(dto.getName());
		entity = repository.save(entity);
		return new ProductDTO(entity); 
		
	}
	
	@Transactional
	public ProductDTO update(Long id, ProductDTO dto) {
		
		// Não usar o findById pq aí a operação de update vai usar 2 requisições ao banco
		// será usado uma nova abordagem para obter o objeto sem consulta ao banco
		
		try {
			Product entity =  repository.getOne(id);
			//entity.setName(dto.getName());
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


}
