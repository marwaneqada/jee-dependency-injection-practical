package com.marwaneqada.ebanking.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("SAVING")
public class SavingAccount extends BankAccount {
    private double interestRate;
    protected SavingAccount() {}
    public SavingAccount(String id, double balance, Customer customer, double interestRate) {
        super(id, balance, customer);
        this.interestRate = interestRate;
    }
    public double getInterestRate() { return interestRate; }
}
