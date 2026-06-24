package com.lpg3.projeto1.dto;

import com.lpg3.projeto1.entities.Role;

public class RoleDTO {

	private Long id;
	private String authority;
	
	public RoleDTO() {
	}

	public RoleDTO(Long id, String authority) {
		this.id = id;
		this.authority = authority;
	}
	
	public RoleDTO(Role role) {
		id = role.getId();
		authority = role.getAuthority();
	}

	public Long getId() {
		return id;
	}

	public String getAuthority() {
		return authority;
	}
}
