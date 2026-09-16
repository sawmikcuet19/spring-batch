package com.sawmik.spring_batch.flow;

import com.sawmik.spring_batch.entity.Employee;
import com.sawmik.spring_batch.tasklet.FileCleanupTasklet;
import com.sawmik.spring_batch.tasklet.FileCountTasklet;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.database.JpaItemWriter;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class SequentialFlowJob {

    @Bean
    public Job sequentialJobBean(JobRepository jobRepository, Step step1CountFiles, Step step2ProcessData, Step step3Cleanup) {
        return new JobBuilder("sequentialJob", jobRepository)
                .start(step1CountFiles)
                .next(step2ProcessData)
                .next(step3Cleanup)
                .build();
    }

    @Bean
    public Step step1CountFiles(JobRepository jobRepository, PlatformTransactionManager transactionManager, FileCountTasklet fileCountTasklet) {
        return new StepBuilder("step1CountFiles", jobRepository)
                .tasklet(fileCountTasklet, transactionManager)
                .build();
    }

    @Bean
    public Step step2ProcessData(JobRepository jobRepository, PlatformTransactionManager transactionManager,
                                  FlatFileItemReader<com.sawmik.spring_batch.dto.EmployeeDTO> employeeCsvReader,
                                  JpaItemWriter<Employee> employeeJpaWriter) {
        return new StepBuilder("step2ProcessData", jobRepository)
                .<com.sawmik.spring_batch.dto.EmployeeDTO, Employee>chunk(100, transactionManager)
                .reader(employeeCsvReader)
                .processor(item -> Employee.builder()
                        .firstName(item.getFirstName())
                        .lastName(item.getLastName())
                        .email(item.getEmail())
                        .department(item.getDepartment())
                        .salary(item.getSalary())
                        .hireDate(item.getHireDate())
                        .position(item.getPosition())
                        .location(item.getLocation())
                        .phone(item.getPhone())
                        .build())
                .writer(employeeJpaWriter)
                .build();
    }

    @Bean
    public Step step3Cleanup(JobRepository jobRepository, PlatformTransactionManager transactionManager, FileCleanupTasklet fileCleanupTasklet) {
        return new StepBuilder("step3Cleanup", jobRepository)
                .tasklet(fileCleanupTasklet, transactionManager)
                .build();
    }

    @Bean
    public FlatFileItemReader<com.sawmik.spring_batch.dto.EmployeeDTO> employeeCsvReader() {
        return new FlatFileItemReaderBuilder<com.sawmik.spring_batch.dto.EmployeeDTO>()
                .name("employeeCsvReader")
                .resource(new org.springframework.core.io.FileSystemResource("Csv_Files/employees_100k.csv"))
                .linesToSkip(1)
                .delimited()
                .names("id", "firstName", "lastName", "email", "department", "salary", "hireDate", "position", "location", "phone")
                .fieldSetMapper(fieldSet -> {
                    com.sawmik.spring_batch.dto.EmployeeDTO dto = new com.sawmik.spring_batch.dto.EmployeeDTO();
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
