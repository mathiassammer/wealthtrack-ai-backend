package com.wealthtrack.api.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    // Mágica do Spring: Só de escrever o nome do método em inglês (findBy...),
    // ele monta a query SQL: 'SELECT * FROM users WHERE email = ?' para você!
    UserDetails findByEmail(String email);
}
