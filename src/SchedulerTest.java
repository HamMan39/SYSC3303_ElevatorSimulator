import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SchedulerTest {

    @Test
    void testSchedulerSchedulesMessage() {
        // Create Scheduler instance
        Scheduler scheduler = new Scheduler();

        // Simulate elevator data
        ElevatorData elevatorData = new ElevatorData();
        elevatorData.getElevatorSubsystemStatus().get(0).setCurrentDirection(Message.Directions.UP); // Set elevator 0 going up
        elevatorData.getElevatorSubsystemStatus().get(0).setCurrentFloor(3); // Set elevator 0 at floor 3

        // Simulate new message from floor
        Message message = new Message("14:05:15.0", 2, Message.Directions.UP, 4);

        // Schedule message
        boolean scheduled = scheduler.schedule(message);

        // Check if message is scheduled
        assertTrue(scheduled);
    }
}
