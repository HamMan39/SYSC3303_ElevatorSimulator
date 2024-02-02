public class Main {
    public static void main(String[] args) {
        Thread elevator, scheduler, floor;
        MessageBox box1, box2;

        box1 = new MessageBox(); // shared by floor and scheduler
        box2 = new MessageBox(); // shared by elevator and scheduler

        // Create the barista and agent threads, passing each thread
        // a reference to the shared Table.
        floor= new Thread(new FloorSubSystem(box1),"FloorSubSystem");
        elevator = new Thread(new ElevatorSubSystem(box2),"ElevatorSubSystem");
        scheduler= new Thread(new Scheduler(),"Scheduler");

        //start threads running
        floor.start();
        elevator.start();
        scheduler.start();
    }
}