import java.util.ArrayList;
import java.util.Arrays;

public class ElevatorSubsystemData {
    private ArrayList<ArrayList> elevatorsStatus;

    public enum Directions {IDLE, UP, DOWN, UPDOWN, DOWNUP}


    public ElevatorSubsystemData(){
        elevatorsStatus = new ArrayList<>();
        //Creates an array for all elevators to represent the current status for each
        for (int i = 4; i > 0; i--){
            elevatorsStatus.add(new ArrayList(Arrays.asList(0, Directions.IDLE, 0)));
        }
    }

    public boolean sameDirection(Directions requestDirection, int elevator) {
        return (requestDirection.equals(elevatorsStatus.get(elevator).get(1)));
    }
    public boolean soonSameDirection(Directions requestDirection, int elevator) {
        if (requestDirection.equals(Directions.UP)){
            return (elevatorsStatus.get(elevator).get(1).equals(Directions.DOWNUP));
        } else {
            return (elevatorsStatus.get(elevator).get(1).equals(Directions.UPDOWN));
        }
    }
    public boolean isIdle(int elevator){
        return (elevatorsStatus.get(elevator).get(1).equals(Directions.IDLE));
    }

    public synchronized void updateStatus(int elevator, ArrayList status){
        elevatorsStatus.set(elevator, status);
    }

    //TODO either needs to ensure mutual exlcusion or alternative access to information
    public synchronized ArrayList getStatus(){
        return elevatorsStatus;
    }

}
