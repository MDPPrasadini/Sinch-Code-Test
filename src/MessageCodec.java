public interface MessageCodec {
    byte[] encode(Message message);
    Message decode(byte[] data);

}