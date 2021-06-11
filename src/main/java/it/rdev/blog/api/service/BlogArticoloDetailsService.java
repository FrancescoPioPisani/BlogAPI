package it.rdev.blog.api.service;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import it.rdev.blog.api.config.JwtTokenUtil;
import it.rdev.blog.api.controller.dto.ArticoliDTO;
import it.rdev.blog.api.dao.ArticoliDao;
import it.rdev.blog.api.dao.entity.Articolo;
import it.rdev.blog.api.dao.entity.User;

@Service
public class BlogArticoloDetailsService {

	@Autowired
	private ArticoliDao articoloDao;
	@Autowired
	private JwtTokenUtil jwtUtil;
	
	/*
	 * METODO CHE CONVERTE UN OGGETTO DI TIPO ARTICOLO IN UNO DI TIPO ArticoliDTO
	 * @param a		Valore singolo di tipo Articolo, viene ottenuto tramite le query
	 */
	public ArticoliDTO toDTO(Articolo a){
		ArticoliDTO dto = new ArticoliDTO();
		dto.setId(a.getId());
		dto.setTitolo(a.getTitolo());
		dto.setSottotitolo(a.getSottotitolo());
		dto.setTesto(a.getTesto());
		dto.setAutore(a.getAutore());
		dto.setCategoria(a.getCategoria());
		dto.setStato(a.getStato());
		dto.setTags(a.getTags());
		dto.setData_creazione(a.getData_crezione());
		dto.setData_modifica(a.getData_modifica());
		dto.setData_pubblicazione(a.getData_pubblicazione());
		return dto;
	}
	
	
	
	//METODO CHE RITORNA LA LISTA COMPLETA DI ARTICOLI PRESENTI SUL DB
	public Set<ArticoliDTO> findAll(String token){
		Set<Articolo> lista = articoloDao.findAll();
		Set<ArticoliDTO> lista_dto = new HashSet<>();
		// SE IL TOKEN è NULL L'UTENTE NON è LOGGATO
		if (token == null) {
			for(Articolo a: lista) {
				// L'UTENTE ANONIMO POTRA' VISUALIZZARE SOLO GLI ARTICOLI PUBBLICATI
				if (a.getStato() == 'P') {
					ArticoliDTO dto = toDTO(a);
					lista_dto.add(dto);
				}
			}
		} else {
			if(token != null && token.startsWith("Bearer")){
				token = token.replaceAll("Bearer ", "");
				String username = jwtUtil.getUsernameFromToken(token);
				for(Articolo a: lista) {
					if(a.getStato() == 'B') {
						if(a.getAutore().getUsername().equals(username)) {
							ArticoliDTO dto = toDTO(a);
							lista_dto.add(dto);
						} 
					} else {
						ArticoliDTO dto = toDTO(a);
						lista_dto.add(dto);
					}
				}
			}
		}
		return lista_dto; 
	}
	
	//METODO CHE RITORNA TUTTI GLI ARTICOLI DI UN DETERMINATO UTENTE
	public Set<ArticoliDTO> findOwn(@RequestBody User autore){
		Set<Articolo> lista = articoloDao.findByAutore(autore.getUsername());
		Set<ArticoliDTO> lista_dto = new HashSet<>();
		for(Articolo a: lista) {
			ArticoliDTO dto = toDTO(a);
			lista_dto.add(dto);
		}
		return lista_dto;
	}
	
	
	
	//METODO CHE RITORNA L'ARTICOLO CON ID PASSATO COME PATH VARIABLE
	public ArticoliDTO findById(@RequestBody long id){
		Articolo a = articoloDao.findById(id);
		ArticoliDTO dto = null;
		if (a != null) {
			//SE LO STATO DELL'ARTICOLO è SALVATO CON IL VALORE B (BOZZA) 
			if (a.getStato()!='B') {
				dto=new ArticoliDTO();
				dto = toDTO(a);
			}
		}
		return dto;
	}
	
	
	public ArticoliDTO deleteArt(long id) {
		Articolo a = articoloDao.deleteById(id);
		ArticoliDTO dto = null;
		if (a.equals(1)) {
			dto = new ArticoliDTO();
			dto = toDTO(a);
		} 
		
		return dto;
	}
	
}
