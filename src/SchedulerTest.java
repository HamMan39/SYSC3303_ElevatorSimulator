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
        scheduler.setElevatorData(elevatorData);

        // Simulate new message from floor
        Message message = new Message("14:05:15.0", 2, Message.Directions.UP, 4);

        // Schedule message
        boolean scheduled = scheduler.schedule(message);

        // Check if message is scheduled
        assertTrue(scheduled);
    }

    @Test
    void testSchedulerDoesNotScheduleMessage() {
        // Create Scheduler instance
        Scheduler scheduler = new Scheduler();

        // Simulate elevator data
        ElevatorData elevatorData = new ElevatorData();
        elevatorData.getElevatorSubsystemStatus().get(0).setCurrentDirection(Message.Directions.DOWN); // Set elevator 0 going down
        elevatorData.getElevatorSubsystemStatus().get(0).setCurrentFloor(5); // Set elevator 0 at floor 5
        scheduler.setElevatorData(elevatorData);

        // Simulate new message from floor
        Message message = new Message("14:05:15.0", 2, Message.Directions.UP, 4);

        // Schedule message
        boolean scheduled = scheduler.schedule(message);

        // Check if message is not scheduled
        assertFalse(scheduled);
    }
}
