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
package com.codenvy.ide.user;

import com.codenvy.ide.MimeType;
import com.codenvy.ide.api.user.UserClientService;
import com.codenvy.ide.json.JsonHelper;
import com.codenvy.ide.rest.AsyncRequest;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.HTTPHeader;
import com.codenvy.ide.ui.loader.Loader;
import com.codenvy.ide.util.Utils;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import java.util.Map;

/**
 * The implementation of {@link com.codenvy.ide.api.user.UserClientService}.
 *
 * @author Andrey Plotnikov
 */
@Singleton
public class UserClientServiceImpl implements UserClientService {
    private static final String BASE_URL               = "/user/" + Utils.getWorkspaceName();
    private static final String GET_USER               = BASE_URL + "/get";
    private static final String UPDATE_USER_ATTRIBUTES = BASE_URL + "/update";
    private String restContext;
    private Loader loader;

    @Inject
    protected UserClientServiceImpl(@Named("restContext") String restContext, Loader loader) {
        this.restContext = restContext;
        this.loader = loader;
    }

    /** {@inheritDoc} */
    @Override
    public void getUser(AsyncRequestCallback<String> callback) throws RequestException {
        final String requestUrl = restContext + GET_USER;

        AsyncRequest.build(RequestBuilder.GET, requestUrl).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void updateUserAttributes(Map<String, String> updateUserAttributes, AsyncRequestCallback<Void> callback)
            throws RequestException {
        final String requestUrl = restContext + UPDATE_USER_ATTRIBUTES;
        String updateAttributesData = JsonHelper.toJson(updateUserAttributes);
        AsyncRequest.build(RequestBuilder.POST, requestUrl).loader(loader).header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON)
                    .data(updateAttributesData).send(callback);
    }
}