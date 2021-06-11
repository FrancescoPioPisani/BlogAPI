package it.rdev.blog.api.dao.entity;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;


@Entity
@Table(name = "categorie")
public class Categoria {

	@Id
    private String nome;
	
	@OneToMany(mappedBy = "categoria")
	private Set<Articolo> articoli;

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
	
	
	
}
