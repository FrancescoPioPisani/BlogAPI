package it.rdev.blog.api.controller;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import it.rdev.blog.api.controller.dto.TagDTO;
import it.rdev.blog.api.dao.TagsDao;
import it.rdev.blog.api.dao.entity.Categoria;
import it.rdev.blog.api.dao.entity.Tag;

@RequestMapping(value = "/api")
@RestController
public class TagsController {

	@Autowired
	TagsDao t;
	
	//METODO CHE RITORNA TUTTI I TAG PRESENTI NEL DB
	@RequestMapping(value = "/tags", method = RequestMethod.GET)
	public ResponseEntity<?> getTags(){
		Set<TagDTO> lista = t.findAllTags();
		ResponseEntity<Set<TagDTO>> re;
		if (lista != null && !lista.isEmpty()) {
			re = new ResponseEntity<Set<TagDTO>>(lista, HttpStatus.OK);
		}else {
			re = new ResponseEntity<Set<TagDTO>>(lista, HttpStatus.NOT_FOUND);
		}
		return re;
	}
	
}
