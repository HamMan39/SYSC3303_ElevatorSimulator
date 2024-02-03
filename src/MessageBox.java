import java.util.ArrayList;

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
        System.out.println(Thread.currentThread().getName() + " sent message: " +message);
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
        //remove oldest message in the box (last message)
        Message msg = messages.remove(messages.size()-1);
        System.out.println(Thread.currentThread().getName() + " received message: " +msg);
        notifyAll();
        return msg;
    }
}
