package it.rdev.blog.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import it.rdev.blog.api.config.JwtTokenUtil;

@RestController
public class TestApiController {
	
	@Autowired
	private JwtTokenUtil jwtUtil;

	@GetMapping({ "/api/test" })
	public String get() {
		return "Risorsa Accesibile";
	}
	
	@PostMapping({ "/api/test" })
	public String post(@RequestHeader(name = "Authorization") String token) {
		String username = null;
		if(token != null && token.startsWith("Bearer")) {
			token = token.replaceAll("Bearer ", "");
			username = jwtUtil.getUsernameFromToken(token);
		}
		return "Risorsa Protetta [" + username + "]";
	}

}