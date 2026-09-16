package com.wealthtrack.api.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    // O @Value busca variáveis de ambiente. Esse valor vai vir do nosso arquivo .env,
    // ou seja, nunca estará "hardcoded" no repositório do GitHub (LGPD).
    @Value("${api.security.token.secret}")
    private String secret;

    // Método que gera o "crachá" (JWT)
    public String generateToken(String email) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret); // Usamos o algoritmo HMAC256 + nossa senha secreta
            return JWT.create()
                    .withIssuer("wealthtrack-api") // Quem emitiu o crachá
                    .withSubject(email) // O dono do crachá
                    .withExpiresAt(generateExpirationDate()) // Validade de 2 horas
                    .sign(algorithm); // Assina digitalmente para ninguém falsificar
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Erro ao gerar token JWT", exception);
        }
    }

    private Instant generateExpirationDate() {
        // Pega a hora atual no Brasil (-03:00) e soma 2 horas. Após isso, o usuário tem que logar de novo.
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }
}
