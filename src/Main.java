public class Main {
    public static void main(String[] args) {
        Message message = new Message();
        message.headers.put("Header1", "Value1");
        message.headers.put("Header2", "Value2");
        message.payload = "This is the payload.".getBytes();

        MessageCodecController codec = new MessageCodecController();
        byte[] encodedMessage = codec.encode(message);

        Message decodedMessage = codec.decode(encodedMessage);
        System.out.println("Decoded Headers: " + decodedMessage.headers);
        System.out.println("Decoded Payload: " + new String(decodedMessage.payload));
    }
}