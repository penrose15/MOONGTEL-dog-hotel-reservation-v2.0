package com.doghotel.reservation.global.batch;

import com.doghotel.reservation.domain.post.entity.PostsScore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class BatchConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;

    @Bean
    public Job testJob() {
        return jobBuilderFactory.get("test")
                .start(testStep()).build();
    }

    @Bean
    @JobScope
    public Step testStep() {
        return stepBuilderFactory.get("testStep")
                .<PostsScore, PostsScore> chunk(10)
                .reader(reader(null))
                .processor(processor(null))
                .writer(writer(null))
                .build();
    }

    @Bean
    @StepScope
    public JpaPagingItemReader <PostsScore> reader(@Value("#{jobParameters[requestDate]}") String requestDate) {
        log.info("reader value  : " + requestDate);

//        Map<String, Object> parameterValues = new HashMap<>();
        return new JpaPagingItemReaderBuilder<PostsScore>()
                .pageSize(10)
                .queryString("select p from PostsScore p group by p.posts.id")
                .entityManagerFactory(entityManagerFactory)
                .name("JpaPagingItemReader")
                .build();
    }

    @Bean
    @StepScope
    public ItemProcessor<PostsScore, PostsScore> processor(@Value("#{jobParameters[requestDate]}") String requestDate) {
        return item -> {
            item.calculateScore();
            return item;
        };
    }

    @Bean
    @StepScope
    public JpaItemWriter<PostsScore> writer(@Value("#{jobParameters[requestDate]}") String requestDate) {
        return new JpaItemWriterBuilder<PostsScore>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }
}
