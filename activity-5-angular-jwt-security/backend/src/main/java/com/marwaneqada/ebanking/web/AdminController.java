package com.marwaneqada.ebanking.web;

import com.marwaneqada.ebanking.service.BankingService;
import com.marwaneqada.ebanking.web.ApiModels.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Validated
@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final BankingService banking;
    public AdminController(BankingService banking) { this.banking = banking; }
    @GetMapping("/customers") public List<CustomerResponse> customers() { return banking.customers(); }
    @PostMapping("/customers") @ResponseStatus(HttpStatus.CREATED)
    public CustomerResponse create(@Valid @RequestBody CustomerRequest request) { return banking.createCustomer(request); }
    @PutMapping("/customers/{id}") public CustomerResponse update(@PathVariable long id, @Valid @RequestBody CustomerRequest request) { return banking.updateCustomer(id, request); }
    @DeleteMapping("/customers/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long id) { banking.deleteCustomer(id); }
    @PostMapping("/customers/{id}/current-account") @ResponseStatus(HttpStatus.CREATED)
    public AccountResponse current(@PathVariable long id, @RequestParam @PositiveOrZero double balance,
                                   @RequestParam @PositiveOrZero double overdraft) { return banking.createCurrent(id, balance, overdraft); }
    @PostMapping("/customers/{id}/saving-account") @ResponseStatus(HttpStatus.CREATED)
    public AccountResponse saving(@PathVariable long id, @RequestParam @PositiveOrZero double balance,
                                  @RequestParam @PositiveOrZero double interestRate) { return banking.createSaving(id, balance, interestRate); }
}
