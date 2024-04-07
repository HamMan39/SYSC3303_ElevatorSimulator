import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;


/**
 * This Class represents the Scheduler which acts as a communication line
 * to pass messages between Floor and Elevator Subsystems.
 * @author Mahnoor Fatima 101192353
 * @author Owen Petersen 101233850
 */
public class Scheduler extends CommunicationRPC implements Runnable {
    private LinkedList<Message> heldRequests;
    private ConcurrentLinkedQueue<Message> newRequests; // input to scheduler from floors
    private ElevatorData elevatorsStatus;
    private DatagramSocket elevatorSocket, floorSocket; //For sending ack to elevator subsystem
    private int numMessages = 0;
    private static final int ELEVATOR_PORT = 23; //elevator socket's port number
    private ArrayList<Integer> activeElevators;
    public Scheduler(int numElevators) {
        heldRequests = new LinkedList<>();
        newRequests = new ConcurrentLinkedQueue<>();
        elevatorsStatus = new ElevatorData();
        activeElevators = new ArrayList<Integer>();
        for (int i = 0; i < numElevators; i++){
            activeElevators.add(i);
        }

        try {
            elevatorSocket = new DatagramSocket();
            floorSocket = new DatagramSocket(67);
        } catch (SocketException e) {
            e.printStackTrace();
            System.exit(1);
        }

        (new ElevatorSubsystemListener()).start();
        (new ElevatorFailureListener()).start();
    }
    /**
     * Prints information about the DatagramPacket packet.
     *
     * @param packet    the packet to be printed
     * @param direction the direction of the packet (received or sending)
     * @param packetNum the packet number
     */
    private void printPacketInfo(DatagramPacket packet, String direction, int packetNum){
        System.out.println(Thread.currentThread().getName() +direction+ " packet: "+packetNum);
        System.out.println("To address: " + packet.getAddress());
        System.out.println("Destination port: " + packet.getPort());
        int len = packet.getLength();
        System.out.println("Length: " + len);
        System.out.print("Containing: ");
        System.out.println(new String(packet.getData(), 0, len));
        System.out.println("\n");
    }

    private void monitorFloor(){
        while(true){
            numMessages++;
            System.out.println(Thread.currentThread().getName()+" Waiting....");
            //receive the input from Floor
            byte receiveData[] = new byte[100];
            DatagramPacket floorReceivePacket = new DatagramPacket(receiveData, receiveData.length);
            try {
                floorSocket.receive(floorReceivePacket);
                printPacketInfo(floorReceivePacket, "received", numMessages);
            }catch (IOException e){
                e.printStackTrace();
                System.exit(1);
            }
            //add receivedData to newRequests queue
            if(floorReceivePacket.getData()!=null){
                Message newMsg = new Message(floorReceivePacket.getData());
                newRequests.add(newMsg);
            }else{
                System.exit(1);
            }

            //create ack packet to send to floor
            String s = "Ack: received Floor input!";
            byte ack[] = s.getBytes();

            DatagramPacket floorSendPacket = new DatagramPacket(ack, ack.length,
                    floorReceivePacket.getAddress(), floorReceivePacket.getPort());
            printPacketInfo(floorSendPacket, "sending", numMessages);

            // Send the acknowledgement to the floor via the floorSocket.
            try {
                floorSocket.send(floorSendPacket);
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
    }

    public boolean schedule(Message request){
        // Case S1 (see Owen's notes)
        synchronized (elevatorsStatus) {

            // Get the current positions of each elevator
            ArrayList<Integer[]> elevatorPositions = new ArrayList<>();
            for (Integer i : activeElevators){
                elevatorPositions.add(new Integer[] {i, elevatorsStatus.getElevatorPosition(i)}); //Store it as (elevator number, elevator position)
            }

            //Insertion sort algorithm (organize elevators from closest to furthest from request)
            int n = elevatorPositions.size();
            for (int i = 1; i < n; ++i) {
                Integer[] key = elevatorPositions.get(i);
                int j = i - 1;

                while (j >= 0 && elevatorPositions.get(j)[1] > key[1]) {
                    elevatorPositions.set(j + 1, elevatorPositions.get(j));
                    j = j - 1;
                }
                elevatorPositions.set(j + 1, key);
            }


            //First try to assign to the closest idle elevator, if there is any idle elevator
            for (Integer[] elevator:elevatorPositions){ // Go through elevators in order of which is closest
                if (elevatorsStatus.isIdle(elevator[0])){ // check if each elevator is idle
                    if (elevatorsStatus.getElevatorLoad(elevator[0]) < Elevator.MAX_CAPACITY) {
                        sendCommand(request, elevator[0]);
                        return true;
                    }
                }
            }

            //Second try to assign to the closest elevator moving towards the request
            for (Integer[] elevator:elevatorPositions){ // Go through elevators in order of which is closest
                if (elevatorsStatus.sameDirection(request.getDirection(), elevator[0])){ // check if each elevator is going in the same direction
                    if (elevatorsStatus.getElevatorLoad(elevator[0]) < Elevator.MAX_CAPACITY) {
                        sendCommand(request, elevator[0]);
                        return true;
                    }
                }
            }

        }
        System.out.println("Held requests: " + heldRequests.size());
        return false;
    }

    private void sendCommand(Message request, int elevator){
        ByteArrayOutputStream commandBuilder = new ByteArrayOutputStream();
        try {
            commandBuilder.write(elevator); // First byte in data will be elevator number
            commandBuilder.write(request.toByteArray()); // Rest of bytes is request
        } catch (IOException e){
            e.printStackTrace();
            System.exit(1);
        }
        byte[] commandData = commandBuilder.toByteArray();
        //send the command to ElevatorSubsystem and receive Ack in form of ElevatorData update
        sendAndReceive(commandData, ELEVATOR_PORT);

        //update elevatorStatus using ElevatorSubsystem response
        byte response[] = sendReceivePacket.getData();


        String s = "received the elevator data!";
        byte[] ack = s.getBytes();
        DatagramPacket ackSendPacket = new DatagramPacket(ack, ack.length,
                sendReceivePacket.getAddress(), sendReceivePacket.getPort());

        // Send the acknowledgement to the ElevatorSubsystem via socket.
        try {
            elevatorSocket.send(ackSendPacket);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    @SuppressWarnings("InfiniteLoopStatement") //stops IntelliJ complaining about this while loop
    public void run() {
        Iterator<Message> it;
        while(true){
            try {
                Message request = newRequests.remove(); //Try to get a new request
                if (!schedule(request)){ //Schedule the request, if it can't be scheduled add it to held requests
                    heldRequests.add(request);
                }
            } catch (NoSuchElementException ignored) {} //If newRequests is empty move on


            // Attempt to schedule requests in wait queue (requests that couldn't be scheduled the first time)
            it = heldRequests.iterator();
            while (it.hasNext()) {
                Message request = it.next();
                if (schedule(request)) {
                    it.remove();
                }
            }
        }

    }

    class ElevatorSubsystemListener extends Thread {
        private DatagramPacket elevatorUpdatePacket;
        private DatagramSocket updateReceiveSocket;

        public ElevatorSubsystemListener(){
            try {
                updateReceiveSocket = new DatagramSocket(65);
            } catch (SocketException e){
                e.printStackTrace();
                System.exit(1);
            }
        }
        public void run(){
            while (true){

//                System.out.println(Thread.currentThread().getName()+" Waiting....");

                byte receiveData[] = new byte[100];
                elevatorUpdatePacket = new DatagramPacket(receiveData, receiveData.length);
                try {
                    updateReceiveSocket.receive(elevatorUpdatePacket);
//                    printPacketInfo(elevatorUpdatePacket, "received", numMessages);
                }catch (IOException e){
                    e.printStackTrace();
                    System.exit(1);
                }

                //create packet to send to port on the Scheduler
                DatagramPacket sendUpdateAckPacket = new DatagramPacket(new byte[0], 0, elevatorUpdatePacket.getAddress(), elevatorUpdatePacket.getPort());

                // Send the acknowledgement to the floor via the floorSocket.
                try {
                    floorSocket.send(sendUpdateAckPacket);
                } catch (IOException e) {
                    e.printStackTrace();
                    System.exit(1);
                }

                elevatorsStatus.updateStatus(elevatorUpdatePacket.getData());
            }
        }
    }

    class ElevatorFailureListener extends Thread {
        private DatagramSocket failureDumpSocket;
        private DatagramPacket failureMessage;
        public ElevatorFailureListener(){
            try {
                this.failureDumpSocket = new DatagramSocket(66);
            } catch (SocketException e){
                e.printStackTrace();
                System.exit(1);
            }
        }
        public void run(){
            while (true){
                failureMessage = new DatagramPacket(new byte[100], 100);
                try {
                    failureDumpSocket.receive(failureMessage);
                } catch (IOException e){
                    e.printStackTrace();
                    System.exit(1);
                }

                //create packet to send to port on the Scheduler
                DatagramPacket sendFailureAckPacket = new DatagramPacket(new byte[0], 0, failureMessage.getAddress(), failureMessage.getPort());

                // Send the acknowledgement to the floor via the floorSocket.
                try {
                    floorSocket.send(sendFailureAckPacket);
                } catch (IOException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
                if (failureMessage.getData()[0] == -1){ // Data is a request that needs to be rescheduled

                    Message rescheduleRequest = new Message(Arrays.copyOfRange(failureMessage.getData(), 1, failureMessage.getLength())); // get the request from the data
                    newRequests.add(rescheduleRequest); // add the request back to the tasks to be scheduled, just like a new request
                } else { // First byte is elevator number that should be removed from active list
                    activeElevators.remove(failureMessage.getData()[0]);
                    System.out.println("!!! Hard Fault Detected: Elevator " + failureMessage.getData()[0] + " removed from service !!!");
                }

            }
        }
    }

    public static void main(String[] args) {
        Scheduler scheduler = new Scheduler(4);
        Thread schedulerThread= new Thread(() -> scheduler.run(),"Scheduler");
        Thread floorMonitor = new Thread(() -> scheduler.monitorFloor(), "FloorMonitor");
        schedulerThread.start();
        floorMonitor.start();
    }

}