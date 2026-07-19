package com.marwaneqada.ebanking.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "account_type")
public abstract class BankAccount {
    @Id
    private String id;
    @Column(nullable = false)
    private double balance;
    @Column(nullable = false)
    private Instant createdAt;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatus status;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Customer customer;

    protected BankAccount() {}
    protected BankAccount(String id, double balance, Customer customer) {
        this.id = id;
        this.balance = balance;
        this.customer = customer;
        this.createdAt = Instant.now();
        this.status = AccountStatus.ACTIVATED;
    }
    public String getId() { return id; }
    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }
    public Instant getCreatedAt() { return createdAt; }
    public AccountStatus getStatus() { return status; }
    public Customer getCustomer() { return customer; }
}
