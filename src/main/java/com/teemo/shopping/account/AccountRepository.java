package com.teemo.shopping.account;

import jakarta.validation.constraints.NotNull;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByUsername(@NotNull String username);
}
