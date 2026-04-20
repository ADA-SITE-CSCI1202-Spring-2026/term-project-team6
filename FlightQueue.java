import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

public class FlightQueue {

    private final Queue<Aircraft> queue;

    public FlightQueue() {
        this.queue = new ArrayDeque<>();
    }

    /** Adds a new aircraft to the back of the queue. */
    public void enqueue(Aircraft aircraft) {
        queue.add(aircraft);
    }

    /** Removes and returns the next aircraft. Returns null if empty. */
    public Aircraft dequeue() {
        return queue.poll();
    }

    /** Peeks at the next aircraft without removing it. */
    public Aircraft peek() {
        return queue.peek();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public int size() {
        return queue.size();
    }

    /** Returns a copy of the queue as a list (for GUI display and save/load). */
    public List<Aircraft> toList() {
        return List.copyOf(queue);
    }
}