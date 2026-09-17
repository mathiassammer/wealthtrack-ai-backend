package com.wealthtrack.api.domain.account.model;

import com.wealthtrack.api.domain.user.User;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Table(name = "accounts")
@Entity(name = "Account")
@Getter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Relacionamento: Muitas contas podem pertencer a UM usuário
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id") // Nome da coluna no banco
    private User user;

    private String name;

    @Enumerated(EnumType.STRING)
    private AccountType type;

    private BigDecimal balance;

    @Column(name = "is_active")
    private Boolean isActive;

    // Construtor para os Testes que usamos na Etapa 9
    public Account(UUID id, String name, BigDecimal balance) {
        this.id = id;
        this.name = name;
        this.balance = balance;
        this.isActive = true;
    }

    // --- ENCAPSULAMENTO (Regras de Negócio de alteração de saldo) ---
    // Repare que NÃO tem setter de saldo. Quem quiser mexer no saldo, precisa usar esses métodos.
    public void addBalance(BigDecimal amount) {
        this.balance = this.balance.add(amount);
    }

    public void subtractBalance(BigDecimal amount) {
        this.balance = this.balance.subtract(amount);
    }
}