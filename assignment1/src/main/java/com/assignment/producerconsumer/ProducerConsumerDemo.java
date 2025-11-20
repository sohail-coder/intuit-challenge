package com.assignment.producerconsumer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Main application demonstrating producer-consumer pattern
 * with multiple producers and consumers.
 * 
 * This program demonstrates:
 * - Thread synchronization
 * - Concurrent programming
 * - Blocking queues
 * - Wait/Notify mechanism
 */
public class ProducerConsumerDemo {
    private static final int QUEUE_CAPACITY = 5;
    private static final int NUM_PRODUCERS = 3;
    private static final int NUM_CONSUMERS = 2;
    
    public static void main(String[] args) {
        System.out.println("=== Producer-Consumer Pattern Demonstration ===\n");
        System.out.println("Configuration:");
        System.out.println("  Queue Capacity: " + QUEUE_CAPACITY);
        System.out.println("  Number of Producers: " + NUM_PRODUCERS);
        System.out.println("  Number of Consumers: " + NUM_CONSUMERS);
        System.out.println();
        
        BlockingQueue<String> sharedQueue = new BlockingQueue<>(QUEUE_CAPACITY);
        
        List<List<String>> sourceContainers = new ArrayList<>();
        sourceContainers.add(new ArrayList<>(Arrays.asList("Apple", "Banana", "Cherry")));
        sourceContainers.add(new ArrayList<>(Arrays.asList("Dog", "Elephant", "Fox")));
        sourceContainers.add(new ArrayList<>(Arrays.asList("Green", "Blue", "Red")));
        
        List<String> destinationContainer = new ArrayList<>();
        
        List<Thread> producerThreads = new ArrayList<>();
        for (int i = 0; i < NUM_PRODUCERS; i++) {
            Producer producer = new Producer(
                "Producer-" + (i + 1),
                sharedQueue,
                sourceContainers.get(i)
            );
            Thread thread = new Thread(producer);
            producerThreads.add(thread);
            thread.start();
        }
        
        int totalItems = sourceContainers.stream()
            .mapToInt(List::size)
            .sum();
        
        List<Thread> consumerThreads = new ArrayList<>();
        int itemsPerConsumer = totalItems / NUM_CONSUMERS;
        int remainder = totalItems % NUM_CONSUMERS;
        
        for (int i = 0; i < NUM_CONSUMERS; i++) {
            int itemsToConsume = itemsPerConsumer + (i < remainder ? 1 : 0);
            Consumer consumer = new Consumer(
                "Consumer-" + (i + 1),
                sharedQueue,
                destinationContainer,
                itemsToConsume
            );
            Thread thread = new Thread(consumer);
            consumerThreads.add(thread);
            thread.start();
        }
        
        System.out.println("\n[Main Thread] Waiting for producer threads to complete...");
        for (Thread producerThread : producerThreads) {
            try {
                producerThread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Main thread interrupted while waiting for producers");
            }
        }
        System.out.println("[Main Thread] All producer threads have completed\n");
        
        System.out.println("[Main Thread] Waiting for consumer threads to complete...");
        for (Thread consumerThread : consumerThreads) {
            try {
                consumerThread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Main thread interrupted while waiting for consumers");
            }
        }
        System.out.println("[Main Thread] All consumer threads have completed\n");
        
        System.out.println("=== Results ===");
        System.out.println("Total items produced: " + totalItems);
        System.out.println("Total items consumed: " + destinationContainer.size());
        System.out.println("\nDestination container contents:");
        for (int i = 0; i < destinationContainer.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + destinationContainer.get(i));
        }
        
        if (destinationContainer.size() == totalItems) {
            System.out.println("\n✓ Success: All items were produced and consumed!");
        } else {
            System.out.println("\n✗ Warning: Item count mismatch!");
        }
    }
}
