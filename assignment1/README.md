# Producer-Consumer Pattern Implementation

A Java implementation of the Producer-Consumer pattern demonstrating thread synchronization, concurrent programming, blocking queues, and wait/notify mechanisms.

## Testing Objectives Covered

- Thread synchronization
- Concurrent programming
- Blocking queues
- Wait/Notify mechanism

## Project Structure

```
assignment1/
├── src/main/java/com/assignment/producerconsumer/
│   ├── BlockingQueue.java          # Custom thread-safe bounded queue
│   ├── Producer.java               # Producer thread implementation
│   ├── Consumer.java               # Consumer thread implementation
│   └── ProducerConsumerDemo.java   # Main application
├── src/test/java/com/assignment/producerconsumer/
│   ├── BlockingQueueTest.java      # Unit tests (15 tests)
│   └── ProducerConsumerTest.java   # Integration tests (5 tests)
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
mvn exec:java -Dexec.mainClass="com.assignment.producerconsumer.ProducerConsumerDemo"
mvn test
```

## How Testing Objectives Are Covered

### 1. Thread Synchronization

All shared queue access is synchronized using Java's `synchronized` keyword. Monitor locks ensure only one thread executes critical sections at a time.

```java
public synchronized void put(T item) throws InterruptedException {
    while (queue.size() == capacity) {
        wait();
    }
    queue.offer(item);
    notifyAll();
}
```

### 2. Concurrent Programming

Multiple producer and consumer threads run simultaneously, sharing a BlockingQueue. The demo creates 3 producers and 2 consumers that execute concurrently.

### 3. Blocking Queues

Custom BlockingQueue implementation (not using java.util.concurrent) with bounded capacity. Producers block when queue is full, consumers block when empty.

```java
// Blocks producers when full
while (queue.size() == capacity) {
    wait();
}

// Blocks consumers when empty
while (queue.isEmpty()) {
    wait();
}
```

### 4. Wait/Notify Mechanism

Uses Java's `wait()` to block threads and `notifyAll()` to wake waiting threads. Proper while-loop guards prevent spurious wakeups.

```java
wait();      // Release lock and enter wait state
notifyAll(); // Wake all waiting threads
```

Console output shows wait/notify activity with "[NOTIFY]" messages.

## Implementation Highlights

- Custom blocking queue built from scratch
- Proper synchronization preventing race conditions
- Wait/notify for thread coordination
- notifyAll() instead of notify() to prevent deadlocks
- While loops (not if) to handle spurious wakeups
- Null item validation
- Thread interruption handling

## Test Coverage

**BlockingQueueTest (15 tests)**

- Queue initialization and capacity
- Put/take operations
- Blocking behavior (full/empty)
- Wait/notify mechanism
- Multiple producers/consumers
- Thread safety
- Edge cases (capacity 1, null items, interruption)

**ProducerConsumerTest (5 tests)**

- Single/multiple producer scenarios
- Single/multiple consumer scenarios
- End-to-end integration
- Complete pattern verification

**Results:**

```
Tests run: 20, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

## Console Output

The demo prints:

- Configuration (queue capacity, thread counts)
- Producer/consumer actions with queue sizes
- Wait messages when threads block
- [NOTIFY] messages when threads are awakened
- Final results showing all items transferred
- Success verification

## Technical Details

**Why synchronized methods?** Simpler than synchronized blocks when the entire method is the critical section.

**Why while loops?** Protects against spurious wakeups by re-checking conditions after wait() returns.

**Why notifyAll()?** Wakes all threads to prevent deadlock scenarios where notify() might wake the wrong thread type.

**Why custom implementation?** Demonstrates understanding of thread synchronization primitives rather than using built-in concurrent utilities.

## Requirements

- Java 11 or higher
- Maven 3.6+ (auto-installed by scripts on macOS)

---

This implementation demonstrates production-quality concurrent programming with comprehensive test coverage and proper thread synchronization mechanisms.
