import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * A data structure class to store live updates on Elevator threads.
 * @author Owen Peterson 101233850
 */
public class ElevatorData {
    private ArrayList<ElevatorStatus> elevatorSubsystemStatus;


    public ElevatorData(){
        elevatorSubsystemStatus = new ArrayList<>();
        //Creates an array for all elevators to represent the current status for each
        for (int i = 4; i > 0; i--){
            elevatorSubsystemStatus.add(new ElevatorStatus());
        }
    }

    public boolean sameDirection(Message.Directions requestDirection, int arrivalFloor, int elevator) {
        boolean matchDirection = requestDirection.equals(elevatorSubsystemStatus.get(elevator).getCurrentDirection());
        boolean movingTowards;
        if (requestDirection == Message.Directions.UP) {
            movingTowards = arrivalFloor > elevatorSubsystemStatus.get(elevator).getCurrentFloor();
        } else {
            movingTowards = arrivalFloor < elevatorSubsystemStatus.get(elevator).getCurrentFloor();

        }
        return (matchDirection && movingTowards);
    }
    public boolean isIdle(int elevator){ // elevator should be elevator number, not index
        return (elevatorSubsystemStatus.get(elevator).getCurrentDirection().equals(Message.Directions.IDLE));
    }

    public Integer getElevatorPosition(Integer elevator){
        return elevatorSubsystemStatus.get(elevator).getCurrentFloor();
    }

    public Integer getElevatorLoad(Integer elevator){
        return elevatorSubsystemStatus.get(elevator).getCurrentLoad();
    }

    public ArrayList<ElevatorStatus> getElevatorSubsystemStatus() {
        return elevatorSubsystemStatus;
    }

    /**
     * updates each elevatorStatus with the information in the byte array
     * @param statusBytes A byte array version of another elevatorData class
     */
    public synchronized void updateStatus(byte[] statusBytes){
        //Create an input stream to parse the byte array
        ByteArrayInputStream inStream = new ByteArrayInputStream(statusBytes);

        for (ElevatorStatus elevator: elevatorSubsystemStatus) {
            elevator.setCurrentFloor(inStream.read());
            elevator.setCurrentDirection(Message.Directions.values()[inStream.read()]);
            elevator.setCurrentLoad(inStream.read());
        }
    }

    public byte[] toByteArray(){

        ByteArrayOutputStream byteArrayBuilder = new ByteArrayOutputStream();
        try {
            for (ElevatorStatus elevator: elevatorSubsystemStatus) {
                byteArrayBuilder.write(elevator.toByteArray());
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        return byteArrayBuilder.toByteArray();

    }
}

/**
 * A status  class to store live status updates on Elevator threads.
 * @author Owen Peterson 101233850
 */
class ElevatorStatus {
    private int currentFloor;
    private Message.Directions currentDirection;
    private int currentLoad; //Number of passengers being carried (cannot exceed capacity)

    public ElevatorStatus(){
        currentFloor = 0;
        currentDirection = Message.Directions.IDLE;
        currentLoad = 0;
    }
    public int getCurrentFloor(){return currentFloor;}
    public Message.Directions getCurrentDirection(){return currentDirection;}
    public int getCurrentLoad(){return currentLoad;}
    public void setCurrentFloor(int newFloor){currentFloor = newFloor;}
    public void setCurrentDirection(Message.Directions newDirection){currentDirection = newDirection;}
    public void setCurrentLoad(int newLoad){currentLoad = newLoad;}
    public byte[] toByteArray(){
        ByteArrayOutputStream byteArrayBuilder = new ByteArrayOutputStream();

        byteArrayBuilder.write(currentFloor);
        byteArrayBuilder.write(currentDirection.ordinal());
        byteArrayBuilder.write(currentLoad);

        return byteArrayBuilder.toByteArray();
    }
}
