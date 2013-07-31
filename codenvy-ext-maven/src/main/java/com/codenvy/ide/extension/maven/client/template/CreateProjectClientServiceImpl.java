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
package com.codenvy.ide.extension.maven.client.template;

import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.resources.model.Property;
import com.codenvy.ide.rest.AsyncRequest;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.ui.loader.Loader;
import com.codenvy.ide.util.Utils;
import com.google.gwt.http.client.RequestException;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import static com.codenvy.ide.resources.marshal.JSONSerializer.PROPERTY_SERIALIZER;
import static com.codenvy.ide.rest.HTTPHeader.CONTENT_TYPE;
import static com.google.gwt.http.client.RequestBuilder.POST;

/**
 * The implementation of {@link CreateProjectClientService}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@Singleton
public class CreateProjectClientServiceImpl implements CreateProjectClientService {
    private static final String BASE_URL              = '/' + Utils.getWorkspaceName() + "/maven/create";
    private static final String CREATE_WAR_PROJECT    = BASE_URL + "/project/war";
    private static final String CREATE_JAVA_PROJECT   = BASE_URL + "/project/java";
    private static final String CREATE_SPRING_PROJECT = BASE_URL + "/project/spring";
    private static final String CREATE_EMPTY_PROJECT  = BASE_URL + "/project/empty";
    private String           restContext;
    private Loader           loader;
    private ResourceProvider resourceProvider;

    /**
     * Create service.
     *
     * @param restContext
     * @param loader
     * @param resourceProvider
     */
    @Inject
    protected CreateProjectClientServiceImpl(@Named("restContext") String restContext, Loader loader, ResourceProvider resourceProvider) {
        this.restContext = restContext;
        this.loader = loader;
        this.resourceProvider = resourceProvider;
    }

    /** {@inheritDoc} */
    @Override
    public void createWarProject(String projectName, JsonArray<Property> properties, AsyncRequestCallback<Void> callback)
            throws RequestException {
        String requestUrl = restContext + CREATE_WAR_PROJECT;

        String param = "?vfsid=" + resourceProvider.getVfsId() + "&name=" + projectName + "&rootId=" + resourceProvider.getRootId();
        String url = requestUrl + param;

        loader.setMessage("Creating new project...");

        AsyncRequest.build(POST, url)
                    .data(PROPERTY_SERIALIZER.fromCollection(properties).toString())
                    .header(CONTENT_TYPE, "application/json").loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void createSpringProject(String projectName, JsonArray<Property> properties, AsyncRequestCallback<Void> callback)
            throws RequestException {
        String requestUrl = restContext + CREATE_SPRING_PROJECT;

        String param = "?vfsid=" + resourceProvider.getVfsId() + "&name=" + projectName + "&rootId=" + resourceProvider.getRootId();
        String url = requestUrl + param;

        loader.setMessage("Creating new project...");

        AsyncRequest.build(POST, url)
                    .data(PROPERTY_SERIALIZER.fromCollection(properties).toString())
                    .header(CONTENT_TYPE, "application/json").loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void createJavaProject(String projectName, JsonArray<Property> properties, AsyncRequestCallback<Void> callback)
            throws RequestException {
        String requestUrl = restContext + CREATE_JAVA_PROJECT;
        String param = "?vfsid=" + resourceProvider.getVfsId() + "&name=" + projectName + "&rootId=" + resourceProvider.getRootId();
        String url = requestUrl + param;

        loader.setMessage("Creating new project...");

        AsyncRequest.build(POST, url)
                    .data(PROPERTY_SERIALIZER.fromCollection(properties).toString())
                    .header(CONTENT_TYPE, "application/json").loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void createEmptyProject(String projectName, JsonArray<Property> properties, AsyncRequestCallback<Void> callback)
            throws RequestException {
        String requestUrl = restContext + CREATE_EMPTY_PROJECT;

        String param = "?vfsid=" + resourceProvider.getVfsId() + "&name=" + projectName;
        String url = requestUrl + param;

        loader.setMessage("Creating project...");

        AsyncRequest.build(POST, url)
                    .data(PROPERTY_SERIALIZER.fromCollection(properties).toString())
                    .header(CONTENT_TYPE, "application/json").loader(loader).send(callback);
    }
}