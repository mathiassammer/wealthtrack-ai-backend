package com.wealthtrack.api.domain.transaction.model;

import com.wealthtrack.api.domain.account.model.Account;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Table(name = "transactions")
@Entity(name = "Transaction")
@Getter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Relacionamento: Muitas transações pertencem a UMA conta
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    private String category;
    private String description;

    @Column(name = "transaction_date")
    private LocalDate transactionDate;

    @Column(name = "attachment_url")
    private String attachmentUrl;

    // Construtor usado pelo nosso TransactionService
    public Transaction(Account account, BigDecimal amount, TransactionType type, String category, String description, LocalDate transactionDate) {
        this.account = account;
        this.amount = amount;
        this.type = type;
        this.category = category;
        this.description = description;
        this.transactionDate = transactionDate;
    }
}