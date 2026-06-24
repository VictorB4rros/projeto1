package com.lpg3.projeto1.repositories;

import com.lpg3.projeto1.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByAuthority(String authority);
}