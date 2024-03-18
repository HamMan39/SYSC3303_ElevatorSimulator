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
    public Scheduler() {
        heldRequests = new LinkedList<>();
        newRequests = new ConcurrentLinkedQueue<>();
        elevatorsStatus = new ElevatorData();

        try {
            elevatorSocket = new DatagramSocket();
            floorSocket = new DatagramSocket(67);
        } catch (SocketException e) {
            e.printStackTrace();
            System.exit(1);
        }

        (new ElevatorSubsystemListener()).start();
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

    private boolean schedule(Message request){
        ArrayList<Integer> sectorElevators = determineSectorsElevator(request);
        // Case S1 (see Owen's notes)
        synchronized (elevatorsStatus) {
            // Case S4 (see Owen's notes)
            for (Integer i : sectorElevators) {
                if (elevatorsStatus.isIdle(i)) {
                    sendCommand(request, i);
                    return true;
                }
            }
            // Case S5 (see Owen's notes)
            for (int i = 0; i < 4; i++) {
                if (!sectorElevators.contains(i)) {
                    if ((elevatorsStatus.soonSameDirection(request.getDirection(), i))) {
                        sendCommand(request, i);
                        return true;
                    }
                }
            }
            for (Integer i : sectorElevators) {
                if (elevatorsStatus.sameDirection(request.getDirection(), i)) {
                    sendCommand(request, i);
                    return true;
                }
            }
            // Case S2 (see Owen's notes)
            for (int i = 0; i < 4; i++) {
                if (!sectorElevators.contains(i)) {
                    if ((elevatorsStatus.sameDirection(request.getDirection(), i))) {
                        sendCommand(request, i);
                        return true;
                    }
                }
            }
            // Case S3 (see Owen's notes)
            for (Integer i : sectorElevators) {
                if (elevatorsStatus.soonSameDirection(request.getDirection(), i)) {
                    sendCommand(request, i);
                    return true;
                }
            }


            // Case S6 (see Owen's notes)
            for (int i = 0; i < 4; i++) {
                if (!sectorElevators.contains(i)) {
                    if (elevatorsStatus.isIdle(i)) {
                        sendCommand(request, i);
                        return true;
                    }
                }
            }
        }
        return false;
    }
    private ArrayList<Integer> determineSectorsElevator(Message request){
        int sourceFloor = request.getArrivalFloor();
        if(sourceFloor == 1){
            return new ArrayList(List.of(0,1));
        } else if (1 < sourceFloor && sourceFloor <=12) {
            return new ArrayList<>(List.of(2));
        } else {
            return new ArrayList<>(List.of(3));
        }
    }

    //TODO implement RPC
    private void sendCommand(Message request, int elevator){
        System.out.println("---------------");
        System.out.println(elevator);
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
        //elevatorsStatus.updateStatus(response);

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
    public void run() {
        Iterator<Message> it;
        while(true){
            it = newRequests.iterator();
            while(it.hasNext()){
                Message request = it.next();
                if (schedule(request)){
                    it.remove();
                }
            }
            // Attempt to schedule requests in wait queue
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

    public static void main(String[] args) {
        Scheduler scheduler = new Scheduler();
        Thread schedulerThread= new Thread(scheduler,"Scheduler");
        Thread floorMonitor = new Thread(() -> scheduler.monitorFloor(), "FloorMonitor");
        schedulerThread.start();
        floorMonitor.start();
    }

}