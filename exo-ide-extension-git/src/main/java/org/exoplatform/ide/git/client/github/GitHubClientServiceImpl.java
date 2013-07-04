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

    private static final String LIST_USER     = BASE_URL + "/list/user";

    private static final String LIST_ALL      = BASE_URL + "/list/available";

    private static final String COLLABORATORS = BASE_URL + "/collaborators";

    private static final String TOKEN         = BASE_URL + "/token";

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
    public void getRepositoriesList(AsyncRequestCallback<List<GitHubRepository>> callback) throws RequestException {
        String url = restServiceContext + LIST;
        AsyncRequest.build(RequestBuilder.GET, url).loader(loader).send(callback);
    }

    /**
     * @throws RequestException
     * @see org.exoplatform.ide.git.client.github.GitHubClientService#getRepositoriesByUser(java.lang.String,
     *      org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
     */
    @Override
    public void getRepositoriesByUser(String userName, AsyncRequestCallback<List<GitHubRepository>> callback)
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
}
