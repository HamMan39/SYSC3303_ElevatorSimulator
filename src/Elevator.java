import static java.lang.Math.abs;
import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * This Class represents the Elevator which travels between floors according to
 * received messages from the scheduler class, and alerts the Scheduler upon arrival
 * at the destination floor.
 * @author Nikita Sara Vijay (101195009)
 * @author Areej Mahmoud 101218260
 * @author Khola Haseeb 101192363
 */
public class Elevator implements Runnable {

    private enum state{IDLE, MOVING, DOOR_OPEN, DOOR_CLOSED}

    private state currentState;

    //current floor that elevator is at
    private int floor, numFloors, elevatorId;
    //Message boxes for communication with Scheduler
    private MessageBox incomingMessages, outgoingMessages;

    //Messages that have been read but not serviced - pending messages
    private ArrayList<Message> pendingMessages;

    private ElevatorData elevatorData;

    private ElevatorStatus elevatorStatus;

    /**
     * Constructor for class Elevator
     *
     * @param box1 Incoming messages MessageBox
     * @param box2 Outgoing messages MessageBox
     */
    public Elevator(int elevatorId, int numFloors, MessageBox box1, MessageBox box2, ElevatorData elevatorData) {
        this.floor = 0;
        this.incomingMessages = box1;
        this.outgoingMessages = box2;
        this.elevatorId = elevatorId;
        this.numFloors = numFloors;
        this.currentState = state.IDLE;
        this.elevatorData = elevatorData;
        this.elevatorStatus = new ElevatorStatus();
    }
    /**
     * Simulate Elevator travelling from current floor to destFloor
     * @param destFloor the destination floor.
     */
    public void travelFloors(int destFloor){
        currentState = state.MOVING;
        SortedSet<Integer> pendingStops = new TreeSet<>();
        System.out.println(Thread.currentThread().getName() + " - " + currentState +  " from floor " + floor + " to floor " + destFloor);
        Message.Directions direction;
        if((floor-destFloor)>0){
            direction = Message.Directions.DOWN;
        }else{direction = Message.Directions.UP;}

        modifyElevatorData(direction);

        lampStatus(direction);
        //TODO: change to separate floor sleeps

        try {
            long travelTime = (long)(1429 *abs(floor-destFloor) +7399.8);
            Thread.sleep(travelTime); //simulate time taken to travel floors
        } catch (InterruptedException e) {
        }

        while(floor != destFloor) {

            if (floor < destFloor) {
                floor++;
                direction = Message.Directions.UP;
                System.out.println(Thread.currentThread().getName() + " - " + currentState + " " + direction);
            } else {
                floor--;
                direction = Message.Directions.DOWN;
                System.out.println(Thread.currentThread().getName() + " - " + currentState + " " + direction);

            }

            modifyElevatorData(direction);

            // Go through pending messages
            Message message = null;
            int n;

            if (pendingMessages != null) {
                n = pendingMessages.size();


            for (int i = 0; i < n; i++) {
                message = pendingMessages.remove(0);

                // Checks whether this service can be processed:
                // If it's direction is the same as current movement or we have not already passed the floor, or it
                // is not beyond the current destination
                if (message.getDirection() != direction || (direction == Message.Directions.UP &&
                        (message.getArrivalFloor() < floor || message.getArrivalFloor() > destFloor)) || (direction == Message.Directions.DOWN &&
                        (message.getArrivalFloor() > floor || message.getArrivalFloor() < destFloor))) {
                    pendingMessages.add(message);    //Adds this request to the list of pending messages
                    continue;
                }

                // Request can be processed, so add a stop for it
                pendingStops.add(message.getArrivalFloor());
                // Check if this request will result in modifying the destination, and add a stop accordingly
                if (direction == Message.Directions.UP && message.getDestinationFloor() > destFloor
                        || direction == Message.Directions.DOWN && message.getDestinationFloor() < destFloor) {
                    // Destination has changed, so the old destination should be added as a stop
                    pendingStops.add(destFloor);
                    destFloor = message.getDestinationFloor();
                } else {
                    pendingStops.add(message.getDestinationFloor());
                }
            }

            // Check for new messages
            n = incomingMessages.getSize();

            // Repeat the above logic for new requests
            for(int i=0; i<n; i++) {
                message = incomingMessages.get();
                if(message.getDirection() != direction
                        || (direction == Message.Directions.UP
                        && (message.getArrivalFloor() < floor || message.getArrivalFloor() > destFloor))
                        || (direction == Message.Directions.DOWN
                        && (message.getArrivalFloor() > floor || message.getArrivalFloor() < destFloor))) {
                    pendingMessages.add(message);
                    continue;
                }
                pendingStops.add(message.getArrivalFloor());
                if(direction == Message.Directions.UP && message.getDestinationFloor() > destFloor
                        || direction == Message.Directions.DOWN && message.getDestinationFloor() < destFloor) {
                    pendingStops.add(destFloor);
                    destFloor = message.getDestinationFloor();
                } else {
                    pendingStops.add(message.getDestinationFloor());
                }
            }
            Integer first = null;
            // The pending stops is in sorted order
            // If it is going up, processing will be done in ascending order, for going down, it will be descending
            if(direction == Message.Directions.UP)
                first = pendingStops.first();
            else
                first = pendingStops.last();       //When travelling down, checks for the largest # and services that floor
            if(first == null || first != floor)
                continue; // no stop at current floor

            doorOpen(floor, currentState);
            // stop at current floor
            pendingStops.remove(first);
        }
        }

   //     floor= destFloor; //arrive at destFloor
    }

    /**
     * Execute the Elevator thread operations. Receive messages from Scheduler
     * and sends the messages back through the MessageBox
     */
    @Override
    public void run() {
        while (true) {
            Message message = null;
            // Process pending requests before new ones
            if(pendingMessages != null && pendingMessages.size() > 0) {
                message = pendingMessages.remove(0);
            } else {
                message = incomingMessages.get();
            }

            if (message == null) {
                System.out.println("Elevator System Exited");
                outgoingMessages.put(null);
                return;
            }
            System.out.println(Thread.currentThread().getName() + " received message from Scheduler : " + message);


            if (message.getArrivalFloor() != this.floor) {
                travelFloors(message.getArrivalFloor());
            }

            currentState = state.DOOR_OPEN;
            doorOpen(floor, currentState);
            lampStatus(message.getDirection());

            try {
                Thread.sleep(10881); //based on iteration 0 (10.881 s to load 1 person)
            } catch (InterruptedException e) {
            }
            currentState = state.DOOR_CLOSED;
            doorClosed(floor, currentState);

            travelFloors(message.getDestinationFloor()); //travel to destination floor

            currentState = state.IDLE;
            arrivalStatus(floor, currentState);

            Message.Directions direction = Message.Directions.IDLE;
            lampStatus(direction);
            outgoingMessages.put(message); //echo the request message to indicate arrival at dest. floor
//            System.out.println(Thread.currentThread().getName() + " sent message to Scheduler : " + message);

        }
    }

    public void lampStatus(Message.Directions direction) {
        if (direction != Message.Directions.IDLE && (direction==Message.Directions.UP || direction==Message.Directions.DOWN)) {
            System.out.println("Lamp ON, elevator going " + direction);
        } else {
            System.out.println("Lamp OFF, elevator has arrived.");
        }
    }

    public Integer getCurrentFloor(){
        return floor;
    }

    public void doorOpen(int floor, state currentState){
        System.out.println(">>" + Thread.currentThread().getName() + " at floor " + floor + " - " + currentState );
    }

    public void doorClosed(int floor, state currentState){
        System.out.println(">>" + Thread.currentThread().getName() + " at floor " + floor + " - " + currentState );
    }
    public void arrivalStatus(int floor, state currentState){
        System.out.println(">>" + Thread.currentThread().getName() + " is " + currentState + " and has arrived at floor " + floor);
    }

    public synchronized void modifyElevatorData(Message.Directions direction) {
        elevatorData.getElevatorSubsystemStatus().get(elevatorId).setCurrentFloor(floor);
        elevatorData.getElevatorSubsystemStatus().get(elevatorId).setCurrentDirection(direction);
    }
}


