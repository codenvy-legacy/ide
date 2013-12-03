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


import com.codenvy.ide.dto.client.RoutableDtoClientImpl;
import com.codenvy.ide.dto.client.ServerErrorImpl;
import com.codenvy.ide.dto.shared.ClientToServerDto;
import com.codenvy.ide.dto.shared.RoutableDto;
import com.codenvy.ide.dto.shared.ServerError;
import com.codenvy.ide.dto.shared.ServerToClientDto;
import com.codenvy.ide.collections.js.Jso;
import com.codenvy.ide.websocket.events.ReplyHandler;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class FrontendApi {

    protected class ApiImpl<REQ extends ClientToServerDto, RESP extends ServerToClientDto>
            implements RequestResponseApi<REQ, RESP>, SendApi<REQ> {
        private final String address;

        protected ApiImpl(String address) {
            this.address = address;
        }

        @Override
        public void send(REQ msg) {
            RoutableDtoClientImpl messageImpl = (RoutableDtoClientImpl)msg;
            messageBus.send(address, messageImpl.serialize());
        }

        @Override
        public void send(REQ msg, final ApiCallback<RESP> callback) {
            RoutableDtoClientImpl messageImpl = (RoutableDtoClientImpl)msg;
            messageBus.send(address, messageImpl.serialize(), new ReplyHandler() {
                @Override
                public void onReply(String message) {
                    Jso jso = Jso.deserialize(message);

                    if (RoutableDto.SERVER_ERROR == jso.getIntField(RoutableDto.TYPE_FIELD)) {
                        ServerErrorImpl serverError = (ServerErrorImpl)jso;
                        callback.onFail(serverError.getFailureReason());
                        return;
                    }

                    ServerToClientDto messageDto = (ServerToClientDto)jso;

                    @SuppressWarnings("unchecked") RESP resp = (RESP)messageDto;
                    callback.onMessageReceived(resp);
                }
            });
        }
    }

    /**
     * EventBus API that documents the message types sent to the frontend. This API is fire and
     * forget, since it does not expect a response.
     *
     * @param <REQ>
     *         The outgoing message type.
     */
    public static interface SendApi<REQ extends ClientToServerDto> {
        public void send(REQ msg);
    }

    /**
     * EventBus API that documents the message types sent to the frontend, and the message type
     * expected to be returned as a response.
     *
     * @param <REQ>
     *         The outgoing message type.
     * @param <RESP>
     *         The incoming message type.
     */
    public static interface RequestResponseApi<
            REQ extends ClientToServerDto, RESP extends ServerToClientDto> {
        public void send(REQ msg, final ApiCallback<RESP> callback);
    }

    /** Callback interface for receiving a matched response for requests to a frontend API. */
    public interface ApiCallback<T extends ServerToClientDto> extends MessageFilter.MessageRecipient<T> {
        void onFail(ServerError.FailureReason reason);
    }

    private MessageBus messageBus;

    public FrontendApi(MessageBus messageBus) {
        this.messageBus = messageBus;
    }

    /**
     * Makes an API given the URL.
     *
     * @param <REQ>
     *         the request object
     * @param <RESP>
     *         the response object
     */
    protected <REQ extends ClientToServerDto, RESP extends ServerToClientDto> ApiImpl<REQ, RESP> makeApi(String url) {
        return new ApiImpl<REQ, RESP>(url);
    }
}
