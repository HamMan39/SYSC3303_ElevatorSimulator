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
    private ConcurrentLinkedQueue<ArrayList> newRequests; // input to scheduler from floors
    private ElevatorData elevatorsStatus;

    public Scheduler() {
        heldRequests = new LinkedList<>();

        newRequests = new ConcurrentLinkedQueue<ArrayList>();
        elevatorsStatus = new ElevatorData();
    }

    private boolean schedule(ArrayList request){
        ArrayList<Integer> sectorElevators = determineSectorsElevator(request);
        // Case S1 (see Owen's notes)
        for (Integer i: sectorElevators){
            if (elevatorsStatus.sameDirection((ElevatorData.Directions) request.get(1), i)) {
                //TODO assign task to elevator i
                return true;
            }
        }
        // Case S2 (see Owen's notes)
        for (int i = 0; i < 4; i++){
            if (!sectorElevators.contains(i)){
                if ((elevatorsStatus.sameDirection((ElevatorData.Directions) request.get(1), i))){
                    //TODO assign task to elevator i
                    return true;
                }
            }
        }
        // Case S3 (see Owen's notes)
        for (Integer i: sectorElevators){
            if (elevatorsStatus.soonSameDirection((ElevatorData.Directions) request.get(1), i)) {
                //TODO assign task to elevator i
                return true;
            }
        }
        // Case S4 (see Owen's notes)
        for (Integer i: sectorElevators){
            if (elevatorsStatus.isIdle(i)) {
                //TODO assign task to elevator i
                return true;
            }
        }
        // Case S5 (see Owen's notes)
        for (int i = 0; i < 4; i++){
            if (!sectorElevators.contains(i)){
                if ((elevatorsStatus.soonSameDirection((ElevatorData.Directions) request.get(1), i))){
                    //TODO assign task to elevator i
                    return true;
                }
            }
        }
        // Case S6 (see Owen's notes)
        for (int i = 0; i < 4; i++){
            if (!sectorElevators.contains(i)){
                if (elevatorsStatus.isIdle(i)){
                    //TODO assign task to elevator i
                    return true;
                }
            }
        }
        return false;
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