public class Main {
    public static void main(String[] args) {
        Thread elevator, scheduler, floor;
        MessageBox box1, box2, box3, box4;

        box1 = new MessageBox(); // incomingFloor box
        box2 = new MessageBox(); // outgoingFloor box
        box3 = new MessageBox(); //incomingElevator box
        box4 = new MessageBox(); //outgoingElevator bpx

        // Create the floor, elevator and schedule threads, passing each thread
        // a reference to its MessageBox.
        floor= new Thread(new Floor(box1, box2),"Floor");
        elevator = new Thread(new Elevator(box3, box4),"Elevator");
        scheduler= new Thread(new Scheduler(),"Scheduler");



        //start threads running
        floor.start();
        elevator.start();
        scheduler.start();
    }
}