import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
/**
 * Unit tests for Class Floor
 * @author Areej Mahmoud 101218260, Mahnoor Fatima 101192353
 */
class FloorTest {

    @Test
    void testImportData() {
        // Create Floor instance
        Floor floor = new Floor();

        // Import test data from file
        floor.importData("input.txt");

        // Assert that the buffer contains the test data
        assertEquals(6, floor.getBuffer().size());
    }

    @Test
    void testCreateMessage() {
        // Create Floor instance
        Floor floor = new Floor();

        // Test input strings
        String testData1 = "14:05:15.0 2 UP 4 0";
        String testData2 = "16:09:23:0 6 DOWN 3 1";

        // Create Message objects
        Message message1 = floor.createMessage(testData1);
        Message message2 = floor.createMessage(testData2);

        // Assert that message properties are parsed correctly
//        assertEquals(new TimeStamp().getTimestamp(), message1.getArrivalTime());
        assertEquals(2, message1.getArrivalFloor());
        assertEquals(Message.Directions.UP, message1.getDirection());
        assertEquals(4, message1.getDestinationFloor());
        assertEquals(Message.Failures.NONE, message1.getFailure());

//        assertEquals(new TimeStamp().getTimestamp(), message2.getArrivalTime());
        assertEquals(6, message2.getArrivalFloor());
        assertEquals(Message.Directions.DOWN, message2.getDirection());
        assertEquals(3, message2.getDestinationFloor());
        assertEquals(Message.Failures.DOORS, message2.getFailure());
    }
}