import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ElevatorTest {

    @Test
    public void run(){

        //create MessageBox instances for testing
        MessageBox incommingMessages = new MessageBox();
        MessageBox outgoingMessages = new MessageBox();

        // Create Elevator instance
        Elevator elevator = new Elevator(incommingMessages,outgoingMessages);

        // create thread for elevator and start it
        Thread elevatorThread = new Thread(elevator);
        elevatorThread.start();

        Message message = new Message("14:05:15.0",2, "Up", 4);

        // Add message to incommingMessages
        incommingMessages.put(message);

        // Check if outgoingMessages contains the test message
        assertEquals(message, outgoingMessages.get());
    }
}