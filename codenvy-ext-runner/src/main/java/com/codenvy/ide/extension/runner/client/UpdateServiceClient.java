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
