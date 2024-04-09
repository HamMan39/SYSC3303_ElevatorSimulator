import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
/**
 * Unit tests for Class ElevatorData
 * @author Mahnoor Fatima 101192353
 */
public class ElevatorDataTest {

    @Test
    public void testSameDirection() {
        // Create ElevatorData object
        ElevatorData elevatorData = new ElevatorData();

        // Set up ElevatorStatus objects with specific directions
        ElevatorStatus elevator1 = elevatorData.getElevatorSubsystemStatus().get(0);
        elevator1.setCurrentDirection(Message.Directions.UP);
        elevator1.setCurrentFloor(0);
        ElevatorStatus elevator2 = elevatorData.getElevatorSubsystemStatus().get(1);
        elevator2.setCurrentDirection(Message.Directions.DOWN);
        elevator2.setCurrentFloor(5);

        // Debug information
        System.out.println("Elevator 1 direction: " + elevator1.getCurrentDirection() + ". Current floor: " + elevator1.getCurrentFloor());
        System.out.println("Elevator 2 direction: " + elevator2.getCurrentDirection() + ". Current floor: " + elevator2.getCurrentFloor());

        // Test sameDirection method
        assertTrue(elevatorData.sameDirection(Message.Directions.UP, 2,0));
        assertFalse(elevatorData.sameDirection(Message.Directions.UP, 4, 1));
    }

    @Test
    public void testIsIdle() {
        ElevatorData elevatorData = new ElevatorData();

        // Set up ElevatorStatus objects with specific directions
        ElevatorStatus elevatorStatus1 = elevatorData.getElevatorSubsystemStatus().get(0);
        elevatorStatus1.setCurrentDirection(Message.Directions.IDLE);
        ElevatorStatus elevatorStatus2 = elevatorData.getElevatorSubsystemStatus().get(1);
        elevatorStatus2.setCurrentDirection(Message.Directions.UP);

        // Test isIdle method
        assertTrue(elevatorData.isIdle(0));
        assertFalse(elevatorData.isIdle(1));
    }

    @Test
    public void testGetElevatorPosition() {

        ElevatorData elevatorData = new ElevatorData();

        // Mock elevator subsystem status
        ElevatorStatus elevatorStatus1 =  elevatorData.getElevatorSubsystemStatus().get(0);
        elevatorStatus1.setCurrentFloor(5);
        ElevatorStatus elevatorStatus2 = elevatorData.getElevatorSubsystemStatus().get(1);
        elevatorStatus2.setCurrentFloor(10);

        // Test getElevatorPosition method
        assertEquals(5, elevatorData.getElevatorPosition(0));
        assertEquals(10, elevatorData.getElevatorPosition(1));
    }
}
