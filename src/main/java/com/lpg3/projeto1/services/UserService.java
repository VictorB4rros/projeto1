package com.lpg3.projeto1.services;

import com.lpg3.projeto1.dto.RoleDTO;
import com.lpg3.projeto1.dto.UserDTO;
import com.lpg3.projeto1.entities.Role;
import com.lpg3.projeto1.entities.User;
import com.lpg3.projeto1.projections.UserDetailsProjection;
import com.lpg3.projeto1.repositories.RoleRepository;
import com.lpg3.projeto1.repositories.UserRepository;
import com.lpg3.projeto1.services.exceptions.DatabaseException;
import com.lpg3.projeto1.services.exceptions.ResourceNotFoundException;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
import com.lpg3.projeto1.dto.UserUpdateDTO;

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
			throw new UsernameNotFoundException("Email não encontrado");
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
			throw new UsernameNotFoundException("Email não encontrado");
		}
	}
	
	@Transactional(readOnly = true)
	public UserDTO getMe() {
		User user = authenticated();
		return new UserDTO(user);
	}

	@Transactional(readOnly = true)
	public Page<UserDTO> findAll(Pageable pageable) {
		Page<User> result = repository.findAll(pageable);
		return result.map(x -> new UserDTO(x));
	}

	@Transactional(readOnly = true)
	public UserDTO findById(Long id) {
		User entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Recurso não encontrado"));
		return new UserDTO(entity);
	}
	
	@Transactional
	public UserDTO insert(UserInsertDTO dto) {
		User entity = new User();
		copyDtoToEntity(dto, entity);
		entity.setPassword(passwordEncoder.encode(dto.getPassword()));
		entity = repository.save(entity);
		return new UserDTO(entity);
	}
	
	@Transactional
	public UserDTO update(Long id, UserUpdateDTO dto) {
		try {
			User entity = repository.getReferenceById(id);
			copyDtoToEntity(dto, entity);
			entity = repository.save(entity);
			return new UserDTO(entity);
		}
		catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException("Recurso não encontrado");
		}
	}

	@Transactional
	public void delete(Long id) {
	    if (!repository.existsById(id)) {
	        throw new ResourceNotFoundException("Usuário não encontrado: " + id);
	    }
	    try {
	        repository.deleteById(id);
	    } catch (DataIntegrityViolationException e) {
	        throw new DatabaseException("Não é possível excluir usuário com vínculos ativos");
	    }
	}

	private void copyDtoToEntity(UserDTO dto, User entity) {
		entity.setName(dto.getName());
		entity.setEmail(dto.getEmail());
		entity.setPhone(dto.getPhone());
		entity.setBirthDate(dto.getBirthDate());

		entity.getRoles().clear();
		for (RoleDTO roleDto : dto.getRoles()) {
			Role role = roleRepository.getReferenceById(roleDto.getId());
			entity.getRoles().add(role);
		}
	}
}
