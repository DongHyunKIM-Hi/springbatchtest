/*
package com.example.springbatchtest.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class JpaPagingItemReaser {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;

    private int chunkSize = 4;

    @Bean //
    @JobScope
    public Job jpaPaingItemReaderJob(){
        return jobBuilderFactory.get("jpaPaingItemReaderJob")
                .start(jpaItemWriterStep())
                .build();
    }

    @Bean
    @StepScope
    public Step jpaItemWriterStep() {
        return stepBuilderFactory.get("jpaItemWriterStep")
                .<Pay, Pay2>chunk(chunkSize) // 다룰 것이다. Pay를 읽고 Pay2를 저장할거야 <reader,writer>
                .reader(jpaPagingItemReader()) // 데이터를 읽어온다. 여러개를 데이터를 불러들인다.
                .processor(jpaItemProcessor())// 불러들인 데이터를 가공해서 처리할거야
                .writer(jpaItemWriter()) // processor에서 처리한 데이터를 저장할거야
                .build();
    }

    @Bean
    @StepScope
    public JpaPagingItemReader<Pay> jpaPagingItemReader() { //Pay 테이블에서 데이터 가져오기
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 데이터 가져오기");
        return new JpaPagingItemReaderBuilder<Pay>() // Pay 엔티티를 기준으로 가져올거야
                .name("jpaPagingItemReader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(4)
                .queryString("SELECT p FROM Pay p")
                .build();
    }

    @Bean //<Read에서 받아오는 값, 작업 수행 후 반환할 값>
    @StepScope
    public ItemProcessor<Pay, Pay2> jpaItemProcessor() { //Pay데이터를 읽고 Pay2 객체를 생성
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 데이터 처리하기");
        return pay -> new Pay2(pay.getAmount(), pay.getTxName());
    }

    @Bean
    @StepScope
    public JpaItemWriter<Pay2> jpaItemWriter() { // 저장하기
        JpaItemWriter<Pay2> jpaItemWriter = new JpaItemWriter<>();
        jpaItemWriter.setEntityManagerFactory(entityManagerFactory);
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 데이터 저장하기");
        return jpaItemWriter;
    }
}
*/
