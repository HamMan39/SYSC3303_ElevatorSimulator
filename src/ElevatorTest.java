import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
/**
 * Unit tests for Class Elevator
 * @author Khola Haseeb 101192363, Mahnoor Fatima 101192353
 */
public class ElevatorTest {

    @Test
    public void testRun(){

        //create MessageBox instances for testing
        MessageBox incomingMessages = new MessageBox();
        MessageBox outgoingMessages = new MessageBox();

        // Create Elevator instance
        Elevator elevator = new Elevator(0,20,incomingMessages,outgoingMessages);

        // create thread for elevator and start it
        Thread elevatorThread = new Thread(elevator);
        elevatorThread.start();

        Message message = new Message("14:05:15.0",2, "Up", 4);

        // Add message to incomingMessages
        incomingMessages.put(message);

        // Check if outgoingMessages contains the test message
        assertEquals(message, outgoingMessages.get());
    }

    @Test
    public void testTravelFloors(){
        //create MessageBox instances for testing
        MessageBox incomingMessages = new MessageBox();
        MessageBox outgoingMessages = new MessageBox();

        // Create Elevator instance
        Elevator elevator = new Elevator(0,20,incomingMessages,outgoingMessages);

        // create thread for elevator and start it
        Thread elevatorThread = new Thread(elevator);
        elevatorThread.start();

        Message message = new Message("14:05:15.0",2, "Up", 4);

        // Add message to incomingMessages
        incomingMessages.put(message);

        //Initial Floor should be 0 and not the destination floor
        assertEquals(0, elevator.getCurrentFloor());
        assertNotEquals(message.getDestinationFloor(), elevator.getCurrentFloor());

        //Travel to the destination floor
        elevator.travelFloors(message.getDestinationFloor());

        // Check if the destination floor matches
        assertEquals(message.getDestinationFloor(), elevator.getCurrentFloor());
    }
}