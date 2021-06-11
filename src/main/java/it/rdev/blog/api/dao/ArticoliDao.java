package it.rdev.blog.api.dao;

import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import it.rdev.blog.api.controller.dto.ArticoliDTO;
import it.rdev.blog.api.dao.entity.Articolo;

public interface ArticoliDao extends CrudRepository<Articolo, Integer>{

//	@Query("Select a From Articolo a WHERE a.titolo LIKE :titolo OR a.sottotitolo Like :titolo OR a.testo :titolo")
//	Set<Articolo> ricercaTitoloSottotitoloTesto(@Param("titolo") String ricerca);
	
	@Query("Select a From Articolo a") 
	Set<Articolo> findAll();
	
	
	Set<Articolo> findByCategoria(String categoria);
	Set<Articolo> findByStato(char stato);
	Set<Articolo> findByTags(String[] tags);
	
	Set<Articolo> findByAutore(String autore);

	
	@Query("Select a from Articolo a where id = :id")
	Articolo findById(long id);
	
	@Query("Delete a from Articolo where id = :id")
	Articolo deleteById(long id);
	
}
