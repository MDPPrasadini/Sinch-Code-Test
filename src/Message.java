import java.util.HashMap;
import java.util.Map;

public class Message {
    public Map<String, String> headers;
    public byte[] payload;

    public Message() {
        headers = new HashMap<>();
    }

    public boolean isValid() {
        return headers.size() <= 63 && payload.length <= 256 * 1024;
    }
}