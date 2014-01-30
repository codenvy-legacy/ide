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
package com.codenvy.ide.ext.github.client;

import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.StringMap;
import com.codenvy.ide.ext.github.shared.Collaborators;
import com.codenvy.ide.ext.github.shared.GitHubRepository;
import com.codenvy.ide.ext.github.shared.GitHubRepositoryList;
import com.codenvy.ide.ext.github.shared.GitHubUser;
import com.codenvy.ide.rest.AsyncRequest;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.ui.loader.Loader;
import com.codenvy.ide.util.Utils;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Implementation for {@link GitHubClientService}.
 *
 * @author Oksana Vereshchaka
 */
@Singleton
public class GitHubClientServiceImpl implements GitHubClientService {
    private static final String LIST          = "/list";
    private static final String LIST_ACCOUNT  = "/list/account";
    private static final String LIST_ORG      = "/list/org";
    private static final String LIST_USER     = "/list/user";
    private static final String LIST_ALL      = "/list/available";
    private static final String COLLABORATORS = "/collaborators";
    private static final String ORGANIZATIONS = "/orgs";
    private static final String PAGE          = "/page";
    private static final String TOKEN         = "/token";
    private static final String USER          = "/user";

    /** REST service context. */
    private final String baseUrl;
    /** Loader to be displayed. */
    private final Loader loader;

    @Inject
    protected GitHubClientServiceImpl(@Named("restContext") String baseUrl, Loader loader) {
        this.baseUrl = baseUrl + "/github/" + Utils.getWorkspaceId();
        this.loader = loader;
    }

    /** {@inheritDoc} */
    @Override
    public void getRepositoriesList(AsyncRequestCallback<GitHubRepositoryList> callback) throws RequestException {
        String url = baseUrl + LIST;
        AsyncRequest.build(RequestBuilder.GET, url).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void getRepositoriesByUser(String userName, AsyncRequestCallback<GitHubRepositoryList> callback) throws RequestException {
        String params = (userName != null) ? "?username=" + userName : "";
        String url = baseUrl + LIST_USER;
        AsyncRequest.build(RequestBuilder.GET, url + params).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void getAllRepositories(AsyncRequestCallback<StringMap<Array<GitHubRepository>>> callback) throws RequestException {
        String url = baseUrl + LIST_ALL;
        AsyncRequest.build(RequestBuilder.GET, url).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void getCollaborators(String user, String repository, AsyncRequestCallback<Collaborators> callback) throws RequestException {
        String url = baseUrl + COLLABORATORS + "/" + user + "/" + repository;
        AsyncRequest.build(RequestBuilder.GET, url).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void getUserToken(@NotNull String user, @NotNull AsyncRequestCallback<String> callback) throws RequestException {
        String url = baseUrl + TOKEN + "/" + user;
        AsyncRequest.build(RequestBuilder.GET, url).loader(loader).send(callback);
    }
    
    /** {@inheritDoc} */
    @Override
    public void getOrganizations(AsyncRequestCallback<List<String>> callback) throws RequestException {
        String url = baseUrl + ORGANIZATIONS;
        AsyncRequest.build(RequestBuilder.GET, url).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void getUserInfo(AsyncRequestCallback<GitHubUser> callback) throws RequestException {
        String url = baseUrl + USER;
        AsyncRequest.build(RequestBuilder.GET, url).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void getRepositoriesByOrganization(String organization, AsyncRequestCallback<GitHubRepositoryList> callback)
            throws RequestException {
        String params = (organization != null) ? "?organization=" + organization : "";
        String url = baseUrl + LIST_ORG;
        AsyncRequest.build(RequestBuilder.GET, url + params).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void getRepositoriesByAccount(String account, AsyncRequestCallback<GitHubRepositoryList> callback) throws RequestException {
        String params = (account != null) ? "?account=" + account : "";
        String url = baseUrl + LIST_ACCOUNT;
        AsyncRequest.build(RequestBuilder.GET, url + params).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void getPage(String pageLocation, AsyncRequestCallback<GitHubRepositoryList> callback) throws RequestException {
        String params = (pageLocation != null) ? "?url=" + pageLocation : "";
        String url = baseUrl + PAGE;
        AsyncRequest.build(RequestBuilder.GET, url + params).loader(loader).send(callback);
    }
}