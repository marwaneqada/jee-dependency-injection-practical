package com.marwaneqada.ebanking.web;

import com.marwaneqada.ebanking.domain.*;
import jakarta.validation.constraints.*;
import java.time.Instant;
import java.util.List;

public final class ApiModels {
    private ApiModels() {}
    public record LoginRequest(@NotBlank String username, @NotBlank String password) {}
    public record AuthResponse(String accessToken, String tokenType, long expiresIn, String username, List<String> roles) {}
    public record CustomerRequest(@NotBlank String name, @Email @NotBlank String email) {}
    public record CustomerResponse(Long id, String name, String email) {
        public static CustomerResponse from(Customer c) { return new CustomerResponse(c.getId(), c.getName(), c.getEmail()); }
    }
    public record AccountResponse(String id, String type, double balance, Instant createdAt, AccountStatus status,
                                  CustomerResponse customer, Double overdraft, Double interestRate) {
        public static AccountResponse from(BankAccount a) {
            return new AccountResponse(a.getId(), a instanceof CurrentAccount ? "CURRENT" : "SAVING", a.getBalance(),
                    a.getCreatedAt(), a.getStatus(), CustomerResponse.from(a.getCustomer()),
                    a instanceof CurrentAccount ca ? ca.getOverdraft() : null,
                    a instanceof SavingAccount sa ? sa.getInterestRate() : null);
        }
    }
    public record OperationRequest(@Positive double amount, @NotBlank String description) {}
    public record TransferRequest(@NotBlank String sourceAccountId, @NotBlank String destinationAccountId,
                                  @Positive double amount, @NotBlank String description) {}
    public record OperationResponse(Long id, Instant operationDate, double amount, OperationType type, String description) {
        public static OperationResponse from(AccountOperation o) {
            return new OperationResponse(o.getId(), o.getOperationDate(), o.getAmount(), o.getType(), o.getDescription());
        }
    }
    public record OperationPage(String accountId, double balance, int page, int size, long totalElements,
                                int totalPages, List<OperationResponse> operations) {}
    public record ApiError(Instant timestamp, int status, String error, String message, String path) {}
}
