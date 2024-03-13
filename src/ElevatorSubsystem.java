public class ElevatorSubsystem implements Runnable{

    //Message boxes for communication with Scheduler
    private MessageBox incomingMessages, outgoingMessages;

    private Thread[] elevators;

    private MessageBox[] messageBoxes;

    /**
     * Constructor for class ElevatorSubsytem
     *
     * @param box3 Incoming messages MessageBox
     * @param box4 Outgoing messages MessageBox
     */
    public ElevatorSubsystem(MessageBox box3, MessageBox box4, Integer numElevators, Integer numFloors) {
        this.incomingMessages = box3;
        this.outgoingMessages = box4;

        elevators = new Thread[numElevators];

        messageBoxes = new MessageBox[numElevators];


        for(int i =0; i < numElevators; i++){
            messageBoxes[i] = new MessageBox();
            elevators[i] = new Thread(new Elevator(i, numFloors, messageBoxes[i], outgoingMessages));
        }

        for(int i =0; i < numElevators; i++){
            elevators[i].start();
        }
    }
    @Override
    public void run() {

        while(true){
            Message message = incomingMessages.get();

            if (message == null) {
                System.out.println("Elevator System Exited");
                outgoingMessages.put(null);
                return;
            }
            System.out.println(Thread.currentThread().getName() + " received message from Scheduler : " + message);

            //assuming elevator 0 for now
            messageBoxes[0].put(message);

            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
            }
        }

    }
}