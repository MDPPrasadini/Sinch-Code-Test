import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;

public class SimpleMessageCodec implements MessageCodec {
    private static final int MAX_HEADER_LENGTH = 1023;
    private static final int MAX_PAYLOAD_LENGTH = 256 * 1024;
    private static final int MAX_HEADERS = 63;

    @Override
    public byte[] encode(Message message) {
        if (!message.isValid()) {
            throw new IllegalArgumentException("Message is not valid.");
        }

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            // Encode headers
            int headerCount = Math.min(message.headers.size(), MAX_HEADERS);
            outputStream.write(headerCount);
            for (Map.Entry<String, String> entry : message.headers.entrySet()) {
                if (headerCount <= 0) {
                    break;
                }
                byte[] nameBytes = entry.getKey().getBytes();
                byte[] valueBytes = entry.getValue().getBytes();

                if (nameBytes.length > MAX_HEADER_LENGTH || valueBytes.length > MAX_HEADER_LENGTH) {
                    throw new IllegalArgumentException("Header name or value too long.");
                }

                outputStream.write(nameBytes.length);
                outputStream.write(nameBytes);
                outputStream.write(valueBytes.length);
                outputStream.write(valueBytes);
                headerCount--;
            }

            // Encode payload
            byte[] payload = message.payload;
            if (payload.length > MAX_PAYLOAD_LENGTH) {
                throw new IllegalArgumentException("Payload too long.");
            }
            outputStream.write(ByteBuffer.allocate(4).putInt(payload.length).array());
            outputStream.write(payload);

            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Encoding failed: " + e.getMessage());
        }
    }

    @Override
    public Message decode(byte[] data) {
        ByteBuffer buffer = ByteBuffer.wrap(data);

        // Decode headers
        int headerCount = buffer.get();
        Message message = new Message();
        for (int i = 0; i < headerCount; i++) {
            int nameLength = buffer.get();
            byte[] nameBytes = new byte[nameLength];
            buffer.get(nameBytes);

            int valueLength = buffer.get();
            byte[] valueBytes = new byte[valueLength];
            buffer.get(valueBytes);

            String name = new String(nameBytes);
            String value = new String(valueBytes);
            message.headers.put(name, value);
        }

        // Decode payload
        int payloadLength = buffer.getInt();
        byte[] payload = new byte[payloadLength];
        buffer.get(payload);
        message.payload = payload;

        return message;
    }
}
