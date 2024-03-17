import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;
/**
 * This Class represents the FloorSubsystem which reads the simulation input
 * and sends the data to the scheduler class.
 * @author Areej Mahmoud 101218260
 */

public class Floor extends RPCClient implements Runnable{
    private ArrayList<String> buffer;
    private static int schedulerPort = 67;

    public Floor() {
        super();
        this.buffer = new ArrayList<>();
    }

    /**
     * Returns a Message object representing the input str.
     * @param str the input line containing a message.
     * @return new Message object
     * */
    public Message createMessage(String str){
        System.out.println(str);
        String[] data = str.split(" ");
        String timestamp = data[0];
        int arrivalFloor = Integer.valueOf(data[1]);

        Message.Directions direction;
        if (data[2].equals("UP")) {
            direction = Message.Directions.UP;
        } else {
            direction = Message.Directions.DOWN;
        }

        int destFloor = Integer.valueOf(data[3]);
        Message newMsg = new Message(timestamp, arrivalFloor, direction, destFloor);

        return newMsg;
    }

    /**
     * Imports simulation input data by reading each line of the input text file
     * and places data into buffer.
     * @param fileName the text file containing the raw data
     */
    public void importData(String fileName) {
        try {
            File file = new File(fileName);
            Scanner reader = new Scanner(file);
            while (reader.hasNextLine()) {
                String data = reader.nextLine();
                //place the new request into buffer
                buffer.add(data);
            }

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

    }
    /**
     * Send DatagramPacket to Host, then wait to receive a response packet
     */
    public void sendData()
    {
        // Create the specified request type: read/write
        String msg;
        byte[] msgBytes;
        for(int i=0; i<buffer.size(); i++) {
            msg = buffer.get(i);
            msgBytes = createMessage(msg).toByteArray();

            sendAndReceive(msgBytes, schedulerPort);

            System.out.println("Client: Packet sent.\n");
        }
    }
    /**
     * Execute the thread operations. Imports data from file
     * and sends/receives messages to and from Scheduler
     */
    @Override
    public void run() {
        importData("input.txt");
        sendData();
    }
    public static void main(String args[])
    {
        Thread f = new Thread(new Floor());
        f.start();
    }
}
