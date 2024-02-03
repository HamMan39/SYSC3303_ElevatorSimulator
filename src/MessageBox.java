import java.util.ArrayList;
/**
 * This class represents a protected Message Box which is shared between threads
 * and stores data that can be retrieved.
 * @author Mahnoor Fatima
 */
public class MessageBox {
    private ArrayList<Message> messages;

    public MessageBox() {
        this.messages = new ArrayList<>();
    }

    /**
     * Puts an object in the box.  This method returns when
     * the object has been put into the box.
     *
     * @param message The object to be put in the box.
     */
    public synchronized void put(Message message) {
        messages.add(message);
//        System.out.println(Thread.currentThread().getName() + " sent message to : " +message);
        notifyAll();
    }

    /**
     * Gets an object from the box.  This method returns once the
     * object has been removed from the box.
     *
     * @return The object taken from the box.
     */
    public synchronized Message get() {
        while (messages.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e) {
                return null;
            }
        }
        //remove oldest message in the box
        Message msg = messages.remove(0);
//        System.out.println(Thread.currentThread().getName() + " received message: " +msg);
        notifyAll();
        return msg;
    }

    /**
     * Returns true if the box is empty, false if there are messages.
     * @return true if the box is empty
     */
    public boolean empty(){
        return messages.isEmpty();
    }

    /**
     * Returns the number of objects in the box
     * @return number of objects in the box.
     */
    public int getSize(){
        return messages.size();
    }
}
