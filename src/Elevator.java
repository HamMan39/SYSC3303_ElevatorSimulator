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

    public static final int MAX_CAPACITY = 5;
    public enum state{IDLE, MOVING, DOOR_OPEN, DOOR_CLOSED, DOOR_STUCK, DISABLED}

    private state currentState;

    //current floor that elevator is at
    private int currentFloor, numFloors, elevatorId;
    //Message boxes for communication with Scheduler

    //Messages that have been read but not serviced - pending messages
    private ArrayList<Message> pendingMessages;

    private ElevatorData elevatorData;

    private ElevatorStatus elevatorStatus;
    private ArrayList<RequestedStop> requestedStops;
    private Message.Directions elevatorDirection; // direction elevator is moving, only used internally and not tied to the state of the elevator (although they should be similar)
    private int currentLoad;

    List<ElevatorViewHandler> views;

    private int doorStuck = 1;
    private SortedSet<Integer> criticalFailFloors;



    /**
     * Constructor for class Elevator
     *
     */
    public Elevator(int elevatorId, int numFloors, ElevatorData elevatorData, ElevatorViewHandler view) {
        this.currentFloor = 0;

        this.elevatorId = elevatorId;
        this.numFloors = numFloors;
        this.currentState = state.IDLE;
        this.elevatorData = elevatorData;
        this.elevatorStatus = new ElevatorStatus();
        this.pendingMessages = new ArrayList<>();
        this.views = new ArrayList<>();
        this.requestedStops = new ArrayList<>();
        this. elevatorDirection = Message.Directions.IDLE;
        this.currentLoad = 0;
        this.criticalFailFloors = new TreeSet<>();
        addElevatorView(view);
    }

    public void addElevatorView (ElevatorViewHandler view){
        views.add(view);
    }

    /**
     * Causes the elevator to move one floor in the current direction (specified by elevatorDirection)
     *
     */
    public void travelFloor(int toFloor) {
        if (currentFloor < toFloor){
            currentState = state.MOVING;
            for (ElevatorViewHandler view : views){
                view.handleStateChange(new ElevatorEvent(this, Message.Directions.UP, currentState));
            }
            for (ElevatorViewHandler view : views){
                view.handleTravelFloor(new ElevatorEvent(this, Message.Directions.UP, currentState));
            }
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.exit(1);
            }
            currentFloor++;
        } else if (currentFloor > toFloor) {
            currentState = state.MOVING;
            for (ElevatorViewHandler view : views){
                view.handleStateChange(new ElevatorEvent(this, Message.Directions.DOWN, currentState));
            }
            for (ElevatorViewHandler view : views){
                view.handleTravelFloor(new ElevatorEvent(this, Message.Directions.DOWN, currentState));
            }
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.exit(1);
            }
            currentFloor--;
        } else { // should never be trying to travel floor when direction is idle
            System.err.println(Thread.currentThread().getName() + " travelFloor() error: direction is IDLE");
        }
//        for (ElevatorViewHandler view : views){
//            view.handleTravelFloor(new ElevatorEvent(this, elevatorDirection, currentState));
//        }


//        currentState = state.MOVING;
//        SortedSet<Integer> pendingStops = new TreeSet<>();
//        System.out.println(Thread.currentThread().getName() + " - " + currentState +  " from floor " + floor + " to floor " + destFloor);
//        Message.Directions direction;
//        if((floor-destFloor)>0){
//            direction = Message.Directions.DOWN;
//        }else{direction = Message.Directions.UP;}
//
//        modifyElevatorData(direction);
//
//        lampStatus(direction);
//
//        try {
//            long travelTime = (long)(7399.8);
//            Thread.sleep(travelTime); //simulate time taken to travel floors
//        } catch (InterruptedException e) {
//        }
//
//        while(floor != destFloor) {
//            currentState = state.MOVING;
//            try {
//                Thread.sleep(1429);         //simulate time taken to travel one floor
//            } catch (InterruptedException e) {}
//
//            for (ElevatorViewHandler view : views){
//                view.handleTravelFloor(new ElevatorEvent(this, direction, currentState));
//            }
//
//            if (floor < destFloor) {
//                floor++;
//                direction = Message.Directions.UP;
//                System.out.println(Thread.currentThread().getName() + " - " + currentState + " " + direction + "(" + floor + ")");
//            } else {
//                floor--;
//                direction = Message.Directions.DOWN;
//                System.out.println(Thread.currentThread().getName() + " - " + currentState + " " + direction + "(" + floor + ")");
//            }
//
//            modifyElevatorData(direction);
//
//            // Go through pending messages
//            Message message = null;
//            int n;
//
//            n = pendingMessages.size();
//
//            for (int i = 0; i < n; i++) {
//                message = pendingMessages.remove(0);
//
//                // Checks whether this service can be processed:
//                // If it's direction is the same as current movement or we have not already passed the floor, or it
//                // is not beyond the current destination
//                if (message.getDirection() != direction || (direction == Message.Directions.UP &&
//                        (message.getArrivalFloor() < floor || message.getArrivalFloor() > destFloor)) || (direction == Message.Directions.DOWN &&
//                        (message.getArrivalFloor() > floor || message.getArrivalFloor() < destFloor))) {
//                    pendingMessages.add(message);    //Adds this request to the list of pending messages
//                    continue;
//                }
//
//                // Request can be processed, so add a stop for it
//                pendingStops.add(message.getArrivalFloor());
//                System.out.println("---" + Thread.currentThread().getName() + " executing request from Scheduler : " + message);
//
//                // Check if this request will result in modifying the destination, and add a stop accordingly
//                if (direction == Message.Directions.UP && message.getDestinationFloor() > destFloor
//                        || direction == Message.Directions.DOWN && message.getDestinationFloor() < destFloor) {
//                    // Destination has changed, so the old destination should be added as a stop
//                    pendingStops.add(destFloor);
//                    destFloor = message.getDestinationFloor();
//                } else {
//                    pendingStops.add(message.getDestinationFloor());
//                }
//            }
//
//            // Check for new messages
//            n = incomingMessages.getSize();
//
//            // Repeat the above logic for new requests
//            for(int i=0; i<n; i++) {
//                message = incomingMessages.get();
//                if(message.getDirection() != direction
//                        || (direction == Message.Directions.UP
//                        && (message.getArrivalFloor() < floor || message.getArrivalFloor() > destFloor))
//                        || (direction == Message.Directions.DOWN
//                        && (message.getArrivalFloor() > floor || message.getArrivalFloor() < destFloor))) {
//                    pendingMessages.add(message);
//                    continue;
//                }
//                pendingStops.add(message.getArrivalFloor());
//                if(direction == Message.Directions.UP && message.getDestinationFloor() > destFloor
//                        || direction == Message.Directions.DOWN && message.getDestinationFloor() < destFloor) {
//                    pendingStops.add(destFloor);
//                    destFloor = message.getDestinationFloor();
//                } else {
//                    pendingStops.add(message.getDestinationFloor());
//                }
//            }
//            Integer first = null;
//            // The pending stops is in sorted order
//            // If it is going up, processing will be done in ascending order, for going down, it will be descending
//            if(!pendingStops.isEmpty()) {
//                 if (direction == Message.Directions.UP)
//                     first = pendingStops.first();
//                 else
//                     first = pendingStops.last();       //When travelling down, checks for the largest # and services that floor
//            }
//            if(first == null || first != floor)
//                continue; // no stop at current floor
//
//            injectTimeoutFailure(message); //check for timeout failure
//
//            loadPassenger(floor);
//            injectDoorFailure(message); //check for door stuck failure
//
//            // stop at current floor
//            pendingStops.remove(first);
//        }
    }

    /**
     * Distributes a request from the ElevatorSubsystem to a specific elevator
     * @param request
     */
    public synchronized void giveRequest(Message request) {
        // If elevator is not moving, save what the new direction is so the stops can be sorted
        if (elevatorDirection == Message.Directions.IDLE){
            elevatorDirection = request.getDirection();
            updateElevatorData();

            // Elevator is "aimed" in a direction (up or down) but not moving yet, so direction is up/down but state is still idle
        } else if (elevatorDirection != request.getDirection()) {
            System.err.println(Thread.currentThread().getName() + " elevator received request in wrong direction. Current direction: " + elevatorDirection + " request direction: " + request.getDirection());
        }

        requestedStops.add(new RequestedStop(request.getArrivalFloor(), false)); // add the arrival floor of the request
        requestedStops.add(new RequestedStop(request.getDestinationFloor(), true)); // add the destination of the request

        currentLoad++; // increase the number of requests (the "load") by one

        System.out.println("---" + Thread.currentThread().getName() + " gave request (" + request + ") to Elevator " + elevatorId + ". Current load: " + currentLoad);

        //Update view to show capacity updates
        for (ElevatorViewHandler view : views){
            view.handleCapacityChange(new ElevatorEvent(this, currentLoad));
        }

        updateElevatorData(); //multiple updates ensure scheduler has the most accurate information

        //Insertion sort algorithm (organize requests from closest to furthest from elevator)
        int n = requestedStops.size();
        for (int i = 1; i < n; ++i) {
            RequestedStop key = requestedStops.get(i);
            int j = i - 1;

            if (elevatorDirection == Message.Directions.UP) { //if direction is up then sort in ascending order
                while (j >= 0 && requestedStops.get(j).getFloor() > key.getFloor()) {
                    requestedStops.set(j + 1, requestedStops.get(j));
                    j = j - 1;
                }
                requestedStops.set(j + 1, key);
            } else { // if direction is down then sort in descending order
                while (j >= 0 && requestedStops.get(j).getFloor() < key.getFloor()) {
                    requestedStops.set(j + 1, requestedStops.get(j));
                    j = j - 1;
                }
                requestedStops.set(j + 1, key);
            }
        }

        if (request.getFailure()== Message.Failures.TIMEOUT) {
            criticalFailFloors.add(request.getArrivalFloor());
        }

        while (!criticalFailFloors.isEmpty()) {
                currentState = state.DISABLED;
                try {
                    injectTimeoutFailure();
                } catch (TimeoutException e) {
                    return; // Stop elevator operation if a timeout failure occurs
                }
        }
        if (request.getFailure() == Message.Failures.DOORS && doorStuck == 1) {
             currentState = state.DOOR_STUCK;
             injectDoorFailure(); //check for door stuck failure
        }

        updateElevatorData();
        notify();

    }

    /**
     * Execute the Elevator thread operations. Receive messages from Scheduler
     * and sends the messages back through the MessageBox
     */
    @Override
    public void run() {

        while (true) {
            synchronized (this) {
                while (currentLoad == 0) {

                    currentState = state.IDLE;
                    for (ElevatorViewHandler view : views) {
                        view.handleStateChange(new ElevatorEvent(this, elevatorDirection, currentState));
                    }
                    elevatorDirection = Message.Directions.IDLE;
                    updateElevatorData();

                    try {
                        wait(); // wait until we have requests to deal with
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        System.exit(1);
                    }
                }
            }



            RequestedStop nextStop = requestedStops.get(0);

            if (nextStop.getFloor() == currentFloor){
                openDoors();

                synchronized (this) {
                    // This while-loop handles loading/unloading all requests at this floor
                    Iterator<RequestedStop> it = requestedStops.iterator(); // using an iterator for easier removal of items
                    while (it.hasNext()) {
                        RequestedStop rs = it.next();
                        if (rs.getFloor() != currentFloor) { // if a request is not from the current floor then stop the loop
                            break;
                        }
                        try {
                            Thread.sleep(5000); // simulate 5 seconds for loading/unloading a single passenger
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            System.exit(1);
                        }
                        if (rs.getIsDestination()) { // if a passenger is getting off then decrease load
                            currentLoad--;

                            //Update view to show capacity updates
                            for (ElevatorViewHandler view : views){
                                view.handleCapacityChange(new ElevatorEvent(this, currentLoad));
                            }
                            System.out.println("---" + Thread.currentThread().getName() + " passenger departed. Current load: " + currentLoad);
                        } else {
                            System.out.println("---" + Thread.currentThread().getName() + " passenger picked up.");
                        }
                        it.remove(); // remove the stop from the list

                        updateElevatorData();
                    }
                }

                closeDoors();
            } else { // need to move elevator one floor then check again
                System.out.println(Thread.currentThread().getName() + " - MOVING from floor " + currentFloor + " to floor " + nextStop.getFloor());
                travelFloor(nextStop.getFloor());
                updateElevatorData();
            }

        }

//        while (true) {
//            Message message = null;
//            // Process pending requests before new ones
//            if(pendingMessages != null && pendingMessages.size() > 0) {
//                message = pendingMessages.remove(0);
//            } else {
//                message = incomingMessages.get();
//            }
//
//            if (message == null) {
//                System.out.println("Elevator System Exited");
//                outgoingMessages.put(null);
//                return;
//            }
//
//            System.out.println(Thread.currentThread().getName() + " executing request from Scheduler : " + message);
//
//            if (message.getArrivalFloor() != this.currentFloor) {
//                try {
//                    travelFloors(message.getArrivalFloor());
//                } catch (TimeoutException e) {
//                    break; // if a timeout failure occurs, stop the elevator
//                }
//            }
//
//            try {
//                injectTimeoutFailure(message); //check for timeout failure
//            } catch (TimeoutException e) {
//                break; // if a timeout failure occurs, stop the elevator
//            }
//
//            loadPassenger(currentFloor);
//            injectDoorFailure(message); //check for door stuck failure
//
//            try {
//                travelFloors(message.getDestinationFloor()); //travel to destination floor
//            } catch (TimeoutException e) {
//                break; // if a timeout failure occurs, stop the elevator
//            }
//
//            loadPassenger(currentFloor);
//            injectDoorFailure(message); //check for door stuck failure
//            arrivalStatus(currentFloor, currentState);
//
//            Message.Directions direction = Message.Directions.IDLE;
//            lampStatus(direction);
//            outgoingMessages.put(message); //echo the request message to indicate arrival at dest. floor
//        }
    }

    public void lampStatus(Message.Directions direction) {
        if (direction != Message.Directions.IDLE && (direction==Message.Directions.UP || direction==Message.Directions.DOWN)) {
            System.out.println("Lamp ON, elevator going " + direction);
        } else {
            System.out.println("Lamp OFF, elevator has arrived.");
        }
    }
    private void injectTimeoutFailure() throws TimeoutException {
            handleTimeout();
            for (ElevatorViewHandler view: views){
                view.handleTimeoutFailure(new ElevatorEvent(this, currentState));
            }
            System.out.println(Thread.currentThread().getName() + "TIMEOUT failure. Shutting down...");
            throw new TimeoutException();

    }
    private void injectDoorFailure(){
            for (ElevatorViewHandler view : views){
                view.handleDoorFailure(new ElevatorEvent(this, currentState));
            }
            System.out.println(Thread.currentThread().getName() + " DOOR STUCK. Attempting to close ...");
            try {
                Thread.sleep(2000); //add a delay for time taken to handle door failure
            } catch (InterruptedException e) {
            }
            doorStuck = (doorStuck + 1) % 2;

        currentState = state.DOOR_CLOSED;
        closeDoors();
    }

    private void handleTimeout(){
         sendAndReceive(new byte[]{(byte) elevatorId}, 66); // tell scheduler which elevator to shut down

        // TODO send messages back to scheduler to be rescheduled
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
        return currentFloor;
    }

    public void loadPassenger(int floor) {
        currentState = state.DOOR_OPEN;
        openDoors();
        Message.Directions direction = Message.Directions.IDLE;
        lampStatus(direction);
        try {
            Thread.sleep(10881); //based on iteration 0 (10.881 s to load 1 person)
        } catch (InterruptedException e) {
        }

    }

    public void openDoors(){
        try {
            Thread.sleep(3000); // simulate 3 seconds for doors opening
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.exit(1);
        }
        currentState = state.DOOR_OPEN;

        System.out.println(">>"+ new TimeStamp().getTimestamp() +" "+ Thread.currentThread().getName() + " at floor " + currentFloor + " - " + currentState );
        for (ElevatorViewHandler view : views){
            view.handleStateChange(new ElevatorEvent(this, currentState));
        }
    }

    public void closeDoors(){
        try {
            Thread.sleep(3000); // simulate 3 seconds for doors closing
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.exit(1);
        }
        currentState = state.DOOR_CLOSED;

        System.out.println(">>" + new TimeStamp().getTimestamp() +" "+Thread.currentThread().getName() + " at floor " + currentFloor + " - " + currentState );
        for (ElevatorViewHandler view : views){
            view.handleStateChange(new ElevatorEvent(this, currentState));
        }
    }

    //TODO figure out what this is doing
    public void arrivalStatus(int floor, state currentState){
        currentState = state.IDLE;
        System.out.println(">>" + new TimeStamp().getTimestamp() +" "+Thread.currentThread().getName() + " is " + currentState + " and has arrived at floor " + floor);
        for (ElevatorViewHandler view : views){
            view.handleStateChange(new ElevatorEvent(this, currentState));
        }
    }

    public synchronized void updateElevatorData() {
        elevatorData.getElevatorSubsystemStatus().get(elevatorId).setCurrentFloor(currentFloor);
        elevatorData.getElevatorSubsystemStatus().get(elevatorId).setCurrentDirection(elevatorDirection);
        elevatorData.getElevatorSubsystemStatus().get(elevatorId).setCurrentLoad(currentLoad);

    }


    public int getElevatorId() {
        return elevatorId;
    }

    /**
     * This data structure is used to store the requested stops for an elevator after it receives a command/Message
     */
    class RequestedStop {
        private int floor;
        private boolean isDestination;
        public RequestedStop(int floor, boolean isDestination){
            this.floor = floor;
            this.isDestination = isDestination;
        }
        public int getFloor(){return floor;}
        public boolean getIsDestination(){return isDestination;}
    }
}


