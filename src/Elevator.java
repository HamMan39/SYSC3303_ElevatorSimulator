/**
 * This Class represents the Elevator Subsystem which sends and receives messages
 * to and from the scheduler class.
 * @author Khola Haseeb
 */
public class Elevator implements Runnable {
    //current floor that elevator is at
    private int floor;
    //Message boxes for communication with Scheduler
    private MessageBox incomingMessages, outgoingMessages;

    /**
     * Constructor for class Elevator
     * @param box3 Incoming messages MessageBox
     * @param box4 Outgoing messages MessageBox
     */
    public Elevator(MessageBox box3, MessageBox box4){
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
        while(true){
            Message message = incomingMessages.get();
            if (message == null){
                System.out.println("Elevator System Exited");
                outgoingMessages.put(null);
                return;
            }
            System.out.println(Thread.currentThread().getName() + " received message from Scheduler : " + message);

            outgoingMessages.put(message);
            System.out.println(Thread.currentThread().getName() + " sent message to Scheduler : " + message);

        }
    }
}
