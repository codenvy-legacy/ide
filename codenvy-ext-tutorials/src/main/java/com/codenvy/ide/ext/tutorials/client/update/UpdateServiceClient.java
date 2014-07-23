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
import com.codenvy.ide.websocket.WebSocketException;
import com.codenvy.ide.websocket.rest.RequestCallback;

import javax.validation.constraints.NotNull;

/**
 * Client for service for updating launched Codenvy Extension on SDK runner.
 *
 * @author Artem Zatsarynnyy
 */
public interface UpdateServiceClient {
    /**
     * Update launched extension.
     *
     * @param applicationProcessDescriptor
     *         {@link ApplicationProcessDescriptor} that represents a launched extension
     * @param callback
     *         the callback to use for the response
     * @throws WebSocketException
     */
    public void update(ApplicationProcessDescriptor applicationProcessDescriptor, @NotNull RequestCallback<Void> callback)
            throws WebSocketException;
}
