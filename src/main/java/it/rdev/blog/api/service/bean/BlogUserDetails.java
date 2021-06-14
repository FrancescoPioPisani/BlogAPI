package it.rdev.blog.api.service.bean;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class BlogUserDetails implements UserDetails {

	private static final long serialVersionUID = -1544236163648366696L;
	
	private Long id;
	private String password;
	private String username;
	private List<? extends GrantedAuthority> authorities;
	
	public BlogUserDetails(Long id, String username, String password, List<? extends GrantedAuthority> authorities) {
		this.id = id;
		this.password = password;
		this.username = username;
		this.authorities = authorities;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return this.authorities;
	}

	@Override
	public String getPassword() {
		return this.password;
	}

	@Override
	public String getUsername() {
		return this.username;
	}
	
	public Long getId() {
		return this.id;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

}