import static java.lang.Math.abs;

/**
 * This Class represents the Elevator which travels between floors according to
 * received messages from the scheduler class, and alerts the Scheduler upon arrival
 * at the destination floor.
 * @author Nikita Sara Vijay (101195009)
 * @author Areej Mahmoud 101218260
 * @author Khola Haseeb 101192363
 */
public class Elevator implements Runnable {
    //current floor that elevator is at
    private int floor, numFloors, elevatorId;
    //Message boxes for communication with Scheduler
    private MessageBox incomingMessages, outgoingMessages;

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
        System.out.println(Thread.currentThread().getName() + " moving from floor " + floor + " to floor " + destFloor);
        String direction;
        if((floor-destFloor)>0){
            direction = "Down";
        }else{direction = "Up";}

        lampStatus(direction);
        try {
            long travelTime = (long)(1429 *abs(floor-destFloor) +7399.8);
            Thread.sleep(travelTime); //simulate time taken to travel floors
        } catch (InterruptedException e) {
        }
        floor= destFloor; //arrive at destFloor
    }

    /**
     * Execute the Elevator thread operations. Receive messages from Scheduler
     * and sends the messages back through the MessageBox
     */
    @Override
    public void run() {
        while (true) {
            Message message = incomingMessages.get();
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


