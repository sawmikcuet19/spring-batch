package com.sawmik.spring_batch.flow;

import com.sawmik.spring_batch.entity.Product;
import com.sawmik.spring_batch.processor.ProductEnrichmentProcessor;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.database.JpaItemWriter;
import org.springframework.batch.infrastructure.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import jakarta.persistence.EntityManagerFactory;

@Configuration
public class ConditionalFlowJob {

    @Bean
    public Job conditionalFlowJobBean(JobRepository jobRepository, Step readProductsStep,
                                    Step enrichHighValueStep, Step enrichLowValueStep) {
        return new JobBuilder("conditionalFlowJob", jobRepository)
                .start(readProductsStep)
                    .on("COMPLETED").to(enrichHighValueStep)
                .from(readProductsStep)
                    .on("FAILED").to(enrichLowValueStep)
                .from(enrichHighValueStep).on("*").end()
                .from(enrichLowValueStep).on("*").fail()
                .build().build();
    }

    @Bean
    public Step readProductsStep(JobRepository jobRepository, PlatformTransactionManager transactionManager,
                                  FlatFileItemReader<com.sawmik.spring_batch.dto.ProductDTO> productCsvReader,
                                  ProductEnrichmentProcessor processor,
                                  JpaItemWriter<Product> productJpaWriter) {
        return new StepBuilder("readProductsStep", jobRepository)
                .<com.sawmik.spring_batch.dto.ProductDTO, Product>chunk(100, transactionManager)
                .reader(productCsvReader)
                .processor(item -> {
                    com.sawmik.spring_batch.dto.ProductDTO enriched = processor.process(item);
                    if (enriched == null) return null;
                    return Product.builder()
                            .name(enriched.getName())
                            .brand(enriched.getBrand())
                            .category(enriched.getCategory())
                            .price(enriched.getPrice())
                            .sku(enriched.getSku())
                            .stockQuantity(enriched.getStockQuantity())
                            .build();
                })
                .writer(productJpaWriter)
                .build();
    }

    @Bean
    public Step enrichHighValueStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("enrichHighValueStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    org.slf4j.LoggerFactory.getLogger(getClass()).info("Enriching HIGH VALUE products");
                    return org.springframework.batch.infrastructure.repeat.RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step enrichLowValueStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("enrichLowValueStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    return org.springframework.batch.infrastructure.repeat.RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public FlatFileItemReader<com.sawmik.spring_batch.dto.ProductDTO> productCsvReader() {
        return new FlatFileItemReaderBuilder<com.sawmik.spring_batch.dto.ProductDTO>()
                .name("productCsvReader")
                .resource(new org.springframework.core.io.FileSystemResource("Csv_Files/products_100k.csv"))
                .linesToSkip(1)
                .delimited()
                .names("id", "name", "brand", "category", "price", "sku", "stockQuantity", "createdAt")
                .fieldSetMapper(fieldSet -> {
                    com.sawmik.spring_batch.dto.ProductDTO dto = new com.sawmik.spring_batch.dto.ProductDTO();
                    dto.setId(fieldSet.readLong("id"));
                    dto.setName(fieldSet.readString("name"));
                    dto.setBrand(fieldSet.readString("brand"));
                    dto.setCategory(fieldSet.readString("category"));
                    dto.setPrice(fieldSet.readBigDecimal("price"));
                    dto.setSku(fieldSet.readString("sku"));
                    dto.setStockQuantity(fieldSet.readInt("stockQuantity"));
                    return dto;
                })
                .build();
    }

    @Bean
    public JpaItemWriter<Product> productJpaWriter(EntityManagerFactory entityManagerFactory) {
        return new JpaItemWriterBuilder<Product>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }
}
