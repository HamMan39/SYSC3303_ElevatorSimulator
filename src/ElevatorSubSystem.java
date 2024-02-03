/**
 * This Class represents the ElevatorSubsystem which reads the simulation input from the scheduler class
 * and sends the data back to scheduler
 * @author Khola Haseeb 101192363
 */

public class ElevatorSubSystem implements Runnable {
    //Shared message box between this system and Scheduler
    private MessageBox elevatorMessage;

    public ElevatorSubSystem(MessageBox message){
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
