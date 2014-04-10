package com.codenvy.ide.factory.client;

import com.codenvy.api.factory.dto.Factory;
import com.codenvy.ide.MimeType;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.rest.HTTPHeader;
import com.codenvy.ide.util.Utils;
import com.codenvy.ide.websocket.Message;
import com.codenvy.ide.websocket.MessageBuilder;
import com.codenvy.ide.websocket.MessageBus;
import com.codenvy.ide.websocket.WebSocketException;
import com.codenvy.ide.websocket.rest.RequestCallback;
import com.google.gwt.http.client.RequestBuilder;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.validation.constraints.NotNull;

/**
 * @author Vladyslav Zhukovskii
 */
@Singleton
public class FactoryClientServiceImpl implements FactoryClientService {
    private final DtoFactory dtoFactory;
    private final MessageBus messageBus;

    @Inject
    public FactoryClientServiceImpl(DtoFactory dtoFactory, MessageBus messageBus) {
        this.dtoFactory = dtoFactory;
        this.messageBus = messageBus;
    }

    @Override
    public void getFactory(@NotNull String raw, boolean encoded, @NotNull RequestCallback<Factory> callback) throws WebSocketException {
        StringBuilder url = new StringBuilder("/factory");

        if (encoded) {
            url.append("/").append(raw).append("?");
        } else {
            url.append("/nonencoded?").append(raw).append("&");
        }

        url.append("legacy=true");

        Message message =
                new MessageBuilder(RequestBuilder.GET, url.toString()).header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON).build();
        messageBus.send(message, callback);
    }

    @Override
    public void acceptFactory(@NotNull Factory factory, @NotNull RequestCallback<Factory> callback) throws WebSocketException {
        Message message =
                new MessageBuilder(RequestBuilder.POST, "/factory-handler/" + Utils.getWorkspaceId() + "/accept")
                        .header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_JSON)
                        .header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON)
                        .data(dtoFactory.toJson(factory)).build();

        messageBus.send(message, callback);
    }
}
