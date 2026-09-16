package com.sawmik.spring_batch.processor;

import com.sawmik.spring_batch.dto.EmployeeDTO;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class EmployeeProcessor implements ItemProcessor<EmployeeDTO, EmployeeDTO> {

    @Override
    public EmployeeDTO process(EmployeeDTO item) {
        item.setFirstName(item.getFirstName().toUpperCase());
        item.setLastName(item.getLastName().toUpperCase());
        item.setEmail(item.getEmail().toLowerCase());
        item.setDepartment(item.getDepartment().trim());
        return item;
    }
}
