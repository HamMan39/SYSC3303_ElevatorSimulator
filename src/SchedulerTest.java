import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
/**
 * Unit tests for Class Scheduler
 * @author Areej Mahmoud 101218260
 */
class SchedulerTest {

    @Test
    void run() {

        //create MessageBox instances for testing
        MessageBox incomingFloor = new MessageBox(); // incomingFloor box
        MessageBox outgoingFloor = new MessageBox(); // outgoingFloor box
        MessageBox incomingElevator = new MessageBox(); //incomingElevator box
        MessageBox outgoingElevator = new MessageBox(); //outgoingElevator box

        // Create Scheduler instance
        Scheduler scheduler = new Scheduler(incomingFloor, outgoingFloor, incomingElevator, outgoingElevator);

        // create thread for scheduler and start it to execute run()
        Thread schedulerThread = new Thread(scheduler);
        schedulerThread.start();

        Message floorMsg = new Message("14:05:15.0",2, "Up", 4);
        Message elevMsg = new Message("14:05:15.0",6, "Down", 3);

        // Add message to incomingFloor
        incomingFloor.put(floorMsg);
        //Add message to outgoingElevator
        outgoingElevator.put(elevMsg);

        // Check that floor message is passed to elevator through incomingElevator box
        assertEquals(floorMsg.toString(), incomingElevator.get().toString());
        // Check that elevator message is passed to floor through outgoingFloor box
        assertEquals(elevMsg.toString(), outgoingFloor.get().toString());

    }
}
