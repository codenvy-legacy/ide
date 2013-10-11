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
package com.codenvy.ide.factory.client;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.client.framework.util.Utils;

/**
 * Implementation of {@link FactoryClientService}.
 * 
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: FactoryClientServiceImpl.java Jun 25, 2013 11:33:15 PM azatsarynnyy $
 */
public class FactoryClientServiceImpl extends FactoryClientService {

    /** Base url. */
    private static final String BASE_URL = Utils.getWorkspaceName() + "/factory";

    private static final String SHARE    = BASE_URL + "/share";

    /** REST-service context. */
    private String              restServiceContext;

    /** Loader to be displayed. */
    private Loader              loader;

    /**
     * Construct a new {@link FactoryClientServiceImpl}.
     * 
     * @param restContext REST-service context
     * @param loader loader to show on server request
     */
    public FactoryClientServiceImpl(Loader loader) {
        this.loader = loader;
        this.restServiceContext = Utils.getRestContext();
    }

    /**
     * Sends e-mail message to share Factory URL.
     * 
     * @throws RequestException
     */
    public void share(String recipient, String message, AsyncRequestCallback<Object> callback)
                                                                                                     throws RequestException {
        final String requesrUrl = restServiceContext + SHARE;

        String params = "recipient=" + recipient + "&message=" + message;
        AsyncRequest.build(RequestBuilder.POST, requesrUrl + "?" + params)
                    .header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_JSON).send(callback);
    }
}
