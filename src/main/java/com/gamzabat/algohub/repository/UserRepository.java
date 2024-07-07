package com.gamzabat.algohub.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gamzabat.algohub.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {
	boolean existsByEmail(String email);
}
