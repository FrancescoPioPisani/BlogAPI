package it.rdev.blog.api.controller.dto;

import java.util.Set;

import it.rdev.blog.api.dao.entity.Tag;

public class TagDTO {

	public String nome;

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

}
