package it.rdev.blog.api.controller;

import java.util.Set;

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
	
	@GetMapping("/api/articolo")
	public ResponseEntity<Set<ArticoliDTO>> find(@RequestHeader(name = "Authorization") String token){
		ResponseEntity<Set<ArticoliDTO>> re = null;
		Set<ArticoliDTO> articoli = service.findAll(token);
		// SE L'UTENTE HA EFFETTUATO L'ACCESSO POSSIEDERA' IL TOKEN DI TIPO BAERER
		if(token != null && token.startsWith("Bearer")) {
			token = token.replaceAll("Bearer ", "");
			String username = jwtUtil.getUsernameFromToken(token);
			if (articoli != null && !articoli.isEmpty()) {
				re = new ResponseEntity<Set<ArticoliDTO>>(articoli, HttpStatus.OK);
			}else {
				re = new ResponseEntity<Set<ArticoliDTO>>(HttpStatus.NOT_FOUND);
			}
		} else {
			
		}
		

		return re;
	}
	
	//METODO CHE RITORNA TUTTI GLI ARTICOLI DELL'UTENTE
	@PostMapping("/ourArticles")
	public ResponseEntity<Set<ArticoliDTO>> findOwnArticles(@RequestBody User autore){
		Set<ArticoliDTO> articoli = service.findOwn(autore);
		ResponseEntity<Set<ArticoliDTO>> re;
		if (articoli != null && !articoli.isEmpty()) {
			re = new ResponseEntity<Set<ArticoliDTO>>(articoli, HttpStatus.OK);
		}else {
			re = new ResponseEntity<Set<ArticoliDTO>>(HttpStatus.NOT_FOUND);
		}
		return re;
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
			} else {
				re = new ResponseEntity<ArticoliDTO>( HttpStatus.NOT_FOUND);
			}
		} else {
			// SE L'UTENTE NON HA EFFETTUATO L'ACCESSO
			// SE L'ARTICOLO ESISTE E IL SUO STATO NON E' 'B' (BOZZA) VERRA' INVIATO UN ERRORE 404
			if(dto != null && dto.getStato()!='B') {
				re = new ResponseEntity<ArticoliDTO>(dto, HttpStatus.OK);
			}
			else {
				re = new ResponseEntity<ArticoliDTO>(HttpStatus.NOT_FOUND);
			}
		}
		return re;
	}
	
	
	
	
	@DeleteMapping(value="/delete/{id:\\d+}")
	public ResponseEntity<ArticoliDTO> eliminaArtById(@PathVariable final Long id,  @RequestHeader(name = "Authorization", required=false) String token){
		ArticoliDTO dto = service.deleteArt(id);
		ResponseEntity<ArticoliDTO> re = null;
		String username = null;
		int app = 1;
		if(token != null && token.startsWith("Bearer")) {
			token = token.replaceAll("Bearer ", "");
			username = jwtUtil.getUsernameFromToken(token);
			app = 0;
		} else {
			re = new ResponseEntity<ArticoliDTO>(HttpStatus.UNAUTHORIZED);
		}
		
		if(dto!=null) {
			if (dto.getAutore().getUsername().equals(username)) {
				re = new ResponseEntity<ArticoliDTO>(HttpStatus.FORBIDDEN);
			}
		} else {
			re = new ResponseEntity<ArticoliDTO>(HttpStatus.NOT_FOUND);
		} 
		
		if (app == 0 && dto != null && !dto.equals(0)) {
			re = new ResponseEntity<ArticoliDTO>(dto, HttpStatus.NO_CONTENT);
		}
		return re;
	}
	
	
	
	
}
