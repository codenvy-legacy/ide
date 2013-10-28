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
package org.exoplatform.ide.git.client.github;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;

import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.client.framework.util.Utils;
import org.exoplatform.ide.git.client.GitExtension;
import org.exoplatform.ide.git.shared.Collaborators;
import org.exoplatform.ide.git.shared.Credentials;
import org.exoplatform.ide.git.shared.GitHubRepository;
import org.exoplatform.ide.git.shared.GitHubRepositoryList;
import org.exoplatform.ide.git.shared.GitHubUser;

import java.util.List;
import java.util.Map;

/**
 * Implementation for {@link GitHubClientService}.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: SamplesClientServiceImpl.java Sep 2, 2011 12:34:27 PM vereshchaka $
 */
public class GitHubClientServiceImpl extends GitHubClientService {
    private static final String BASE_URL      = Utils.getWorkspaceName() + "/github";

    private static final String LIST          = BASE_URL + "/list";

    private static final String LOGIN         = BASE_URL + "/login";

    private static final String LIST_ACOUNT     = BASE_URL + "/list/account";
    
    private static final String LIST_ORG     = BASE_URL + "/list/org";
    
    private static final String LIST_USER     = BASE_URL + "/list/user";

    private static final String LIST_ALL      = BASE_URL + "/list/available";

    private static final String COLLABORATORS = BASE_URL + "/collaborators";
    
    private static final String ORGANIZATIONS = BASE_URL + "/orgs";

    private static final String PAGE          = BASE_URL + "/page";

    private static final String TOKEN         = BASE_URL + "/token";
    
    private static final String USER         = BASE_URL + "/user";

    /** REST service context. */
    private String              restServiceContext;

    /** Loader to be displayed. */
    private Loader              loader;

    public static final String  SUPPORT       = "support";

    public GitHubClientServiceImpl(Loader loader) {
        this.loader = loader;
        this.restServiceContext = Utils.getRestContext();
    }

    /**
     * @throws RequestException
     * @see org.exoplatform.ide.git.client.github.GitHubClientService#getRepositoriesList(org.exoplatform.gwtframework.commons.rest
     *      .AsyncRequestCallback)
     */
    @Override
    public void getRepositoriesList(AsyncRequestCallback<GitHubRepositoryList> callback) throws RequestException {
        String url = restServiceContext + LIST;
        AsyncRequest.build(RequestBuilder.GET, url).loader(loader).send(callback);
    }

    /**
     * @throws RequestException
     * @see org.exoplatform.ide.git.client.github.GitHubClientService#getRepositoriesByUser(java.lang.String,
     *      org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
     */
    @Override
    public void getRepositoriesByUser(String userName, AsyncRequestCallback<GitHubRepositoryList> callback)
                                                                                                             throws RequestException {
        String params = (userName != null) ? "?username=" + userName : "";
        String url = restServiceContext + LIST_USER;
        AsyncRequest.build(RequestBuilder.GET, url + params).loader(loader).send(callback);
    }

    /**
     * @throws RequestException
     * @see org.exoplatform.ide.git.client.github.GitHubClientService#getRepositoriesByUser(java.lang.String,
     *      org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
     */
    @Override
    public void getAllRepositories(AsyncRequestCallback<Map<String, List<GitHubRepository>>> callback)
                                                                                                      throws RequestException {
        String url = restServiceContext + LIST_ALL;
        AsyncRequest.build(RequestBuilder.GET, url).loader(loader).send(callback);
    }

    /**
     * @throws RequestException
     * @see org.exoplatform.ide.git.client.github.GitHubClientService#loginGitHub(java.lang.String, java.lang.String,
     *      org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
     */
    @Override
    public void loginGitHub(String login, String password, AsyncRequestCallback<String> callback)
                                                                                                 throws RequestException {
        String url = restServiceContext + LOGIN;

        Credentials credentialsBean = GitExtension.AUTO_BEAN_FACTORY.githubCredentials().as();
        credentialsBean.setLogin(login);
        credentialsBean.setPassword(password);
        String credentials = AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(credentialsBean)).getPayload();

        AsyncRequest.build(RequestBuilder.POST, url).loader(loader).data(credentials)
                    .header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_JSON).send(callback);
    }

    @Override
    public void getCollaborators(String user, String repository, AsyncRequestCallback<Collaborators> callback)
                                                                                                              throws RequestException {
        String url = restServiceContext + COLLABORATORS + "/" + user + "/" + repository;
        AsyncRequest.build(RequestBuilder.GET, url).loader(loader).send(callback);
    }

    /**
     * @see org.exoplatform.ide.git.client.github.GitHubClientService#getUserToken(java.lang.String,
     *      org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
     */
    @Override
    public void getUserToken(String user, AsyncRequestCallback<StringBuilder> callback) throws RequestException {
        String url = restServiceContext + TOKEN + "/" + user;
        AsyncRequest.build(RequestBuilder.GET, url).loader(loader).send(callback);
    }

    @Override
    public void getOrganizations(AsyncRequestCallback<List<String>> callback) throws RequestException {
        String url = restServiceContext + ORGANIZATIONS;
        AsyncRequest.build(RequestBuilder.GET, url).loader(loader).send(callback);
    }

    /**
     * @see org.exoplatform.ide.git.client.github.GitHubClientService#getUserInfo(org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
     */
    @Override
    public void getUserInfo(AsyncRequestCallback<GitHubUser> callback) throws RequestException {
        String url = restServiceContext + USER;
        AsyncRequest.build(RequestBuilder.GET, url).loader(loader).send(callback);
    }

    /**
     * @see org.exoplatform.ide.git.client.github.GitHubClientService#getRepositoriesByOrganization(java.lang.String, org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
     */
    @Override
    public void getRepositoriesByOrganization(String organization, AsyncRequestCallback<GitHubRepositoryList> callback) throws RequestException {
        String params = (organization != null) ? "?organization=" + organization : "";
        String url = restServiceContext + LIST_ORG;
        AsyncRequest.build(RequestBuilder.GET, url + params).loader(loader).send(callback);
    }

    /**
     * @see org.exoplatform.ide.git.client.github.GitHubClientService#getRepositoriesByAccount(java.lang.String, org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
     */
    @Override
    public void getRepositoriesByAccount(String account, AsyncRequestCallback<GitHubRepositoryList> callback) throws RequestException {
        String params = (account != null) ? "?account=" + account : "";
        String url = restServiceContext + LIST_ACOUNT;
        AsyncRequest.build(RequestBuilder.GET, url + params).loader(loader).send(callback);
    }

    /**
     * @see org.exoplatform.ide.git.client.github.GitHubClientService#getPage(java.lang.String, org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
     */
    @Override
    public void getPage(String pageLocation, AsyncRequestCallback<GitHubRepositoryList> callback) throws RequestException {
        String params = (pageLocation != null) ? "?url=" + pageLocation : "";
        String url = restServiceContext + PAGE;
        AsyncRequest.build(RequestBuilder.GET, url + params).loader(loader).send(callback);
    }
}
