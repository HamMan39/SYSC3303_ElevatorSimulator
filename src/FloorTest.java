import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
/**
 * Unit tests for Class Floor
 * @author Areej Mahmoud 101218260
 */
class FloorTest {

    @Test
    void testImportData() {
        //create MessageBox instances for testing
        MessageBox incommingMessages = new MessageBox();
        MessageBox outgoingMessages = new MessageBox();

        // Create Floor instance
        Floor floor = new Floor(incommingMessages,outgoingMessages);

        //import test data from file
        floor.importData("input.txt");

        String testData1 = "14:05:15.0 2 Up 4";
        String testData2 = "16:09:23:0 3 Down 6";

        // Check if incommingMessages contains the test data
        assertEquals(testData1, incommingMessages.get().toString());
        assertEquals(testData2, incommingMessages.get().toString());

    }

    @Test
    void tesrRun() {
        //create MessageBox instances for testing
        MessageBox incommingMessages = new MessageBox();
        MessageBox outgoingMessages = new MessageBox();

        // Create Floor instance
        Floor floor = new Floor(incommingMessages,outgoingMessages);

        // create thread for floor and start it to execute run()
        Thread floorThread = new Thread(floor);
        floorThread.start();

        Message message = new Message("14:05:15.0",2, "Up", 4);

        // Add message to outgoingMessages
        outgoingMessages.put(message);

        // Check if incommingMessages contains the test message
        assertEquals(message.toString(), incommingMessages.get().toString());
    }
}