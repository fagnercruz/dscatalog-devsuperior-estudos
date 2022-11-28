package br.devsuperior.dscatalog.services;

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
import br.devsuperior.dscatalog.entities.Category;
import br.devsuperior.dscatalog.repositories.CategoryRepository;
import br.devsuperior.dscatalog.services.exceptions.DatabaseException;
import br.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;

@Service
public class CategoryService {
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	@Transactional(readOnly = true)
	public Page<CategoryDTO> findAllPaginado(Pageable pageable){
		Page<Category> list = categoryRepository.findAll(pageable);
		return list.map(item -> new CategoryDTO(item));
//		return list.stream().map(item -> new CategoryDTO(item)).collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public CategoryDTO findById(Long id) {
		//return new CategoryDTO(categoryRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada")));
		// mesma coisa acima só que destrinchada para entender melhor
		Optional<Category> obj = categoryRepository.findById(id);
		Category entity = obj.orElseThrow( () -> new ResourceNotFoundException("Categoria não encontrada."));  
		return new CategoryDTO(entity);
		
	}
	@Transactional
	public CategoryDTO save(CategoryDTO dto) {
		// Forma Otimizada - by Fagner
		return new CategoryDTO(categoryRepository.save(new Category(null, dto.getName())));
		
		/*
		Forma acadêmica (passo-a-passo)
		Category entity = new Category();
		entity.setName(dto.getName());
		entity = categoryRepository.save(entity);
		return new CategoryDTO(entity); 
		*/
	}
	
	@Transactional
	public CategoryDTO update(Long id, CategoryDTO dto) {
		
		// Não usar o findById pq aí a operação de update vai usar 2 requisições ao banco
		// será usado uma nova abordagem para obter o objeto sem consulta ao banco
		
		try {
			Category entity =  categoryRepository.getOne(id);
			entity.setName(dto.getName());
			entity = categoryRepository.save(entity);
			return new CategoryDTO(entity);
		} catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException("Id não encontrado: " + id);
		}
		
	}

	public void delete(Long id) {
		try {
			categoryRepository.deleteById(id);
		} catch (EmptyResultDataAccessException e) {
			throw new ResourceNotFoundException("Id não encontrado: " + id);
		} catch (DataIntegrityViolationException e) {
			throw new DatabaseException("Violação de integridade (Integrity voilation)");
		}
		
	}


}
