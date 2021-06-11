package it.rdev.blog.api.dao;

import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import it.rdev.blog.api.controller.dto.TagDTO;
import it.rdev.blog.api.dao.entity.Tag;

public interface TagsDao extends CrudRepository<Tag, String> {

	Tag findByNome(String nome);
	
	@Query("Select t from Tag t")
	Set<TagDTO> findAllTags();
}
