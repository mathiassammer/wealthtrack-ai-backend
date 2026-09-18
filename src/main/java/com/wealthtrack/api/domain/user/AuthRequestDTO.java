package com.wealthtrack.api.domain.user;

import java.math.BigDecimal;

// O que esperamos receber do front-end quando o usuário logar ou cadastrar
public record AuthRequestDTO(String email, String password, BigDecimal alertThreshold) {}
