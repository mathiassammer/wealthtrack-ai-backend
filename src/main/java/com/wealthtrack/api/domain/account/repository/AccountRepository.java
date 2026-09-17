package com.wealthtrack.api.domain.account.repository;

import com.wealthtrack.api.domain.account.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {
}
