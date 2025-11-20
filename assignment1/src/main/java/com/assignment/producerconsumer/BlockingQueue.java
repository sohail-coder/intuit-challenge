package assignment1.src.main.java.com.assignment.producerconsumer;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Thread-safe bounded blocking queue implementation.
 * Demonstrates wait/notify mechanism for thread synchronization.
 * 
 * This queue blocks:
 * - Producer threads when the queue is full
 * - Consumer threads when the queue is empty
 */
public class BlockingQueue<T> {
    private final Queue<T> queue;
    private final int capacity;
    
    /**
     * Constructs a blocking queue with the specified capacity.
     * 
     * @param capacity the maximum number of elements the queue can hold
     * @throws IllegalArgumentException if capacity is less than 1
     */
    public BlockingQueue(int capacity) {
        if (capacity < 1) {
            throw new IllegalArgumentException("Capacity must be at least 1");
        }
        this.capacity = capacity;
        this.queue = new LinkedList<>();
    }
    
    /**
     * Adds an item to the queue. Blocks if the queue is full.
     * Uses wait/notify mechanism for synchronization.
     * 
     * @param item the item to add
     * @throws InterruptedException if the thread is interrupted while waiting
     */
    public synchronized void put(T item) throws InterruptedException {
        if (item == null) {
            throw new NullPointerException("Cannot add null item to queue");
        }
        
        while (queue.size() == capacity) {
            System.out.println(Thread.currentThread().getName() + " waiting: Queue is full (size=" + queue.size() + ")");
            wait();
        }
        
        queue.offer(item);
        System.out.println(Thread.currentThread().getName() + " produced: " + item + " (queue size=" + queue.size() + ")");
        System.out.println("[NOTIFY] " + Thread.currentThread().getName() + " calling notifyAll() - waking up ALL threads in wait set");
        notifyAll();
    }
    
    /**
     * Removes and returns an item from the queue. Blocks if the queue is empty.
     * Uses wait/notify mechanism for synchronization.
     * 
     * @return the item removed from the queue
     * @throws InterruptedException if the thread is interrupted while waiting
     */
    public synchronized T take() throws InterruptedException {
        while (queue.isEmpty()) {
            System.out.println(Thread.currentThread().getName() + " waiting: Queue is empty");
            wait();
        }
        
        T item = queue.poll();
        System.out.println(Thread.currentThread().getName() + " consumed: " + item + " (queue size=" + queue.size() + ")");
        System.out.println("[NOTIFY] " + Thread.currentThread().getName() + " calling notifyAll() - waking up ALL threads in wait set");
        notifyAll();
        
        return item;
    }
    
    /**
     * Returns the current size of the queue.
     * 
     * @return the number of elements in the queue
     */
    public synchronized int size() {
        return queue.size();
    }
    
    /**
     * Returns the capacity of the queue.
     * 
     * @return the maximum capacity of the queue
     */
    public int getCapacity() {
        return capacity;
    }
    
    /**
     * Checks if the queue is empty.
     * 
     * @return true if the queue is empty, false otherwise
     */
    public synchronized boolean isEmpty() {
        return queue.isEmpty();
    }
    
    /**
     * Checks if the queue is full.
     * 
     * @return true if the queue is full, false otherwise
     */
    public synchronized boolean isFull() {
        return queue.size() == capacity;
    }
}
