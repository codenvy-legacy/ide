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
package com.codenvy.ide.extension.maven.client.wizard;

import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.AsyncRequestFactory;
import com.codenvy.ide.ui.loader.Loader;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import javax.inject.Inject;

/**
 * @author Evgen Vidolob
 */
@Singleton
public class MavenPomReaderClient {

    private final AsyncRequestFactory asyncRequestFactory;
    private final String              baseUrl;
    private final Loader              loader;

    @Inject
    public MavenPomReaderClient(@Named("restContext") String baseUrl,
                                @Named("workspaceId") String workspaceId,
                                Loader loader,
                                AsyncRequestFactory asyncRequestFactory) {
        this.asyncRequestFactory = asyncRequestFactory;
        this.baseUrl = baseUrl + "/maven/pom/" + workspaceId;
        this.loader = loader;
    }

    public void readPomAttributes(String projectPath, AsyncRequestCallback<String> callback) {
        final String requestUrl = baseUrl + "/read?projectpath=" + projectPath;
        callback.setSuccessCodes(new int[]{200, 201, 202, 204, 207, 1223});
        asyncRequestFactory.createGetRequest(requestUrl)
                           .send(callback);
    }
}
