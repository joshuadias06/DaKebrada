package com.da.kebrada.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import com.da.kebrada.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
