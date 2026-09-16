package com.sawmik.spring_batch.util;

import net.datafaker.Faker;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class CsvFileGenerator {

    private final Faker faker = new Faker(new Locale("en-US"));
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void generateAllFiles(String outputDir) throws IOException {
        System.out.println("Generating CSV files in: " + outputDir);
        generateEmployees(outputDir + "/employees_100k.csv", 100_000);
        generateCustomers(outputDir + "/customers_100k.csv", 100_000);
        generateProducts(outputDir + "/products_100k.csv", 100_000);
        generateOrders(outputDir + "/orders_100k.csv", 100_000);
        generateTransactions(outputDir + "/transactions_100k.csv", 100_000);
        generateStudents(outputDir + "/students_100k.csv", 100_000);
        generateServerLogs(outputDir + "/server_logs_100k.csv", 100_000);
        generateWeatherData(outputDir + "/weather_data_100k.csv", 100_000);
        System.out.println("All CSV files generated successfully!");
    }

    public void generateEmployees(String filePath, long count) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.println("id,first_name,last_name,email,department,salary,hire_date,position,location,phone");
            for (long i = 1; i <= count; i++) {
                String firstName = faker.name().firstName().replace(",", "");
                String lastName = faker.name().lastName().replace(",", "");
                String email = firstName.toLowerCase() + "." + lastName.toLowerCase() + "@company.com";
                String department = faker.options().option("Engineering", "Marketing", "Sales", "HR", "Finance", "Operations", "Legal", "Support");
                BigDecimal salary = BigDecimal.valueOf(faker.number().numberBetween(40000, 200000));
                LocalDate hireDate = faker.date().past(3650, java.util.concurrent.TimeUnit.DAYS).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
                String position = faker.job().position();
                String location = faker.address().city().replace(",", "");
                String phone = faker.phoneNumber().cellPhone();
                writer.printf("%d,%s,%s,%s,%s,%s,%s,%s,%s,%s%n",
                        i, firstName, lastName, email, department, salary, hireDate.format(DATE_FMT), position, location, phone);
            }
            System.out.println("Generated employees_100k.csv with " + count + " rows");
        }
    }

    public void generateCustomers(String filePath, long count) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.println("id,first_name,last_name,company,city,country,phone,email,subscription_date,website");
            for (long i = 1; i <= count; i++) {
                String firstName = faker.name().firstName().replace(",", "");
                String lastName = faker.name().lastName().replace(",", "");
                String company = faker.company().name().replace(",", "");
                String city = faker.address().city().replace(",", "");
                String country = faker.address().country().replace(",", "");
                String phone = faker.phoneNumber().cellPhone();
                String email = firstName.toLowerCase() + "." + lastName.toLowerCase() + "@email.com";
                LocalDate subDate = faker.date().past(1825, java.util.concurrent.TimeUnit.DAYS).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
                String website = "www." + company.toLowerCase().replaceAll("[^a-z]", "") + ".com";
                writer.printf("%d,%s,%s,%s,%s,%s,%s,%s,%s,%s%n",
                        i, firstName, lastName, company, city, country, phone, email, subDate.format(DATE_FMT), website);
            }
            System.out.println("Generated customers_100k.csv with " + count + " rows");
        }
    }

    public void generateProducts(String filePath, long count) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.println("id,name,brand,category,price,sku,stock_quantity,created_at");
            String[] categories = {"Electronics", "Clothing", "Home", "Books", "Sports", "Toys", "Food", "Beauty"};
            String[] brands = {"Apple", "Samsung", "Nike", "Adidas", "Sony", "LG", "Dell", "HP"};
            for (long i = 1; i <= count; i++) {
                String name = faker.commerce().productName().replace(",", "");
                String brand = faker.options().option(brands);
                String category = faker.options().option(categories);
                BigDecimal price = new BigDecimal(faker.commerce().price(10, 2000));
                String sku = "SKU-" + String.format("%08d", i);
                int stock = faker.number().numberBetween(0, 1000);
                LocalDateTime createdAt = faker.date().past(730, java.util.concurrent.TimeUnit.DAYS).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
                writer.printf("%d,%s,%s,%s,%s,%s,%d,%s%n",
                        i, name, brand, category, price, sku, stock, createdAt.format(DATETIME_FMT));
            }
            System.out.println("Generated products_100k.csv with " + count + " rows");
        }
    }

    public void generateOrders(String filePath, long count) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.println("id,customer_id,order_date,total_amount,status,shipping_address,payment_method");
            String[] statuses = {"PENDING", "PROCESSING", "SHIPPED", "DELIVERED", "CANCELLED"};
            String[] payments = {"CREDIT_CARD", "DEBIT_CARD", "PAYPAL", "BANK_TRANSFER", "CASH_ON_DELIVERY"};
            for (long i = 1; i <= count; i++) {
                long customerId = faker.number().numberBetween(1, 100_000);
                LocalDateTime orderDate = faker.date().past(365, java.util.concurrent.TimeUnit.DAYS).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
                BigDecimal amount = new BigDecimal(faker.commerce().price(10, 5000));
                String status = faker.options().option(statuses);
                String address = faker.address().streetAddress().replace(",", "");
                String payment = faker.options().option(payments);
                writer.printf("%d,%d,%s,%s,%s,%s,%s%n",
                        i, customerId, orderDate.format(DATETIME_FMT), amount, status, address, payment);
            }
            System.out.println("Generated orders_100k.csv with " + count + " rows");
        }
    }

    public void generateTransactions(String filePath, long count) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.println("id,account_id,amount,type,timestamp,description,balance,status");
            String[] types = {"CREDIT", "DEBIT", "TRANSFER", "WITHDRAWAL", "DEPOSIT"};
            String[] statuses = {"COMPLETED", "PENDING", "FAILED", "REVERSED"};
            AtomicLong balance = new AtomicLong(10000);
            for (long i = 1; i <= count; i++) {
                long accountId = faker.number().numberBetween(1000, 9999);
                BigDecimal amount = new BigDecimal(faker.number().numberBetween(10, 50000));
                String type = faker.options().option(types);
                LocalDateTime timestamp = faker.date().past(365, java.util.concurrent.TimeUnit.DAYS).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
                String desc = faker.lorem().sentence(3).replace(",", "");
                long newBalance = balance.addAndGet(type.equals("CREDIT") || type.equals("DEPOSIT") ? amount.longValue() : -amount.longValue());
                String status = faker.options().option(statuses);
                writer.printf("%d,%d,%s,%s,%s,%s,%d,%s%n",
                        i, accountId, amount, type, timestamp.format(DATETIME_FMT), desc, newBalance, status);
            }
            System.out.println("Generated transactions_100k.csv with " + count + " rows");
        }
    }

    public void generateStudents(String filePath, long count) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.println("id,name,email,course,grade,gpa,enrollment_date,department,year");
            String[] courses = {"Mathematics", "Physics", "Chemistry", "Biology", "CS", "English", "History", "Economics"};
            String[] grades = {"A+", "A", "A-", "B+", "B", "B-", "C+", "C", "C-", "D", "F"};
            String[] departments = {"Science", "Arts", "Engineering", "Business", "Medicine", "Law"};
            for (long i = 1; i <= count; i++) {
                String name = faker.name().fullName().replace(",", "");
                String email = name.toLowerCase().replaceAll("[^a-z.]", "") + "@university.edu";
                String course = faker.options().option(courses);
                String grade = faker.options().option(grades);
                BigDecimal gpa = new BigDecimal(faker.number().randomDouble(2, 0, 4)).min(BigDecimal.valueOf(4.00));
                LocalDate enrollDate = faker.date().past(1460, java.util.concurrent.TimeUnit.DAYS).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
                String dept = faker.options().option(departments);
                int year = faker.number().numberBetween(1, 5);
                writer.printf("%d,%s,%s,%s,%s,%s,%s,%s,%d%n",
                        i, name, email, course, grade, gpa, enrollDate.format(DATE_FMT), dept, year);
            }
            System.out.println("Generated students_100k.csv with " + count + " rows");
        }
    }

    public void generateServerLogs(String filePath, long count) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.println("timestamp,ip,method,path,status,size,response_time,user_agent,referer");
            String[] methods = {"GET", "POST", "PUT", "DELETE", "PATCH"};
            int[] statuses = {200, 201, 204, 301, 400, 401, 403, 404, 500, 502, 503};
            String[] agents = {"Mozilla/5.0", "Chrome/120.0", "Safari/17.0", "Edge/120.0", "curl/8.0"};
            for (long i = 1; i <= count; i++) {
                LocalDateTime ts = faker.date().past(30, java.util.concurrent.TimeUnit.DAYS).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
                String ip = faker.internet().ipV4Address();
                String method = faker.options().option(methods);
                String path = "/" + faker.options().option("api", "web", "static", "admin") + "/" + faker.lorem().word();
                int status = faker.options().option(statuses);
                long size = faker.number().numberBetween(0, 50000);
                long rt = faker.number().numberBetween(1, 5000);
                String agent = faker.options().option(agents);
                String referer = faker.internet().url();
                writer.printf("%s,%s,%s,%s,%d,%d,%d,%s,%s%n",
                        ts.format(DATETIME_FMT), ip, method, path, status, size, rt, agent, referer);
            }
            System.out.println("Generated server_logs_100k.csv with " + count + " rows");
        }
    }

    public void generateWeatherData(String filePath, long count) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.println("id,city,temperature,humidity,wind_speed,pressure,recorded_at,condition");
            String[] cities = {"New York", "Los Angeles", "Chicago", "Houston", "Phoenix", "London", "Tokyo", "Sydney"};
            String[] conditions = {"Sunny", "Cloudy", "Rainy", "Stormy", "Snowy", "Foggy", "Windy", "Clear"};
            for (long i = 1; i <= count; i++) {
                String city = faker.options().option(cities);
                BigDecimal temp = new BigDecimal(faker.number().randomDouble(2, -10, 45));
                BigDecimal humidity = new BigDecimal(faker.number().randomDouble(2, 10, 100));
                BigDecimal windSpeed = new BigDecimal(faker.number().randomDouble(2, 0, 80));
                BigDecimal pressure = new BigDecimal(faker.number().randomDouble(2, 980, 1050));
                LocalDateTime recordedAt = faker.date().past(365, java.util.concurrent.TimeUnit.DAYS).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
                String condition = faker.options().option(conditions);
                writer.printf("%d,%s,%s,%s,%s,%s,%s,%s%n",
                        i, city, temp, humidity, windSpeed, pressure, recordedAt.format(DATETIME_FMT), condition);
            }
            System.out.println("Generated weather_data_100k.csv with " + count + " rows");
        }
    }
}
