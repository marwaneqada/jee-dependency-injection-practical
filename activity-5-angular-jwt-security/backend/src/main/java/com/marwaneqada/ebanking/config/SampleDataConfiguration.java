package com.marwaneqada.ebanking.config;

import com.marwaneqada.ebanking.domain.AppUser;
import com.marwaneqada.ebanking.domain.Customer;
import com.marwaneqada.ebanking.repository.AppUserRepository;
import com.marwaneqada.ebanking.repository.CustomerRepository;
import com.marwaneqada.ebanking.service.BankingService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SampleDataConfiguration {
    @Bean
    CommandLineRunner seed(AppUserRepository users, CustomerRepository customers, BankingService banking, PasswordEncoder encoder) {
        return args -> {
            users.save(new AppUser("admin", encoder.encode("admin123"), "ROLE_USER,ROLE_ADMIN"));
            users.save(new AppUser("user", encoder.encode("user123"), "ROLE_USER"));
            Customer hassan = customers.save(new Customer("Hassan El Alami", "hassan@example.com"));
            Customer aicha = customers.save(new Customer("Aicha Bennani", "aicha@example.com"));
            var current = banking.createCurrent(hassan.getId(), 12500, 5000);
            var saving = banking.createSaving(aicha.getId(), 28400, 4.5);
            banking.credit(current.id(), new com.marwaneqada.ebanking.web.ApiModels.OperationRequest(1200, "Salary payment"));
            banking.debit(current.id(), new com.marwaneqada.ebanking.web.ApiModels.OperationRequest(350, "Utility bill"));
            banking.credit(saving.id(), new com.marwaneqada.ebanking.web.ApiModels.OperationRequest(800, "Monthly savings"));
        };
    }
}
