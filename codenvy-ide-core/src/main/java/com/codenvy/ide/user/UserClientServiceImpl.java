/*
 * Copyright (C) 2013 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.codenvy.ide.user;

import com.codenvy.ide.api.user.UpdateUserAttributes;
import com.codenvy.ide.api.user.User;
import com.codenvy.ide.api.user.UserClientService;
import com.codenvy.ide.client.DtoClientImpls;
import com.codenvy.ide.rest.AsyncRequest;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.ui.loader.Loader;
import com.codenvy.ide.util.Utils;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

/**
 * The implementation of {@link com.codenvy.ide.api.user.UserClientService}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@Singleton
public class UserClientServiceImpl implements UserClientService {
    private static final String BASE_URL               = '/' + Utils.getWorkspaceName() + "/user";
    private static final String GET_USER               = BASE_URL + "/get";
    private static final String UPDATE_USER_ATTRIBUTES = BASE_URL + "/update";
    private String restContext;
    private Loader loader;

    /**
     * Create service.
     *
     * @param restContext
     * @param loader
     */
    @Inject
    protected UserClientServiceImpl(@Named("restContext") String restContext, Loader loader) {
        this.restContext = restContext;
        this.loader = loader;
    }

    /** {@inheritDoc} */
    @Override
    public void getUser(AsyncRequestCallback<User> callback) throws RequestException {
        final String requestUrl = restContext + GET_USER;

        AsyncRequest.build(RequestBuilder.GET, requestUrl).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void updateUserAttributes(UpdateUserAttributes updateUserAttributes, AsyncRequestCallback<Void> callback)
            throws RequestException {
        final String requestUrl = restContext + UPDATE_USER_ATTRIBUTES;

        DtoClientImpls.UpdateUserAttributesImpl userAttributes = (DtoClientImpls.UpdateUserAttributesImpl)updateUserAttributes;
        String updateAttributesData = userAttributes.serialize();

        AsyncRequest.build(RequestBuilder.POST, requestUrl).loader(loader).data(updateAttributesData).send(callback);
    }
}