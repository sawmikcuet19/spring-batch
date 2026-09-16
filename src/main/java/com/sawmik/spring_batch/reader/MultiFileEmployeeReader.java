package com.sawmik.spring_batch.reader;

import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;
import com.sawmik.spring_batch.dto.EmployeeDTO;

@Component
public class MultiFileEmployeeReader {

    public FlatFileItemReader<EmployeeDTO> createSingleFileReader(String filePath) {
        return new FlatFileItemReaderBuilder<EmployeeDTO>()
                .name("employeeCsvReader")
                .resource(new FileSystemResource(filePath))
                .linesToSkip(1)
                .delimited()
                .names("id", "firstName", "lastName", "email", "department", "salary", "hireDate", "position", "location", "phone")
                .fieldSetMapper(fieldSet -> {
                    EmployeeDTO dto = new EmployeeDTO();
                    dto.setId(fieldSet.readLong("id"));
                    dto.setFirstName(fieldSet.readString("firstName"));
                    dto.setLastName(fieldSet.readString("lastName"));
                    dto.setEmail(fieldSet.readString("email"));
                    dto.setDepartment(fieldSet.readString("department"));
                    dto.setSalary(fieldSet.readBigDecimal("salary"));
                    String hireDateStr = fieldSet.readString("hireDate");
                    if (hireDateStr != null && !hireDateStr.isEmpty()) {
                        dto.setHireDate(java.time.LocalDate.parse(hireDateStr));
                    }
                    dto.setPosition(fieldSet.readString("position"));
                    dto.setLocation(fieldSet.readString("location"));
                    dto.setPhone(fieldSet.readString("phone"));
                    return dto;
                })
                .build();
    }
}
