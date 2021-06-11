package it.rdev.blog.api.config;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ExpiredJwtException;
import it.rdev.blog.api.service.BlogUserDetailsService;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

	@Autowired
	private BlogUserDetailsService jwtUserDetailsService;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	
	@Value("${jwt.header}")
	private String jwtHeader;
	
	@Value("${jwt.token.type}")
	private String jwtTokenType;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {

		final String requestTokenHeader = request.getHeader(jwtHeader);

		String username = null;
		String jwtToken = null;
		/*
		 * Il token utilizzato è in formato "Bearer Token". Se si vuole evitare 
		 * di inserire Bearer nell'header si può rimuovere il valore dal file di configurazione
		 */
		if (requestTokenHeader != null ) {
			if(requestTokenHeader.startsWith(jwtTokenType)) {
			jwtToken = requestTokenHeader.substring(7);
				try {
					username = jwtTokenUtil.getUsernameFromToken(jwtToken);
				} catch (IllegalArgumentException e) {
					logger.error("Impossibile recuperare il token JWT", e);
				} catch (ExpiredJwtException e) {
					logger.error("Token scaduto!", e);
				}
			} else {
				logger.warn("Il token recuperato dall'header Authorization non inizia con la parola chiave Bearer!");
			}
		} else {
			logger.debug("Utente anonimo.");
		}

		// Una volta ottenuto il token deve essere validato
		if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

			UserDetails userDetails = this.jwtUserDetailsService.loadUserByUsername(username);

			// if token is valid configure Spring Security to manually set
			// authentication
			/*
			 * Se il token è valido imposto l'autenticazione in Spring Security
			 */
			if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {

				UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
						userDetails, null, userDetails.getAuthorities());
				usernamePasswordAuthenticationToken
						.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				/*
				 * Dopo aver impostato l'autenticazione nel contesto specifichiamo
				 * che l'utente corrente risulta autenticato 
				 */
				SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
			}
		}
		chain.doFilter(request, response);
	}

}