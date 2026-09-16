package com.sawmik.spring_batch.processor;

import com.sawmik.spring_batch.dto.CustomerDTO;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.support.CompositeItemProcessor;
import org.springframework.batch.infrastructure.item.support.builder.CompositeItemProcessorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.Arrays;

@Configuration
public class CompositeCustomerProcessor {

    @Bean
    public CompositeItemProcessor<CustomerDTO, CustomerDTO> compositeProcessor(
            CustomerValidationProcessor validationProcessor) {
        return new CompositeItemProcessorBuilder<CustomerDTO, CustomerDTO>()
                .delegates(Arrays.asList(validationProcessor, new TransformDelegateProcessor()))
                .build();
    }

    private static class TransformDelegateProcessor implements ItemProcessor<CustomerDTO, CustomerDTO> {
        @Override
        public CustomerDTO process(CustomerDTO item) {
            item.setFirstName(item.getFirstName().toUpperCase());
            item.setLastName(item.getLastName().toUpperCase());
            item.setEmail(item.getEmail().toLowerCase());
            return item;
        }
    }
}
