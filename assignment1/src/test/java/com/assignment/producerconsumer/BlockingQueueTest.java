package com.assignment.producerconsumer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Comprehensive unit tests for BlockingQueue.
 * 
 * Tests cover all testing objectives:
 * - Thread synchronization
 * - Concurrent programming
 * - Blocking queues
 * - Wait/Notify mechanism
 */
public class BlockingQueueTest {
    private BlockingQueue<String> queue;
    private static final int CAPACITY = 5;
    
    @BeforeEach
    void setUp() {
        queue = new BlockingQueue<>(CAPACITY);
    }
    
    @Test
    void testQueueInitialization() {
        assertEquals(0, queue.size());
        assertTrue(queue.isEmpty());
        assertFalse(queue.isFull());
        assertEquals(CAPACITY, queue.getCapacity());
    }
    
    @Test
    void testInvalidCapacity() {
        assertThrows(IllegalArgumentException.class, () -> {
            new BlockingQueue<>(0);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new BlockingQueue<>(-1);
        });
    }
    
    @Test
    @Timeout(5)
    void testPutTakeBasicOperations() throws InterruptedException {
        queue.put("item1");
        queue.put("item2");
        assertEquals(2, queue.size());
        
        String item1 = queue.take();
        assertEquals("item1", item1);
        assertEquals(1, queue.size());
        
        String item2 = queue.take();
        assertEquals("item2", item2);
        assertEquals(0, queue.size());
        assertTrue(queue.isEmpty());
    }
    
    @Test
    @Timeout(5)
    void testQueueBecomesFull() throws InterruptedException {
        for (int i = 0; i < CAPACITY; i++) {
            queue.put("item" + i);
        }
        
        assertEquals(CAPACITY, queue.size());
        assertTrue(queue.isFull());
        assertFalse(queue.isEmpty());
    }
    
    @Test
    @Timeout(10)
    void testProducerBlocksWhenQueueIsFull() throws InterruptedException {
        for (int i = 0; i < CAPACITY; i++) {
            queue.put("item" + i);
        }
        
        assertTrue(queue.isFull());
        
        CountDownLatch putStarted = new CountDownLatch(1);
        CountDownLatch putCompleted = new CountDownLatch(1);
        AtomicInteger putResult = new AtomicInteger(0);
        
        Thread producerThread = new Thread(() -> {
            try {
                putStarted.countDown();
                queue.put("blocked-item");
                putResult.set(1);
                putCompleted.countDown();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        
        producerThread.start();
        putStarted.await();
        
        Thread.sleep(500);
        
        assertEquals(0, putResult.get());
        assertEquals(CAPACITY, queue.size());
        
        String item = queue.take();
        assertEquals("item0", item);
        
        putCompleted.await();
        assertEquals(1, putResult.get());
        assertEquals(CAPACITY, queue.size());
        
        producerThread.join();
    }
    
    @Test
    @Timeout(10)
    void testConsumerBlocksWhenQueueIsEmpty() throws InterruptedException {
        CountDownLatch takeStarted = new CountDownLatch(1);
        CountDownLatch takeCompleted = new CountDownLatch(1);
        AtomicInteger takeResult = new AtomicInteger(0);
        List<String> consumedItems = new ArrayList<>();
        
        Thread consumerThread = new Thread(() -> {
            try {
                takeStarted.countDown();
                String item = queue.take();
                consumedItems.add(item);
                takeResult.set(1);
                takeCompleted.countDown();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        
        consumerThread.start();
        takeStarted.await();
        
        Thread.sleep(500);
        
        assertEquals(0, takeResult.get());
        assertTrue(consumedItems.isEmpty());
        assertTrue(queue.isEmpty());
        
        queue.put("new-item");
        
        takeCompleted.await();
        assertEquals(1, takeResult.get());
        assertEquals(1, consumedItems.size());
        assertEquals("new-item", consumedItems.get(0));
        assertTrue(queue.isEmpty());
        
        consumerThread.join();
    }
    
    @Test
    @Timeout(10)
    void testMultipleProducers() throws InterruptedException {
        int numProducers = 3;
        int itemsPerProducer = 10;
        int totalItems = numProducers * itemsPerProducer;
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch completionLatch = new CountDownLatch(numProducers);
        AtomicInteger totalProduced = new AtomicInteger(0);
        
        // Start consumer thread to prevent queue from filling up
        Thread consumer = new Thread(() -> {
            try {
                for (int i = 0; i < totalItems; i++) {
                    queue.take();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        consumer.start();
        
        for (int i = 0; i < numProducers; i++) {
            final int producerId = i;
            new Thread(() -> {
                try {
                    startLatch.await();
                    
                    for (int j = 0; j < itemsPerProducer; j++) {
                        queue.put("Producer-" + producerId + "-Item-" + j);
                        totalProduced.incrementAndGet();
                    }
                    
                    completionLatch.countDown();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        }
        
        startLatch.countDown();
        completionLatch.await();
        consumer.join();
        
        assertEquals(totalItems, totalProduced.get());
    }
    
    @Test
    @Timeout(10)
    void testMultipleConsumers() throws InterruptedException {
        int numConsumers = 3;
        int totalItems = 15;
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch completionLatch = new CountDownLatch(numConsumers);
        AtomicInteger totalConsumed = new AtomicInteger(0);
        List<String> consumedItems = new ArrayList<>();
        Object lock = new Object();
        
        // Start consumer threads first
        for (int i = 0; i < numConsumers; i++) {
            new Thread(() -> {
                try {
                    startLatch.await();
                    
                    int itemsConsumed = 0;
                    while (itemsConsumed < (totalItems / numConsumers)) {
                        String item = queue.take();
                        synchronized (lock) {
                            consumedItems.add(item);
                        }
                        totalConsumed.incrementAndGet();
                        itemsConsumed++;
                    }
                    
                    completionLatch.countDown();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        }
        
        startLatch.countDown();
        
        // Produce items after consumers are waiting
        for (int i = 0; i < totalItems; i++) {
            queue.put("Item-" + i);
        }
        
        completionLatch.await();
        
        assertEquals(totalItems, consumedItems.size());
        assertEquals(totalItems, totalConsumed.get());
    }
    
    @Test
    @Timeout(15)
    void testProducerConsumerConcurrency() throws InterruptedException {
        int numProducers = 2;
        int numConsumers = 2;
        int itemsPerProducer = 10;
        int totalItems = numProducers * itemsPerProducer;
        
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch producersDone = new CountDownLatch(numProducers);
        CountDownLatch consumersDone = new CountDownLatch(numConsumers);
        
        AtomicInteger produced = new AtomicInteger(0);
        AtomicInteger consumed = new AtomicInteger(0);
        List<String> consumedItems = new ArrayList<>();
        Object lock = new Object();
        
        for (int i = 0; i < numProducers; i++) {
            final int producerId = i;
            new Thread(() -> {
                try {
                    startLatch.await();
                    
                    for (int j = 0; j < itemsPerProducer; j++) {
                        queue.put("P" + producerId + "-I" + j);
                        produced.incrementAndGet();
                    }
                    
                    producersDone.countDown();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        }
        
        for (int i = 0; i < numConsumers; i++) {
            final int itemsPerConsumer = totalItems / numConsumers;
            new Thread(() -> {
                try {
                    startLatch.await();
                    
                    for (int j = 0; j < itemsPerConsumer; j++) {
                        String item = queue.take();
                        synchronized (lock) {
                            consumedItems.add(item);
                        }
                        consumed.incrementAndGet();
                    }
                    
                    consumersDone.countDown();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        }
        
        startLatch.countDown();
        
        producersDone.await();
        consumersDone.await();
        
        assertEquals(totalItems, produced.get());
        assertEquals(totalItems, consumed.get());
        assertEquals(totalItems, consumedItems.size());
        assertTrue(queue.isEmpty());
    }
    
    @Test
    @Timeout(10)
    void testThreadSafety() throws InterruptedException {
        int numThreads = 10;
        int operationsPerThread = 100;
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch completionLatch = new CountDownLatch(numThreads);
        AtomicInteger operationsCompleted = new AtomicInteger(0);
        
        for (int i = 0; i < numThreads; i++) {
            final boolean isProducer = (i % 2 == 0);
            new Thread(() -> {
                try {
                    startLatch.await();
                    
                    for (int j = 0; j < operationsPerThread; j++) {
                        if (isProducer) {
                            queue.put("Item-" + Thread.currentThread().getId() + "-" + j);
                        } else {
                            try {
                                queue.take();
                            } catch (InterruptedException e) {
                                break;
                            }
                        }
                        operationsCompleted.incrementAndGet();
                    }
                    
                    completionLatch.countDown();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        }
        
        startLatch.countDown();
        completionLatch.await();
        
        assertEquals(numThreads * operationsPerThread, operationsCompleted.get());
    }
    
    @Test
    @Timeout(5)
    void testCapacityOne() throws InterruptedException {
        BlockingQueue<String> singleQueue = new BlockingQueue<>(1);
        
        singleQueue.put("item");
        assertTrue(singleQueue.isFull());
        
        String item = singleQueue.take();
        assertEquals("item", item);
        assertTrue(singleQueue.isEmpty());
    }
    
    @Test
    @Timeout(10)
    void testInterruptedWait() throws InterruptedException {
        for (int i = 0; i < CAPACITY; i++) {
            queue.put("item" + i);
        }
        
        Thread producerThread = new Thread(() -> {
            try {
                queue.put("blocked");
                fail("Should have been interrupted");
            } catch (InterruptedException e) {
                assertTrue(Thread.currentThread().isInterrupted());
            }
        });
        
        producerThread.start();
        Thread.sleep(200);
        producerThread.interrupt();
        producerThread.join(1000);
        
        assertFalse(producerThread.isAlive());
    }

    @Test
    void testNullItemNotAllowed() {
        assertThrows(NullPointerException.class, () -> {
            queue.put(null);
        });
    }

    @Test
    void testQueueOperationsAfterInterruption() throws InterruptedException {
        queue.put("item1");
        
        Thread producerThread = new Thread(() -> {
            try {
                for (int i = 0; i < CAPACITY; i++) {
                    queue.put("item" + i);
                }
                queue.put("should block");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        
        producerThread.start();
        Thread.sleep(100);
        producerThread.interrupt();
        producerThread.join();
        
        String item = queue.take();
        assertEquals("item1", item);
    }

    @Test
    void testEmptyQueueOperations() {
        assertTrue(queue.isEmpty());
        assertFalse(queue.isFull());
        assertEquals(0, queue.size());
        assertEquals(CAPACITY, queue.getCapacity());
    }
}
