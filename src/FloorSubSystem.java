import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class FloorSubSystem implements Runnable{

    //Shared message box between this system and Scheduler
    MessageBox box;

    public FloorSubSystem(MessageBox box) {
        this.box = box;
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
                box.put(this.createMessage(data));
            }

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void run() {
        importData("input.txt");
    }
}
