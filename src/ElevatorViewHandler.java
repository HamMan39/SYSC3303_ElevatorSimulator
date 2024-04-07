/** This class represents the ElevatorViewHandler Interface. The class
 * has function declarations for handling Elevator events where the view is updated.
 * @author Areej Mahmoud 101218260
 * */
public interface ElevatorViewHandler {
    public void handleStateChange(ElevatorEvent e);
    public void handleCapacityChange(ElevatorEvent e);
    public void handleTravelFloor(ElevatorEvent e);
    public void handleTimeoutFailure(ElevatorEvent e);
    public void handleDoorFailure(ElevatorEvent e);

}
