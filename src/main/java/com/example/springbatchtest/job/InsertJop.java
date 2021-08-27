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
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class InsertJop {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final BatchIterm batchIterm;
    private final EntityManagerFactory entityManagerFactory;
    private final CalculateRepository calculateRepository;

    private int chunkSize = 10000;

    @Bean
    public Job insertData(){
        return jobBuilderFactory.get("insertData")
                /*.start(insertDataStep1(null)) //데이터 삽입 코드
                .next(calculateStep(null)) // 데이블을 조회하고 배치 작업 수행*/
                .start(calculateStep(null))
                .build();
    }

    @Bean
    @JobScope
    public Step insertDataStep1(@Value("#{jobParameters[requestDate]}") String requestData){
        return stepBuilderFactory.get("insertDataStep1")
                .tasklet((contribution, chunkContext) -> {
                    log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> Start date init");
                    batchIterm.insertData(requestData+ ":");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    @Bean
    @JobScope
    public Step calculateStep(@Value("#{jobParameters[requestDate]}") String requestData){
        return stepBuilderFactory.get("calculateStep")
                .<Pay, Long>chunk(chunkSize)
                .reader(jpaPagingItemReader())
                .processor(processor())
                .writer(writer(null))
                .build();
    }
    @Bean
    @StepScope
    public JpaPagingItemReader<Pay> jpaPagingItemReader() {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 데이터 읽기");
        return new JpaPagingItemReaderBuilder<Pay>()
                .name("jpaPagingItemReader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(1000)
                .queryString("SELECT p FROM Pay p ")
                .build();
    }

    @Bean
    @StepScope
    public ItemProcessor<Pay, Long> processor() {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>. 데이터 변환");
        return Pay::getAmount;
    }

    @Bean
    @StepScope
    public ItemWriter<Long> writer(@Value("#{jobParameters[requestDate]}") String requestData) {
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 데이터 전환 시작");
        return items -> {
            long sum = 0L;
            for(Long item : items){
                sum +=item;
            }
            Calculate calculate = new Calculate(sum,requestData);
            calculateRepository.save(calculate);
        };
    }

}
