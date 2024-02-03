public class Scheduler implements Runnable {

    MessageBox floorBox, elevatorBox;
    public Scheduler(MessageBox box1, MessageBox box2) {
        floorBox = box1;
        elevatorBox = box2;

    }
    public void checkFloorBox(){
        Message floorMessage = floorBox.get();
        elevatorBox.put(floorMessage);
    }

    public void checkElevatorBox(){
        Message elevatorMessage = elevatorBox.get();
        floorBox.put(elevatorMessage);
    }

    @Override
    public void run() {
        System.out.println("Scheduler Started...");

        while (true){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {}

            checkFloorBox();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {}

            checkElevatorBox();

        }
    }
}