package br.devsuperior.dscatalog.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.devsuperior.dscatalog.dto.CategoryDTO;
import br.devsuperior.dscatalog.entities.Category;
import br.devsuperior.dscatalog.repositories.CategoryRepository;
import br.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;

@Service
public class CategoryService {
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	@Transactional(readOnly = true)
	public List<CategoryDTO> findAll(){
		List<Category> list = categoryRepository.findAll();
		return list.stream().map(item -> new CategoryDTO(item)).collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public CategoryDTO findById(Long id) {
		//return new CategoryDTO(categoryRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada")));
		
		
		// mesma coisa acima só que destrinchada para entender melhor
		Optional<Category> obj = categoryRepository.findById(id);
		Category entity = /* obj.get(); */ obj.orElseThrow( () -> new ResourceNotFoundException("Categoria não encontrada."));  
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


}
