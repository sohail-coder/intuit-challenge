# Intuit Take-Home Assignments

This repository contains two Java programming assignments demonstrating core software engineering concepts.

## Assignment 1: Producer-Consumer Pattern

**Short Description:** Implement producer-consumer pattern with thread synchronization

Demonstrates thread synchronization, concurrent programming, blocking queues, and wait/notify mechanisms through a custom BlockingQueue implementation.

**Key Features:**
- Custom thread-safe blocking queue
- Multiple producer and consumer threads
- Proper synchronization using wait/notify
- Comprehensive test coverage

[View Assignment 1 →](assignment1/)

---

## Assignment 2: Sales Data Analysis

**Short Description:** Perform data analysis using appropriate API on CSV data

Demonstrates functional programming, stream operations, data aggregation, and lambda expressions through sales data analysis.

**Key Features:**
- Apache Commons CSV API for data parsing
- 10 analytical methods using Java Streams
- Functional programming principles
- Immutable data structures

[View Assignment 2 →](assignment2/)

---

## Quick Start

### Assignment 1
```bash
cd assignment1
./run-demo.sh    # Run producer-consumer demo
./run-tests.sh   # Run all tests
```

### Assignment 2
```bash
cd assignment2
./run-demo.sh    # Run sales analysis
./run-tests.sh   # Run all tests
```

## Requirements

- Java 11+ (Assignment 1) / Java 17+ (Assignment 2)
- Maven 3.6+ (auto-installed by scripts on macOS)

## Test Results

**Assignment 1:** 20 tests passing  
**Assignment 2:** 19 tests passing  
**Total:** All 39 tests passing with 100% success rate

