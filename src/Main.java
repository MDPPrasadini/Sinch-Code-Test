import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) {
        MessageCodecController codec = new MessageCodecController();
        Message message = new Message();
        message.headers.put("Header1", "Value1");
        message.headers.put("Header2", "Value2");
        message.payload = "This is the payload.".getBytes(StandardCharsets.UTF_8);

        // Encode the message
        byte[] encodedMessage = codec.encode(message);

        // Decode the message
        Message decodedMessage = codec.decode(encodedMessage);

        // Print the decoded message
        System.out.println("Decoded Headers: " + decodedMessage.headers);
        System.out.println("Decoded Payload: " + new String(decodedMessage.payload, StandardCharsets.UTF_8));
    }
}