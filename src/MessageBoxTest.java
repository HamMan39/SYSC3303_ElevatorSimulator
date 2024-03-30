import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
/**
 * Unit tests for Class MessageBox
 * @author Khola Haseeb 101192363
 */
class MessageBoxTest {

    @Test
    void testPutAndGet() {

        //create instance for testing
        MessageBox messageBox = new MessageBox();

        // create message for testing
        Message message = new Message("14:05:15.0",2, Message.Directions.UP, 4, Message.Failures.DOORS);

        //put message into the box
        messageBox.put(message);

        // get the newly added message put into the messageBox
        Message receivedMessage = messageBox.get();

        //Check to see if receivedMessage equals to the message put
        assertEquals(message,receivedMessage);
    }

    @Test
    void testGetSize(){
        //create instance for testing
        MessageBox messageBox = new MessageBox();

        // create messages for testing
        Message message = new Message("14:05:15.0",2, Message.Directions.DOWN, 4, Message.Failures.TIMEOUT);
        Message message2 = new Message("14:09:15.0",3, Message.Directions.UP, 1, Message.Failures.DOORS);

        //put message into the box
        messageBox.put(message);
        messageBox.put(message2);

        assertEquals(2, messageBox.getSize());
    }
}