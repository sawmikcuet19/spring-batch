package com.sawmik.spring_batch.writer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.batch.infrastructure.item.json.JsonFileItemWriter;
import org.springframework.batch.infrastructure.item.json.builder.JsonFileItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

@Configuration
public class JsonFileWriter {

    @Bean
    public JsonFileItemWriter<Object> jsonFileItemWriter() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        return new JsonFileItemWriterBuilder<Object>()
                .name("jsonFileItemWriter")
                .resource(new FileSystemResource("output/output.json"))
                .jsonObjectMarshaller(object -> {
                    try {
                        return mapper.writeValueAsString(object);
                    } catch (Exception e) {
                        throw new RuntimeException("Error serializing to JSON", e);
                    }
                })
                .build();
    }
}
