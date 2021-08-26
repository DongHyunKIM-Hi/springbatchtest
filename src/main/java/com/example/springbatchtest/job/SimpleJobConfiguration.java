package com.example.springbatchtest.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j // 로그를 위한 어노테이션
@RequiredArgsConstructor
@Configuration
public class SimpleJobConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;


    @Bean
    public Job simpleJob(){
        return jobBuilderFactory.get("simpleJob") //"simpleJob이라는 job을 생성합니다."
                .start(simpleStep1()) // "simpleJob은 simpleStep1을 step으로 가지고 있습니다"
                .build();
    }

    @Bean
    public Step simpleStep1(){
        return stepBuilderFactory.get("simpleStep1") //"simpleStep1이라는 Step을 생성합니다."
                .tasklet((contribution, chunkContext) -> { //각각의 Step 안에서 수행될 기능들을 명시합니다.
                    log.info("스텝 1단계 시작");  //tasklet에서 수행될 기능들을 명시하고
                    return RepeatStatus.FINISHED; // reader,processor,write에서 실질적인 작업을 수행한다.
                })
                .build();
    }
}
