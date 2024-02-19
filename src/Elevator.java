/**
 * This Class represents the Elevator Subsystem which sends and receives messages
 * to and from the scheduler class.
 */
public class Elevator implements Runnable {
    //current floor that elevator is at
    private int floor;
    //Message boxes for communication with Scheduler
    private MessageBox incomingMessages, outgoingMessages;

    /**
     * Constructor for class Elevator
     *
     * @param box3 Incoming messages MessageBox
     * @param box4 Outgoing messages MessageBox
     */
    public Elevator(MessageBox box3, MessageBox box4) {
        this.floor = 0;
        this.incomingMessages = box3;
        this.outgoingMessages = box4;
    }

    /**
     * Execute the Elevator thread operations. Receives messages from Scheduler
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
                System.out.println(Thread.currentThread().getName() + " moving from floor " + floor + " to floor " + message.getArrivalFloor());
                lampStatus(message.getDirection());
                try {
                    Thread.sleep(7 * 1000);
                } catch (InterruptedException e) {
                }
            }
            floor = message.getArrivalFloor();
            System.out.println(">>" + Thread.currentThread().getName() + " is at floor " + floor + ". Waiting for doors to close");
            lampStatus(message.getDirection());

            try {
                Thread.sleep(10 * 1000);
            } catch (InterruptedException e) {
            }

            System.out.println(Thread.currentThread().getName() + " doors closed. Moving to floor " + message.getDestinationFloor());
            try {
                Thread.sleep(7 * 1000);
            } catch (InterruptedException e) {
            }

            floor = message.getDestinationFloor();

            System.out.println(">>" + Thread.currentThread().getName() + " arrived at floor " + floor);

            String direction = null;
            lampStatus(direction); 
            outgoingMessages.put(message);
            System.out.println(Thread.currentThread().getName() + " sent message to Scheduler : " + message);

        }
    }

    public void lampStatus(String direction) {
        if (direction != null && (direction.equals("Up") || direction.equals("Down"))) {
            System.out.println("Lamp has been turned on, elevator going " + direction);
        } else {
            System.out.println("Lamp has been turned off, elevator has arrived.");
        }
    }
}
