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
    @JobScope // @JobScope, @StepScope를 사용하면 lazy loading이 되어 파라미터값을 전달이 가능하다. 해당 어노테이션을 사용하지 않으면 파라티터 값이 전달되지 않는다.
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
                .<Pay, Long>chunk(chunkSize) // 처리해서 저장할 양, chunkSize만큼 데이터를 읽고 처리하면 저장한다. 1000개를 작업을 하는데 chunkSize가 100이면 1000번의 Reader가 발생하고 1000번의 Processor가 발생하고 10번의  Writer가 발생한다.
                .reader(jpaPagingItemReader())
                .processor(processor())
                .writer(writer(null))
                .build();
    }
    @Bean
    @StepScope
    public JpaPagingItemReader<Pay> jpaPagingItemReader() { // <읽어올 데이터 타입>
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 데이터 읽기");
        return new JpaPagingItemReaderBuilder<Pay>()
                .name("jpaPagingItemReader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(1000) //한번에 불러올 데이터의 양    // 한번에 불러오는 데이터의 양이 클수록 속도가 빠르다. 다만 클수록 실패했을 때 다시 실행해야하는 양이 많아지기 때문에 적절하게 양을 설정하는게 좋다.
                .queryString("SELECT p FROM Pay p ")
                .build();
    }

    @Bean
    @StepScope
    public ItemProcessor<Pay, Long> processor() { // <읽어올 테이터 타입 , 처리해서 반환할 데이터 타입>
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>. 데이터 변환");
        return Pay::getAmount;
    }

    @Bean
    @StepScope
    public ItemWriter<Long> writer(@Value("#{jobParameters[requestDate]}") String requestData) { // Processor에서 하나씩 전달된 데이터가 chunkSize 만큼 쌓이면 Writer가 수행
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 데이터 저장 시작"); 
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
