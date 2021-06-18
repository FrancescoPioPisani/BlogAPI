package it.rdev.blog.api.dao.entity;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name = "tags")
public class Tag {

    @Id
    @Column
    private String nome;
    @ManyToMany (cascade = CascadeType.ALL)
    @JoinTable(
            name = "tags_articoli",
            joinColumns = @JoinColumn(name = "id_articolo"),
            inverseJoinColumns = @JoinColumn(name = "nome_tag")
            )
    private Set<Articolo> articoli;
    
    
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public Set<Articolo> getArticoli() {
		return articoli;
	}
	public void setArticoli(Set<Articolo> articoli) {
		this.articoli = articoli;
	}
	
	
    
}
