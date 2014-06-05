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
package com.codenvy.ide.ext.java.client.format;

import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.AsyncRequestFactory;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import static com.codenvy.ide.MimeType.APPLICATION_JSON;
import static com.codenvy.ide.rest.HTTPHeader.ACCEPT;

/**
 * @author Roman Nikitenko
 */
public class FormatClientService {


    public static final String CODENVY = "/codenvy";

    private final AsyncRequestFactory asyncRequestFactory;
    private final String              baseHttpUrl;
    public final  String              formatServicePath;

    @Inject
    public FormatClientService(@Named("restContext") String baseHttpUrl,
                               AsyncRequestFactory asyncRequestFactory) {
        this.asyncRequestFactory = asyncRequestFactory;
        this.formatServicePath = "/code-formatting";
        this.baseHttpUrl = baseHttpUrl;
    }

    public void formattingCodenvySettings(AsyncRequestCallback<String> callback) {
        String url = baseHttpUrl + formatServicePath + CODENVY;
        asyncRequestFactory.createGetRequest(url).send(callback);
    }
}
