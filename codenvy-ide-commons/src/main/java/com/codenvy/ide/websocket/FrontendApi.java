/*
 * Copyright (C) 2013 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.codenvy.ide.websocket;


import com.codenvy.ide.dtogen.client.RoutableDtoClientImpl;
import com.codenvy.ide.dtogen.client.ServerErrorImpl;
import com.codenvy.ide.dtogen.shared.ClientToServerDto;
import com.codenvy.ide.dtogen.shared.RoutableDto;
import com.codenvy.ide.dtogen.shared.ServerError;
import com.codenvy.ide.dtogen.shared.ServerToClientDto;
import com.codenvy.ide.json.js.Jso;
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
