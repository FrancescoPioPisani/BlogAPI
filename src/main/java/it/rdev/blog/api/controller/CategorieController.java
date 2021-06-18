package it.rdev.blog.api.controller;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import it.rdev.blog.api.controller.dto.CategorieDTO;
import it.rdev.blog.api.dao.CategorieDao;
import it.rdev.blog.api.dao.entity.Categoria;


@RequestMapping(value = "/api")
@RestController
public class CategorieController {
	
	@Autowired
	CategorieDao cat;

	@RequestMapping(value = "/categorie", method = RequestMethod.GET)
	public ResponseEntity<?> getCategorie(){
		Set<Categoria> categorie =  cat.getAllCategorie();
		ResponseEntity<Set<Categoria>> re;
		if (categorie != null && !categorie.isEmpty()) {
			re = new ResponseEntity<Set<Categoria>>(categorie, HttpStatus.OK);
		}else {
			re = new ResponseEntity<Set<Categoria>>(categorie, HttpStatus.NOT_FOUND);
		}
		return re;
	}
	
}
