/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aalvarotex.sd.sdchatv2.json;

import com.aalvarotex.sd.sdchatv2.entities.Chat;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.json.Json;
import javax.json.stream.JsonParser;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;

/**
 *
 * @author alvar
 */
public class ChatReader implements MessageBodyReader<Chat> {

    /**
     *
     * @param type
     * @param type1
     * @param antns
     * @param mt
     * @return
     */
    @Override
    public boolean isReadable(Class<?> type, Type type1, Annotation[] antns, MediaType mt) {
        return Chat.class.isAssignableFrom(type);
    }

    /**
     *
     * @param type
     * @param genericType
     * @param annotations
     * @param mediaType
     * @param httpHeaders
     * @param entityStream
     * @return
     * @throws IOException
     * @throws WebApplicationException
     */
    @Override
    public Chat readFrom(Class<Chat> type,
            Type genericType,
            Annotation[] annotations,
            MediaType mediaType,
            MultivaluedMap<String, String> httpHeaders,
            InputStream entityStream)
            throws IOException, WebApplicationException {

        Chat chat = new Chat();
        JsonParser parser = Json.createParser(entityStream);
        while (parser.hasNext()) {
            switch (parser.next()) {
                case KEY_NAME:
                    String key = parser.getString();
                    parser.next();
                    switch (key) {
                        case "id":
                            chat.setId(parser.getLong());
                        case "idConversacion":
                            chat.setIdConversacion(parser.getString());
                            break;
                        case "idEmisor":
                            chat.setIdEmisor(parser.getLong());
                            break;
                        case "idReceptor":
                            chat.setIdReceptor(parser.getLong());
                            break;
                        case "mensaje":
                            chat.setMensaje(parser.getString());
                            break;
                        case "timestamp":
                            chat.setTimestamp(parser.getString());
                            break;
                        default:
                            break;
                    }
                    break;
                default:
                    break;
            }
        }
        return chat;
    }
}
