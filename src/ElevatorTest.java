import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;
/**
 * Unit tests for Class Elevator
 * @author Khola Haseeb 101192363, Mahnoor Fatima 101192353
 */
public class ElevatorTest {

    @Test
    public void testRun(){

        ElevatorView view = new ElevatorView();

        //create MessageBox instances for testing
        MessageBox incomingMessages = new MessageBox();
        MessageBox outgoingMessages = new MessageBox();
        ElevatorData elevatorData;

        elevatorData = new ElevatorData();

        // Create Elevator instance
        Elevator elevator = new Elevator(0,20,incomingMessages,outgoingMessages,elevatorData, view);

        // create thread for elevator and start it
        Thread elevatorThread = new Thread(elevator);
        elevatorThread.start();

        Message message = new Message("14:05:15.0",2, Message.Directions.UP, 4, Message.Failures.NONE);

        // Add message to incomingMessages
        incomingMessages.put(message);

        // Check if outgoingMessages contains the test message
        assertEquals(message, outgoingMessages.get());

        //Testing Elevator run if Door fault present
        Message message2 = new Message("15:06:15.0",2, Message.Directions.UP, 4, Message.Failures.DOORS);

        // Add message to incomingMessages
        incomingMessages.put(message2);

        // Check if outgoingMessages contains the test message
        assertEquals(message2, outgoingMessages.get());
    }

    @Test
    public void testTravelFloors(){
        ElevatorView view = new ElevatorView();

        //create MessageBox instances for testing
        MessageBox incomingMessages = new MessageBox();
        MessageBox outgoingMessages = new MessageBox();
        ElevatorData elevatorData;

        elevatorData = new ElevatorData();

        // Create Elevator instance
        Elevator elevator = new Elevator(0,20, elevatorData, view);

        // create thread for elevator and start it
        Thread elevatorThread = new Thread(elevator);

        // -- Testing with no failures
        Message message = new Message("14:05:15.0",2, Message.Directions.UP, 4, Message.Failures.NONE);

        // Add message to incomingMessages
        incomingMessages.put(message);

        //Initial Floor should be 0 and not the destination floor
        assertEquals(0, elevator.getCurrentFloor());
        assertNotEquals(message.getDestinationFloor(), elevator.getCurrentFloor());

        //Travel to the destination floor

        elevator.travelFloor();


        // Check if the destination floor matches
        assertEquals(message.getArrivalFloor(), 2);
    }
}