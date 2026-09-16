# Spring Batch Management API

A Spring Boot application demonstrating **20+ Spring Batch features** via REST API. Manage, launch, and monitor batch jobs through HTTP endpoints.

## Tech Stack
- **Spring Boot 4.1.1** with Java 25
- **Spring Batch** for batch processing
- **PostgreSQL** (H2 for tests)
- **Maven** build tool
- Actuator endpoints for monitoring

## Getting Started

### Prerequisites
- Java 25 JDK
- Maven 3.6+
- PostgreSQL (or H2 for tests)

### Run the Application
```bash
cd spring-batch
./mvnw spring-boot:run
```

### Access Endpoints
- **API Base:** `http://localhost:8080/api/batch/`
- **Actuator:** `http://localhost:8080/actuator/`

### Test Endpoints Summary
| Endpoint | Method | Description |
|----------|--------|-------------|
| `/launch` | POST | Launch a job synchronously |
| `/launch-async` | POST | Launch a job asynchronously |
| `/launch-all` | POST | Launch all 12 jobs |
| `/concurrent-test?threadCount=5` | POST | Concurrent launch test |
| `/status/{id}` | GET | Get job execution status |
| `/admin/jobs` | GET | List registered jobs |
| `/admin/jobs/{id}` | GET | Get execution details |
| `/admin/stop/{id}` | POST | Stop a running job |

## Spring Batch Concepts

### 1. Job Flow Patterns
- **Sequential Flow:** Steps run one after another (`start().next().next()`)
- **Conditional Flow:** Steps branch based on exit status (`on("COMPLETED").to(...)`)
- **Decider Flow:** `JobExecutionDecider` routes based on time of day
- **Split Flow:** Parallel step execution via `.split()` with thread pool
- **Job Chaining:** Cross-job data via `JobExecution.getExecutionContext()`

### 2. Chunk-Oriented Processing
```
Reader → Processor → Writer
```
- `FlatFileItemReader` reads CSV files
- `JpaItemWriter` writes to database
- Chunk size controls batch commits (e.g., chunk(100))

### 3. Item Readers
- **FlatFile Reader:** Reads CSV with delimited names and FieldSetMapper
- **JDBC Paging Reader:** `JdbcPagingItemReader` with paging and sorting
- **Multi-file Reader:** Factory pattern for different input files

### 4. Item Processors
- **Enrichment:** Add business data (tax, discounts)
- **Filtering:** Return `null` to skip items
- **Validation:** Throw exceptions for invalid data
- **Composite:** Chain multiple processors
- **Classifier:** Route items to different processors based on content type

### 5. Item Writers
- **JPA Writer:** Writes entities to database
- **JSON Writer:** Writes to `output/output.json`
- **Lambda Writers:** Inline anonymous writers

### 6. Fault Tolerance
- **Skip Policy:** `.skipLimit(50).skip(SkippableException.class)`
- **Retry Policy:** `.retryLimit(3).retry(RetryableException.class)`
- **Combined:** Both skip and retry in same job
- **Listeners:** `CustomSkipListener`, `CustomRetryListener` track events

### 7. Scalability
- **Multi-threaded Chunks:** `.taskExecutor()` with ThreadPoolTaskExecutor (core=5, max=10)
- **Partitioning:** Master-worker architecture with `SimplePartitioner`
- **TaskExecutorPartitionHandler:** Controls grid size (5 workers, 4 file workers)
- **Grid Size:** Determines number of parallel worker instances

### 8. Scoping
- **`@StepScope`:** Beans created per step execution (lazy init)
  - Example: Read from `jobParameters['inputSource']`
- **`@JobScope`:** Beans created per job execution
  - Example: Track total items written across job lifetime
- **SpEL Expressions:** `#{jobParameters}`, `#{jobExecutionContext}`, `#{stepExecutionContext}`

### 9. REST API Endpoints

#### BatchJobController (8 endpoints)
- `POST /launch` - Launch job synchronously
- `POST /launch-async` - Launch job asynchronously  
- `POST /launch-all` - Launch all 12 jobs
- `POST /concurrent-test?threadCount=N` - Concurrent launch test
- `GET /status/{executionId}` - Get execution status
- `GET /jobs` - List registered job names
- `GET /history` - Get launch history
- `GET /stats` - Get request statistics

#### FaultToleranceController (3 endpoints)
- `POST /fault-tolerance/skip` - Launch skip job
- `POST /fault-tolerance/retry` - Launch retry job
- `POST /fault-tolerance/skip-retry` - Launch combined job
- `GET /fault-tolerance/jobs` - List fault-tolerance jobs

#### ScalabilityController (3 endpoints)
- `POST /scalability/multi-thread` - Launch multi-threaded job
- `POST /scalability/partition` - Launch partitioned job
- `POST /scalability/file-partition` - Launch file partition job
- `GET /scalability/jobs` - List scalability jobs

#### AdminController (4 endpoints)
- `GET /admin/jobs` - List jobs with count
- `GET /admin/jobs/{executionId}` - Get execution details
- `POST /admin/stop/{executionId}` - Stop running job
- `GET /admin/stats` - Get total execution statistics

## Database Tables
Spring Batch auto-creates metadata tables:
- `batch_job_instance`, `batch_job_execution`, `batch_job_execution_params`
- `batch_step_execution`, `batch_step_execution_params`
- `batch_job_seq`, `batch_step_seq`, `batch_exec_seq`

Plus 8 entity tables: Employee, Product, Customer, Order, Transaction, Student, ServerLog, Weather

## CSV Data Generation
The app includes a `CsvFileGenerator` that generates 100,000-row CSV files for all 8 entity types using DataFaker. Files are in `Csv_Files/` directory.

Generated files:
- `employees_100k.csv`, `customers_100k.csv`, `products_100k.csv`
- `orders_100k.csv`, `transactions_100k.csv`, `students_100k.csv`
- `server_logs_100k.csv`, `weather_data_100k.csv`

## Project Structure
```
src/main/java/com/sawmik/spring_batch/
├── config/         # BatchConfig, AsyncConfig, FaultToleranceConfig, ScalabilityConfig
├── entity/         # JPA entities (8 tables)
├── flow/           # Job configurations (Sequential, Conditional, Decider, Split, Chained)
├── listener/       # 11+ listener classes
├── model/          # DTOs
├── processor/      # 6 item processors
├── reader/         # Item readers (FlatFile, JDBC Paging, Multi-file)
├── scope/          # @StepScope, @JobScope config
├── scheduler/      # BatchScheduler with @Scheduled
├── tasklet/        # 3 tasklet implementations
├── util/           # CsvFileGenerator
├── writer/         # JSON file writer
├── exception/      # SkippableException, RetryableException
├── controller/     # 4 REST controllers
└── service/        # BatchAdminService
```

## License
MIT License - see LICENSE file

---

## Spring Batch Diagram Examples (Text Format)

### Sequential Job Flow
```
Start Job → Step 1: Count Files → Step 2: Process Data → Step 3: Cleanup → End Job
```

### Conditional Flow with Exit Status
```
Start → Read Products → {Exit Status: COMPLETED → Enrich High Value,
                           FAILED → Enrich Low Value} → End
```

### JobExecutionDecider (Time-Based Routing)
```
Job Start → TimeOfDayDecider → {Hour < 12 → Morning Step,
                              Hour 12-18 → Afternoon Step,
                              Hour ≥ 18 → Evening Step} → End
```

### Split Flow (Parallel Steps)
```
Start → Parallel Step 1 → {Split to Steps 2&3} → Parallel Step 2 & 3 → End
```

### Fault Tolerance: Skip Policy
```
Item Read → {Is Item Skippable? Yes → Skip Item, No → Process Item} → Write Item → Continue Chunk
```

### Fault Tolerance: Retry Policy
```
Item Read → {Is Item Retryable? Yes → Retry Item (max 3 attempts), 
              No → Process Item} → Write Item
```

### Multi-threaded Chunk Processing
```
Chunk Start → [Thread 1: Read/Process/Write] → [Thread 2: Read/Process/Write] → 
[Thread 3: Read/Process/Write] → Chunk Complete
```

### Partitioning (Master-Worker)
```
Master Step → SimplePartitioner → Grid Size: 5 Workers → 
[Worker Step 1 through 5] → End
```

### SpEL @StepScope
```
Step Execution → ItemReader created per step using jobParameters → 
Read items (Item-1, Item-2, Item-3...) → Step Completes
```

### SpEL @JobScope
```
Job Execution → Job-scoped Bean → Total Items Written Counter → 
Log Written Count → Job Completes
```
