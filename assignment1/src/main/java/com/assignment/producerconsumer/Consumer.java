package assignment1.src.main.java.com.assignment.producerconsumer;

import java.util.List;

/**
 * Consumer thread that reads items from a shared blocking queue
 * and stores them in a destination container.
 * 
 * Demonstrates concurrent programming with multiple consumer threads.
 */
public class Consumer implements Runnable {
    private final BlockingQueue<String> queue;
    private final List<String> destinationContainer;
    private final String consumerName;
    private final int itemsToConsume;
    
    /**
     * Constructs a consumer with the specified queue and destination container.
     * 
     * @param consumerName identifier for this consumer thread
     * @param queue the shared blocking queue to take items from
     * @param destinationContainer the destination container to store items in
     * @param itemsToConsume the number of items this consumer should consume
     */
    public Consumer(String consumerName, BlockingQueue<String> queue, 
                    List<String> destinationContainer, int itemsToConsume) {
        this.consumerName = consumerName;
        this.queue = queue;
        this.destinationContainer = destinationContainer;
        this.itemsToConsume = itemsToConsume;
    }
    
    /**
     * Consumer thread execution: takes items from queue and stores them in destination.
     * Demonstrates concurrent programming and thread synchronization.
     */
    @Override
    public void run() {
        Thread.currentThread().setName(consumerName);
        System.out.println(consumerName + " started");
        
        try {
            int consumed = 0;
            while (consumed < itemsToConsume) {
                String item = queue.take();
                
                synchronized (destinationContainer) {
                    destinationContainer.add(item);
                }
                
                consumed++;
                Thread.sleep(150);
            }
            
            System.out.println(consumerName + " finished consuming " + consumed + " items");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println(consumerName + " was interrupted");
        }
    }
}
