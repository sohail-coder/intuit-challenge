# Sales Data Analysis

A Java application demonstrating functional programming, stream operations, data aggregation, and lambda expressions through sales data analysis.

## Testing Objectives Covered

- Functional programming
- Stream operations
- Data aggregation
- Lambda expressions

## Project Structure

```
assignment2/
├── src/main/java/com/example/sales/
│   ├── Main.java                   # Main application
│   ├── analysis/
│   │   └── SalesAnalyzer.java      # 10 analysis methods
│   ├── csv/
│   │   └── SaleRecordLoader.java   # CSV loader
│   └── model/
│       └── SaleRecord.java         # Immutable data model
├── src/test/java/com/example/sales/
│   └── analysis/
│       └── SalesAnalyzerTest.java  # 19 unit tests
├── data/sales_data.csv             # Sales data (43 records)
├── run-demo.sh                     # Execute demo
├── run-tests.sh                    # Execute tests
└── pom.xml                         # Maven configuration
```

## Quick Start

**Run the demo:**

```bash
./run-demo.sh
```

**Run tests:**

```bash
./run-tests.sh
```

**Or use Maven directly:**

```bash
mvn clean compile
mvn exec:java
mvn test
```

## How Testing Objectives Are Covered

### 1. Functional Programming

All analysis methods are pure functions with no side effects. SaleRecord is immutable with final fields. Methods always return new data structures.

```java
public Map<String, BigDecimal> totalRevenueByRegion(List<SaleRecord> records) {
    return records.stream()
            .filter(r -> "Completed".equalsIgnoreCase(r.status()))
            .collect(Collectors.groupingBy(...));
}
```

### 2. Stream Operations

Multiple stream operations used: filter(), map(), reduce(), collect(), sorted(), limit(), flatMap(), forEach()

```java
records.stream()
        .filter(...)      // Intermediate
        .map(...)         // Intermediate
        .sorted(...)      // Intermediate
        .limit(5)         // Intermediate
        .collect(...);    // Terminal
```

### 3. Data Aggregation

Advanced collectors: groupingBy(), counting(), summingInt(), averagingDouble(), reducing(), collectingAndThen(), mapping()

```java
.collect(Collectors.groupingBy(
    SaleRecord::region,
    Collectors.mapping(this::netRevenue,
        Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
));
```

### 4. Lambda Expressions

Method references, lambda expressions, comparators, and function composition throughout:

```java
SaleRecord::region                              // Method reference
r -> "Completed".equals(r.status())             // Lambda
Comparator.comparing(ProductRevenue::revenue)   // Comparator
this::netRevenue                                // Function composition
```

## Analysis Methods (All 10 Printed to Console)

| Method                    | Description          | Key Operations       |
| ------------------------- | -------------------- | -------------------- |
| totalRevenueByRegion      | Revenue by region    | groupingBy, reducing |
| topProductsByRevenue      | Top N products       | sorted, limit        |
| salesCountByCategory      | Sales per category   | counting             |
| averageOrderValueByRegion | AOV by region        | averagingDouble      |
| monthlyRevenueTrend       | Time-series analysis | TreeMap, groupingBy  |
| topCustomersBySpending    | Top customers        | sorted, limit        |
| paymentMethodDistribution | Payment stats        | counting             |
| discountImpactByCategory  | Discount analysis    | Custom collectors    |
| revenueByCategory         | Category revenue     | groupingBy, reducing |
| totalUnitsSoldByRegion    | Units per region     | summingInt           |

## Implementation Highlights

- Immutable data model (SaleRecord with Builder pattern)
- Pure functions throughout (no side effects)
- Record types for results (ProductRevenue, CustomerSpending, DiscountStats)
- Stream pipelines for all analyses
- Proper error handling (invalid CSV rows logged and skipped)
- BigDecimal for monetary precision

## Test Coverage

**SalesAnalyzerTest (19 tests)**

- All 10 analysis methods tested
- Edge cases: empty list, single record, zero limit, 100% discount, zero price, large quantities
- Filtering logic verification
- Aggregation accuracy
- Sorting and limiting
- Statistical calculations

**Results:**

```
Tests run: 19, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

## Console Output

The application prints all 10 analyses with formatted data:

```
SALES DATA ANALYSIS - Functional Programming Demo

✓ Loaded 43 sales records from CSV

1. Total Revenue by Region
   North                $   65,074.13
   South                $   26,713.73
   ...

[All 10 analyses displayed with formatted output]

Analysis Complete - All Objectives Demonstrated

Testing Objectives Covered:
  ✓ Functional Programming
  ✓ Stream Operations
  ✓ Data Aggregation
  ✓ Lambda Expressions
```

## Technical Details

**Immutable Design:** All data objects are immutable by design, ensuring thread safety and predictable behavior.

**Stream Pipelines:** Each analysis is a clear, declarative stream pipeline that's easy to understand and maintain.

**Separation of Concerns:** CSV loading is isolated from analysis logic. Domain model is independent.

**Error Handling:** Invalid CSV rows are logged and skipped gracefully rather than failing the entire load.

## Dependencies

- Apache Commons CSV (1.11.0) - CSV parsing
- JUnit Jupiter (5.10.2) - Testing
- AssertJ (3.25.3) - Fluent assertions

## Requirements

- Java 17 or higher
- Maven 3.8+ (auto-installed by scripts on macOS)

---

This implementation demonstrates production-quality functional programming with comprehensive stream operations, proper data aggregation, and extensive use of lambda expressions.
