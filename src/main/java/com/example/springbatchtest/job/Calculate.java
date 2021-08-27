package com.example.springbatchtest.job;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Getter
@Setter
public class Calculate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Long totalSum;

    public String requestDate;

    public Calculate(Long totalSum, String requestDate){
        this.totalSum = totalSum;
        this.requestDate = requestDate;
    }
}
