/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 *  [2012] - [2014] Codenvy, S.A.
 *  All Rights Reserved.
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
package com.codenvy.ide.extension.runner.client;

import com.codenvy.ide.websocket.Message;
import com.codenvy.ide.websocket.MessageBuilder;
import com.codenvy.ide.websocket.MessageBus;
import com.codenvy.ide.websocket.WebSocketException;
import com.codenvy.ide.websocket.rest.RequestCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import javax.validation.constraints.NotNull;

import static com.google.gwt.http.client.RequestBuilder.POST;

/**
 * //
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class UpdateServiceClientImpl implements UpdateServiceClient {
    private final String     updateServicePath;
    private final MessageBus wsMessageBus;

    @Inject
    public UpdateServiceClientImpl(@Named("workspaceId") String workspaceId, MessageBus wsMessageBus) {
        this.updateServicePath = "/sdk/" + workspaceId;
        this.wsMessageBus = wsMessageBus;
    }

    @Override
    public void update(Long id, @NotNull RequestCallback<Void> callback) throws WebSocketException {
        final String url = updateServicePath + "/update" + "?id=" + id;
        MessageBuilder messageBuilder = new MessageBuilder(POST, url);
        Message message = messageBuilder.build();

        wsMessageBus.send(message, callback);
    }
}
