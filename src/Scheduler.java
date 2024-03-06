import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * This Class represents the Scheduler which acts as a communication line
 * to pass messages between Floor and Elevator Subsystems.
 * @author Mahnoor Fatima 101192353
 * @author Owen Petersen 101233850
 */
public class Scheduler implements Runnable {
    private LinkedList<ArrayList> heldRequests;
    private ConcurrentLinkedQueue<ArrayList> newRequests;
    private ElevatorSubsystemData elevatorsStatus;

    public Scheduler() {
        heldRequests = new LinkedList<>();

        newRequests = new ConcurrentLinkedQueue<ArrayList>();
        elevatorsStatus = new ElevatorSubsystemData();
    }

    private boolean schedule(ArrayList request){
        ArrayList<Integer> sectorElevators = determineSectorsElevator(request);
        for (Integer i: sectorElevators){
            if (elevatorsStatus.sameDirection((ElevatorSubsystemData.Directions) request.get(1), i)) {
                //TODO assign task to elevator i
                return true;
            }
        }
        return true;
    }
    private ArrayList<Integer> determineSectorsElevator(ArrayList request){
        int sourceFloor = (int) request.get(1);
        if(sourceFloor == 1){
            return new ArrayList(List.of(1,2));
        } else if (1 < sourceFloor && sourceFloor <=12) {
            return new ArrayList<>(List.of(3));
        } else {
            return new ArrayList<>(List.of(4));
        }
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
        // Attempt to schedule requests in wait queue
        it = heldRequests.iterator();
        while (it.hasNext()) {
            ArrayList request = it.next();
            if (schedule(request)) {
                it.remove();
            }
        }

    }
}