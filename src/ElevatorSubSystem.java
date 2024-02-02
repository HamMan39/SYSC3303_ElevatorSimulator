public class ElevatorSubSystem implements Runnable {
    private int floor;
    private MessageBox box;

    public ElevatorSubSystem(MessageBox box){

        this.floor = 0;
        this.box = box;
    }

    @Override
    public void run() {

    }
}
