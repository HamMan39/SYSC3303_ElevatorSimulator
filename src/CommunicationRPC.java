import java.io.IOException;
import java.net.*;

public class CommunicationRPC {
    DatagramPacket sendPacket, receivePacket, receiveAckPacket;
    DatagramSocket sendReceiveSocket;
    private static int numMessages = 0;

    public CommunicationRPC() {
        try {
            sendReceiveSocket = new DatagramSocket();
            sendReceiveSocket.setSoTimeout(100000);
        }catch (SocketException e){
            e.printStackTrace();
            System.exit(1);
        }
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
    /**
     * Receives packets from host, validates them, and responds accordingly.
     *
     * @throws Exception if the packet received is invalid
     */
    public void sendAndReceive(byte[] msg, int port){
        numMessages++;

        byte receiveData[] = new byte[100];
        //create packet to send to port on the Scheduler
        try {
            sendPacket = new DatagramPacket(msg, msg.length,
                    InetAddress.getLocalHost(), port);
            printPacketInfo(sendPacket, "sending", numMessages);
            // Construct a DatagramPacket for receiving packets up
            // to 100 bytes long (the length of the byte array).
            receivePacket = new DatagramPacket(receiveData, receiveData.length);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            System.exit(1);
        }

        rpc_send(sendPacket, receivePacket);

    }

    /**
     * Receives packets from host, validates them, and responds accordingly.
     *
     */
    public void receiveAndSend(byte[] msg) {
        numMessages++;

        byte receiveData[] = new byte[100];
        receivePacket = new DatagramPacket(receiveData, receiveData.length);
        try {
            sendReceiveSocket.receive(receivePacket);
            printPacketInfo(receivePacket, "received", numMessages);
        }catch (IOException e){
            e.printStackTrace();
            System.exit(1);
        }

        //create packet to send to port on the Scheduler
        sendPacket = new DatagramPacket(msg, msg.length, receivePacket.getAddress(), receivePacket.getPort());
        printPacketInfo(sendPacket, "sending", numMessages);

        byte receiveAck[] = new byte[100];
        receiveAckPacket = new DatagramPacket(receiveAck, receiveAck.length);
        rpc_send(sendPacket, receivePacket);
    }


    private void handleAcknowledgment(DatagramPacket acknowledgmentPacket) {
        // Handle the acknowledgment packet received from the scheduler
        System.out.println(Thread.currentThread().getName() + ": Acknowledgment received from host. " +
                new String(acknowledgmentPacket.getData(), 0, acknowledgmentPacket.getLength()));
    }
    public void rpc_send(DatagramPacket request, DatagramPacket response) {
        // Send message and receive the ack with timeout handling
        int attempt = 0;
        boolean receivedResponse = false;

        while (attempt < 3 && !receivedResponse) { // Retry up to 3 times
            System.out.println(Thread.currentThread().getName() + ": Attempt " + (attempt + 1));
            try {
                sendReceiveSocket.send(request);

                try{ //slow things down
                    Thread.sleep(5000);
                }catch(InterruptedException e) {}

                // Attempt to receive the acknowledgment
                sendReceiveSocket.receive(response);
                printPacketInfo(response, "received", numMessages);

                // Handle the acknowledgment
                handleAcknowledgment(response);
                receivedResponse = true;
            } catch (SocketTimeoutException ste) {
                // Handle timeout exception
                System.out.println(Thread.currentThread().getName() + ": Timeout. Resending packet.");
                attempt++;
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }

        if (!receivedResponse) {
            System.out.println(Thread.currentThread().getName() + ": No response after multiple attempts. Exiting.");
            System.exit(1);
        }

    }
}
