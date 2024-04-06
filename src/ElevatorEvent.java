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
    /**
     * Constructs an elevator Event.
     * @param source the Elevator object on which the Event initially occurred
     * @throws IllegalArgumentException if source is null
     */
    public ElevatorEvent(Elevator source) {
        super(source);
    }

    /**
     * Elevator Failure event constructor
     * @param source
     * @param failure
     */
    public ElevatorEvent(Elevator source, Message.Failures failure){
        super(source);
        this.failure = failure;
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

    public Message.Directions getDirection() {
        return direction;
    }

    public Message.Failures getFailure() {
        return failure;
    }

    public Elevator.state getCurrState() {
        return currState;
    }
}
