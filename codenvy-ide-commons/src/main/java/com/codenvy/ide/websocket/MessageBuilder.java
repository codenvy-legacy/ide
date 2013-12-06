/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package com.codenvy.ide.websocket;

import com.codenvy.ide.collections.js.JsoArray;
import com.codenvy.ide.util.UUID;
import com.codenvy.ide.websocket.rest.Pair;
import com.google.gwt.http.client.RequestBuilder.Method;

/**
 * Builder for constructing {@link Message}.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatfrom.com">Artem Zatsarynnyy</a>
 * @version $Id: MessageBuilder.java Nov 9, 2012 4:14:46 PM azatsarynnyy $
 */
public class MessageBuilder {
    public static final String UUID_FIELD = "uuid";
    /** Message which is constructing and may be send. */
    private final Message message;

    /**
     * Creates a {@link MessageBuilder} using the parameters for configuration.
     *
     * @param method
     *         HTTP method to use for the request
     * @param path
     *         URI
     */
    public MessageBuilder(Method method, String path) {
        message = Message.create();
        message.addField(UUID_FIELD, UUID.uuid());
        message.setMethod((method == null) ? null : method.toString());
        message.setPath(path);
    }

    /**
     * Sets a request header with the given name and value. If a header with the
     * specified name has already been set then the new value overwrites the
     * current value.
     *
     * @param name
     *         the name of the header
     * @param value
     *         the value of the header
     * @return this {@link MessageBuilder}
     */
    public final MessageBuilder header(String name, String value) {
        JsoArray<Pair> headers = message.getHeaders();
        if (headers == null) {
            headers = JsoArray.create();
        }

        for (int i = 0; i < headers.size(); i++) {
            Pair header = headers.get(i);
            if (name.equals(header.getName())) {
                header.setValue(value);
                return this;
            }
        }

        Pair header = Pair.create();
        header.setName(name);
        header.setValue(value);
        headers.add(header);
        message.setHeaders(headers);
        return this;
    }

    /**
     * Sets the data to send as body of this request.
     *
     * @param requestData
     *         the data to send as body of the request
     * @return this {@link MessageBuilder}
     */
    public final MessageBuilder data(String requestData) {
        message.setBody(requestData);
        return this;
    }

    /**
     * Builds message.
     *
     * @return message
     */
    public Message build() {
        return message;
    }
}