package com.neu.prattle.websocket;

import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

import com.neu.prattle.model.Message;

import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Decodes in-bound messages that come in as JSON structures into Message objects. The fields must include
 * from, to, id, content, and timestamp.
 *
 * @author https://github.com/eugenp/tutorials/java-websocket/src/main/java/com/baeldung/websocket/MessageDecoder.java
 * @version dated 2017-03-05
 */
public class MessageDecoder implements Decoder.Text<Message> {

    /** @see org.codehaus.jackson.map.ObjectMapper */
    private static ObjectMapper objectMapper = new ObjectMapper();

    /**
     * The logger. Stores message content coming in and going out.
     */
    private Logger logger = Logger.getLogger(this.getClass().getName());

    /**
     *
     * From a JSON string input, this method converts it into a Java object, the Message. The Message
     * fields and JSON labels must be identical. For a Message object, it includes from, to,
     * content, timestamp, and id. If there is any issue with the input, a severe error is logged.
     *
     * @param s    the JSON structure that was sent.
     * @return a Message object.
     */
    @Override
    public Message decode(String s) {
        Message message = null;


        try {
            message = objectMapper.readValue(s, Message.class);
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
        return message;
    }

    /**
     * Will decode.
     *
     * Tests if there's a string to decode. This is required by the Decoder.Text<> interface.
     *
     * @param s the String.
     * @return true, if successful; otherwise false.
     */
    @Override
    public boolean willDecode(String s) {
        return (s != null);
    }

    /**
     * Custom code if anything special is needed when establishing the session with a particular
     * endpoint (the websocket). Not used at present.
     *
     * @param endpointConfig the endpoint config.
     */
    @Override
    public void init(EndpointConfig endpointConfig) {
        // Custom initialization logic
    }

    /**
     * Destroy.
     *
     * Close the connection. Nothing implemented in the prototype. But then again, there's no
     * disconnect message.
     */
    @Override
    public void destroy() {
        // Close resources
    }
}
