package com.wealthtrack.api.controller;

import com.wealthtrack.api.domain.user.AuthRequestDTO;
import com.wealthtrack.api.domain.user.AuthResponseDTO;
import com.wealthtrack.api.domain.user.User;
import com.wealthtrack.api.domain.user.UserRepository;
import com.wealthtrack.api.security.TokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager; // O maestro do Spring
    @Autowired
    private UserRepository repository;
    @Autowired
    private TokenService tokenService;

    // ROTA DE LOGIN
    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Valid AuthRequestDTO data) {
        // Empacota o email e senha digitados
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.email(), data.password());

        // O maestro vai no banco, criptografa o que foi digitado e compara com o banco.
        // Se errar a senha, ele trava aqui mesmo e devolve erro 403 (Forbidden)
        var auth = this.authenticationManager.authenticate(usernamePassword);

        // Se passou, geramos o crachá JWT e devolvemos na resposta
        var token = tokenService.generateToken(data.email());
        return ResponseEntity.ok(new AuthResponseDTO(token));
    }

    // ROTA DE CADASTRO (SIGNUP)
    @PostMapping("/signup")
    public ResponseEntity register(@RequestBody @Valid AuthRequestDTO data) {
        // 1. Verifica se o email já existe
        if (this.repository.findByEmail(data.email()) != null) {
            return ResponseEntity.badRequest().body("E-mail já cadastrado!"); // Erro 400
        }

        // 2. Criptografa a senha antes de salvar no banco (NUNCA salvamos texto puro)
        String encryptedPassword = new BCryptPasswordEncoder().encode(data.password());

        // 3. Cria o usuário e salva no banco de dados
        User newUser = new User(data.email(), encryptedPassword, data.alertThreshold());
        this.repository.save(newUser);

        return ResponseEntity.ok().build(); // Sucesso 200 OK
    }
}
