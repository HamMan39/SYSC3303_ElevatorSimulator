import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SchedulerTest {

    @Test
    void testSchedulerSchedulesMessage() {
        // Create Scheduler instance
        Scheduler scheduler = new Scheduler(4);

        //create MessageBox instances for testing
        MessageBox incomingMessages = new MessageBox();
        MessageBox outgoingMessages = new MessageBox();
        ElevatorSubsystem elevatorSubsystem = new ElevatorSubsystem(4,21,incomingMessages,outgoingMessages);

        Thread thread = new Thread(elevatorSubsystem, "E");
        thread.start();

        // Simulate new message from floor
        Message message = new Message("14:05:15.0", 2, Message.Directions.UP, 4, Message.Failures.NONE);

        // Schedule message
        boolean scheduled = scheduler.schedule(message);

        // Check if message is scheduled
        assertTrue(scheduled);
    }
}
