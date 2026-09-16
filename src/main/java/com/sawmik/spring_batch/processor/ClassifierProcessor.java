package com.sawmik.spring_batch.processor;

import org.springframework.batch.infrastructure.item.support.ClassifierCompositeItemProcessor;
import org.springframework.batch.infrastructure.item.support.builder.ClassifierCompositeItemProcessorBuilder;
import org.springframework.classify.Classifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.sawmik.spring_batch.dto.ProductDTO;
import java.math.BigDecimal;

@Configuration
public class ClassifierProcessor {

    @Bean
    public ClassifierCompositeItemProcessor<Object, Object> classifierCompositeProcessor() {
        return new ClassifierCompositeItemProcessorBuilder<Object, Object>()
                .classifier((Classifier<Object, org.springframework.batch.infrastructure.item.ItemProcessor<?, ?>>) item -> {
                    if (item instanceof ProductDTO product) {
                        if (product.getPrice().compareTo(new BigDecimal("100")) > 0) {
                            return new HighValueProductProcessor();
                        } else {
                            return new LowValueProductProcessor();
                        }
                    }
                    return new PassThroughProcessor();
                })
                .build();
    }

    private static class HighValueProductProcessor implements org.springframework.batch.infrastructure.item.ItemProcessor<ProductDTO, ProductDTO> {
        @Override
        public ProductDTO process(ProductDTO item) {
            item.setCategory("PREMIUM_" + item.getCategory());
            return item;
        }
    }

    private static class LowValueProductProcessor implements org.springframework.batch.infrastructure.item.ItemProcessor<ProductDTO, ProductDTO> {
        @Override
        public ProductDTO process(ProductDTO item) {
            item.setCategory("BUDGET_" + item.getCategory());
            return item;
        }
    }

    private static class PassThroughProcessor implements org.springframework.batch.infrastructure.item.ItemProcessor<Object, Object> {
        @Override
        public Object process(Object item) {
            return item;
        }
    }
}
