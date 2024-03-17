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
public class Scheduler implements Runnable {
    private LinkedList<Message> heldRequests;
    private ConcurrentLinkedQueue<Message> newRequests; // input to scheduler from floors
    private ElevatorData elevatorsStatus;
    private DatagramSocket sendCommandSocket; //For sending commands to elevator subsystem

    public Scheduler() {
        heldRequests = new LinkedList<>();

        newRequests = new ConcurrentLinkedQueue<>();
        elevatorsStatus = new ElevatorData();

        new ElevatorSubsystemListener();

        try {
            sendCommandSocket = new DatagramSocket(21);
        } catch (SocketException e) {
            e.printStackTrace();
            System.exit(1);
        }
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
        DatagramPacket commandMessage;
        try {
            commandBuilder.write(elevator); // First byte in data will be elevator
            commandBuilder.write(request.toByteArray()); // Rest of bytes is request
        } catch (IOException e){
            e.printStackTrace();
            System.exit(1);
        }
        byte[] commandData = commandBuilder.toByteArray();
        try {
            commandMessage = new DatagramPacket(commandData, commandData.length, InetAddress.getLocalHost(), 0); //TODO set port to elevator subsystem
        } catch (UnknownHostException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }




    @Override
    public void run() {
        Iterator<Message> it;

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

    class ElevatorSubsystemListener extends Thread{
        DatagramSocket receiveElevatorUpdatesSocket; //For receiving real-time updates of elevator status
        public ElevatorSubsystemListener(){
            try {
                receiveElevatorUpdatesSocket = new DatagramSocket(27);
            } catch (SocketException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }

        public void run(){

            //TODO RPC receive message from elevator subsystem (message has elevator status)
            //Probably best as byte array of elevatorData

            elevatorsStatus.updateStatus(new byte[0]); //Update with byte array
        }
    }

}