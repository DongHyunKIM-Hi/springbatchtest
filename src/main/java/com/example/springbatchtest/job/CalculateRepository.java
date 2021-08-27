package com.example.springbatchtest.job;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CalculateRepository extends JpaRepository<Calculate,Long> {
}
