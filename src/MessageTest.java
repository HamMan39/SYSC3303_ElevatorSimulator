import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
/**
 * Unit tests for Class Floor
 * @author Areej Mahmoud 101218260, Mahnoor Fatima 101192353
 */
class MessageTest {

    @Test
    void testToByte() {
        // Create a sample Message object for testing
        Message message = new Message("2024-03-30T12:00:00", 5, Message.Directions.UP, 10, Message.Failures.NONE);

        // Call the toByteArray() method
        byte[] byteArray = message.toByteArray();

        // Create a ByteArrayInputStream to read the bytes
        ByteArrayInputStream inputStream = new ByteArrayInputStream(byteArray);

        // Read and check if they match the original Message object
        try {
            // Read arrivalTime bytes
            byte[] arrivalTimeBytes = new byte[19]; // Assuming arrivalTime is maximum 20 characters
            inputStream.read(arrivalTimeBytes);
            String arrivalTime = new String(arrivalTimeBytes).trim();
            assertEquals(message.getArrivalTime(), arrivalTime);

            // Read arrivalFloor
            int arrivalFloor = inputStream.read();
            assertEquals(message.getArrivalFloor(), arrivalFloor);

            // Read direction ordinal
            int directionOrdinal = inputStream.read();
            assertEquals(message.getDirection().ordinal(), directionOrdinal);

            // Read destinationFloor
            int destinationFloor = inputStream.read();
            assertEquals(message.getDestinationFloor(), destinationFloor);

            // Read failure ordinal
            int failureOrdinal = inputStream.read();
            assertEquals(message.getFailure().ordinal(), failureOrdinal);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
