import java.nio.ByteBuffer;
import java.util.Map;

public class MessageCodecController implements MessageCodec {
    @Override
    public byte[] encode(Message message) {
    int totalSize = calculateTotalSize(message);
    ByteBuffer buffer = ByteBuffer.allocate(totalSize);

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
        ByteBuffer buffer = ByteBuffer.wrap(data);
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
        buffer.putShort((short) name.length());
        buffer.put(name.getBytes());
        buffer.putShort((short) value.length());
        buffer.put(value.getBytes());
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
        String name = new String(nameBytes);
        short valueLength = buffer.getShort();
        byte[] valueBytes = new byte[valueLength];
        buffer.get(valueBytes);
        String value = new String(valueBytes);
        headers.put(name, value);
    }

    private int decodePayloadLength(ByteBuffer buffer) {
        int length = (buffer.get() & 0xFF) << 16;
        length |= (buffer.get() & 0xFF) << 8;
        length |= (buffer.get() & 0xFF);
        return length;
    }
}
