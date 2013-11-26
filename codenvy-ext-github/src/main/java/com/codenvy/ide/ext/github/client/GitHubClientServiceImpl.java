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

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.ext.github.shared.Collaborators;
import com.codenvy.ide.ext.github.shared.GitHubRepository;
import com.codenvy.ide.ext.github.shared.GitHubRepositoryList;
import com.codenvy.ide.ext.github.shared.GitHubUser;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonStringMap;
import com.codenvy.ide.rest.AsyncRequest;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.ui.loader.Loader;
import com.codenvy.ide.util.Utils;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import java.util.List;

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
    private static final String LIST_ACOUNT   = BASE_URL + "/list/account";
    private static final String LIST_ORG      = BASE_URL + "/list/org";
    private static final String LIST_USER     = BASE_URL + "/list/user";
    private static final String LIST_ALL      = BASE_URL + "/list/available";
    private static final String COLLABORATORS = BASE_URL + "/collaborators";
    private static final String ORGANIZATIONS = BASE_URL + "/orgs";
    private static final String PAGE          = BASE_URL + "/page";
    private static final String TOKEN         = BASE_URL + "/token";
    private static final String USER          = BASE_URL + "/user";
    
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
    public void getRepositoriesList(AsyncRequestCallback<GitHubRepositoryList> callback) throws RequestException {
        String url = restServiceContext + LIST;
        AsyncRequest.build(RequestBuilder.GET, url).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void getRepositoriesByUser(String userName, AsyncRequestCallback<GitHubRepositoryList> callback)
                                                                                                             throws RequestException {
        String params = (userName != null) ? "?username=" + userName : "";
        String url = restServiceContext + LIST_USER;
        AsyncRequest.build(RequestBuilder.GET, url + params).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void getAllRepositories(AsyncRequestCallback<JsonStringMap<JsonArray<GitHubRepository>>> callback)
                                                                                                      throws RequestException {
        String url = restServiceContext + LIST_ALL;
        AsyncRequest.build(RequestBuilder.GET, url).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void getCollaborators(String user, String repository, AsyncRequestCallback<Collaborators> callback)
                                                                                                              throws RequestException {
        String url = restServiceContext + COLLABORATORS + "/" + user + "/" + repository;
        AsyncRequest.build(RequestBuilder.GET, url).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void getUserToken(@NotNull String user, @NotNull AsyncRequestCallback<String> callback) throws RequestException {
        String url = restServiceContext + TOKEN + "/" + user;
        AsyncRequest.build(RequestBuilder.GET, url).loader(loader).send(callback);
    }
    
    /** {@inheritDoc} */
    @Override
    public void getOrganizations(AsyncRequestCallback<List<String>> callback) throws RequestException {
        String url = restServiceContext + ORGANIZATIONS;
        AsyncRequest.build(RequestBuilder.GET, url).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void getUserInfo(AsyncRequestCallback<GitHubUser> callback) throws RequestException {
        String url = restServiceContext + USER;
        AsyncRequest.build(RequestBuilder.GET, url).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void getRepositoriesByOrganization(String organization, AsyncRequestCallback<GitHubRepositoryList> callback) throws RequestException {
        String params = (organization != null) ? "?organization=" + organization : "";
        String url = restServiceContext + LIST_ORG;
        AsyncRequest.build(RequestBuilder.GET, url + params).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void getRepositoriesByAccount(String account, AsyncRequestCallback<GitHubRepositoryList> callback) throws RequestException {
        String params = (account != null) ? "?account=" + account : "";
        String url = restServiceContext + LIST_ACOUNT;
        AsyncRequest.build(RequestBuilder.GET, url + params).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void getPage(String pageLocation, AsyncRequestCallback<GitHubRepositoryList> callback) throws RequestException {
        String params = (pageLocation != null) ? "?url=" + pageLocation : "";
        String url = restServiceContext + PAGE;
        AsyncRequest.build(RequestBuilder.GET, url + params).loader(loader).send(callback);
    }
}