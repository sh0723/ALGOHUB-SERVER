package com.gamzabat.algohub.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gamzabat.algohub.domain.Problem;

public interface ProblemRepository extends JpaRepository<Problem, Long> {
}
