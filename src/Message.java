/**
 * This Class represents a message passed between the scheduler and a SubSystem class.
 * The message contains raw data to start the elevator simulation.
 * @author Areej Mahmoud 101218260
 */
public class Message {
    //Timestamp person presses the floor button
    String arrivalTime;
    //Floor number where button was pressed
    int arrivalFloor;
    //Direction of floor button pressed
    Directions direction;
    //Dest Elevator button pressed
    int destinationFloor;

    public enum Directions {IDLE, UP, DOWN, UPDOWN, DOWNUP}


    /**
     * Constructor for class Message.
     * */
    public Message(String arrivalTime, int arrivalFloor, Directions direction, int destinationFloor) {
        this.arrivalTime = arrivalTime;
        this.arrivalFloor = arrivalFloor;
        this.direction = direction;
        this.destinationFloor = destinationFloor;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public int getArrivalFloor() {
        return arrivalFloor;
    }

    public Directions getDirection() {
        return direction;
    }

    public int getDestinationFloor() {
        return destinationFloor;
    }

    @Override
    public String toString() {
        return arrivalTime + " " + arrivalFloor +
                " " + direction+ " " + destinationFloor;
    }
}
