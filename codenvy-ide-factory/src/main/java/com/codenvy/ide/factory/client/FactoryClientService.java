package com.codenvy.ide.factory.client;


import com.codenvy.api.factory.dto.Factory;
import com.codenvy.ide.websocket.WebSocketException;
import com.codenvy.ide.websocket.rest.RequestCallback;

import javax.validation.constraints.NotNull;

/**
 * @author vzhukovskii@codenvy.com
 */
public interface FactoryClientService {
    void getFactory(@NotNull String raw, boolean encoded, @NotNull RequestCallback<Factory> callback) throws WebSocketException;

    void acceptFactory(@NotNull Factory factory, @NotNull RequestCallback<Factory> callback) throws WebSocketException;
}
