package it.rdev.blog.api.controller;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.rdev.blog.api.config.JwtTokenUtil;
import it.rdev.blog.api.controller.dto.ArticoliDTO;
import it.rdev.blog.api.service.BlogArticoloDetailsService;

@RestController
public class ArticoliController {

	@Autowired
	private BlogArticoloDetailsService service;
	
	@Autowired
	private JwtTokenUtil jwtUtil;
	
	private Logger log = LoggerFactory.getLogger(ArticoliController.class);
	
	// METODO CHE EFFETTUA LA RICERCA DEGLI ARTICOLI CONTENENTI LE PAROLE CHIAVI PASSATE
	@GetMapping("/api/articolo")
	@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Set<ArticoliDTO>> findArticlesResearch (@RequestHeader(name="Authorization", required = false) String token,  @RequestBody String testo){
		// INIZIALIZZO LE VARIABILI PER POTERLE UTILIZZARE IN QUALSIASI PUNTO DEL METODO
		ResponseEntity<Set<ArticoliDTO>> re = null;
		String username = null;
		Set<ArticoliDTO> result = null;
		boolean ricercaAnd = false;
		Set<String> search = null;
		String[] parole = null;
		
		if(testo == null || testo.equals("") || testo.length()<=3) {
			// SE NON VIENE INSERITO ALCUN VALORE PER EFFETTUARE LA RICERCA
			re = new ResponseEntity<Set<ArticoliDTO>>(HttpStatus.BAD_REQUEST);
			return re;
		} else {
			// SE VIENE INSERITA UNA STRINGA PER LA RICERCA QUESTA VERRA' SUDDIVISA PAROLA PER PAROLA
			parole = testo.split(" ");
			search = new HashSet<String>();
			if(parole.length>1) {
				// SE LA STRINGA HA PIU' DI UNA PAROLA SI DOVRA' EFFETTUARE UNA SELECT QUERY IN AND
				ricercaAnd = true;
			}
			for (String s: parole) {
				search.add(s);
			}
		}
		
		if(token != null && token.startsWith("Bearer")) {
			// CONTROLLO SUL TOKEN DELL'UTENTE LOGGATO
			token = token.replaceAll("Bearer ", "");
			username = jwtUtil.getUsernameFromToken(token);
		}
		
		if(ricercaAnd==false) {
			// EFFETTUO LA RICERCA TRAMITE UNA QUERY IN OR
			result = service.findAll(username, testo);
			if (result != null && !result.isEmpty()) {
				// SE LA RICERCA HA PRODOTTO ALMENO 1 RISULTATO
				re = new ResponseEntity<Set<ArticoliDTO>>(result, HttpStatus.OK);
				return re;
			} else {
				// SE LA RICERCA NON HA PRODOTTO RISULTATI
				re = new ResponseEntity<Set<ArticoliDTO>>(HttpStatus.NOT_FOUND);
				return re;
			}
		} else {
			// EFFETTUO LA RICERCA TRAMITE UNA QUERY IN AND
			result = service.findWithAnd(username, search);
			if (result != null && !result.isEmpty()) {
				// SE LA RICERCA HA PRODOTTO ALMENO 1 RISULTATO
				re = new ResponseEntity<Set<ArticoliDTO>>(result, HttpStatus.OK);
				return re;
			} else {
				// SE LA RICERCA NON HA PRODOTTO RISULTATI
				re = new ResponseEntity<Set<ArticoliDTO>>(HttpStatus.NOT_FOUND);
				return re;
			}
		}
	}
	
	// METODO CHE PERMETTE A TUTTI GLI UTENTI DI CERCARE UN DETERMINATO ARTICOLO
	@GetMapping("/api/articolo/{id:\\d+}")
	@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ArticoliDTO> findArticles(@PathVariable final Long id,@RequestHeader(name="Authorization", required = false) String token){
		ResponseEntity<ArticoliDTO> re = null;
		ArticoliDTO result = null;
		String username = null;
		
		if(id==null || id<1) {
			// SE VIENE INSERITO UN ID CHE NON PUO' ESISTERE
			re = new ResponseEntity<ArticoliDTO>(HttpStatus.BAD_REQUEST);
			return re;
		}
			
		if(token != null && token.startsWith("Bearer")) {
			// CONTROLLO SUL TOKEN DELL'UTENTE LOGGATO
			token = token.replaceAll("Bearer ", "");
			username = jwtUtil.getUsernameFromToken(token);
		}
		
		
		// EFFETTUO LA RICERCA DELL'ARTICOLO
		result = service.findArticle(username, id);
		if (result != null) {
			// SE LA RICERCA HA PRODOTTO ALMENO 1 RISULTATO
			re = new ResponseEntity<ArticoliDTO>(result, HttpStatus.OK);
			return re;
		}else {
			// SE LA RICERCA NON HA PRODOTTO RISULTATI
			re = new ResponseEntity<ArticoliDTO>(HttpStatus.NOT_FOUND);
			return re;
		}
	}
	
	// METODO CHE INSERISCE UN NUOVO ARTICOLO
	@PostMapping("/api/articolo")
	public ResponseEntity<Integer> salvaBozza(@RequestHeader(name = "Authorization") String token, @RequestBody ArticoliDTO articolo){
		ResponseEntity<Integer> re = null;
		if(token != null && token.startsWith("Bearer")) {
			// CONTROLLO SUL TOKEN DELL'UTENTE LOGGATO
			token = token.replaceAll("Bearer ", "");
			String username = jwtUtil.getUsernameFromToken(token);
			int insert_resp = service.insertNewArticolo(articolo, username);
			if (insert_resp == 1) {
				// SE LA QUERY INSERISCE 1 SOLO ARTICOLO
				re = new ResponseEntity<Integer>(HttpStatus.OK);
				return re;
			}else if (insert_resp <= 0) {
				// SE LA QUERY NON INSERISCE L'ARTICOLO
				re = new ResponseEntity<Integer>(HttpStatus.BAD_REQUEST);
				return re;
			} else if (insert_resp > 1) {
				// SE LA QUERY INSERISCE PIU' DI UN ARTICOLO
				re = new ResponseEntity<Integer>(HttpStatus.BAD_REQUEST);
				return re;
			}
		} else {
			// SE L'UTENTE NON E' LOGGATO O NON POSSIEDE UN TOKEN DI TIPO BEARER
			re = new ResponseEntity<Integer>(HttpStatus.UNAUTHORIZED);
			return re;
		}
		return re;
	}
	
	// METODO CHE ELIMINA UN ARTICOLO DAL DATABASE
	@DeleteMapping(value="/delete/{id:\\d+}")
	public ResponseEntity<Integer> eliminaArtById(@PathVariable final Long id,  @RequestHeader(name = "Authorization", required=false) String token){
		ResponseEntity<Integer> re = null;
		String username = null;
		// CONTROLLI PRELIMINARI SUL TOKEN
		if(token != null && token.startsWith("Bearer")) {
			token = token.replaceAll("Bearer ", "");
			username = jwtUtil.getUsernameFromToken(token);
		} else {
			// SE SI EFFETTUA LA CHIAMATA SENZA AVER EFFETTUATO L'ACCESSO
			re = new ResponseEntity<Integer>(HttpStatus.UNAUTHORIZED);
			return re;
		}
		
		// RICERCO L'ARTICOLO DA ELIMINARE PER EFFETTUARE I CONTROLLI SUI PERMESSI
		ArticoliDTO delete = service.findForCrudArt(id);
		if(delete!=null) {
			// CONTROLLO DELL'AUTORE DELL'ARTICOLO
			if (!delete.getAutore().getUsername().equals(username)) {
				// SE L'UTENTE NON E' AUTORIZZATO
				re = new ResponseEntity<Integer>(HttpStatus.FORBIDDEN);
				return re;
			}
		} else {
			// SE LA TUPLA NON VIENE TROVATA ALL'INTERNO DEL DATABASE
			re = new ResponseEntity<Integer>(HttpStatus.NOT_FOUND);
			return re;
		}
		
		// ESEGUO LA DELETE TRAMITE L'ID
		int queryResp = service.deleteById(id);
		if (queryResp <= 0) {
			// SE LA QUERY NON HA IMPATTATO SU NESSUNA DELLE TUPLE DELLA TABELLA
			log.info("L'articolo non è presente sul DataBase...");
		} else if (queryResp == 1){
			// SE LA QUERY HA ELIMINATO SOLO 1 RIGA ALL'INTERNO DEL DATABASE
			log.info("L'Articolo è stato eliminato.");
		} else {
			// SE LA QUERY HA ELIMINATO PIU' DI UNA RIGA DELLA TABELLA
			log.error("Sono stati eliminati più articoli. Errore di gestione all'interno del Database.");
		}
		re = new ResponseEntity<Integer>(queryResp, HttpStatus.NO_CONTENT);
		return re;
	}
	
	// METODO CHE EFFETTUA L'AGGIORNAMENTO DI UN ARTICOLO
	@PutMapping("/api/articolo/{id:\\d+")
	@RequestMapping(consumes =  MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Integer> updateArtByID(@RequestBody ArticoliDTO articolo , @PathVariable final Long id,  @RequestHeader(name = "Authorization", required=false) String token){
		ResponseEntity<Integer> re = null;
		String username = null;
		if(token != null && token.startsWith("Bearer")) {
			// CONTROLLO SUL TOKEN DELL'UTENTE LOGGATO
			token = token.replaceAll("Bearer ", "");
			username = jwtUtil.getUsernameFromToken(token);
		} else {
			// SE L'UTENTE NON HA ESEGUITO L'ACCESSO
			re = new ResponseEntity<Integer>(HttpStatus.UNAUTHORIZED);
			return re;
		}
		
		
		ArticoliDTO update = service.findForCrudArt(id);
		if(update!=null) {
			// CONTROLLO DELL'AUTORE DELL'ARTICOLO
			if (!update.getAutore().getUsername().equals(username)) {
				// SE L'UTENTE NON E' AUTORIZZATO
				re = new ResponseEntity<Integer>(HttpStatus.FORBIDDEN);
				return re;
			}
		} else {
			// SE LA TUPLA NON VIENE TROVATA ALL'INTERNO DEL DATABASE
			re = new ResponseEntity<Integer>(HttpStatus.NOT_FOUND);
			return re;
		}
		
		// ESEGUO L'UPDATE TRAMITE L'ID
		int queryResp = service.updateById(id, articolo,username);
		if (queryResp <= 0) {
			// SE LA QUERY NON HA IMPATTATO SU NESSUNA DELLE TUPLE DELLA TABELLA
			log.info("L'Articolo non è presente sul DataBase...");
		} else if (queryResp == 1){
			// SE LA QUERY HA AGGIORNATO SOLO 1 RIGA ALL'INTERNO DEL DATABASE
			log.info("L'Articolo è stato aggiornato.");
		} else {
			// SE LA QUERY HA AGGIORNATO PIU' DI UNA RIGA DELLA TABELLA
			log.error("Sono stati aggiornati più articoli. Errore di gestione all'interno del Database.");
		}
		
		re = new ResponseEntity<Integer>(queryResp, HttpStatus.NO_CONTENT);
		return re;
	}
	
	
	
}
