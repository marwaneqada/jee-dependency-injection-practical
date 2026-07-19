package com.marwaneqada.ebanking.web;

import com.marwaneqada.ebanking.service.BankingService;
import com.marwaneqada.ebanking.web.ApiModels.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class BankAccountController {
    private final BankingService banking;
    public BankAccountController(BankingService banking) { this.banking = banking; }
    @GetMapping public List<AccountResponse> all() { return banking.accounts(); }
    @GetMapping("/{id}") public AccountResponse one(@PathVariable String id) { return banking.account(id); }
    @GetMapping("/{id}/operations")
    public OperationPage history(@PathVariable String id, @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "10") int size) { return banking.history(id, page, size); }
    @PostMapping("/{id}/debit") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void debit(@PathVariable String id, @Valid @RequestBody OperationRequest request) { banking.debit(id, request); }
    @PostMapping("/{id}/credit") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void credit(@PathVariable String id, @Valid @RequestBody OperationRequest request) { banking.credit(id, request); }
    @PostMapping("/transfer") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void transfer(@Valid @RequestBody TransferRequest request) { banking.transfer(request); }
}
