package com.neu.prattle.websocket;

import com.google.gson.Gson;
import com.neu.prattle.model.Message;
import com.neu.prattle.utils.GsonUtil;

import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

/**
 * The Class MessageEncoder converts Message objects into JSON String formats.
 *
 * @author team 9
 * @version dated 2019-11-21
 */
public class MessageEncoder implements Encoder.Text<Message> {

    /**
     * A gson object to serialize the message
     */
    private Gson gson = GsonUtil.getGson();

    /**
     * Encode converts a Message object into a JSON structured string.
     * A Message contains a from, to, content, and timestamp which
     * will all be serialized into the JSON string.
     *
     * @param message What needs to be serialized.
     * @return the resulting JSON (String).
     */
    @Override
    public String encode(Message message) {
        return gson.toJson(message);
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
     * Close the connection.  Nothing implemented in the prototype. But then again, there's no
     * disconnect message.
     */
    @Override
    public void destroy() {
        // Close resources
    }
}
