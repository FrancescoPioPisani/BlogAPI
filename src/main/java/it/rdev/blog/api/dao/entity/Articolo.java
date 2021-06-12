package it.rdev.blog.api.dao.entity;

import java.time.LocalDateTime;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "articoli")
public class Articolo {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	@Column(name="titolo", length = 50, nullable = false )
	private String titolo;
	@Column(name="sottotitolo", length = 50, nullable = false )
	private String sottotitolo;
	@Column(name="testo", length = 500, nullable = false )
	private String testo;  
	
	@Column (name = "stato", nullable = false)
	private char stato;
	
	@ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "autore", referencedColumnName="id")
    private User autore;
	
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "categoria", referencedColumnName="nome")
    private Categoria categoria;
	
	@ManyToMany (mappedBy = "articoli")
	private Set<Tag> tags;
	
	@Column(name="data_pubblicazione")
	private LocalDateTime data_pubblicazione;
	@Column(name="data_modifica")
	private LocalDateTime data_modifica;
	@Column(name="data_creazione",  nullable = false)
	private LocalDateTime data_creazione;
	
	
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTitolo() {
		return titolo;
	}
	
	public void setTitolo(String titolo) {
		this.titolo = titolo;
	}
	
	public String getSottotitolo() {
		return sottotitolo;
	}
	
	public void setSottotitolo(String sottotitolo) {
		this.sottotitolo = sottotitolo;
	}
	
	public String getTesto() {
		return testo;
	}
	
	public void setTesto(String testo) {
		this.testo = testo;
	}
	
	public Categoria getCategoria() {
		return categoria;
	}
	
	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}
	
	public User getAutore() {
		return autore;
	}
	
	public void setAutore(User autore) {
		this.autore = autore;
	}
	
	public LocalDateTime getData_pubblicazione() {
		return data_pubblicazione;
	}
	
	public void setData_pubblicazione(LocalDateTime data_pubblicazione) {
		this.data_pubblicazione = data_pubblicazione;
	}
	
	public LocalDateTime getData_modifica() {
		return data_modifica;
	}
	
	public void setData_modifica(LocalDateTime data_modifica) {
		this.data_modifica = data_modifica;
	}
	
	public LocalDateTime getData_creazione() {
		return data_creazione;
	}
	
	public void setData_creazione(LocalDateTime data_crezione) {
		this.data_creazione = data_crezione;
	}

	public char getStato() {
		return stato;
	}

	public void setStato(char stato) {
		this.stato = stato;
	}

	public Set<Tag> getTags() {
		return tags;
	}

	public void setTags(Set<Tag> tags) {
		this.tags = tags;
	}
	
	
}
