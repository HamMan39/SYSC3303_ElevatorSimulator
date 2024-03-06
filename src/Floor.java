import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;
/**
 * This Class represents the FloorSubsystem which reads the simulation input
 * and sends the data to the scheduler class.
 * @author Areej Mahmoud 101218260
 */

public class Floor implements Runnable{

    //Message boxes for communication with Scheduler
    //MessageBox outgoingMessages, incomingMessages;
    private ArrayList<String> buffer;
    //Datagram packets to send and receive to/from scheduler
    DatagramPacket sendPacket, receivePacket;
    //This socket will be used to send and receive packets
    DatagramSocket sendReceiveSocket;

    public Floor() {
        try {
            sendReceiveSocket = new DatagramSocket();
        } catch (SocketException se) {   // Unable to create socket.
            se.printStackTrace();
            System.exit(1);
        }
    }

//    /**
//     * Returns a Message object representing the input str.
//     * @param str the input line containing a message.
//     * @return new Message object
//     * */
//    public Message createMessage(String str){
//        System.out.println(str);
//        String[] data = str.split(" ");
//        String timestamp = data[0];
//        int arrivalFloor = Integer.valueOf(data[1]);
//        String direction = data[2];
//        int destFloor = Integer.valueOf(data[3]);
//        Message newMsg = new Message(timestamp, arrivalFloor, direction, destFloor);
//
//        return newMsg;
//    }
    /**
     * Prints information about the DatagramPacket packet.
     *
     * @param packet    the packet to be printed
     * @param direction the direction of the packet (received or sending)
     * @param packetNum the packet number
     */
    private void printPacketInfo(DatagramPacket packet, String direction, int packetNum){
        System.out.println("Client: " +direction+ " packet: "+packetNum);
        System.out.println("To host: " + packet.getAddress());
        System.out.println("Destination host port: " + packet.getPort());
        int len = packet.getLength();
        System.out.println("Length: " + len);
        System.out.print("Containing: ");
        System.out.println(new String(packet.getData(), 0, len));
    }
    /**
     * Imports simulation input data by reading each line of the input text file
     * and places data into buffer.
     * @param fileName the text file containing the raw data
     */
    public void importData(String fileName) {
        try {
            File file = new File(fileName);
            Scanner reader = new Scanner(file);
            while (reader.hasNextLine()) {
                String data = reader.nextLine();
                //place the new request into buffer
                buffer.add(data);
            }
            buffer.add(null);

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

    }
    /**
     * Send DatagramPacket to Host, then wait to receive a response packet
     */
    public void sendAndReceive()
    {
        // Create the specified request type: read/write
        byte msg[];
        for(int i=1; i<buffer.size(); i++) {
            msg = buffer.get(i).getBytes();

            //create packet to send to port 23 on the Intermediate Host
            try {
                sendPacket = new DatagramPacket(msg, msg.length,
                        InetAddress.getLocalHost(), 20);
            } catch (UnknownHostException e) {
                e.printStackTrace();
                System.exit(1);
            }

            printPacketInfo(sendPacket, "sending", i);

            // Send the datagram packet to the IntermediateHost via the send/receive socket.

            try {
                sendReceiveSocket.send(sendPacket);
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }

            System.out.println("Client: Packet sent.\n");

            // Construct a DatagramPacket for receiving packets up
            // to 100 bytes long (the length of the byte array).

            byte data[] = new byte[100];
            receivePacket = new DatagramPacket(data, data.length);

            try {
                // Block until a datagram is received via sendReceiveSocket.
                sendReceiveSocket.receive(receivePacket);
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }

            // Process the received datagram.
            printPacketInfo(receivePacket,"received",i);
        }
        // We're finished, so close the socket.
        sendReceiveSocket.close();
    }
    /**
     * Execute the thread operations. Imports data from file
     * and sends/receives messages to and from Scheduler
     */
    @Override
    public void run() {
        importData("input.txt");
        while (true){
              sendAndReceive();
//            while(!incomingMessages.empty()) {
//                Message floorMessage = incomingMessages.get();
//                System.out.println(Thread.currentThread().getName() + " received message from Scheduler : " + floorMessage);
//                if (floorMessage == null) {
//                    System.out.println("Floor System Exited");
//                    return;
//                }
//            }
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {}
        }
    }
}
