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
    private DatagramSocket elevatorSendSocket; //For sending ack to elevator subsystem

    private static final int ELEVATOR_PORT = 23; //elevator socket's port number
    public Scheduler() {
        heldRequests = new LinkedList<>();

        newRequests = new ConcurrentLinkedQueue<>();
        elevatorsStatus = new ElevatorData();

//        new ElevatorSubsystemListener();

        try {
            elevatorSendSocket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
    private void monitorFloor(){
        String s = "Ack: received Floor input!";
        receiveAndSend(s.getBytes());

    }

    private boolean schedule(Message request){
        ArrayList<Integer> sectorElevators = determineSectorsElevator(request);
        // Case S1 (see Owen's notes)
        synchronized (elevatorsStatus) {
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
            return new ArrayList(List.of(1,2));
        } else if (1 < sourceFloor && sourceFloor <=12) {
            return new ArrayList<>(List.of(3));
        } else {
            return new ArrayList<>(List.of(4));
        }
    }

    //TODO implement RPC
    private void sendCommand(Message request, int elevator){
        ByteArrayOutputStream commandBuilder = new ByteArrayOutputStream();
        try {
            commandBuilder.write(elevator); // First byte in data will be elevator
            commandBuilder.write(request.toByteArray()); // Rest of bytes is request
        } catch (IOException e){
            e.printStackTrace();
            System.exit(1);
        }
        byte[] commandData = commandBuilder.toByteArray();
        //send the command to ElevatorSubsystem and receive Ack in form of ElevatorData update
        sendAndReceive(commandData, ELEVATOR_PORT);

        //update elevatorStatus using ElevatorSubsystem response
        byte response[] = receivePacket.getData();
        elevatorsStatus.updateStatus(response);

        String s = "received the elevator data!";
        byte[] ack = s.getBytes();
        DatagramPacket ackSendPacket = new DatagramPacket(ack, ack.length,
                receivePacket.getAddress(), receivePacket.getPort());

        // Send the acknowledgement to the ElevatorSubsystem via socket.
        try {
            elevatorSendSocket.send(ackSendPacket);
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
//
//    class ElevatorSubsystemListener extends Thread{
//        DatagramSocket receiveElevatorUpdatesSocket; //For receiving real-time updates of elevator status
//        public ElevatorSubsystemListener(){
//            try {
//                receiveElevatorUpdatesSocket = new DatagramSocket(32);
//            } catch (SocketException e) {
//                e.printStackTrace();
//                System.exit(1);
//            }
//        }
//
//        public void run(){
//
//            //TODO RPC receive message from elevator subsystem (message has elevator status)
//            //Probably best as byte array of elevatorData
//
//            elevatorsStatus.updateStatus(new byte[0]); //Update with byte array
//        }
//    }

    public static void main(String[] args) {
        Thread scheduler;
        scheduler= new Thread(new Scheduler(),"Scheduler");
        scheduler.start();

    }

}