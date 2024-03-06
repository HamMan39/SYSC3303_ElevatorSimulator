import java.util.ArrayList;
import java.util.Arrays;

public class ElevatorSubsystemData {
    private ArrayList elevator1Details;
    private ArrayList elevator2Details;
    private ArrayList elevator3Details;
    private ArrayList elevator4Details;
    public enum Directions {IDLE, UP, DOWN, UPDOWN, DOWNUP}


    public ElevatorSubsystemData(){
        elevator1Details = new ArrayList(Arrays.asList(0, Directions.IDLE, 0));
        elevator2Details = new ArrayList(Arrays.asList(0, Directions.IDLE, 0));
        elevator3Details = new ArrayList(Arrays.asList(0, Directions.IDLE, 0));
        elevator4Details = new ArrayList(Arrays.asList(0, Directions.IDLE, 0));



    }


}
