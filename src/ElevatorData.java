import java.util.ArrayList;
import java.util.Arrays;

public class ElevatorData {
    private ArrayList<ArrayList> elevatorsStatus;


    public ElevatorData(){
        elevatorsStatus = new ArrayList<>();
        //Creates an array for all elevators to represent the current status for each
        for (int i = 4; i > 0; i--){
            elevatorsStatus.add(new ArrayList(Arrays.asList(0, Message.Directions.IDLE, 0)));
        }
    }

    public boolean sameDirection(Message.Directions requestDirection, int elevator) {
        return (requestDirection.equals(elevatorsStatus.get(elevator).get(1)));
    }
    public boolean soonSameDirection(Message.Directions requestDirection, int elevator) {
        if (requestDirection.equals(Message.Directions.UP)){
            return (elevatorsStatus.get(elevator).get(1).equals(Message.Directions.DOWNUP));
        } else {
            return (elevatorsStatus.get(elevator).get(1).equals(Message.Directions.UPDOWN));
        }
    }
    public boolean isIdle(int elevator){
        return (elevatorsStatus.get(elevator).get(1).equals(Message.Directions.IDLE));
    }

    public synchronized void updateStatus(int elevator, ArrayList status){
        elevatorsStatus.set(elevator, status);
    }

    //TODO either needs to ensure mutual exlcusion or alternative access to information
    public synchronized ArrayList getStatus(){
        return elevatorsStatus;
    }

}
