package it.rdev.blog.api.controller;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import it.rdev.blog.api.config.JwtTokenUtil;
import it.rdev.blog.api.controller.dto.ArticoliDTO;
import it.rdev.blog.api.dao.entity.Articolo;
import it.rdev.blog.api.dao.entity.User;
import it.rdev.blog.api.service.BlogArticoloDetailsService;

@RestController
public class ArticoliController {

	@Autowired
	private BlogArticoloDetailsService service;
	
	@Autowired
	private JwtTokenUtil jwtUtil;
	
	private Logger log = LoggerFactory.getLogger(ArticoliController.class);
	
	@GetMapping("/api/articolo")
	public ResponseEntity <Set<ArticoliDTO>> findAllArticles(@RequestHeader(name="Authorization", required = false) String token){
		ResponseEntity<Set<ArticoliDTO>> re = null;
		Set<ArticoliDTO> lista = null;
		String username = null;
		
		if(token != null && token.startsWith("Bearer")) {
			// CONTROLLO SUL TOKEN DELL'UTENTE LOGGATO
			token = token.replaceAll("Bearer ", "");
			username = jwtUtil.getUsernameFromToken(token);
		}
		
		lista = service.findAll(username);
		if (lista != null && !lista.isEmpty()) {
			re = new ResponseEntity<Set<ArticoliDTO>>(lista, HttpStatus.OK);
			return re;
		}else {
			re = new ResponseEntity<Set<ArticoliDTO>>(HttpStatus.NOT_FOUND);
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
	
	//METODO CHE RITORNA TUTTI GLI ARTICOLI DELL'UTENTE
	@PostMapping("/myArticles")
	public ResponseEntity<Set<ArticoliDTO>> findOwnArticles(@RequestHeader(name = "Authorization", required=false) String token){
		ResponseEntity<Set<ArticoliDTO>> re = null;
		if(token != null && token.startsWith("Bearer")) {
			token = token.replaceAll("Bearer ", "");
			String username = jwtUtil.getUsernameFromToken(token);
			Set<ArticoliDTO> articoli = service.findOwn(username);
			if (articoli != null && !articoli.isEmpty()) {
				re = new ResponseEntity<Set<ArticoliDTO>>(articoli, HttpStatus.OK);
				return re;
			}else {
				re = new ResponseEntity<Set<ArticoliDTO>>(HttpStatus.NOT_FOUND);
				return re;
			}
		} else {
			// SE L'UTENTE NON HA EFFETTUATO IL LOGIN 
			re = new ResponseEntity<Set<ArticoliDTO>>(HttpStatus.METHOD_NOT_ALLOWED);
			return re;
		}
		
	}
	
	//METODO CHE RITORNA L'ARTICOLO CON ID PASSATO COME PATH VARIABLE
	@GetMapping("/api/articolo/{id:\\d+}")
	public ResponseEntity<ArticoliDTO> ArticlesById(@PathVariable final Long id, @RequestHeader(name = "Authorization", required=false) String token ){
		ResponseEntity<ArticoliDTO> re = null;
		ArticoliDTO dto = service.findById(id);
		// SE L'UTENTE HA EFFETTUATO L'ACCESSO POSSIEDERA' IL TOKEN DI TIPO BAERER
		if(token != null && token.startsWith("Bearer")) {
			token = token.replaceAll("Bearer ", "");
			String username = jwtUtil.getUsernameFromToken(token);
			// SE L'UTENTE LOGGATO NON E' L'AUTORE DELL'ARTICOLO VIENE RESTITUITO UN ERRORE 404
			if (dto != null && dto.getAutore().getUsername().equals(username)) {
				re = new ResponseEntity<ArticoliDTO>(dto, HttpStatus.OK);
				return re;
			} else {
				re = new ResponseEntity<ArticoliDTO>(HttpStatus.NOT_FOUND);
				return re;
			}
		} else {
			// SE L'UTENTE NON HA EFFETTUATO L'ACCESSO
			if(dto != null && dto.getStato()!='B') {
				// SE L'ARTICOLO ESISTE E IL SUO STATO NON E' 'B' (BOZZA) VERRA' INVIATO UNO HttpStatus 201
				re = new ResponseEntity<ArticoliDTO>(dto, HttpStatus.OK);
				return re;
			} else {
				// SE L'ARTICOLO NON ESISTE OPPURE E' STATO SALVATO COME BOZZA 
				re = new ResponseEntity<ArticoliDTO>(HttpStatus.NOT_FOUND);
				return re;
			}
		}
	}
	
	
	
	
	@DeleteMapping(value="/delete/{id:\\d+}")
	public ResponseEntity<ArticoliDTO> eliminaArtById(@PathVariable final Long id,  @RequestHeader(name = "Authorization", required=false) String token){
		ResponseEntity<ArticoliDTO> re = null;
		String username = null;
		int app = 1;
		// CONTROLLI PRELIMINARI SUL TOKEN
		if(token != null && token.startsWith("Bearer")) {
			token = token.replaceAll("Bearer ", "");
			username = jwtUtil.getUsernameFromToken(token);
			app = 0;
		} else {
			// SE SI EFFETTUA LA CHIAMATA SENZA ESSERE LOGGATI
			re = new ResponseEntity<ArticoliDTO>(HttpStatus.UNAUTHORIZED);
			return re;
		}
		
		// RICERCO L'ARTICOLO DA ELIMINARE PER EFFETTUARE I CONTROLLI SUI PERMESSI
		ArticoliDTO delete = service.findForDeleteArt(id);
		if(delete!=null) {
			// CONTROLLO DELL'AUTORE DELL'ARTICOLO
			if (!delete.getAutore().getUsername().equals(username)) {
				// SE L'UTENTE NON E' AUTORIZZATO
				re = new ResponseEntity<ArticoliDTO>(HttpStatus.FORBIDDEN);
				return re;
			}
		} else {
			// SE LA TUPLA NON VIENE TROVATA ALL'INTERNO DEL DATABASE
			re = new ResponseEntity<ArticoliDTO>(HttpStatus.NOT_FOUND);
			return re;
		}
		
		// ESEGUO LA DELETE TRAMITE ID
		int queryResp = service.deleteById(id);
		if (queryResp <= 0) {
			// SE LA QUERY NON HA IMPATTATO SU NESSUNA DELLE TUPLE DELLA TABELLA
			log.info("L'articolo non è presente sul DataBase...");
		} else if (queryResp == 1){
			// SE LA QUERY HA ELIMINATO SOLO 1 RIGA ALL'INTERNO DEL DATABASE
			log.info("L'Articolo è stato eliminato.");
		} else {
			// SE LA QUERY HA ELIMINATO PIU' DI UNA RIGA DELLA TABELLA
			log.error("Sono stati eliminati più articoli. Errore di gestione della primary key all'interno del Database.");
		}
		
		re = new ResponseEntity<ArticoliDTO>(HttpStatus.NO_CONTENT);
		return re;
	}
	
	
	
	
}
