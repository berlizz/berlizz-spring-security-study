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

/* 
 *	JdbcDaoImpl은 JdbcTemplate을 통해 DB조회하기 위하여 JdbcDaoSupport를 상속하고, 사용자 조회목적으로 UserDetailsService인터페이스를 구현한다.
 *	MemeberInfo 클래스를 사용하기 위해(MemberInfo는 UserDetails인터페이스를 구현, loadUserByUsername메소드에서 UserDetails리턴) JdbcDaoImpl을 상속한다.
 */
public class CustomJdbcDAOImpl extends JdbcDaoImpl {

	/* 
	 *	loadUsersByUsername메소드를 호출하여 사용자 조회한다.
	 *	사용자 조회 후 loadUserAuthoritie메소드로 권한조회하여 dbAuthSet에 저장 후 사용자 객체에 setAuthorities로 저장
	 */
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
			throw new UsernameNotFoundException("JdbcDaoImpl not found. " + username + " has no authorities");
		}
		
		return user;
	}
	
	/* 
	 *	JdbcTemplate을 사용하여 사용자 아이디로 조회
	 *	UserDetails인터페이스에서 패스워드를 리턴하기 때문에 Spring Security에서는 계정에 맞는 비밀번호 인지를 DB레벨에서 하는것이 아니라
	 *	DB에서 검색된 패스워드를 자바 레벨에서 일치하는지 확인한다.
	 * 	getJdbcTemplate()으로 JdbcTemplate을 얻은 뒤 getUsersByUsernameQuery()로 usersByUsernameQuery변수의 sql문 사용
	 *	usersByUsernameQuery멤버변수는 JdbcDaoImpl에 있는데 이는 setter가 존재하므로 security-context.xml파일에서 작성할 수 있다.
	 *	UserDetails에서의 멤버변수가 아닌 추가적인 정보는 이 메소드에서 DB조회 후 UserDetails를 구현하는 클래스의 생성자로 전달하면 된다.(여기서는 name)
	 *	필요하다면 계정만료, 잠김, 패스워드만료, 사용가능 여부를 DB에서 조회하여 boolean값 전달한다.
	 *	이 시점에선는 권한을 검사하지 않기 때문에 AuthorityUtils.NO_AUTHORITIES 전달한다.
	 */
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
	
	/* 
	 *	loadUsersByUsername 메소드와 비슷하다. 권한을 조회하는 것이기에 query를 실행하면 권한이름이 조회되어야 한다.
	 *	GrantedAuthority 인터페이스를 구현한 SimpleGrantedAuthority클래스를 사용하여 권한 클래스를 생성한 후 리턴한다.
	 *	getRolePrefix()로 권한명에 앞에 문자열을 붙이는데, rolePrefix멤버젼수는 setter를 통하여 security-context.xml에서 지정한다. default값은 ROLE_ 
	 */
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
	
	/* 
	 *	그룹 권한을 조회하는 메소드이다. 그룹 권한이라는 것은 여러개의 권한을 하나의 그룹으로 묶은 개념이다. 
	 *	이 기능을 사용하려면 enableGroups를 true로 세팅하고 메소드를 구현하면 된다.
	 */
	@Override
	protected List<GrantedAuthority> loadGroupAuthorities(String username) {
		return super.loadGroupAuthorities(username);
	}
	
	
}
