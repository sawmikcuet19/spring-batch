package com.sawmik.spring_batch.processor;

import com.sawmik.spring_batch.dto.OrderDTO;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class OrderFilterProcessor implements ItemProcessor<OrderDTO, OrderDTO> {

    private static final BigDecimal MIN_AMOUNT = new BigDecimal("50");
    private static final BigDecimal MAX_AMOUNT = new BigDecimal("10000");

    @Override
    public OrderDTO process(OrderDTO item) {
        if (item.getTotalAmount().compareTo(MIN_AMOUNT) < 0) {
            return null;
        }
        if (item.getTotalAmount().compareTo(MAX_AMOUNT) > 0) {
            item.setStatus("NEEDS_REVIEW");
        }
        return item;
    }
}
