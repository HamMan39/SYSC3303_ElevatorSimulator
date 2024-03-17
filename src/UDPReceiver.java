import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class UDPReceiver implements Runnable{
    MessageBox newMessages;
    DatagramPacket receivePacket;
    //This socket will be used to receive packets
    DatagramSocket receiveSocket;
    int requestCount;
    int receivePort;

    public UDPReceiver(int receivePort, MessageBox box) {
        this.receivePort = receivePort;
        newMessages = box;
        try {
            requestCount =0;
            receiveSocket = new DatagramSocket(receivePort);
        } catch (SocketException se) {
            se.printStackTrace();
            System.exit(1);
        }
    }
    private void printPacketInfo(DatagramPacket packet, String direction, int packetNum){
        System.out.println("Receiver: " +direction+ " packet: "+packetNum);
        System.out.println("To host: " + packet.getAddress());
        System.out.println("Destination host port: " + packet.getPort());
        int len = packet.getLength();
        System.out.println("Length: " + len);
        System.out.print("Containing: ");
        System.out.println(new String(packet.getData(), 0, len));
    }


    public void receive() {
        while (true) {
            //increment request count
            requestCount++;

            // Construct a DatagramPacket for receiving packets up
            // to 100 bytes long (the length of the byte array).

            byte data[] = new byte[100];
            receivePacket = new DatagramPacket(data, data.length);

            // Block until a datagram packet is received from receiveSocket.
            try {
                System.out.println("Waiting..."); // so we know we're waiting
                receiveSocket.receive(receivePacket);
            } catch (IOException e) {
                System.out.print("IO Exception: likely:");
                System.out.println("Receive Socket Timed Out.\n" + e);
                e.printStackTrace();
                System.exit(1);
            }

            printPacketInfo(receivePacket, "received", requestCount);

            // Slow things down (wait 1 second)
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.exit(1);
            }
            //create a Message object from the received packet
            Message newMsg = this.createMessage(new String(data));
            newMessages.put(newMsg);
        }
    }
    @Override
    public void run() {
        receive();
    }
    public static void main(String args[])
    {
        MessageBox box = new MessageBox();
        Thread r = new Thread(new UDPReceiver(20, box));
        r.start();
    }
}
