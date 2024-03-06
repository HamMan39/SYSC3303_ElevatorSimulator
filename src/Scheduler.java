import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * This Class represents the Scheduler which acts as a communication line
 * to pass messages between Floor and Elevator Subsystems.
 * @author Mahnoor Fatima 101192353
 */
public class Scheduler implements Runnable {
    private LinkedList<ArrayList> heldRequests;
    private ConcurrentLinkedQueue<ArrayList> newRequests;
    private boolean checkWaitingRequests;

    public Scheduler() {
        heldRequests = new LinkedList<>();

        newRequests = new ConcurrentLinkedQueue<ArrayList>();
    }

    private boolean schedule(ArrayList request){
        return true;
    }




    @Override
    public void run() {
        Iterator<ArrayList> it;

        it = newRequests.iterator();
        while(it.hasNext()){
            ArrayList request = it.next();
            if (schedule(request)){
                it.remove();
            }
        }
        // Attempt to schedule requests in first wait queue
        it = heldRequests.iterator();
        while (it.hasNext()) {
            ArrayList request = it.next();
            if (schedule(request)) {
                it.remove();
            }
        }

    }
}