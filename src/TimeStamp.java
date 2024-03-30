import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class TimeStamp {
    private String timestamp;
    private static final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");

    public TimeStamp(){
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        this.timestamp = sdf.format(timestamp);
    }

    public String getTimestamp() {
        return timestamp;
    }
}
