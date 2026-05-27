# FakeStore API Test Automation Framework

## Overview

A comprehensive REST API test automation framework designed for the [FakeStore API](https://fakestoreapi.com), implementing complete CRUD operations testing across Products, Carts, and Users/Authentication endpoints. Built using Java 17, REST Assured, and TestNG with data-driven testing capabilities through Apache POI Excel integration.

## Technical Architecture

### Core Technologies

- **Java 17** - Base programming language with modern language features
- **REST Assured 5.4.0** - HTTP client for RESTful API testing
- **TestNG 7.7.1** - Testing framework with parallel execution support
- **Apache POI 5.2.5** - Excel-based test data management
- **ExtentReports 5.1.1** - Interactive HTML test reporting
- **Maven** - Dependency management and build automation
- **Jackson 2.17.1** - JSON processing and data binding

### Framework Design Patterns

**Page Factory Pattern**: Encapsulates API endpoints and operations into reusable classes (`Product.java`, `Cart.java`, `Login.java`) extending a common base class with shared HTTP request handling.

**Data-Driven Testing**: Test data externalized to Excel spreadsheets (`combinedTestData.xlsx`) with 15 separate sheets, enabling parameterized test execution without code changes.

**Centralized Utilities**: Modular utility classes for configuration management (`ConfigReader`), logging (`LoggerUtil`), Excel data reading (`ExcelUtils`), and JSON manipulation (`JsonUtils`).

## Project Structure

```
fakestore-automation/
├── src/main/
│   ├── java/com/fakestoreapi/automation/
│   │   ├── pagefactory/          # API endpoint abstractions
│   │   │   ├── RestCommonDefs.java
│   │   │   ├── Product.java
│   │   │   ├── Cart.java
│   │   │   └── Login.java
│   │   ├── test/                 # TestNG test classes
│   │   │   ├── ProductTests.java
│   │   │   ├── CartTests.java
│   │   │   └── LoginTests.java
│   │   └── utils/                # Utility classes
│   │       ├── ConfigReader.java
│   │       ├── ExcelUtils.java
│   │       ├── JsonUtils.java
│   │       ├── LoggerUtil.java
│   │       ├── ExtentManager.java
│   │       └── TestListener.java
│   └── resources/
│       ├── config.properties     # Environment configuration
│       └── testdata/
│           └── combinedTestData.xlsx  # Test data repository
├── testng.xml                    # TestNG suite configuration
└── pom.xml                       # Maven dependencies
```

## Test Coverage

The framework implements **46 automated test cases** across **15 test suites**:

### Product API Tests (16 tests)
- **Add New Product** (5 tests) - Validates product creation with various payloads
- **Update Product** (3 tests) - Verifies product modification operations
- **Delete Product** (4 tests) - Tests product deletion including edge cases
- **Get Single Product** (3 tests) - Validates individual product retrieval with attribute verification
- **Get All Products** (1 test) - Confirms bulk product listing with complete attribute validation

### Cart API Tests (17 tests)
- **Add New Cart** (5 tests) - Tests cart creation with product arrays
- **Update Cart** (4 tests) - Validates cart modification operations
- **Delete Cart** (3 tests) - Verifies cart deletion functionality
- **Get Single Cart** (5 tests) - Retrieves individual carts with nested product validation
- **Get All Carts** (1 test) - Tests bulk cart retrieval with JSON array parsing

### User/Login API Tests (13 tests)
- **Add New User** (2 tests) - User registration validation
- **Delete User** (3 tests) - User deletion operations
- **Login** (1 test) - Authentication token generation
- **Get Single User** (5 tests) - Individual user retrieval with nested object validation
- **Get All Users** (1 test) - Bulk user listing with attribute verification

## Key Features

### Comprehensive Validation
- HTTP status code verification (200, 201, 404)
- JSON structure validation
- Response attribute presence checks
- ID matching for GET operations
- Nested object validation (address, geolocation, name)
- Array parsing with brace-depth tracking algorithm

### Advanced Capabilities
- Parallel test execution support
- Dynamic test data filtering from Excel
- Detailed logging with SLF4J and Logback
- ExtentReports with test parameters and execution metrics
- Request/Response payload logging
- Custom timeout configuration (5 minutes default)

### Robust Error Handling
- Null cell handling in Excel data
- Empty array validation
- DataProvider failure protection
- ISO 8601 date format validation
- MongoDB metadata field detection

## Prerequisites

- **Java Development Kit (JDK) 17** or higher
- **Apache Maven 3.8+** for dependency resolution
- **Git** for version control

## Installation & Setup

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd fakestore-automation
   ```

2. **Install dependencies**
   ```bash
   mvn clean install
   ```

3. **Configure environment** (Optional)
   
   Edit `src/main/resources/config.properties`:
   ```properties
   base.url=https://fakestoreapi.com
   timeout=300000
   ```

## Execution

### Run All Tests
```bash
mvn clean test
```

### Run Specific Suite
```bash
mvn clean test -DsuiteXmlFile=testng.xml
```

### Run via TestNG XML
```bash
mvn test -Dsurefire.suiteXmlFiles=testng.xml
```

### Run Individual Test Class
```bash
mvn test -Dtest=ProductTests
```

## Test Data Management

Test data is managed in `src/main/resources/testdata/combinedTestData.xlsx` with the following sheets:

| Sheet Name | Purpose | Test Count |
|------------|---------|------------|
| AddNewProduct | Product creation scenarios | 5 |
| UpdateProduct | Product update scenarios | 3 |
| DeleteProduct | Product deletion scenarios | 4 |
| GetSingleProduct | Individual product retrieval | 3 |
| AddNewCart | Cart creation scenarios | 5 |
| UpdateCart | Cart modification scenarios | 4 |
| DeleteCart | Cart deletion scenarios | 3 |
| GetSingleCart | Individual cart retrieval | 5 |
| AddNewUser | User registration scenarios | 2 |
| DeleteUser | User deletion scenarios | 3 |
| Login | Authentication scenarios | 1 |
| GetSingleUser | Individual user retrieval | 5 |

**Note**: Each Excel sheet maps to a TestNG `@DataProvider`, enabling seamless test parameterization.

## Reporting

### ExtentReports
After test execution, open the HTML report:
```
test-output/ExtentReport.html
```

Features:
- Dashboard with pass/fail statistics
- Test execution timeline
- Request/response payload capture
- Screenshot on failure capability
- Environment information

### TestNG Reports
Standard TestNG reports available at:
```
test-output/emailable-report.html
test-output/index.html
```

### Logs
Detailed execution logs stored in:
```
src/logs/FakeStore<TestClass>_<TestMethod>.log
```

## Configuration

### TestNG XML Structure
The `testng.xml` defines 15 test suites with parameters:
- `testData`: Excel file path and sheet name
- `testDataSets`: Filter for specific test cases ("ALL" or "TC1,TC2,...")
- `loggerFileName`: Custom log file names per test

### Config Properties
Key configurations in `config.properties`:
- `base.url`: FakeStore API base URL
- `timeout`: HTTP request timeout in milliseconds

## Best Practices Implemented

1. **Separation of Concerns**: Page Factory pattern isolates API logic from test logic
2. **Reusability**: Common HTTP operations abstracted in `RestCommonDefs`
3. **Maintainability**: External test data eliminates hardcoded values
4. **Scalability**: Modular design allows easy addition of new endpoints
5. **Observability**: Comprehensive logging at DEBUG and INFO levels
6. **Reliability**: Explicit validation with pass/fail markers (✓/✗)

## API Endpoints Tested

| Endpoint | Methods | Coverage |
|----------|---------|----------|
| `/products` | GET, POST, PUT, DELETE | Complete CRUD |
| `/products/{id}` | GET, PUT, DELETE | Individual operations |
| `/carts` | GET, POST, PUT, DELETE | Complete CRUD |
| `/carts/{id}` | GET, PUT, DELETE | Individual operations |
| `/users` | GET, POST, DELETE | User management |
| `/users/{id}` | GET, DELETE | Individual operations |
| `/auth/login` | POST | Authentication |

## Validation Strategy

Each test performs multi-layered validation:
1. **HTTP Status Code** - Verifies expected response codes
2. **Response Structure** - Validates JSON format and structure
3. **Attribute Presence** - Confirms required fields exist
4. **Data Integrity** - Checks ID matching, value correctness
5. **Edge Cases** - Tests empty arrays, null values, nested objects

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/new-endpoint`)
3. Commit changes (`git commit -m 'Add new endpoint tests'`)
4. Push to branch (`git push origin feature/new-endpoint`)
5. Open a Pull Request

## License

This project is open-source and available for educational and testing purposes.

## Author

Developed as a comprehensive REST API testing solution demonstrating industry-standard automation practices with Java and REST Assured.

---

**Framework Version**: 0.0.1-SNAPSHOT  
**Last Updated**: May 2026  
**Status**: Production Ready ✅
