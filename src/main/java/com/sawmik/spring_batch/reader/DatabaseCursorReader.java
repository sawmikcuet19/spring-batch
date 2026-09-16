package com.sawmik.spring_batch.reader;

import com.sawmik.spring_batch.dto.EmployeeDTO;
import org.springframework.batch.infrastructure.item.database.JdbcPagingItemReader;
import org.springframework.batch.infrastructure.item.database.Order;
import org.springframework.batch.infrastructure.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.stereotype.Component;
import javax.sql.DataSource;
import java.util.Map;

@Component
public class DatabaseCursorReader {

    public JdbcPagingItemReader<EmployeeDTO> createPagingReader(DataSource dataSource) throws Exception {
        return new JdbcPagingItemReaderBuilder<EmployeeDTO>()
                .name("employeePagingReader")
                .dataSource(dataSource)
                .fetchSize(100)
                .pageSize(100)
                .selectClause("id, first_name, last_name, email, department, salary, hire_date, position, location, phone")
                .fromClause("employees")
                .sortKeys(Map.of("id", Order.ASCENDING))
                .rowMapper((rs, rowNum) -> {
                    EmployeeDTO dto = new EmployeeDTO();
                    dto.setId(rs.getLong("id"));
                    dto.setFirstName(rs.getString("first_name"));
                    dto.setLastName(rs.getString("last_name"));
                    dto.setEmail(rs.getString("email"));
                    dto.setDepartment(rs.getString("department"));
                    dto.setSalary(rs.getBigDecimal("salary"));
                    if (rs.getDate("hire_date") != null) {
                        dto.setHireDate(rs.getDate("hire_date").toLocalDate());
                    }
                    dto.setPosition(rs.getString("position"));
                    dto.setLocation(rs.getString("location"));
                    dto.setPhone(rs.getString("phone"));
                    return dto;
                })
                .build();
    }
}
