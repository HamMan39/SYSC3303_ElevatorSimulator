import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
/**
 * This Class represents the FloorSubsystem which reads the simulation input
 * and sends the data to the scheduler class.
 * @author Areej Mahmoud 101218260
 */

public class Floor implements Runnable{

    //Message boxes for communication with Scheduler
    MessageBox outgoingMessages, incomingMessages;

    public Floor(MessageBox box1, MessageBox box2) {
        this.outgoingMessages = box1;
        this.incomingMessages = box2;

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
        String direction = data[2];
        int destFloor = Integer.valueOf(data[3]);
        Message newMsg = new Message(timestamp, arrivalFloor, direction, destFloor);

        return newMsg;
    }

    /**
     * Imports simulation input data by reading each line of the input text file
     * @param fileName the text file containing the raw data
     */
    public void importData(String fileName) {
        try {
            File file = new File(fileName);
            Scanner reader = new Scanner(file);
            while (reader.hasNextLine()) {
                String data = reader.nextLine();
                //put the new message in the shared box
                Message newMsg = this.createMessage(data);
                outgoingMessages.put(newMsg);
                System.out.println(Thread.currentThread().getName() + " sent message to Scheduler : " + newMsg);
            }
            outgoingMessages.put(null);

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Execute the thread operations. Imports data from file and aends
     * and receives messages to and from Scheduler
     */
    @Override
    public void run() {
        importData("input.txt");
        while (true){
            while(!incomingMessages.empty()) {
                Message floorMessage = incomingMessages.get();
                System.out.println(Thread.currentThread().getName() + " received message from Scheduler : " + floorMessage);

                if (floorMessage == null) {
                    System.out.println("Floor System Exited");
                    return;
                }
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {}
        }
    }
}
