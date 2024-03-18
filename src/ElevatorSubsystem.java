public class ElevatorSubsystem extends CommunicationRPC implements Runnable{

    //Message boxes for communication with Scheduler
    private MessageBox incomingMessages, outgoingMessages;

    private Thread[] elevators;

    private MessageBox[] messageBoxes;
    private ElevatorData elevatorData;
    private static final int ELEVATOR_PORT = 23;

    /**
     * Constructor for class ElevatorSubsytem
     *
     * @param box3 Incoming messages MessageBox
     * @param box4 Outgoing messages MessageBox
     */
    public ElevatorSubsystem(Integer numElevators, Integer numFloors, MessageBox box3, MessageBox box4) {
        super(ELEVATOR_PORT);
        this.incomingMessages = box3;
        this.outgoingMessages = box4;

        elevatorData = new ElevatorData();
        elevators = new Thread[numElevators];

        messageBoxes = new MessageBox[numElevators];


        for(int i =0; i < numElevators; i++){
            messageBoxes[i] = new MessageBox();
            elevators[i] = new Thread(new Elevator(i, numFloors, messageBoxes[i], outgoingMessages, elevatorData));
        }

        for(int i =0; i < numElevators; i++){
            elevators[i].start();
        }
    }
    @Override
    public void run() {
        while(true){
            receiveAndSend(elevatorData.toByteArray());

            byte command[] = receiveSendPacket.getData();

            //read the first byte of the command, which is elevator id
            int elevatorId = command[0];

            byte[] byteMessage = new byte[command.length - 1];

            for(int i = 1; i < command.length; i++){
                byteMessage[i-1] = command[i];
            }
            if (byteMessage == null) {
                System.out.println("Elevator System Exited");
                outgoingMessages.put(null);
                return;
            }
            Message message = new Message(byteMessage);



            System.out.println(Thread.currentThread().getName() + " received message from Scheduler : " + message);

            //send message to correct elevator
            messageBoxes[elevatorId].put(message);

            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
            }
        }

    }

    public static void main(String[] args) {
        Thread elevator;
        MessageBox box1, box2;

        box1 = new MessageBox(); //incomingElevator box
        box2 = new MessageBox(); //outgoingElevator bpx
        elevator = new Thread(new ElevatorSubsystem( 4, 20, box1, box2),"ElevatorSubsystem");
        elevator.start();
    }
}