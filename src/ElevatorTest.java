import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;
/**
 * Unit tests for Class Elevator
 * @author Khola Haseeb 101192363, Mahnoor Fatima 101192353
 */
public class ElevatorTest {

    @Test
    public void testGiveRequest(){

        //create MessageBox instances for testing
        MessageBox incomingMessages = new MessageBox();
        MessageBox outgoingMessages = new MessageBox();

        Scheduler scheduler = new Scheduler(4);
        Thread schedulerThread = new Thread(scheduler, "Scheduler Thread");
        schedulerThread.start();
        ElevatorSubsystem elevatorSubsystem = new ElevatorSubsystem(4,21,incomingMessages,outgoingMessages);

        // -- Testing with no failures
        Message message = new Message("14:05:15.0",1,Message.Directions.UP, 3,Message.Failures.NONE);
        // -- Testing with Door Failure
        Message message2 = new Message("14:05:15.0",1,Message.Directions.UP, 3,Message.Failures.DOORS);

        Message message3 = new Message("14:05:15.0",1,Message.Directions.UP, 3,Message.Failures.TIMEOUT);


        elevatorSubsystem.getElevator(0).giveRequest(message);
        elevatorSubsystem.getElevator(1).giveRequest(message2);
        elevatorSubsystem.getElevator(2).giveRequest(message3);

        try {
            // Sleep for 50 seconds (50000 milliseconds)
            Thread.sleep(50000);
        } catch (InterruptedException e) {
            // Handle interrupted exception
            e.printStackTrace();
        }

        //Test if current floor and state are accurate
        assertEquals( message.getDestinationFloor(), elevatorSubsystem.getElevator(0).getCurrentFloor());
        assertEquals( message2.getDestinationFloor(), elevatorSubsystem.getElevator(1).getCurrentFloor());
        assertEquals( Elevator.state.DISABLED, elevatorSubsystem.getElevator(2).getCurrentState());
    }
}


