package br.devsuperior.dscatalog.resources;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.devsuperior.dscatalog.entities.Category;

/**
 * Resourse é responsável por implementar o controlador REST
 * 
 * */
@RestController
@RequestMapping(value = "/categories")
public class CategoryResource {

	@GetMapping
	public ResponseEntity<List<Category>> findAll(){
		List<Category> list = new ArrayList<Category>();
		list.add(new Category(1L, "Books"));
		list.add(new Category(2L, "Mangas"));
		list.add(new Category(3L, "Electronics"));
		
		return ResponseEntity.ok(list);
	}
}
