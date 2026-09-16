package com.sawmik.spring_batch.processor;

import com.sawmik.spring_batch.dto.ProductDTO;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class ProductEnrichmentProcessor implements ItemProcessor<ProductDTO, ProductDTO> {

    private static final BigDecimal TAX_RATE = new BigDecimal("0.08");
    private static final BigDecimal DISCOUNT_THRESHOLD = new BigDecimal("100");

    @Override
    public ProductDTO process(ProductDTO item) {
        BigDecimal price = item.getPrice();
        BigDecimal tax = price.multiply(TAX_RATE).setScale(2, RoundingMode.HALF_UP);
        BigDecimal finalPrice = price.add(tax);

        if (price.compareTo(DISCOUNT_THRESHOLD) > 0) {
            BigDecimal discount = finalPrice.multiply(new BigDecimal("0.10")).setScale(2, RoundingMode.HALF_UP);
            finalPrice = finalPrice.subtract(discount);
        }

        item.setPrice(finalPrice);
        if (item.getStockQuantity() == null) {
            item.setStockQuantity(0);
        }
        return item;
    }
}
