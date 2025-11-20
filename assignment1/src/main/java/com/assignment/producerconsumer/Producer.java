package com.assignment.producerconsumer;

import java.util.List;

/**
 * Producer thread that reads items from a source container
 * and places them into a shared blocking queue.
 * 
 * Demonstrates concurrent programming with multiple producer threads.
 */
public class Producer implements Runnable {
    private final BlockingQueue<String> queue;
    private final List<String> sourceContainer;
    private final String producerName;
    
    /**
     * Constructs a producer with the specified queue and source container.
     * 
     * @param producerName identifier for this producer thread
     * @param queue the shared blocking queue to put items into
     * @param sourceContainer the source container to read items from
     */
    public Producer(String producerName, BlockingQueue<String> queue, List<String> sourceContainer) {
        this.producerName = producerName;
        this.queue = queue;
        this.sourceContainer = sourceContainer;
    }
    
    /**
     * Producer thread execution: reads items from source and puts them into queue.
     * Demonstrates concurrent programming and thread synchronization.
     */
    @Override
    public void run() {
        Thread.currentThread().setName(producerName);
        System.out.println(producerName + " started");
        
        try {
            for (String item : sourceContainer) {
                queue.put(item);
                Thread.sleep(100);
            }
            
            System.out.println(producerName + " finished producing all items");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println(producerName + " was interrupted");
        }
    }
}
