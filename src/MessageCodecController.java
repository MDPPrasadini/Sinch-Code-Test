import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class MessageCodecController implements MessageCodec {
    private static final int MAX_HEADER_SIZE = 2046; // Maximum size for an individual header (1023 bytes for name + 1023 bytes for value)
    private static final int MAX_NUM_HEADERS = 63; // Maximum number of headers allowed
    private static final int MAX_PAYLOAD_SIZE = 256 * 1024; // Maximum payload size (256 KiB)

    @Override
    public byte[] encode(Message message) {
        validateMessage(message);
        int totalSize = calculateTotalSize(message);
        ByteBuffer buffer = ByteBuffer.allocate(totalSize).order(ByteOrder.BIG_ENDIAN); // Explicitly set byte order to BIG_ENDIAN

        // Encode header count
        buffer.put((byte) message.headers.size());

        // Encode headers
        for (Map.Entry<String, String> entry : message.headers.entrySet()) {
            encodeHeader(entry.getKey(), entry.getValue(), buffer);
    }

    // Encode payload length
    encodePayloadLength(message.payload.length, buffer);

    // Encode payload
    buffer.put(message.payload);

    return buffer.array();
    }

    @Override
    public Message decode(byte[] data) {
        ByteBuffer buffer = ByteBuffer.wrap(data).order(ByteOrder.BIG_ENDIAN); // Explicitly set byte order to BIG_ENDIAN
        Message message = new Message();

        // Decode header count
        int headerCount = Byte.toUnsignedInt(buffer.get());

        // Decode headers
        for (int i = 0; i < headerCount; i++) {
            decodeHeader(buffer, message.headers);
        }

        // Decode payload length
        int payloadLength = decodePayloadLength(buffer);

        // Decode payload
        message.payload = new byte[payloadLength];
        buffer.get(message.payload);

        validateMessage(message);
        return message;
    }

    private int calculateTotalSize(Message message) {
        int totalSize = 1; // Header count size

        // Calculate header sizes
        for (Map.Entry<String, String> entry : message.headers.entrySet()) {
            totalSize += 2 + entry.getKey().length() + 2 + entry.getValue().length();
        }

        // Add payload size
        totalSize += 3 + message.payload.length; // Payload length size

        return totalSize;
    }

    private void encodeHeader(String name, String value, ByteBuffer buffer) {
        byte[] nameBytes = name.getBytes(StandardCharsets.UTF_8);
        byte[] valueBytes = value.getBytes(StandardCharsets.UTF_8);

        // Check if the header size exceeds the limit
        if (nameBytes.length > MAX_HEADER_SIZE || valueBytes.length > MAX_HEADER_SIZE) {
            throw new IllegalArgumentException("Header size exceeds the allowed limit.");
        }

        buffer.putShort((short) nameBytes.length);
        buffer.put(nameBytes);
        buffer.putShort((short) valueBytes.length);
        buffer.put(valueBytes);
    }

    private void encodePayloadLength(int length, ByteBuffer buffer) {
        buffer.put((byte) (length >> 16 & 0xFF));
        buffer.put((byte) (length >> 8 & 0xFF));
        buffer.put((byte) (length & 0xFF));
    }

    private void decodeHeader(ByteBuffer buffer, Map<String, String> headers) {

        short nameLength = buffer.getShort();
        byte[] nameBytes = new byte[nameLength];
        buffer.get(nameBytes);
        String name = new String(nameBytes, StandardCharsets.UTF_8);

        short valueLength = buffer.getShort();
        byte[] valueBytes = new byte[valueLength];
        buffer.get(valueBytes);
        String value = new String(valueBytes, StandardCharsets.UTF_8);

        // Check if the decoded header size exceeds the limit
        if (nameLength > MAX_HEADER_SIZE || valueLength > MAX_HEADER_SIZE) {
            throw new IllegalArgumentException("Decoded header size exceeds the allowed limit.");
        }

        headers.put(name, value);
    }

    private int decodePayloadLength(ByteBuffer buffer) {
        int length = (buffer.get() & 0xFF) << 16;
        length |= (buffer.get() & 0xFF) << 8;
        length |= (buffer.get() & 0xFF);
        return length;
    }

    private void validateMessage(Message message) {
        // Validate header
        for (Map.Entry<String, String> entry : message.headers.entrySet()) {
            if (entry.getKey().length() > MAX_HEADER_SIZE || entry.getValue().length() > MAX_HEADER_SIZE) {
                throw new IllegalArgumentException("Header size exceeds the allowed limit.");
            }
        }

        // Validate total message size (headers + payload)
        int totalSize = calculateTotalSize(message);
        if (totalSize > MAX_PAYLOAD_SIZE) {
            throw new IllegalArgumentException("Total message size exceeds the allowed payload limit.");
        }

        // Validate the number of headers
        if (message.headers.size() > MAX_NUM_HEADERS) {
            throw new IllegalArgumentException("Number of headers exceeds the allowed limit.");
        }
    }
}
