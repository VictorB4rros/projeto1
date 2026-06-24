package com.lpg3.projeto1.dto;

import com.lpg3.projeto1.entities.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Past;
import org.springframework.security.core.GrantedAuthority;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UserDTO {

	private Long id;
	private String name;
	@Email(message = "Formato de email inválido")
	private String email;
	private String phone;
	@Past(message = "Data de nascimento deve estar no passado")
	private LocalDate birthDate;

	@NotEmpty
	Set<RoleDTO> roles = new HashSet<>();
	
	public UserDTO() {
	}

	public UserDTO(User entity) {
		id = entity.getId();
		name = entity.getName();
		email = entity.getEmail();
		phone = entity.getPhone();
		birthDate = entity.getBirthDate();
		entity.getRoles().forEach(role -> this.roles.add(new RoleDTO(role)));
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getEmail() {
		return email;
	}

	public String getPhone() {
		return phone;
	}

	public LocalDate getBirthDate() {
		return birthDate;
	}

	public Set<RoleDTO> getRoles() {
		return roles;
	}
}
