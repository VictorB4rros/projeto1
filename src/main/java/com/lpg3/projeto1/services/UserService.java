package com.lpg3.projeto1.services;

import com.lpg3.projeto1.dto.UserDTO;
import com.lpg3.projeto1.entities.Role;
import com.lpg3.projeto1.entities.User;
import com.lpg3.projeto1.projections.UserDetailsProjection;
import com.lpg3.projeto1.repositories.RoleRepository;
import com.lpg3.projeto1.repositories.UserRepository;
import com.lpg3.projeto1.services.exceptions.DatabaseException;
import com.lpg3.projeto1.services.exceptions.ResourceNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.lpg3.projeto1.dto.UserInsertDTO;

import java.util.List;

@Service
public class UserService implements UserDetailsService {

	@Autowired
	private UserRepository repository;
	
	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;
	
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		List<UserDetailsProjection> result = repository.searchUserAndRolesByEmail(username);
		if (result.size() == 0) {
			throw new UsernameNotFoundException("Email not found.");
		}
		User user = new User();
		user.setEmail(result.get(0).getUsername());
		user.setPassword(result.get(0).getPassword());
		user.setId(result.get(0).getUserId());
		for (UserDetailsProjection projection : result) {
			user.addRole(new Role(projection.getRoleId(), projection.getAuthority()));
		}
		return user;
	}

	protected User authenticated() {
		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			Jwt jwtPrincipal = (Jwt) authentication.getPrincipal();
			String username = jwtPrincipal.getClaim("username");
			User user = repository.findByEmail(username).get();
			return user;
		} catch (Exception e) {
			throw new UsernameNotFoundException("Email not found");
		}
	}
	
	@Transactional(readOnly = true)
	public UserDTO getMe() {
		User user = authenticated();
		return new UserDTO(user);
	}
	
	@Transactional
	public UserDTO insert(UserInsertDTO dto) {
	    if (repository.findByEmail(dto.getEmail()).isPresent()) {
	        throw new DatabaseException("Email já cadastrado: " + dto.getEmail());
	    }

	    User user = new User();
	    user.setName(dto.getName());
	    user.setEmail(dto.getEmail());
	    user.setPhone(dto.getPhone());
	    user.setBirthDate(dto.getBirthDate());
	    user.setPassword(passwordEncoder.encode(dto.getPassword()));

	    Role role = roleRepository.findByAuthority("ROLE_CLIENT")
	            .orElseThrow(() -> new ResourceNotFoundException("Role padrão não encontrada"));
	    user.addRole(role);

	    user = repository.save(user);
	    return new UserDTO(user);
	}
}
