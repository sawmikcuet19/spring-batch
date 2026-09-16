package com.sawmik.spring_batch.writer;

import com.sawmik.spring_batch.entity.Employee;
import org.springframework.batch.infrastructure.item.database.JpaItemWriter;
import org.springframework.batch.infrastructure.item.database.builder.JpaItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import jakarta.persistence.EntityManagerFactory;

@Configuration
public class CompositeFileDbWriter {

    @Bean
    public JpaItemWriter<Employee> employeeJpaWriter(EntityManagerFactory entityManagerFactory) {
        return new JpaItemWriterBuilder<Employee>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }
}
