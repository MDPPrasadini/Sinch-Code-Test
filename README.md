# Sinch-Code-Test
RTC- Sinch - Technical Test

# Binary Message Encoding and Decoding

This repository contains a Java implementation of a simple binary message encoding and decoding scheme for use in a signaling protocol. This encoding scheme is designed to pass messages between peers in a real-time communication application.

## Message Structure

The binary message encoding scheme follows the structure below:

### Header Count (1 byte):
Represents how many headers are present in the message. Since the maximum number of headers is 63, a single byte (which can represent values 0-255) is sufficient.

### Headers:
- Header Name Length (2 bytes): Represents the length of the header's name.
- Header Name (variable length): The ASCII-encoded name of the header.
- Header Value Length (2 bytes): Represents the length of the header's value.
- Header Value (variable length): The ASCII-encoded value of the header.

### Payload Length (3 bytes):
Represents the length of the payload. 3 bytes are used to ensure we can represent values large enough for the 256 KiB limit.

### Payload (variable length):
The actual binary payload.

## Encoding and Decoding

Encoding:
- It first calculates the total size of the binary data based on headers and payload.
- Using a ByteBuffer, it writes the header count, each header's name and value, and then the payload length followed by the payload itself.

Decoding:
- It reads the header count from the binary data.
- Then, for each header, it reads the name and value lengths, followed by the actual name and value.
- Finally, it reads the payload length and the actual payload.

## Considerations

- The implementation ensures data integrity by using length-prefix encoding for variable-length data, like header names and values.
- There's room to add error handling for scenarios like exceeding header or payload size limits or if decoding corrupt data.
- The code currently assumes a system's default charset for string-to-byte conversions. For more consistency across platforms, you might specify a charset like "UTF-8."

## Usage
-To use this encoding scheme, follow these steps:
- Create a `Message` object:
ex:
   Message message = new Message();
   message.headers.put("Header1", "Value1");
   message.headers.put("Header2", "Value2");
   message.payload = "This is the payload.".getBytes(StandardCharsets.UTF_8);
  
- Encode the message using the MessageCodecController:
  MessageCodecController codec = new MessageCodecController();
  byte[] encodedMessage = codec.encode(message);
  
- Decode the message using the MessageCodecController:
  Message decodedMessage = codec.decode(encodedMessage);

## Project Structure
The project follows a standard Java project structure:

src/: Contains the Java source code files.
test/: Contains unit tests for the code. // Not included
lib/: You can place external libraries here if needed.
README.md: This file containing project documentation.

## Unit Testing
The project should includes unit tests to ensure code reliability. You can run the tests using a testing framework like JUnit. In this assignment didnt add unit tests and better includes units test befor use to production.

## Requirements
To build and run this project, you need the following:

- Java Development Kit (JDK)
- Gradle (or any build tool of your choice)





