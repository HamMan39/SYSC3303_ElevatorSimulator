public class ElevatorSubSystem implements Runnable {
    private int floor;
    private MessageBox elevatorMessage;
    private String message = null;

    public ElevatorSubSystem(MessageBox message){
        this.floor = 0;
        elevatorMessage = message;
    }

    public String goToFloor(String message){
        System.out.print(message);
        return message;
    }

    @Override
    public void run() {
        while(true){
            message = (String) elevatorMessage.get();
            String messageRecieved = goToFloor(message);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {}

            elevatorMessage.put(messageRecieved);
        }
    }
}
