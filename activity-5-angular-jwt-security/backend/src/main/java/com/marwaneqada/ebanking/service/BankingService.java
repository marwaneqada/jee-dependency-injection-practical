package com.marwaneqada.ebanking.service;

import com.marwaneqada.ebanking.domain.*;
import com.marwaneqada.ebanking.repository.*;
import com.marwaneqada.ebanking.web.ApiModels.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class BankingService {
    private final CustomerRepository customers;
    private final BankAccountRepository accounts;
    private final AccountOperationRepository operations;

    public BankingService(CustomerRepository customers, BankAccountRepository accounts, AccountOperationRepository operations) {
        this.customers = customers;
        this.accounts = accounts;
        this.operations = operations;
    }

    @Transactional(readOnly = true)
    public List<CustomerResponse> customers() { return customers.findAll().stream().map(CustomerResponse::from).toList(); }
    public CustomerResponse createCustomer(CustomerRequest request) {
        if (customers.existsByEmailIgnoreCase(request.email())) throw new IllegalArgumentException("Email is already used");
        return CustomerResponse.from(customers.save(new Customer(request.name(), request.email())));
    }
    public CustomerResponse updateCustomer(long id, CustomerRequest request) {
        Customer customer = customer(id);
        customer.setName(request.name());
        customer.setEmail(request.email());
        return CustomerResponse.from(customer);
    }
    public void deleteCustomer(long id) { customers.delete(customer(id)); }
    @Transactional(readOnly = true)
    public List<AccountResponse> accounts() { return accounts.findAll().stream().map(AccountResponse::from).toList(); }
    @Transactional(readOnly = true)
    public AccountResponse account(String id) { return AccountResponse.from(accountEntity(id)); }
    public AccountResponse createCurrent(long customerId, double balance, double overdraft) {
        return AccountResponse.from(accounts.save(new CurrentAccount(java.util.UUID.randomUUID().toString(), balance, customer(customerId), overdraft)));
    }
    public AccountResponse createSaving(long customerId, double balance, double interestRate) {
        return AccountResponse.from(accounts.save(new SavingAccount(java.util.UUID.randomUUID().toString(), balance, customer(customerId), interestRate)));
    }
    public void debit(String id, OperationRequest request) {
        BankAccount account = accountEntity(id);
        double limit = account instanceof CurrentAccount ca ? ca.getOverdraft() : 0;
        if (account.getBalance() + limit < request.amount()) throw new IllegalStateException("Insufficient balance");
        account.setBalance(account.getBalance() - request.amount());
        operations.save(new AccountOperation(request.amount(), OperationType.DEBIT, request.description(), account));
    }
    public void credit(String id, OperationRequest request) {
        BankAccount account = accountEntity(id);
        account.setBalance(account.getBalance() + request.amount());
        operations.save(new AccountOperation(request.amount(), OperationType.CREDIT, request.description(), account));
    }
    public void transfer(TransferRequest request) {
        if (request.sourceAccountId().equals(request.destinationAccountId())) throw new IllegalArgumentException("Accounts must be different");
        debit(request.sourceAccountId(), new OperationRequest(request.amount(), request.description()));
        credit(request.destinationAccountId(), new OperationRequest(request.amount(), request.description()));
    }
    @Transactional(readOnly = true)
    public OperationPage history(String id, int page, int size) {
        BankAccount account = accountEntity(id);
        Page<AccountOperation> result = operations.findByBankAccountIdOrderByOperationDateDesc(id, PageRequest.of(page, size));
        return new OperationPage(id, account.getBalance(), page, size, result.getTotalElements(), result.getTotalPages(),
                result.getContent().stream().map(OperationResponse::from).toList());
    }
    private Customer customer(long id) { return customers.findById(id).orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + id)); }
    private BankAccount accountEntity(String id) { return accounts.findById(id).orElseThrow(() -> new ResourceNotFoundException("Account not found: " + id)); }
}
