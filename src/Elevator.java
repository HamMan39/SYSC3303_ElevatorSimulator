import static java.lang.Math.abs;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.ArrayList;

/**
 * This Class represents the Elevator which travels between floors according to
 * received messages from the scheduler class, and alerts the Scheduler upon arrival
 * at the destination floor.
 * @author Nikita Sara Vijay 101195009
 * @author Areej Mahmoud 101218260
 * @author Khola Haseeb 101192363
 */
public class Elevator implements Runnable {
    //current floor that elevator is at
    private int floor, numFloors, elevatorId;
    //Message boxes for communication with Scheduler
    private MessageBox incomingMessages, outgoingMessages;

    //Messages that have been read but not serviced - pending messages
    private ArrayList<Message> pendingMessages;

    /**
     * Constructor for class Elevator
     *
     * @param box3 Incoming messages MessageBox
     * @param box4 Outgoing messages MessageBox
     */
    public Elevator(int elevatorId, int numFloors, MessageBox box3, MessageBox box4) {
        this.floor = 0;
        this.outgoingMessages = box4;
        this.incomingMessages = box3;
        this.elevatorId = elevatorId;
        this.numFloors = numFloors;
    }
    /**
     * Simulate Elevator travelling from current floor to destFloor
     * @param destFloor the destination floor.
     */
    public void travelFloors(int destFloor){
        //Processing of other messages while one is being served will result in situation where we
        //have a list of floors to stop at - these pending floors should also be stored
        SortedSet<Integer> pendingStops = new TreeSet<>();

        System.out.println(Thread.currentThread().getName() + " moving from floor " + floor + " to floor " + destFloor);
        String direction;
        if((floor-destFloor)>0){
            direction = "Down";
        }else{direction = "Up";}

        lampStatus(direction);
        while(floor != destFloor) {
            try {
                long travelTime = 1429;
                Thread.sleep(travelTime); //simulate time taken to travel floors
            } catch (InterruptedException e) {
            }
            if(floor < destFloor)
                floor++;
            else
                floor--;

            // Go through pending messages
            Message message = null;
            int n = pendingMessages.size();
            for(int i = 0; i < n; i++) {
                message = pendingMessages.remove(0);

                // Checks whether this service can be processed
                if(message.getDirection().compareToIgnoreCase(direction) != 0
                        || (direction.equals("Up")
                        && (message.getArrivalFloor() < floor || message.getArrivalFloor() > destFloor))
                        || (direction.equals("Down")
                        && (message.getArrivalFloor() > floor || message.getArrivalFloor() < destFloor))) {
                    pendingMessages.add(message);    //Adds this request to the list of pending messages
                    continue;
                }

                // Request can be processed, so add a stop for it
                pendingStops.add(message.getArrivalFloor());
                // Check if this request will result in modifying the destination, and add a stop accordingly
                if(direction.equals("Up") && message.getDestinationFloor() > destFloor
                        || direction.equals("Down") && message.getDestinationFloor() < destFloor) {
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
                if(message.getDirection().compareToIgnoreCase(direction) != 0
                        || (direction.equals("Up")
                        && (message.getArrivalFloor() < floor || message.getArrivalFloor() > destFloor))
                        || (direction.equals("Down")
                        && (message.getArrivalFloor() > floor || message.getArrivalFloor() < destFloor))) {
                    pendingMessages.add(message);
                    continue;
                }
                pendingStops.add(message.getArrivalFloor());
                if(direction.equals("Up") && message.getDestinationFloor() > destFloor
                        || direction.equals("Down") && message.getDestinationFloor() < destFloor) {
                    pendingStops.add(destFloor);
                    destFloor = message.getDestinationFloor();
                } else {
                    pendingStops.add(message.getDestinationFloor());
                }
            }
            Integer first = null;
            // The pending stops is in sorted order
            // If it is going up, processing will be done in ascending order, for going down, it will be descending
            if(direction.equals("Up"))
                first = pendingStops.first();
            else
                first = pendingStops.last();       //When travelling down, checks for the largest # and services that floor
            if(first == null || first != floor)
                continue; // no stop at current floor

            // stop at current floor
            pendingStops.remove(first);
        }
        try {
            long time = (long)7399.8;
            Thread.sleep(time);
        } catch(InterruptedException e) {}
        //floor= destFloor; //arrive at destFloor
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
            if(pendingMessages.size() > 0) {
                message = pendingMessages.remove(0);
            } else {
                incomingMessages.get();
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
            System.out.println(">>" + Thread.currentThread().getName() + " at floor " + floor + ". Waiting for doors to close");
            lampStatus(message.getDirection());

            try {
                Thread.sleep(10881); //based on iteration 0 (10.881 s to load 1 person)
            } catch (InterruptedException e) {
            }

            travelFloors(message.getDestinationFloor()); //travel to destination floor

            System.out.println(">>" + Thread.currentThread().getName() + " arrived at floor " + floor);

            String direction = null;
            lampStatus(direction);
            outgoingMessages.put(message); //echo the request message to indicate arrival at dest. floor
//            System.out.println(Thread.currentThread().getName() + " sent message to Scheduler : " + message);

        }
    }

    public void lampStatus(String direction) {
        if (direction != null && (direction.equals("Up") || direction.equals("Down"))) {
            System.out.println("Lamp ON, elevator going " + direction);
        } else {
            System.out.println("Lamp OFF, elevator has arrived.");
        }
    }

    public Integer getCurrentFloor(){
        return floor;
    }
}


