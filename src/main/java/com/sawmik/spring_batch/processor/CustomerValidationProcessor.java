package com.sawmik.spring_batch.processor;

import com.sawmik.spring_batch.dto.CustomerDTO;
import com.sawmik.spring_batch.exception.SkippableException;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class CustomerValidationProcessor implements ItemProcessor<CustomerDTO, CustomerDTO> {

    @Override
    public CustomerDTO process(CustomerDTO item) throws Exception {
        if (item.getFirstName() == null || item.getFirstName().isBlank()) {
            throw new SkippableException("Customer first name is required");
        }
        if (item.getLastName() == null || item.getLastName().isBlank()) {
            throw new SkippableException("Customer last name is required");
        }
        if (item.getEmail() == null || !item.getEmail().contains("@")) {
            throw new SkippableException("Invalid email format: " + item.getEmail());
        }
        return item;
    }
}
