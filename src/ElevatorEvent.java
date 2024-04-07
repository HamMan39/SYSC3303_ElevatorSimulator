import java.util.EventObject;

/**
 * This class represents an Elevator Event that causes a change of state
 * or a change in the Elevator View.
 * @author Areej Mahmoud 101218260
 */
public class ElevatorEvent extends EventObject {
    private Message.Directions direction;
    private Message.Failures failure;
    private  Elevator.state currState;
    private int capacity;
    /**
     * Constructs an elevator Event.
     * @param source the Elevator object on which the Event initially occurred
     * @throws IllegalArgumentException if source is null
     */
    public ElevatorEvent(Elevator source) {
        super(source);
    }

    /**
     * Elevator travel or change of state event
     * @param source
     * @param dir
     * @param state
     */
    public ElevatorEvent(Elevator source, Message.Directions dir, Elevator.state state){
        super(source);
        this.direction = dir;
        this.currState = state;
    }

    /**
     * Elevator change of state event
     * @param source
     * @param state
     */
    public ElevatorEvent(Elevator source, Elevator.state state){
        super(source);
        this.currState = state;
    }

    /**
     * Elevator change of state event
     * @param source
     * @param capacity
     */
    public ElevatorEvent(Elevator source, int capacity){
        super(source);
        this.capacity = capacity;
    }

    public Message.Directions getDirection() {
        return direction;
    }

    public int getCapacity(){return capacity;}

    public Elevator.state getCurrState() {
        return currState;
    }
}
