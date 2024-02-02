public class ElevatorSubSystem implements Runnable {
    private int floor;
    private ElevatorCommunication elevatorCommunication;
    public ElevatorSubSystem(ElevatorCommunication communication){
        elevatorCommunication = communication;
        this.floor = 0;
    }

    @Override
    public void run() {

    }
}
