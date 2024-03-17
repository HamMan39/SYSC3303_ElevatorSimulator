import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

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

    /**
     * Construct a new message from a byte array when receiving a datagram packet
     * @param messageData A byte[] representation of a Message created from toByteArray method
     */
    public Message(byte[] messageData){
        //Create an input stream to parse the byte array
        byte[] buffer = new byte[50];
        ByteArrayInputStream inStream = new ByteArrayInputStream(buffer);

        try {
            this.arrivalTime = Arrays.toString(inStream.readNBytes(10));
            this.arrivalFloor = inStream.read();
            this.direction = Directions.values()[inStream.read()];
            this.destinationFloor = inStream.read();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

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

    /**
     * Converts the Message object into a byte array so it can be sent as a datagram packet
     * @return A byte[] representation of the Message object
     */
    public byte[] toByteArray(){
        ByteArrayOutputStream messageBuilder = new ByteArrayOutputStream();
        try {
            messageBuilder.write(arrivalTime.getBytes());
            messageBuilder.write(arrivalFloor);
            messageBuilder.write(direction.ordinal());
            messageBuilder.write(destinationFloor);

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        return messageBuilder.toByteArray();
    }
}
