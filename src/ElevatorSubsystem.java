public class ElevatorSubsystem extends CommunicationRPC implements Runnable{

    //Message boxes for communication with Scheduler
    private MessageBox incomingMessages, outgoingMessages;

    private Thread[] elevators;

    private MessageBox[] messageBoxes;
    private static final int ELEVATOR_PORT = 23;

    /**
     * Constructor for class ElevatorSubsytem
     *
     * @param box3 Incoming messages MessageBox
     * @param box4 Outgoing messages MessageBox
     */
    public ElevatorSubsystem(MessageBox box3, MessageBox box4, Integer numElevators, Integer numFloors) {
        super(ELEVATOR_PORT);
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
            String s = "This will be the elevator data";
            receiveAndSend(s.getBytes());

            byte command[] = receiveSendPacket.getData();

            //TODO: read the first byte of the command, and send to correct elevator

//            Message message = incomingMessages.get();
//
//            if (message == null) {
//                System.out.println("Elevator System Exited");
//                outgoingMessages.put(null);
//                return;
//            }
//            System.out.println(Thread.currentThread().getName() + " received message from Scheduler : " + message);
//
//            //assuming elevator 0 for now
//            messageBoxes[0].put(message);
        }

    }

    public static void main(String[] args) {
        Thread elevator;
        MessageBox box1, box2;

        box1 = new MessageBox(); //incomingElevator box
        box2 = new MessageBox(); //outgoingElevator bpx
        elevator = new Thread(new ElevatorSubsystem(box1, box2, 4, 20),"ElevatorSubsystem");
        elevator.start();
    }
}