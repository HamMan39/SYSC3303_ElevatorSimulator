import java.util.ArrayList;
import java.util.Arrays;

public class ElevatorSubsystemData {
    private ArrayList<ArrayList> elevatorsStatus;

    public enum Directions {IDLE, UP, DOWN, UPDOWN, DOWNUP}


    public ElevatorSubsystemData(){
        elevatorsStatus = new ArrayList<>();
        for (int i = 4; i > 0; i--){
            elevatorsStatus.add(new ArrayList(Arrays.asList(0, Directions.IDLE, 0)));
        }
    }

    public synchronized void updateStatus(int elevator, ArrayList status){
        elevatorsStatus.set(elevator, status);
    }

    //TODO either needs to ensure mutual exlcusion or alternative access to information
    public synchronized ArrayList getStatus(){
        return elevatorsStatus;
    }

}
