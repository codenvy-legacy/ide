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
import com.codenvy.ide.api.user.User;
import com.codenvy.ide.api.user.UserClientService;
import com.codenvy.ide.json.JsonHelper;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.AsyncRequestFactory;
import com.codenvy.ide.rest.HTTPHeader;
import com.codenvy.ide.ui.loader.Loader;
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
    private static final String GET_USER               = "/get";
    private static final String UPDATE_USER_ATTRIBUTES = "/update";
    private final AsyncRequestFactory asyncRequestFactory;
    private       String              baseUrl;
    private       Loader              loader;

    @Inject
    protected UserClientServiceImpl(@Named("restContext") String baseUrl, @Named("workspaceId") String workspaceId, Loader loader,
                                    AsyncRequestFactory asyncRequestFactory) {
        this.asyncRequestFactory = asyncRequestFactory;
        this.baseUrl = baseUrl + "/user/" + workspaceId;
        this.loader = loader;
    }

    /** {@inheritDoc} */
    @Override
    public void getUser(AsyncRequestCallback<User> callback) {
        final String requestUrl = baseUrl + GET_USER;
        asyncRequestFactory.createGetRequest(requestUrl).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void updateUserAttributes(Map<String, String> updateUserAttributes, AsyncRequestCallback<Void> callback) {
        final String requestUrl = baseUrl + UPDATE_USER_ATTRIBUTES;
        final String updateAttributesData = JsonHelper.toJson(updateUserAttributes);
        asyncRequestFactory.createPostRequest(requestUrl, null)
                           .loader(loader)
                           .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON)
                           .data(updateAttributesData)
                           .send(callback);
    }
}