import java.util.ArrayList;
import java.util.Arrays;

public class ElevatorData {
    private ArrayList<ElevatorStatus> elevatorSubsystemStatus;


    public ElevatorData(){
        elevatorSubsystemStatus = new ArrayList<>();
        //Creates an array for all elevators to represent the current status for each
        for (int i = 4; i > 0; i--){
            elevatorSubsystemStatus.add(new ElevatorStatus());
        }
    }

    public boolean sameDirection(Message.Directions requestDirection, int elevator) {
        return (requestDirection.equals(elevatorSubsystemStatus.get(elevator).getCurrentDirection()));
    }
    public boolean soonSameDirection(Message.Directions requestDirection, int elevator) {
        if (requestDirection.equals(Message.Directions.UP)){
            return (elevatorSubsystemStatus.get(elevator).getCurrentDirection().equals(Message.Directions.DOWNUP));
        } else {
            return (elevatorSubsystemStatus.get(elevator).getCurrentDirection().equals(Message.Directions.UPDOWN));
        }
    }
    public boolean isIdle(int elevator){
        return (elevatorSubsystemStatus.get(elevator).getCurrentDirection().equals(Message.Directions.IDLE));
    }

    /**
     * updates each elevatorStatus with the information in the byte array
     * @param statusBytes A byte array version of another elevatorData class
     */
    public synchronized void updateStatus(byte[] statusBytes){
        return;
    }


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
        public void setCurrentFloor(int newFloor){currentLoad = newFloor;}
        public void setCurrentDirection(Message.Directions newDirection){currentDirection = newDirection;}
        public void setCurrentLoad(int newLoad){currentLoad = newLoad;}
    }
}
