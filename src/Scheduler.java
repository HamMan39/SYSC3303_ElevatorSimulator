import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.SynchronousQueue;

/**
 * This Class represents the Scheduler which acts as a communication line
 * to pass messages between Floor and Elevator Subsystems.
 * @author Mahnoor Fatima 101192353
 */
public class Scheduler implements Runnable {
    private ArrayList<LinkedList<ArrayList>> heldRequests;
    private ConcurrentLinkedQueue<ArrayList> newRequests;

    public Scheduler() {
        heldRequests = new ArrayList<>();
        heldRequests.add(new LinkedList<>());
        heldRequests.add(new LinkedList<>());
        heldRequests.add(new LinkedList<>());

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
            } else {
                heldRequests.get(2).add(request);
                it.remove();
            }
        }

        // Attempt to schedule requests in first wait queue
        it = heldRequests.get(0).iterator();
        while(it.hasNext()){
            ArrayList request = it.next();
            if (schedule(request)){
                it.remove();
            }
        }
        // Attempt to schedule requests in second wait queue
        it = heldRequests.get(1).iterator();
        while(it.hasNext()){
            ArrayList request = it.next();
            if (schedule(request)){
                it.remove();
            } else {
                heldRequests.get(0).add(request);
                it.remove();
            }
        }
        // Attempt to schedule requests in third wait queue
        it = heldRequests.get(2).iterator();
        while(it.hasNext()){
            ArrayList request = it.next();
            if (schedule(request)){
                it.remove();
            } else {
                heldRequests.get(1).add(request);
                it.remove();
            }
        }


    }
}