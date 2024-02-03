public class ElevatorSubSystem implements Runnable {
    private int floor;
    private MessageBox elevatorMessage;
    private Message messageRecieved;

    public ElevatorSubSystem(MessageBox message){
        this.floor = 0;
        elevatorMessage = message;
    }

    @Override
    public void run() {
        while(true){
            Message message = elevatorMessage.get();

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {}
            elevatorMessage.put(message);
        }
    }
}
