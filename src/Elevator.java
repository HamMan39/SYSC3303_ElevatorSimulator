public class Elevator implements Runnable {
    private int floor;
    private MessageBox incomingMessages, outgoingMessages;

    public Elevator(MessageBox box3, MessageBox box4){
        this.floor = 0;
        this.incomingMessages = box3;
        this.outgoingMessages = box4;
    }

    @Override
    public void run() {
        while(true){
            Message message = incomingMessages.get();
            if (message == null){
                System.out.println("Elevator System Exited");
                outgoingMessages.put(null);
                return;
            }
            System.out.println(Thread.currentThread().getName() + " received message from Scheduler : " + message);

            outgoingMessages.put(message);
            System.out.println(Thread.currentThread().getName() + " sent message to Scheduler : " + message);

        }
    }
}
