import java.net.InetAddress;
import java.net.UnknownHostException;

public class RemoteIP{
    public static final String SCHEDULER_IP = "172.17.75.43";
    public static final String ELEVATOR_IP = "172.17.52.231";
    public static final String FLOOR_IP = "172.17.51.148";

    public InetAddress getSchedulerIP(){
        try {
            InetAddress ip = InetAddress.getByName(SCHEDULER_IP);
            return ip;
        }catch(UnknownHostException e){
            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }
    public InetAddress getFloorIP(){
        try {
            InetAddress ip = InetAddress.getByName(FLOOR_IP);
            return ip;
        }catch(UnknownHostException e){
            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }
    public InetAddress getElevatorIP(){
        InetAddress ip;
        try {
            ip = InetAddress.getByName(ELEVATOR_IP);
            return ip;
        }catch(UnknownHostException e){
            e.printStackTrace();
        }
        return null;
    }

}
