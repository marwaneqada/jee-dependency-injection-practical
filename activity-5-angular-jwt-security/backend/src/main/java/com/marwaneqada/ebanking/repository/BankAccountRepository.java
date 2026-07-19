package com.marwaneqada.ebanking.repository;

import com.marwaneqada.ebanking.domain.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankAccountRepository extends JpaRepository<BankAccount, String> {}
