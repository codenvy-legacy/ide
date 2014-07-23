/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.ext.tutorials.client.update;

import com.codenvy.api.runner.dto.ApplicationProcessDescriptor;
import com.codenvy.ide.websocket.Message;
import com.codenvy.ide.websocket.MessageBuilder;
import com.codenvy.ide.websocket.MessageBus;
import com.codenvy.ide.websocket.WebSocketException;
import com.codenvy.ide.websocket.rest.RequestCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.validation.constraints.NotNull;

import static com.google.gwt.http.client.RequestBuilder.POST;

/** @author Artem Zatsarynnyy */
@Singleton
public class UpdateServiceClientImpl implements UpdateServiceClient {
    private final String     updateServicePath;
    private final MessageBus wsMessageBus;

    @Inject
    public UpdateServiceClientImpl(MessageBus wsMessageBus) {
        this.updateServicePath = "/runner-sdk";
        this.wsMessageBus = wsMessageBus;
    }

    @Override
    public void update(ApplicationProcessDescriptor applicationProcessDescriptor, @NotNull RequestCallback<Void> callback)
            throws WebSocketException {
        final String url = updateServicePath + "/update/" + applicationProcessDescriptor.getProcessId();
        MessageBuilder messageBuilder = new MessageBuilder(POST, url);
        Message message = messageBuilder.build();
        wsMessageBus.send(message, callback);
    }
}
