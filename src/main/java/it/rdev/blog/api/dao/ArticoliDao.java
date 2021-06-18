package it.rdev.blog.api.dao;

import java.time.LocalDateTime;
import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import it.rdev.blog.api.dao.entity.Articolo;
import it.rdev.blog.api.dao.entity.Categoria;
import it.rdev.blog.api.dao.entity.User;

public interface ArticoliDao extends CrudRepository<Articolo, Integer>{

	@Query("Select a From Articolo a WHERE a.titolo LIKE :ricerca OR a.sottotitolo Like :ricerca OR a.testo :ricerca")
	Set<Articolo> ricercaOr(String ricerca);
	
	@Query("Select a From Articolo a") 
	Set<Articolo> findAll();
	
	
	Set<Articolo> findByCategoria(String categoria);
	Set<Articolo> findByStato(char stato);
	Set<Articolo> findByTags(String[] tags);
	Set<Articolo> findByAutore(String autore);
	int deleteById(long id);
	
	
	@Query("SELECT a FROM Articolo a WHERE id = :id")
	Articolo findById(long id);
	
	@Query("INSERT INTO Articolo (stato, titolo, sottotitolo, testo, categoria, autore, data_pubblicazione, data_modifica, data_creazione)"
			+ " values ( :stato, :titolo, :sottotitolo, :testo, :categoria, :autore, :data_pubblicazione, :data_modifica, :data_creazione) ")
	int insertArticolo(char stato, String titolo, String sottotitolo, String testo, Categoria categoria, User autore,
			LocalDateTime data_creazione, LocalDateTime data_modifica, LocalDateTime data_pubblicazione);

	@Query("Select a From Articolo a Where a.titolo IN :search AND a.sottotitolo IN :search AND a.testo IN :search")
	Set<Articolo> ricercaAnd(Set<String> search);

	
	Articolo save(Articolo art);


	

	
	
}
