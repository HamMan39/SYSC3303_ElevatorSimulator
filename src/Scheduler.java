import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * This Class represents the Scheduler which acts as a communication line
 * to pass messages between Floor and Elevator Subsystems.
 * @author Mahnoor Fatima 101192353
 * @author Owen Petersen 101233850
 */
public class Scheduler implements Runnable {
    private LinkedList<Message> heldRequests;
    private ConcurrentLinkedQueue<Message> newRequests; // input to scheduler from floors
    private ElevatorData elevatorsStatus;

    public Scheduler() {
        heldRequests = new LinkedList<>();

        newRequests = new ConcurrentLinkedQueue<>();
        elevatorsStatus = new ElevatorData();
    }

    private boolean schedule(Message request){
        ArrayList<Integer> sectorElevators = determineSectorsElevator(request);
        // Case S1 (see Owen's notes)
        synchronized (elevatorsStatus) {
            for (Integer i : sectorElevators) {
                if (elevatorsStatus.sameDirection(request.getDirection(), i)) {
                    //TODO assign task to elevator i
                    return true;
                }
            }
            // Case S2 (see Owen's notes)
            for (int i = 0; i < 4; i++) {
                if (!sectorElevators.contains(i)) {
                    if ((elevatorsStatus.sameDirection(request.getDirection(), i))) {
                        //TODO assign task to elevator i
                        return true;
                    }
                }
            }
            // Case S3 (see Owen's notes)
            for (Integer i : sectorElevators) {
                if (elevatorsStatus.soonSameDirection(request.getDirection(), i)) {
                    //TODO assign task to elevator i
                    return true;
                }
            }
            // Case S4 (see Owen's notes)
            for (Integer i : sectorElevators) {
                if (elevatorsStatus.isIdle(i)) {
                    //TODO assign task to elevator i
                    return true;
                }
            }
            // Case S5 (see Owen's notes)
            for (int i = 0; i < 4; i++) {
                if (!sectorElevators.contains(i)) {
                    if ((elevatorsStatus.soonSameDirection(request.getDirection(), i))) {
                        //TODO assign task to elevator i
                        return true;
                    }
                }
            }
            // Case S6 (see Owen's notes)
            for (int i = 0; i < 4; i++) {
                if (!sectorElevators.contains(i)) {
                    if (elevatorsStatus.isIdle(i)) {
                        //TODO assign task to elevator i
                        return true;
                    }
                }
            }
        }
        return false;
    }
    private ArrayList<Integer> determineSectorsElevator(Message request){
        int sourceFloor = request.getArrivalFloor();
        if(sourceFloor == 1){
            return new ArrayList(List.of(1,2));
        } else if (1 < sourceFloor && sourceFloor <=12) {
            return new ArrayList<>(List.of(3));
        } else {
            return new ArrayList<>(List.of(4));
        }
    }

    private void sendCommand()




    @Override
    public void run() {
        Iterator<Message> it;

        it = newRequests.iterator();
        while(it.hasNext()){
            Message request = it.next();
            if (schedule(request)){
                it.remove();
            }
        }
        // Attempt to schedule requests in wait queue
        it = heldRequests.iterator();
        while (it.hasNext()) {
            Message request = it.next();
            if (schedule(request)) {
                it.remove();
            }
        }

    }

}