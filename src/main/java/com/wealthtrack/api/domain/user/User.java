package com.wealthtrack.api.domain.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Table(name = "users") // Aponta para a tabela que criamos no DBeaver
@Entity(name = "user")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class User implements UserDetails { // O contrato com o Spring Security

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String email;
    @Column(name = "password_hash")
    private String passwordHash;
    @Column(name = "alert_threshold")
    private BigDecimal alertThreshold;

    // Construtor para o momento do Cadastro
    public User(String email, String passwordHash, BigDecimal alertThreshold) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.alertThreshold = (alertThreshold != null) ? alertThreshold : new BigDecimal("1000.00");
    }

    // --- Métodos obrigatórios do contrato UserDatails ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Por enquanto, todo mundo tem a "permissão" (Role) de usuário comum
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        return passwordHash; // O Spring precisa saber onde está a senha
    }

    @Override
    public String getUsername() {
        return email; // Nosso "username" de login é o email
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}
