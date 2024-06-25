package com.sametakbal.user_management.repository;

import com.sametakbal.user_management.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User u WHERE u.username = ?1 OR u.email = ?1")
    Optional<User> findByUsernameOrEmail(String usernameOrEmail);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
