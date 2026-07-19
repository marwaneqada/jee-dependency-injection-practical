package com.marwaneqada.ebanking.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("CURRENT")
public class CurrentAccount extends BankAccount {
    private double overdraft;
    protected CurrentAccount() {}
    public CurrentAccount(String id, double balance, Customer customer, double overdraft) {
        super(id, balance, customer);
        this.overdraft = overdraft;
    }
    public double getOverdraft() { return overdraft; }
}
