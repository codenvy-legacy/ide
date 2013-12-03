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
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.JsonStringMap;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.google.gwt.http.client.RequestException;

import java.util.List;

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
     * @param callback the callback client has to implement
     */
    public abstract void getRepositoriesList(@NotNull AsyncRequestCallback<GitHubRepositoryList> callback)
                                                                                                   throws RequestException;

    /**
     * Get the list of available public repositories from GitHub user.
     * 
     * @param userName Name of GitHub User
     * @param callback the callback client has to implement
     */
    public abstract void getRepositoriesByUser(@NotNull String userName, @NotNull AsyncRequestCallback<GitHubRepositoryList> callback)
                                                                                                                      throws RequestException;
    
    /**
     * Get the page with GitHub repositories.
     * 
     * @param pageLocation page location
     * @param callback
     * @throws RequestException
     */
    public abstract void getPage(@NotNull String pageLocation, @NotNull AsyncRequestCallback<GitHubRepositoryList> callback)
                                                                                                 throws RequestException;
    
    /**
     * Get the list of available repositories by GitHub organization.
     * 
     * @param organization Name of GitHub organization
     * @param callback the callback client has to implement
     */
    public abstract void getRepositoriesByOrganization(@NotNull String organization, @NotNull AsyncRequestCallback<GitHubRepositoryList> callback)
                                                                                                                      throws RequestException;
    /**
     * Get the list of available public repositories from GitHub account.
     * 
     * @param account Name of GitHub Account
     * @param callback the callback client has to implement
     */
    public abstract void getRepositoriesByAccount(@NotNull String account, @NotNull AsyncRequestCallback<GitHubRepositoryList> callback)
                                                                                                                      throws RequestException;
    
    /**
     * Get list of collaborators of GitHub repository. For detail see GitHub REST API http://developer.github.com/v3/repos/collaborators/.
     * 
     * @param user
     * @param repository
     * @param callback
     * @throws RequestException
     */
    public abstract void getCollaborators(@NotNull String user, @NotNull String repository, @NotNull AsyncRequestCallback<Collaborators> callback)
                                                                                                                       throws RequestException;

    /**
     * Get the GitHub oAuth token for the pointed user.
     * 
     * @param user user's id
     * @param callback
     * @throws RequestException
     */
    public abstract void getUserToken(@NotNull String user, @NotNull AsyncRequestCallback<String> callback) throws RequestException;

    /**
     * Get the map of available public and private repositories of the authorized user and organizations he exists in.
     * 
     * @param callback the callback client has to implement
     */
    public abstract void getAllRepositories(@NotNull AsyncRequestCallback<JsonStringMap<Array<GitHubRepository>>> callback) throws RequestException;
    
    /**
     * Get the list of the organizations, where authorized user is a member.
     * 
     * @param callback
     * @throws RequestException
     */
    public abstract void getOrganizations(@NotNull AsyncRequestCallback<List<String>> callback) throws RequestException;
    
    /**
     * Get authorized user information.
     * 
     * @param callback
     * @throws RequestException
     */
    public abstract void getUserInfo(@NotNull AsyncRequestCallback<GitHubUser> callback) throws RequestException;
}