import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeoutException;

/**
 * This Class represents the Elevator which travels between floors according to
 * received messages from the scheduler class, and alerts the Scheduler upon arrival
 * at the destination floor.
 * @author Nikita Sara Vijay 101195009
 * @author Areej Mahmoud 101218260
 * @author Khola Haseeb 101192363
 */
public class Elevator extends CommunicationRPC implements Runnable {

    public enum state{IDLE, MOVING, DOOR_OPEN, DOOR_CLOSED, DOOR_STUCK, DISABLED}

    private state currentState;

    //current floor that elevator is at
    private int floor, numFloors, elevatorId;
    //Message boxes for communication with Scheduler
    private MessageBox incomingMessages, outgoingMessages;

    //Messages that have been read but not serviced - pending messages
    private ArrayList<Message> pendingMessages;

    private ElevatorData elevatorData;

    private ElevatorStatus elevatorStatus;

    List<ElevatorViewHandler> views;

    private int doorStuck = 1;
    
    /**
     * Constructor for class Elevator
     *
     * @param box1 Incoming messages MessageBox
     * @param box2 Outgoing messages MessageBox
     */
    public Elevator(int elevatorId, int numFloors, MessageBox box1, MessageBox box2, ElevatorData elevatorData, ElevatorViewHandler view) {
        this.floor = 0;
        this.incomingMessages = box1;
        this.outgoingMessages = box2;
        this.elevatorId = elevatorId;
        this.numFloors = numFloors;
        this.currentState = state.IDLE;
        this.elevatorData = elevatorData;
        this.elevatorStatus = new ElevatorStatus();
        this.pendingMessages = new ArrayList<>();
        this.views = new ArrayList<>();

        addElevatorView(view);
    }

    public void addElevatorView (ElevatorViewHandler view){
        views.add(view);
    }

    /**
     * Simulate Elevator travelling from current floor to destFloor
     * @param destFloor the destination floor.
     */
    public void travelFloors(int destFloor) throws TimeoutException {
        currentState = state.MOVING;
        SortedSet<Integer> pendingStops = new TreeSet<>();
        System.out.println(Thread.currentThread().getName() + " - " + currentState +  " from floor " + floor + " to floor " + destFloor);
        Message.Directions direction;
        if((floor-destFloor)>0){
            direction = Message.Directions.DOWN;
        }else{direction = Message.Directions.UP;}

        modifyElevatorData(direction);

        lampStatus(direction);

        try {
            long travelTime = (long)(7399.8);
            Thread.sleep(travelTime); //simulate time taken to travel floors
        } catch (InterruptedException e) {
        }

        while(floor != destFloor) {
            currentState = state.MOVING;
            try {
                Thread.sleep(1429);         //simulate time taken to travel one floor
            } catch (InterruptedException e) {}

            for (ElevatorViewHandler view : views){
                view.handleTravelFloor(new ElevatorEvent(this, direction, currentState));
            }

            if (floor < destFloor) {
                floor++;
                direction = Message.Directions.UP;
                System.out.println(Thread.currentThread().getName() + " - " + currentState + " " + direction + "(" + floor + ")");
            } else {
                floor--;
                direction = Message.Directions.DOWN;
                System.out.println(Thread.currentThread().getName() + " - " + currentState + " " + direction + "(" + floor + ")");
            }

            modifyElevatorData(direction);

            // Go through pending messages
            Message message = null;
            int n;

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
                System.out.println("---" + Thread.currentThread().getName() + " executing request from Scheduler : " + message);

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
            if(!pendingStops.isEmpty()) {
                 if (direction == Message.Directions.UP)
                     first = pendingStops.first();
                 else
                     first = pendingStops.last();       //When travelling down, checks for the largest # and services that floor
            }
            if(first == null || first != floor)
                continue; // no stop at current floor

            injectTimeoutFailure(message); //check for timeout failure

            loadPassenger(floor);
            injectDoorFailure(message); //check for door stuck failure

            // stop at current floor
            pendingStops.remove(first);
        }
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

            System.out.println(Thread.currentThread().getName() + " executing request from Scheduler : " + message);

            if (message.getArrivalFloor() != this.floor) {
                try {
                    travelFloors(message.getArrivalFloor());
                } catch (TimeoutException e) {
                    break; // if a timeout failure occurs, stop the elevator
                }
            }

            try {
                injectTimeoutFailure(message); //check for timeout failure
            } catch (TimeoutException e) {
                break; // if a timeout failure occurs, stop the elevator
            }

            loadPassenger(floor);
            injectDoorFailure(message); //check for door stuck failure

            try {
                travelFloors(message.getDestinationFloor()); //travel to destination floor
            } catch (TimeoutException e) {
                break; // if a timeout failure occurs, stop the elevator
            }

            loadPassenger(floor);
            injectDoorFailure(message); //check for door stuck failure
            arrivalStatus(floor, currentState);

            Message.Directions direction = Message.Directions.IDLE;
            lampStatus(direction);
            outgoingMessages.put(message); //echo the request message to indicate arrival at dest. floor
        }
    }

    public void lampStatus(Message.Directions direction) {
        if (direction != Message.Directions.IDLE && (direction==Message.Directions.UP || direction==Message.Directions.DOWN)) {
            System.out.println("Lamp ON, elevator going " + direction);
        } else {
            System.out.println("Lamp OFF, elevator has arrived.");
        }
    }
    private void injectTimeoutFailure(Message msg) throws TimeoutException {
        if (msg.getFailure()== Message.Failures.TIMEOUT){
            currentState = state.DISABLED;
            handleTimeout();
            for (ElevatorViewHandler view: views){
                view.handleTimeoutFailure(new ElevatorEvent(this, currentState));
            }
            System.out.println(Thread.currentThread().getName() + "TIMEOUT failure. Shutting down...");
            throw new TimeoutException();
        }
    }
    private void injectDoorFailure(Message msg){
        if (msg.getFailure() == Message.Failures.DOORS && doorStuck == 1){
            currentState = state.DOOR_STUCK;
            for (ElevatorViewHandler view : views){
                view.handleDoorFailure(new ElevatorEvent(this, currentState));
            }
            System.out.println(Thread.currentThread().getName() + " DOOR STUCK. Attempting to close ...");
            try {
                Thread.sleep(2000); //add a delay for time taken to handle door failure
            } catch (InterruptedException e) {
            }
            doorStuck = (doorStuck + 1) % 2;
        }
        currentState = state.DOOR_CLOSED;
        doorClosed(floor, currentState);
    }

    private void handleTimeout(){
         sendAndReceive(new byte[]{(byte) elevatorId}, 66); // tell scheduler which elevator to shut down

        // send messages back to scheduler to be rescheduled
        for (Message m: pendingMessages){
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            byteStream.write(-1);
            try {
                byteStream.write(m.toByteArray());
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
            sendAndReceive(byteStream.toByteArray(), 66);
        }
    }

    public Integer getCurrentFloor(){
        return floor;
    }

    public void loadPassenger(int floor) {
        currentState = state.DOOR_OPEN;
        doorOpen(floor, currentState);
        Message.Directions direction = Message.Directions.IDLE;
        lampStatus(direction);
        try {
            Thread.sleep(10881); //based on iteration 0 (10.881 s to load 1 person)
        } catch (InterruptedException e) {
        }

    }

    public void doorOpen(int floor, state currentState){
        System.out.println(">>"+ new TimeStamp().getTimestamp() +" "+ Thread.currentThread().getName() + " at floor " + floor + " - " + currentState );
        for (ElevatorViewHandler view : views){
            view.handleStateChange(new ElevatorEvent(this, currentState));
        }
    }

    public void doorClosed(int floor, state currentState){
        System.out.println(">>" + new TimeStamp().getTimestamp() +" "+Thread.currentThread().getName() + " at floor " + floor + " - " + currentState );
        for (ElevatorViewHandler view : views){
            view.handleStateChange(new ElevatorEvent(this, currentState));
        }
    }

    public void arrivalStatus(int floor, state currentState){
        currentState = state.IDLE;
        System.out.println(">>" + new TimeStamp().getTimestamp() +" "+Thread.currentThread().getName() + " is " + currentState + " and has arrived at floor " + floor);
        for (ElevatorViewHandler view : views){
            view.handleStateChange(new ElevatorEvent(this, currentState));
        }
    }

    public synchronized void modifyElevatorData(Message.Directions direction) {
        elevatorData.getElevatorSubsystemStatus().get(elevatorId).setCurrentFloor(floor);
        elevatorData.getElevatorSubsystemStatus().get(elevatorId).setCurrentDirection(direction);
    }

    public synchronized void updateElevatorLoad(int elevatorLoad){
        elevatorData.getElevatorSubsystemStatus().get(elevatorId).setCurrentLoad(elevatorLoad);
    }

    public int getElevatorId() {
        return elevatorId;
    }
}


