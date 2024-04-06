//import org.junit.jupiter.api.Test;
//import static org.junit.jupiter.api.Assertions.*;
//
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Arrays;
//
//public class ElevatorDataTest {
//
//    @Test
//    public void testSameDirection() {
//        // Create ElevatorData object
//        ElevatorData elevatorData = new ElevatorData();
//
//        // Set up ElevatorStatus objects with specific directions
//        ElevatorStatus elevator1 = new ElevatorStatus();
//        elevator1.setCurrentDirection(Message.Directions.UP);
//        ElevatorStatus elevator2 = new ElevatorStatus();
//        elevator2.setCurrentDirection(Message.Directions.DOWN);
//        elevatorData.getElevatorSubsystemStatus().addAll(Arrays.asList(elevator1, elevator2));
//
//        // Debug information
//        System.out.println("Elevator 1 direction: " + elevator1.getCurrentDirection());
//        System.out.println("Elevator 2 direction: " + elevator2.getCurrentDirection());
//        System.out.println(elevatorData.getElevatorSubsystemStatus().get(2).getCurrentDirection());
//
//        // Test sameDirection method
//        assertTrue(elevatorData.sameDirection(Message.Directions.UP, 0));
//        assertFalse(elevatorData.sameDirection(Message.Directions.UP, 1));
//    }
//
//    @Test
//    public void testIsIdle() {
//        // Mock elevator subsystem status
//        ElevatorStatus elevatorStatus1 = new ElevatorStatus();
//        elevatorStatus1.setCurrentDirection(Message.Directions.IDLE);
//        ElevatorStatus elevatorStatus2 = new ElevatorStatus();
//        elevatorStatus2.setCurrentDirection(Message.Directions.UP);
//        ArrayList<ElevatorStatus> elevatorStatusList = new ArrayList<>(Arrays.asList(elevatorStatus1, elevatorStatus2));
//
//        // Create ElevatorData object
//        ElevatorData elevatorData = new ElevatorData();
//        elevatorData.updateStatus(getStatusBytes(elevatorStatusList));
//
//        // Test isIdle method
//        assertTrue(elevatorData.isIdle(0));
//        assertFalse(elevatorData.isIdle(1));
//    }
//
//    @Test
//    public void testGetElevatorPosition() {
//        // Mock elevator subsystem status
//        ElevatorStatus elevatorStatus1 = new ElevatorStatus();
//        elevatorStatus1.setCurrentFloor(5);
//        ElevatorStatus elevatorStatus2 = new ElevatorStatus();
//        elevatorStatus2.setCurrentFloor(10);
//        ArrayList<ElevatorStatus> elevatorStatusList = new ArrayList<>(Arrays.asList(elevatorStatus1, elevatorStatus2));
//
//        // Create ElevatorData object
//        ElevatorData elevatorData = new ElevatorData();
//        elevatorData.updateStatus(getStatusBytes(elevatorStatusList));
//
//        // Test getElevatorPosition method
//        assertEquals(5, elevatorData.getElevatorPosition(0));
//        assertEquals(10, elevatorData.getElevatorPosition(1));
//    }
//
//    private byte[] getStatusBytes(ArrayList<ElevatorStatus> elevatorStatusList) {
//        ByteArrayOutputStream byteArrayBuilder = new ByteArrayOutputStream();
//        for (ElevatorStatus elevator : elevatorStatusList) {
//            byte[] statusBytes = elevator.toByteArray();
//            if (statusBytes.length != 3) {
//                throw new IllegalArgumentException("Invalid byte array length in ElevatorStatus");
//            }
//            byteArrayBuilder.write(statusBytes, 0, statusBytes.length);
//        }
//        return byteArrayBuilder.toByteArray();
//    }
//
//
//}
