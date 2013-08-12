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
package com.codenvy.ide.ext.git.client.github;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.annotations.Nullable;
import com.codenvy.ide.ext.git.shared.Collaborators;
import com.codenvy.ide.ext.git.shared.GitHubRepository;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonStringMap;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.google.gwt.http.client.RequestException;

/**
 * Client service for Samples.
 *
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: SamplesClientService.java Sep 2, 2011 12:34:16 PM vereshchaka $
 */
public interface GitHubClientService {
    /**
     * Get the list of available public and private repositories of the authorized user.
     *
     * @param callback
     *         the callback client has to implement
     */
    void getRepositoriesList(@NotNull AsyncRequestCallback<JsonArray<GitHubRepository>> callback) throws RequestException;

    /**
     * Get the list of available public repositories from GitHub user.
     *
     * @param userName
     *         Name of GitHub User
     * @param callback
     *         the callback client has to implement
     */
    void getRepositoriesByUser(@Nullable String userName, @NotNull AsyncRequestCallback<JsonArray<GitHubRepository>> callback)
            throws RequestException;

    /**
     * Log in GitHub account.
     *
     * @param login
     *         user's login
     * @param password
     *         user's password
     * @param callback
     *         callback the client has to implement
     */
    void loginGitHub(@NotNull String login, @NotNull String password, @NotNull AsyncRequestCallback<String> callback)
            throws RequestException;

    /**
     * Get list of collaborators of GitHub repository. For detail see GitHub REST API http://developer.github.com/v3/repos/collaborators/.
     *
     * @param user
     * @param repository
     * @param callback
     * @throws RequestException
     */
    void getCollaborators(@NotNull String user, @NotNull String repository, @NotNull AsyncRequestCallback<Collaborators> callback)
            throws RequestException;

    /**
     * Get the GitHub oAuth token for the pointed user.
     *
     * @param user
     *         user's id
     * @param callback
     * @throws RequestException
     */
    void getUserToken(@NotNull String user, @NotNull AsyncRequestCallback<StringBuilder> callback) throws RequestException;

    /**
     * Get the map of available public and private repositories of the authorized user and organizations he exists in.
     *
     * @param callback
     *         the callback client has to implement
     */
    void getAllRepositories(@NotNull AsyncRequestCallback<JsonStringMap<JsonArray<GitHubRepository>>> callback) throws RequestException;
}