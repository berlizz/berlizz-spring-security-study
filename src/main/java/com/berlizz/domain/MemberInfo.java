package com.berlizz.domain;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;

public class MemberInfo implements UserDetails {

	private static final long serialVersionUID = 1L;
	
	private String id;
	private String password;
	private String name;
	
	private Set<GrantedAuthority> authorities;

	public MemberInfo(String id, String password, String name, Collection<? extends GrantedAuthority> authorities) {
		this.id = id;
		this.password = password;
		this.name = name;
		this.authorities = Collections.unmodifiableSet(sortAuthorities(authorities));
	}
	
	

	public String getId() {
		return id;
	}



	public void setId(String id) {
		this.id = id;
	}


	@Override
	public String getPassword() {
		return password;
	}



	public void setPassword(String password) {
		this.password = password;
	}



	public String getName() {
		return name;
	}



	public void setName(String name) {
		this.name = name;
	}

	
	@Override
	public Set<GrantedAuthority> getAuthorities() {
		return authorities;
	}
	
	
	@Override
	public String getUsername() {
		return getId();
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


	public void setAuthorities(Collection<? extends GrantedAuthority> authorities) {
		this.authorities = Collections.unmodifiableSet(sortAuthorities(authorities));
	}


	private SortedSet<GrantedAuthority> sortAuthorities(Collection<? extends GrantedAuthority> authorities) {
		Assert.notNull(authorities, "cannot pass a null grantedAutority collection");
		SortedSet<GrantedAuthority> sortedAuthorities = new TreeSet<GrantedAuthority>(new AuthorityComparator());
		
		for(GrantedAuthority grantedAuthority : authorities) {
			Assert.notNull(grantedAuthority, "GrantedAuthority list cannot contain any null elements");
			sortedAuthorities.add(grantedAuthority);
		}
		
		return sortedAuthorities;
	}
	
	
	private static class AuthorityComparator implements Comparator<GrantedAuthority>, Serializable {
		private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;
		
		public int compare(GrantedAuthority g1, GrantedAuthority g2) {
			if(g2.getAuthority() == null) {
				return -1;
			}
			
			if(g1.getAuthority() == null) {
				return 1;
			}
			
			return g1.getAuthority().compareTo(g2.getAuthority());
		}
		
	}
	
	
}
