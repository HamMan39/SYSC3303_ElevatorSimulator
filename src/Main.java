public class Main {
    public static void main(String[] args) {
        Thread elevator, scheduler, floor;
        MessageBox box1, box2, box3, box4;

        box1 = new MessageBox(); // shared by floor and scheduler
        box2 = new MessageBox(); // shared by elevator and scheduler
        box3 = new MessageBox(); //shared by floor and scheduler
        box4 = new MessageBox(); //shared by floor and scheduler

        // Create the floor, elevator and schedule threads, passing each thread
        // a reference to its MessageBox.
        floor= new Thread(new Floor(box1, box2),"Floor");
        elevator = new Thread(new Elevator(box3, box4),"Elevator");
        scheduler= new Thread(new Scheduler(box1, box2, box3, box4),"Scheduler");

        //start threads running
        floor.start();
        elevator.start();
        scheduler.start();
    }
}