package assignment1.src.test.java.com.assignment.producerconsumer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import src.main.java.com.assignment.producerconsumer.BlockingQueue;
import src.main.java.com.assignment.producerconsumer.Consumer;
import src.main.java.com.assignment.producerconsumer.Producer;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Integration tests for Producer and Consumer classes.
 * 
 * Tests the complete producer-consumer pattern with:
 * - Thread synchronization
 * - Concurrent programming
 * - Blocking queues
 * - Wait/Notify mechanism
 */
public class ProducerConsumerTest {
    
    @Test
    @Timeout(10)
    void testSingleProducerSingleConsumer() throws InterruptedException {
        BlockingQueue<String> queue = new BlockingQueue<>(3);
        List<String> source = new ArrayList<>(Arrays.asList("A", "B", "C"));
        List<String> destination = new ArrayList<>();
        
        Producer producer = new Producer("Producer-1", queue, source);
        Consumer consumer = new Consumer("Consumer-1", queue, destination, 3);
        
        Thread producerThread = new Thread(producer);
        Thread consumerThread = new Thread(consumer);
        
        consumerThread.start();
        Thread.sleep(100);
        producerThread.start();
        
        producerThread.join();
        consumerThread.join();
        
        assertEquals(3, destination.size());
        assertTrue(destination.containsAll(source));
        assertTrue(queue.isEmpty());
    }
    
    @Test
    @Timeout(15)
    void testMultipleProducersSingleConsumer() throws InterruptedException {
        BlockingQueue<String> queue = new BlockingQueue<>(5);
        List<String> destination = new ArrayList<>();
        
        List<String> source1 = new ArrayList<>(Arrays.asList("P1-A", "P1-B", "P1-C"));
        List<String> source2 = new ArrayList<>(Arrays.asList("P2-A", "P2-B", "P2-C"));
        
        Producer producer1 = new Producer("Producer-1", queue, source1);
        Producer producer2 = new Producer("Producer-2", queue, source2);
        Consumer consumer = new Consumer("Consumer-1", queue, destination, 6);
        
        Thread producerThread1 = new Thread(producer1);
        Thread producerThread2 = new Thread(producer2);
        Thread consumerThread = new Thread(consumer);
        
        consumerThread.start();
        producerThread1.start();
        producerThread2.start();
        
        producerThread1.join();
        producerThread2.join();
        consumerThread.join();
        
        assertEquals(6, destination.size());
        assertTrue(queue.isEmpty());
    }
    
    @Test
    @Timeout(15)
    void testSingleProducerMultipleConsumers() throws InterruptedException {
        BlockingQueue<String> queue = new BlockingQueue<>(5);
        List<String> destination = new ArrayList<>();
        List<String> source = new ArrayList<>(Arrays.asList("A", "B", "C", "D", "E", "F"));
        
        Producer producer = new Producer("Producer-1", queue, source);
        Consumer consumer1 = new Consumer("Consumer-1", queue, destination, 3);
        Consumer consumer2 = new Consumer("Consumer-2", queue, destination, 3);
        
        Thread producerThread = new Thread(producer);
        Thread consumerThread1 = new Thread(consumer1);
        Thread consumerThread2 = new Thread(consumer2);
        
        consumerThread1.start();
        consumerThread2.start();
        Thread.sleep(100);
        producerThread.start();
        
        producerThread.join();
        consumerThread1.join();
        consumerThread2.join();
        
        assertEquals(6, destination.size());
        assertTrue(destination.containsAll(source));
        assertTrue(queue.isEmpty());
    }
    
    @Test
    @Timeout(20)
    void testMultipleProducersMultipleConsumers() throws InterruptedException {
        BlockingQueue<String> queue = new BlockingQueue<>(5);
        List<String> destination = new ArrayList<>();
        
        List<String> source1 = new ArrayList<>(Arrays.asList("P1-A", "P1-B", "P1-C"));
        List<String> source2 = new ArrayList<>(Arrays.asList("P2-A", "P2-B", "P2-C"));
        List<String> source3 = new ArrayList<>(Arrays.asList("P3-A", "P3-B", "P3-C"));
        
        Producer producer1 = new Producer("Producer-1", queue, source1);
        Producer producer2 = new Producer("Producer-2", queue, source2);
        Producer producer3 = new Producer("Producer-3", queue, source3);
        
        Consumer consumer1 = new Consumer("Consumer-1", queue, destination, 5);
        Consumer consumer2 = new Consumer("Consumer-2", queue, destination, 4);
        
        Thread producerThread1 = new Thread(producer1);
        Thread producerThread2 = new Thread(producer2);
        Thread producerThread3 = new Thread(producer3);
        Thread consumerThread1 = new Thread(consumer1);
        Thread consumerThread2 = new Thread(consumer2);
        
        consumerThread1.start();
        consumerThread2.start();
        Thread.sleep(100);
        
        producerThread1.start();
        producerThread2.start();
        producerThread3.start();
        
        producerThread1.join();
        producerThread2.join();
        producerThread3.join();
        consumerThread1.join();
        consumerThread2.join();
        
        assertEquals(9, destination.size());
        assertTrue(queue.isEmpty());
    }
    
    @Test
    @Timeout(10)
    void testBlockingBehaviorWithFullQueue() throws InterruptedException {
        BlockingQueue<String> smallQueue = new BlockingQueue<>(2);
        List<String> destination = new ArrayList<>();
        
        List<String> source = new ArrayList<>(Arrays.asList("A", "B", "C", "D", "E"));
        Producer producer = new Producer("Producer-1", smallQueue, source);
        Consumer consumer = new Consumer("Consumer-1", smallQueue, destination, 5);
        
        Thread producerThread = new Thread(producer);
        Thread consumerThread = new Thread(consumer);
        
        producerThread.start();
        Thread.sleep(300);
        
        consumerThread.start();
        
        producerThread.join();
        consumerThread.join();
        
        assertEquals(5, destination.size());
        assertTrue(destination.containsAll(source));
        assertTrue(smallQueue.isEmpty());
    }
}
