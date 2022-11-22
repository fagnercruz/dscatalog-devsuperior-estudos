package br.devsuperior.dscatalog.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.devsuperior.dscatalog.entities.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

}
