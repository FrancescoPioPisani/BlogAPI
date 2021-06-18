package it.rdev.blog.api.dao;

import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import it.rdev.blog.api.dao.entity.Categoria;

public interface CategorieDao extends CrudRepository<Categoria, String>{

	Categoria findByNome(String nome);
	
	@Query("Select c From Categoria c")
	Set<Categoria> getAllCategorie();
	
}
