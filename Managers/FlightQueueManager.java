package Managers;

import aircrafts.Aircraft;

import java.util.*;

public class FlightQueueManager {

    private final ArrayDeque<Aircraft> queue;

    public FlightQueueManager() {
        this.queue = new ArrayDeque<>();
    }

    //Adds a new aircraft to the back of the queue.
    public void enqueue(Aircraft aircraft) {
        queue.add(aircraft);
    }

    //Removes and returns the next aircraft. Returns null if empty.
    public Aircraft dequeue() {
        return queue.poll();
    }

    //Peeks at the next aircraft without removing it.
    public Aircraft peekNextFlight() {
        return queue.peek();
    }

    // Returns the number of aircraft in the queue.
    public int size() {
        return queue.size();
    }

    // Returns a copy of the queue as a list (for GUI display and save/load).
    public List<Aircraft> toList() {
        return List.copyOf(queue);
    }

    // Clears the queue (used for reset).
    public void clear() {
        queue.clear();
    }
}