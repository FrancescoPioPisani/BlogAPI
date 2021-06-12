package it.rdev.blog.api.service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.rdev.blog.api.config.JwtTokenUtil;
import it.rdev.blog.api.controller.dto.ArticoliDTO;
import it.rdev.blog.api.dao.ArticoliDao;
import it.rdev.blog.api.dao.entity.Articolo;

@Service
public class BlogArticoloDetailsService {

	@Autowired
	private ArticoliDao articoloDao;
	@Autowired
	private JwtTokenUtil jwtUtil;
	
	private Logger log = LoggerFactory.getLogger(BlogArticoloDetailsService.class);
	/*
	 * METODO CHE CONVERTE UN OGGETTO DI TIPO ARTICOLO IN UNO DI TIPO ArticoliDTO
	 * @param a		Valore singolo di tipo Articolo, viene ottenuto tramite le query
	 */
	private ArticoliDTO toDTO(Articolo a){
		ArticoliDTO dto = new ArticoliDTO();
		dto.setId(a.getId());
		dto.setTitolo(a.getTitolo());
		dto.setSottotitolo(a.getSottotitolo());
		dto.setTesto(a.getTesto());
		dto.setAutore(a.getAutore());
		dto.setCategoria(a.getCategoria());
		dto.setStato(a.getStato());
		dto.setTags(a.getTags());
		dto.setData_creazione(a.getData_creazione());
		dto.setData_modifica(a.getData_modifica());
		dto.setData_pubblicazione(a.getData_pubblicazione());
		return dto;
	}
	
	private Articolo toDbForm(ArticoliDTO a, String user) {
		Articolo art = null;
		LocalDateTime d =  LocalDateTime.now();
		if (a.getTitolo()!=null && a.getSottotitolo()!=null && a.getTesto()!=null && a.getAutore()!=null 
				&& a.getAutore()!=null && a.getCategoria()!=null && a.getTags()!=null 
				&& a.getData_creazione()!=null && a.getData_modifica()!=null 
				&& a.getData_pubblicazione()!=null) {
			art = new Articolo();
			art.setTitolo(a.getTitolo());
			art.setSottotitolo(a.getSottotitolo());
			art.setTesto(a.getTesto());
			// VERRA' SEMPRE INSERITO IL NOME DELL'UTENTE LOGGATO
			a.getAutore().setUsername(user);
			art.setAutore(a.getAutore());
			
			art.setCategoria(a.getCategoria());
			art.setStato('B');
			art.setTags(a.getTags());
			art.setData_creazione(d);
			art.setData_modifica(d);
			art.setData_pubblicazione(a.getData_pubblicazione());
		} else {
			log.info("Uno dei campi non è stato compilato.");
		}
		
		return art;
	}
	
	// METODO CHE EFFETTUA LA INSERT DI UN ARTICOLO ALL'INTERNO DEL DATABASE
	public int insertNewArticolo (ArticoliDTO articolo, String user) {
		Articolo art = toDbForm(articolo, user);
		if (art != null) {
			int insert_resp = articoloDao.insertArticolo(art.getStato(),art.getTitolo(),art.getSottotitolo(),
					art.getTesto(),art.getCategoria(),art.getAutore(),art.getData_creazione(),art.getData_modifica(),art.getData_pubblicazione());
			if (insert_resp <= 0) {
				log.info("L'Articolo non è stato inserito...");
			}
			if (insert_resp > 1) {
				log.info("Sono stati inseriti più di un articolo, errore...");
				return insert_resp;
			}
			if (insert_resp == 1) {
				log.info("L'Articolo è stato salvato con successo.");
				return insert_resp;
			}
			return 0;
		} else {
			log.info("Errore nella convesione del DTO...");
			return 0;
		}
	}
	
	
	// METODO CHE RITORNA LA LISTA COMPLETA DI ARTICOLI PRESENTI SUL DB
	public Set<ArticoliDTO> findAll(String username){
		Set<Articolo> lista = articoloDao.findAll();
		Set<ArticoliDTO> lista_dto = new HashSet<>();
		// SE IL TOKEN è NULL L'UTENTE NON è LOGGATO
		if (username == null) {
			for(Articolo a: lista) {
				// L'UTENTE ANONIMO POTRA' VISUALIZZARE SOLO GLI ARTICOLI PUBBLICATI
				if (a.getStato() == 'P') {
					ArticoliDTO dto = toDTO(a);
					lista_dto.add(dto);
				}
			}
		} else {
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
		return lista_dto; 
}

	
	// METODO CHE RITORNA TUTTI GLI ARTICOLI DI UN DETERMINATO UTENTE
	public Set<ArticoliDTO> findOwn(String autore){
		Set<Articolo> lista = articoloDao.findByAutore(autore);
		Set<ArticoliDTO> lista_dto = null;
		if (!lista.isEmpty()) {
			// SE LA QUERY RITORNA UNA LISTA CONTENENTE ALMENO 1 ARTICOLO
			lista_dto = new HashSet<>();
			for(Articolo a: lista) {
				// CONVERTO E AGGIUNGO ALL'HashSet GLI ARTICOLI INDIVIDUATI
				ArticoliDTO dto = toDTO(a);
				lista_dto.add(dto);
			}
		}else{
			// SE LA QUERY RITORNA UNA LISTA VUOTA
			log.info("Non è stato individuato nessun articolo...");
			return null;
		}
		return lista_dto;
	}
	
	
	// METODO CHE RITORNA L'ARTICOLO CON ID PASSATO COME PATH VARIABLE
	public ArticoliDTO findById(long id){
		Articolo a = articoloDao.findById(id);
		ArticoliDTO dto = null;
		if (a!=null && a.getTitolo().equals(null)) {
			dto = new ArticoliDTO();
			dto = toDTO(a);
			log.info("Articolo ricercato è stato individuato...");
		} else {
			log.info("L'Articolo ricercato non è stato individuato...");
		}
		return dto;
	}
	
	
	// METODO CHE RICERCA UN ARTICOLO TRAMITE L'ID PER ESEGUIRE CONTROLLI PRECENDETI ALL'ELIMINAZIONE
	public ArticoliDTO findForDeleteArt(long id) {
		Articolo a = articoloDao.findById(id);
		ArticoliDTO dto = null;
		if (a!=null && a.getTitolo().equals(null)) {
			dto = new ArticoliDTO();
			dto = toDTO(a);
			log.info("Articolo da eliminare individuato...");
		} else {
			log.info("L'Articolo da eliminare non è stato trovato...");
		}
		return dto;
	}
	

	/*
	 * METODO CHE ESEGUE LA QUERY DI DELETE SUL DATABASE
	 * RITORNA IL NUMERO DI TUPLE CHE SONO STATE ELIMINATE TRAMITE LA QUERY STESSA
	 */
	public int deleteById(Long id) {
		int resp = articoloDao.deleteById(id);
		return resp;
	}



	
}
