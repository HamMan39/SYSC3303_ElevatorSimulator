import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MessageBoxTest {

    @Test
    void testPutAndGet() {

        //create instance for testing
        MessageBox messageBox = new MessageBox();

        // create message for testing
        Message message = new Message("14:05:15.0",2, "Up", 4);

        //put message into the box
        messageBox.put(message);

        // get the newly added message put into the messageBox
        Message receivedMessage = messageBox.get();

        //Check to see if receivedMessage equals to the message put
        assertEquals(message,receivedMessage);
    }

    @Test
    void empty() {
        //create instance for testing
        MessageBox messageBox = new MessageBox();

        // testing to see if messageBox is empty initially
        assertTrue(messageBox.empty());

        // create message for testing
        Message message = new Message("14:05:15.0",2, "Up", 4);

        //put message into the box
        messageBox.put(message);

        // testing to see if messageBox is now notEmpty
        assertFalse(messageBox.empty());
    }

    @Test
    void getSize(){
        //create instance for testing
        MessageBox messageBox = new MessageBox();

        // create messages for testing
        Message message = new Message("14:05:15.0",2, "Up", 4);
        Message message2 = new Message("14:09:15.0",1, "Down", 3);

        //put message into the box
        messageBox.put(message);
        messageBox.put(message2);

        assertEquals(2, messageBox.getSize());
    }
}