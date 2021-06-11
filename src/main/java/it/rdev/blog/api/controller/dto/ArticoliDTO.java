package it.rdev.blog.api.controller.dto;

import java.time.LocalDateTime;
import java.util.Set;

import javax.websocket.Decoder.Text;

import it.rdev.blog.api.dao.entity.Categoria;
import it.rdev.blog.api.dao.entity.Tag;
import it.rdev.blog.api.dao.entity.User;

public class ArticoliDTO {

	private long id;
	private String titolo;
	private String sottotitolo;
	private char stato;
	private String testo;
	private Categoria categoria;
	private User autore;
	private Set<Tag> tags;
	private LocalDateTime data_pubblicazione;
	private LocalDateTime data_modifica;
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
	public char getStato() {
		return stato;
	}
	public void setStato(char stato) {
		this.stato = stato;
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
	public void setCategoria(Categoria cat) {
		this.categoria = cat;
	}
	public User getAutore() {
		return autore;
	}
	public void setAutore(User user) {
		this.autore = user;
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
	public void setData_creazione(LocalDateTime data_creazione) {
		this.data_creazione = data_creazione;
	}
	public Set<Tag> getTags() {
		return tags;
	}
	public void setTags(Set<Tag> set) {
		this.tags = set;
	}
	
	
	
}
