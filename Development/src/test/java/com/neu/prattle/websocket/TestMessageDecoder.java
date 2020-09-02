package com.neu.prattle.websocket;

import com.neu.prattle.model.Message;

import org.junit.Test;

import javax.websocket.EncodeException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestMessageDecoder {
    private MessageDecoder messageDecoder = new MessageDecoder();
    private MessageEncoder messageEncoder = new MessageEncoder();

    @Test
    public void testEncode() throws EncodeException {
        String s = messageEncoder.encode(Message.messageBuilder().setFrom("u0").setToUsername("u1").setMessageContent("test").build());

        assertTrue(messageDecoder.willDecode(s));

        Message message = messageDecoder.decode(s);
        assertEquals("u0", message.getFromUsername());
        assertEquals("u1", message.getToUsername());
        assertEquals("test", message.getContent());
        messageEncoder.destroy();
        messageDecoder.destroy();
    }
}
