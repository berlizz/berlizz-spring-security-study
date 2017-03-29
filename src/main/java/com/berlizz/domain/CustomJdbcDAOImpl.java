package com.berlizz.domain;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl;

public class CustomJdbcDAOImpl extends JdbcDaoImpl {

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		List<UserDetails> users = loadUsersByUsername(username);
		if(users.size() == 0) {
			throw new UsernameNotFoundException("JdbcDaoImpl not found " + username);
		}
		
		MemberInfo user = (MemberInfo) users.get(0);
		
		Set<GrantedAuthority> dbAuthSet = new HashSet<>();
		
		if(getEnableAuthorities()) {
			dbAuthSet.addAll(loadUserAuthorities(user.getUsername()));
		}
		if(getEnableGroups()) {
			dbAuthSet.addAll(loadGroupAuthorities(user.getUsername()));
		}
		
		List<GrantedAuthority> dbAuths = new ArrayList<>(dbAuthSet);
		user.setAuthorities(dbAuths);
		
		if(dbAuths.size() == 0) {
			throw new UsernameNotFoundException("JdbcDaoImpl not found " + username);
		}
		
		return user;
	}
	
	@Override
	protected List<UserDetails> loadUsersByUsername(String username) {
		return getJdbcTemplate().query(getUsersByUsernameQuery(), 
				new String[]{username}, 
				new RowMapper<UserDetails>() {
					public UserDetails mapRow(ResultSet resultSet, int rowNum) throws SQLException {
						String username = resultSet.getString(1);
						String password = resultSet.getString(2);
						String name = resultSet.getString(3);
						return new MemberInfo(username, password, name, AuthorityUtils.NO_AUTHORITIES);
					}
		});
		
	}
	
	@Override
	protected List<GrantedAuthority> loadUserAuthorities(String username) {
		return getJdbcTemplate().query(getAuthoritiesByUsernameQuery(), 
				new String[]{username}, 
				new RowMapper<GrantedAuthority>() {
					public GrantedAuthority mapRow(ResultSet resultSet, int rowNum) throws SQLException {
						String roleName = getRolePrefix() + resultSet.getString(1);
						
						return new SimpleGrantedAuthority(roleName);
					}
		});
	}
	
	@Override
	protected List<GrantedAuthority> loadGroupAuthorities(String username) {
		return super.loadGroupAuthorities(username);
	}
	
	
}
