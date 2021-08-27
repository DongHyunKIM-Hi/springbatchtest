package com.example.springbatchtest.job;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Random;

@Configuration
@RequiredArgsConstructor
public class BatchIterm {
    private final PayRepository repository;

    public void insertData(String code){
        for(int i = 1; i < 1000; i+=1){
            Random random = new Random();
            random.setSeed(System.currentTimeMillis());
            long rand = random.nextInt(1000);
            Pay pay = new Pay(rand, code +i);
            repository.save(pay);
        }
    }
}
