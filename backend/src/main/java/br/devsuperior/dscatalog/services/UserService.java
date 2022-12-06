package br.devsuperior.dscatalog.services;

import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.devsuperior.dscatalog.dto.RoleDTO;
import br.devsuperior.dscatalog.dto.UserDTO;
import br.devsuperior.dscatalog.dto.UserInsertDTO;
import br.devsuperior.dscatalog.dto.UserUpdateDTO;
import br.devsuperior.dscatalog.entities.Role;
import br.devsuperior.dscatalog.entities.User;
import br.devsuperior.dscatalog.repositories.RoleRepository;
import br.devsuperior.dscatalog.repositories.UserRepository;
import br.devsuperior.dscatalog.services.exceptions.DatabaseException;
import br.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;

@Service
public class UserService implements UserDetailsService {
	
	//Para logs
	private static Logger logger = LoggerFactory.getLogger(UserService.class);
	
	@Autowired
	private UserRepository repository;
	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;


	
	@Transactional(readOnly = true)
	public Page<UserDTO> findAllPaginado(Pageable pageable){
		Page<User> list = repository.findAll(pageable);
		return list.map(item -> new UserDTO(item));
	}

	@Transactional(readOnly = true)
	public UserDTO findById(Long id) {
		Optional<User> obj = repository.findById(id);
		User entity = obj.orElseThrow( () -> new ResourceNotFoundException("Produto não encontrado."));  
		return new UserDTO(entity);
		
	}
	@Transactional
	public UserDTO insert(UserInsertDTO dto) {
		User entity = new User();
		copyDTOToEntity(dto, entity);
		entity.setPassword(passwordEncoder.encode(dto.getPassword()));
		entity = repository.save(entity);
		return new UserDTO(entity); 
		
	}

	@Transactional
	public UserDTO update(Long id, UserUpdateDTO dto) {
		try {
			User entity =  repository.getOne(id);
			copyDTOToEntity(dto, entity);
			entity = repository.save(entity);
			return new UserDTO(entity);
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
	
	
	private void copyDTOToEntity(UserDTO dto, User entity) {
		entity.setFirstName(dto.getFirstName());
		entity.setLastName(dto.getLastName());
		entity.setEmail(dto.getEmail());
		
		entity.getRoles().clear();
		
		for(RoleDTO roleDTO : dto.getRoles()) {
			Role role = roleRepository.getOne(roleDTO.getId());
			entity.getRoles().add(role);
		}
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = repository.findByEmail(username);
		if(user == null) {
			logger.error("Username not found: " + username);
			throw new UsernameNotFoundException("Username não encontrado: " + username);
		}
		logger.info("Username encontrado: " + username);
		return user;
	}


}
