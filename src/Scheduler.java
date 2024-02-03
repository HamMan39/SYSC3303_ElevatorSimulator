public class Scheduler implements Runnable {

    MessageBox incomingFloor, outgoingFloor, incomingElevator, outgoingElevator;
    public Scheduler(MessageBox box1, MessageBox box2, MessageBox box3, MessageBox box4) {
        incomingFloor = box1;
        outgoingFloor = box3;
        incomingElevator = box2;
        outgoingElevator = box4;

    }
    public Message checkFloorBox(){
        Message floorMessage = incomingFloor.get();
        if (floorMessage == null){
            return null;
        }
        System.out.println(Thread.currentThread().getName() + " received message from Floor : " + floorMessage);
        outgoingFloor.put(floorMessage);
        System.out.println(Thread.currentThread().getName() + " sent message to Elevator : " + floorMessage);
        return floorMessage;
    }

    public Message checkElevatorBox(){
        Message elevatorMessage = outgoingElevator.get();
        if (elevatorMessage == null){
            return null;
        }
        System.out.println(Thread.currentThread().getName() + " received message from Elevator : " + elevatorMessage);
        incomingElevator.put(elevatorMessage);
        System.out.println(Thread.currentThread().getName() + " sent message to Floor : " + elevatorMessage);
        return elevatorMessage;
    }

    @Override
    public void run() {
        while (true){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {}

            checkFloorBox();

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {}

            Message elevatorMessage = checkElevatorBox();
            if (elevatorMessage == null){
                System.out.println("Scheduler System Exited");
                return;
            }

        }
    }
}