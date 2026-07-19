package com.marwaneqada.ebanking.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "account_operations")
public class AccountOperation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Instant operationDate;
    @Column(nullable = false)
    private double amount;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OperationType type;
    private String description;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private BankAccount bankAccount;

    protected AccountOperation() {}
    public AccountOperation(double amount, OperationType type, String description, BankAccount account) {
        this.operationDate = Instant.now();
        this.amount = amount;
        this.type = type;
        this.description = description;
        this.bankAccount = account;
    }
    public Long getId() { return id; }
    public Instant getOperationDate() { return operationDate; }
    public double getAmount() { return amount; }
    public OperationType getType() { return type; }
    public String getDescription() { return description; }
}
