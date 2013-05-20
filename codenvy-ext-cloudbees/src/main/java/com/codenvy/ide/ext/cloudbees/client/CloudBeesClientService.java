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
package com.codenvy.ide.ext.cloudbees.client;

import com.codenvy.ide.ext.cloudbees.shared.ApplicationInfo;
import com.codenvy.ide.ext.cloudbees.shared.CloudBeesAccount;
import com.codenvy.ide.ext.cloudbees.shared.CloudBeesUser;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.websocket.WebSocketException;
import com.google.gwt.http.client.RequestException;

import java.util.List;
import java.util.Map;

/**
 * Client service for CloudBees.
 *
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: CloudBessService.java Jun 23, 2011 10:11:13 AM vereshchaka $
 */
public interface CloudBeesClientService {
    /**
     * Initialize application.
     *
     * @param appId
     *         application's id
     * @param vfsId
     *         virtual file system's id
     * @param projectId
     *         project's id
     * @param warFile
     *         location of the build war with application
     * @param message
     *         initialization message
     * @param callback
     *         callback
     */
    void initializeApplication(String appId, String vfsId, String projectId, String warFile, String message,
                               CloudBeesAsyncRequestCallback<ApplicationInfo> callback) throws RequestException;

    /**
     * Initialize application by sending request over WebSocket.
     *
     * @param appId
     *         application's id
     * @param vfsId
     *         virtual file system's id
     * @param projectId
     *         project's id
     * @param warFile
     *         location of the build war with application
     * @param message
     *         initialization message
     * @param callback
     *         callback
     */
    void initializeApplicationWS(String appId, String vfsId, String projectId, String warFile, String message,
                                 CloudBeesRESTfulRequestCallback<ApplicationInfo> callback) throws WebSocketException;

    /**
     * Get the available domains.
     *
     * @param callback
     *         - callback that client has to implement
     */
    void getDomains(CloudBeesAsyncRequestCallback<List<String>> callback) throws RequestException;

    /**
     * Login CloudBees.
     *
     * @param email
     *         user's email (login)
     * @param password
     *         user's password
     * @param callback
     *         callback
     */
    void login(String email, String password, AsyncRequestCallback<String> callback) throws RequestException;

    /**
     * Logout CloudBees.
     *
     * @param callback
     *         callback
     */
    void logout(AsyncRequestCallback<String> callback) throws RequestException;

    /**
     * Get the application info.
     *
     * @param appId
     *         application's id
     * @param vfsId
     *         virtual file system's id
     * @param projectId
     *         project's id
     * @param callback
     *         callback
     */
    void getApplicationInfo(String appId, String vfsId, String projectId, CloudBeesAsyncRequestCallback<ApplicationInfo> callback)
            throws RequestException;

    /**
     * Delete application from CloudBees.
     *
     * @param appId
     *         application's id
     * @param vfsId
     *         virtual file system's id
     * @param projectId
     *         project's id
     * @param callback
     *         callback
     */
    void deleteApplication(String appId, String vfsId, String projectId, CloudBeesAsyncRequestCallback<String> callback)
            throws RequestException;

    /**
     * Update application on CloudBees.
     *
     * @param appId
     *         application's id
     * @param vfsId
     *         virtual file system's id
     * @param projectId
     *         project's id
     * @param warFile
     *         location of the build war with application
     * @param message
     *         initialization message
     * @param callback
     *         callback
     */
    void updateApplication(String appId, String vfsId, String projectId, String warFile, String message,
                           CloudBeesAsyncRequestCallback<ApplicationInfo> callback) throws RequestException;

    /**
     * Deploy war with the application.
     *
     * @param appId
     *         application's id
     * @param warFile
     *         deploy built war with the application
     * @param message
     *         message for deploying war
     * @param callback
     *         callback
     */
    void deployWar(String appId, String warFile, String message, CloudBeesAsyncRequestCallback<Map<String, String>> callback)
            throws RequestException;

    /** Receive all CB applications for this account. */
    void applicationList(CloudBeesAsyncRequestCallback<List<ApplicationInfo>> callback) throws RequestException;

    /**
     * Create new CloudBees account/domain.
     *
     * @param account
     *         CloudBees account
     * @param callback
     *         callback
     * @throws RequestException
     */
    void createAccount(CloudBeesAccount account, AsyncRequestCallback<CloudBeesAccount> callback) throws RequestException;

    /**
     * Adds user to CloudBees account.
     *
     * @param account
     *         account's name
     * @param user
     *         user's data
     * @param isExisting
     *         is user exists or create new one
     * @param callback
     * @throws RequestException
     */
    void addUserToAccount(String account, CloudBeesUser user, boolean isExisting, AsyncRequestCallback<CloudBeesUser> callback)
            throws RequestException;
}