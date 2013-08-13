/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package com.codenvy.ide.ext.git.client.github;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.annotations.Nullable;
import com.codenvy.ide.ext.git.dto.client.DtoClientImpls;
import com.codenvy.ide.ext.git.shared.Collaborators;
import com.codenvy.ide.ext.git.shared.GitHubRepository;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonStringMap;
import com.codenvy.ide.rest.AsyncRequest;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.ui.loader.Loader;
import com.codenvy.ide.util.Utils;
import com.google.gwt.http.client.RequestException;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import static com.codenvy.ide.rest.HTTPHeader.CONTENT_TYPE;
import static com.codenvy.ide.rest.MimeType.APPLICATION_JSON;
import static com.google.gwt.http.client.RequestBuilder.GET;
import static com.google.gwt.http.client.RequestBuilder.POST;

/**
 * Implementation for {@link GitHubClientService}.
 *
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: SamplesClientServiceImpl.java Sep 2, 2011 12:34:27 PM vereshchaka $
 */
@Singleton
public class GitHubClientServiceImpl implements GitHubClientService {
    private static final String BASE_URL      = '/' + Utils.getWorkspaceName() + "/github";
    private static final String LIST          = BASE_URL + "/list";
    private static final String LOGIN         = BASE_URL + "/login";
    private static final String LIST_USER     = BASE_URL + "/list/user";
    private static final String LIST_ALL      = BASE_URL + "/list/available";
    private static final String COLLABORATORS = BASE_URL + "/collaborators";
    private static final String TOKEN         = BASE_URL + "/token";
    /** REST service context. */
    private String restServiceContext;
    /** Loader to be displayed. */
    private Loader loader;

    /**
     * Create service.
     *
     * @param restContext
     * @param loader
     */
    @Inject
    protected GitHubClientServiceImpl(@Named("restContext") String restContext, Loader loader) {
        this.restServiceContext = restContext;
        this.loader = loader;
    }

    /** {@inheritDoc} */
    @Override
    public void getRepositoriesList(@NotNull AsyncRequestCallback<JsonArray<GitHubRepository>> callback) throws RequestException {
        String url = restServiceContext + LIST;
        AsyncRequest.build(GET, url).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void getRepositoriesByUser(@Nullable String userName, @NotNull AsyncRequestCallback<JsonArray<GitHubRepository>> callback)
            throws RequestException {
        String params = (userName != null) ? "?username=" + userName : "";
        String url = restServiceContext + LIST_USER;
        AsyncRequest.build(GET, url + params).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void getAllRepositories(@NotNull AsyncRequestCallback<JsonStringMap<JsonArray<GitHubRepository>>> callback)
            throws RequestException {
        String url = restServiceContext + LIST_ALL;
        AsyncRequest.build(GET, url).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void loginGitHub(@NotNull String login, @NotNull String password, @NotNull AsyncRequestCallback<String> callback)
            throws RequestException {
        String url = restServiceContext + LOGIN;

        DtoClientImpls.CredentialsImpl credentials = DtoClientImpls.CredentialsImpl.make();
        credentials.setLogin(login);
        credentials.setPassword(password);

        AsyncRequest.build(POST, url).loader(loader).data(credentials.serialize())
                    .header(CONTENT_TYPE, APPLICATION_JSON).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void getCollaborators(@NotNull String user, @NotNull String repository, @NotNull AsyncRequestCallback<Collaborators> callback)
            throws RequestException {
        String url = restServiceContext + COLLABORATORS + "/" + user + "/" + repository;
        AsyncRequest.build(GET, url).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void getUserToken(@NotNull String user, @NotNull AsyncRequestCallback<StringBuilder> callback) throws RequestException {
        String url = restServiceContext + TOKEN + "/" + user;
        AsyncRequest.build(GET, url).loader(loader).send(callback);
    }
}