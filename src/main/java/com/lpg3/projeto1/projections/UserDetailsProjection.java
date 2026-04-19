package com.lpg3.projeto1.projections;

public interface UserDetailsProjection {

	Long getUserId();
	String getUsername();
	String getPassword();
	Long getRoleId();
	String getAuthority();
}
