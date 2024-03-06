import java.util.ArrayList;
import java.util.LinkedList;

/**
 * This Class represents the Scheduler which acts as a communication line
 * to pass messages between Floor and Elevator Subsystems.
 * @author Mahnoor Fatima 101192353
 */
public class Scheduler implements Runnable {
    private ArrayList<LinkedList> unscheduledRequests;

    public Scheduler() {
        unscheduledRequests = new ArrayList<>();
        unscheduledRequests.add(new LinkedList<>());
        unscheduledRequests.add(new LinkedList<>());
        unscheduledRequests.add(new LinkedList<>());
    }



    @Override
    public void run() {




    }
}