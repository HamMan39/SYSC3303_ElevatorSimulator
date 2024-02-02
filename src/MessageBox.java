import java.util.ArrayList;

public class MessageBox {
    private ArrayList<String> messages;

    /**
     * Puts an object in the box.  This method returns when
     * the object has been put into the box.
     *
     * @param item The object to be put in the box.
     */
    public synchronized void put(String item) {
        messages.add(item);
        notifyAll();
    }

    /**
     * Gets an object from the box.  This method returns once the
     * object has been removed from the box.
     *
     * @return The object taken from the box.
     */
    public synchronized Object get() {
        while (messages.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e) {
                return null;
            }
        }
        String item = messages.removeLast();
        notifyAll();
        return item;
    }
}
