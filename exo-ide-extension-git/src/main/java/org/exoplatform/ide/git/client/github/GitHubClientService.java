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

import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.git.shared.Collaborators;
import org.exoplatform.ide.git.shared.GitHubRepository;

import java.util.List;
import java.util.Map;

/**
 * Client service for Samples.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: SamplesClientService.java Sep 2, 2011 12:34:16 PM vereshchaka $
 */
public abstract class GitHubClientService {
    private static GitHubClientService instance;

    public static GitHubClientService getInstance() {
        return instance;
    }

    protected GitHubClientService() {
        instance = this;
    }

    /**
     * Get the list of available public and private repositories of the authorized user.
     * 
     * @param callback the callback client has to implement
     */
    public abstract void getRepositoriesList(AsyncRequestCallback<List<GitHubRepository>> callback)
                                                                                                   throws RequestException;

    /**
     * Get the list of available public repositories from GitHub user.
     * 
     * @param userName Name of GitHub User
     * @param callback the callback client has to implement
     */
    public abstract void getRepositoriesByUser(String userName, AsyncRequestCallback<List<GitHubRepository>> callback)
                                                                                                                      throws RequestException;

    /**
     * Log in GitHub account.
     * 
     * @param login user's login
     * @param password user's password
     * @param callback callback the client has to implement
     */
    public abstract void loginGitHub(String login, String password, AsyncRequestCallback<String> callback)
                                                                                                          throws RequestException;

    /**
     * Get list of collaborators of GitHub repository. For detail see GitHub REST API http://developer.github.com/v3/repos/collaborators/.
     * 
     * @param user
     * @param repository
     * @param callback
     * @throws RequestException
     */
    public abstract void getCollaborators(String user, String repository, AsyncRequestCallback<Collaborators> callback)
                                                                                                                       throws RequestException;

    /**
     * Get the GitHub oAuth token for the pointed user.
     * 
     * @param user user's id
     * @param callback
     * @throws RequestException
     */
    public abstract void getUserToken(String user, AsyncRequestCallback<StringBuilder> callback) throws RequestException;

    /**
     * Get the map of available public and private repositories of the authorized user and organizations he exists in.
     * 
     * @param callback the callback client has to implement
     */
    public abstract void getAllRepositories(AsyncRequestCallback<Map<String, List<GitHubRepository>>> callback) throws RequestException;
}
