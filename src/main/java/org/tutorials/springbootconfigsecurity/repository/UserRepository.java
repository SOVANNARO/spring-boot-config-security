package org.tutorials.springbootconfigsecurity.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tutorials.springbootconfigsecurity.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);

}