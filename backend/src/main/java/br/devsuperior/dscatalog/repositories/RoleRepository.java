package br.devsuperior.dscatalog.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.devsuperior.dscatalog.entities.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

}
