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

/* 
 *	계정 클래스 
 */
public class MemberInfo implements UserDetails {

	private static final long serialVersionUID = 1L;
	
	// 추가로 필요한 멤버변수들은 다 만들고, getter와 setter 메소드를 만들어두자
	private String id;			// 사용자 아이디
	private String password;
	private String name;		// 사용자 이름 (추가된 멤버변수)
	
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
	
	
	// id 멤버변수의 getter 사용하여 id 리턴
	@Override
	public String getUsername() {
		return getId();
	}
	
	/* 
	 *	나머지 오버라이드한 메소드 들은 사용하지 않을경우 return true
	 *	사용해야할 경우 true가 아니라 검사하는 로직을 넣고 그에 맞게 true, false 리턴
	 */
	//  계정 만료 여부
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}
	
	// 계정잠김 여부
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}
	
	// 패스워드 만료 여부
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}
	
	// 계정 사용가능 여부
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
