public class Main {
    public static void main(String[] args) {
        Thread elevator, scheduler, floor;
        MessageBox box1, box2;

        box1 = new MessageBox(); //incomingElevator box
        box2 = new MessageBox(); //outgoingElevator bpx

        // Create the floor, elevator and schedule threads, passing each thread
        // a reference to its MessageBox.
        floor= new Thread(new Floor(),"Floor");
        elevator = new Thread(new ElevatorSubsystem(box1, box2, 4, 20),"ElevatorSubsystem");
        scheduler= new Thread(new Scheduler(),"Scheduler");

        //start threads running
        floor.start();
        elevator.start();
        scheduler.start();
    }
}