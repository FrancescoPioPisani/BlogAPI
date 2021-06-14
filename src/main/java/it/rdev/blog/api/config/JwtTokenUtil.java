package it.rdev.blog.api.config;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import it.rdev.blog.api.service.bean.BlogUserDetails;

@Component
public class JwtTokenUtil implements Serializable {

	private static final long serialVersionUID = -2550185165626007488L;

	@Value("${jwt.secret}")
	private String secret;
	@Value("${jwt.expiration:1}")
	private Integer expiration;

	/**
	 * Metodo che recupera il nome utente dal token JWT
	 * @param token Stringa che rappresenta il token da cui recuperare lo username
	 * @return String che rappresenta lo username dell'utente loggato
	 */
	public String getUsernameFromToken(String token) {
		return getClaimFromToken(token, Claims::getSubject);
	}
	
	public Long getUserIdFromToken(String token) {
		return Long.parseLong(getClaimFromToken(token, Claims::getIssuer));
	}

	/**
	 * Metodo che recupera la data di scadenza del token passato in input
	 * @param token String che rappresenta il token da processare
	 * @return oggetto Date che rappresenta la data di scadenza del token
	 */
	public Date getExpirationDateFromToken(String token) {
		return getClaimFromToken(token, Claims::getExpiration);
	}

	public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = getAllClaimsFromToken(token);
		return claimsResolver.apply(claims);
	}

	/**
	 * Metodo che recupera tutte le informazioni dal token. Per decodificare il token
	 * in modo da ottenerne le informazioni abbiamo bisogno di utilizzare la secret
	 * 
	 * @param token String che rappresenta il token da processare
	 * @return
	 */
	private Claims getAllClaimsFromToken(String token) {
		return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
	}

	/**
	 * Metodo per verificare se il token è scaduto
	 * @param token
	 * @return
	 */
	private Boolean isTokenExpired(String token) {
		final Date expiration = getExpirationDateFromToken(token);
		return expiration.before(new Date());
	}

	/**
	 * Metodo per generare il token a partire dalle informazioni dell'utente
	 * @param userDetails
	 * @return
	 */
	public String generateToken(UserDetails userDetails) {
		Map<String, Object> claims = new HashMap<>();
		if(userDetails instanceof BlogUserDetails) {
			BlogUserDetails user = (BlogUserDetails) userDetails;
			claims.put(Claims.ISSUER, user.getId());
		}
		return doGenerateToken(claims, userDetails.getUsername());
	}

	//while creating the token -
	//1. Define  claims of the token, like Issuer, Expiration, Subject, and the ID
	//2. Sign the JWT using the HS512 algorithm and secret key.
	//3. According to JWS Compact Serialization(https://tools.ietf.org/html/draft-ietf-jose-json-web-signature-41#section-3.1)
	//   compaction of the JWT to a URL-safe string 
	private String doGenerateToken(Map<String, Object> claims, String subject) {
		return Jwts
				.builder()
				.setClaims(claims)
				.setSubject(subject)
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + expiration * 1000))
				.signWith(SignatureAlgorithm.HS512, secret).compact();
	}

	/**
	 * Valida il token JWT
	 * @param token
	 * @param userDetails
	 * @return
	 */
	public Boolean validateToken(String token, UserDetails userDetails) {
		final String username = getUsernameFromToken(token);
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}
}