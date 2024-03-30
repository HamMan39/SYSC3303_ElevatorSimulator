import java.sql.Timestamp;
import java.text.SimpleDateFormat;

/**
 * This class represents a timestamp of the current computer clock in the
 * format hh:mm:ss:ms
 * @author Areej Mahmoud 101218260
 */
public class TimeStamp {
    private String timestamp;
    private static final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");

    public TimeStamp(){
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        this.timestamp = sdf.format(ts);
    }

    /**
     * @return the current clock timestamp
     */
    public String getTimestamp() {
        return timestamp;
    }
}
