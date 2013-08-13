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
package com.codenvy.ide.ext.openshift.client;

import com.codenvy.ide.ext.openshift.shared.AppInfo;
import com.codenvy.ide.ext.openshift.shared.RHUserInfo;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.websocket.WebSocketException;
import com.codenvy.ide.websocket.rest.RequestCallback;
import com.google.gwt.http.client.RequestException;

import java.util.List;

/**
 * OpenShift client service.
 *
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public interface OpenShiftClientService {
    /**
     * Performs log in OpenShift.
     *
     * @param login
     *         user's email
     * @param password
     *         user's password
     * @param callback
     *         callback
     */
    public void login(String login, String password, AsyncRequestCallback<String> callback) throws RequestException;

    /**
     * Get user's information. You can view the Framework Type, Creation Date, GitURL and PublicURL details for each application.
     *
     * @param appsInfo
     *         if <code>true</code>, then list applications
     * @param callback
     *         callback
     */
    public void getUserInfo(boolean appsInfo, AsyncRequestCallback<RHUserInfo> callback) throws RequestException;

    /**
     * Creates new domain name. A domain name is a requirement for each new application that you create in the cloud and is part of
     * the application name.
     *
     * @param name
     *         domain's name
     * @param alter
     *         alter namespace (will change urls) and/or ssh key
     * @param callback
     *         callback
     */
    public void createDomain(String name, boolean alter, AsyncRequestCallback<String> callback) throws RequestException;

    /**
     * Creates OpenShift application.
     *
     * @param name
     *         application's name
     * @param vfsId
     *         virtual file system's id
     * @param projectId
     *         project's id
     * @param type
     *         application's type
     * @param callback
     *         callback
     */
    public void createApplication(String name, String vfsId, String projectId, String type, boolean scale,
                                  AsyncRequestCallback<AppInfo> callback) throws RequestException;

    /**
     * Creates OpenShift application by sending request over WebSocket.
     *
     * @param name
     *         application's name
     * @param vfsId
     *         virtual file system's id
     * @param projectId
     *         project's id
     * @param type
     *         application's type
     * @param callback
     *         callback
     */
    public void createApplicationWS(String name, String vfsId, String projectId, String type, boolean scale,
                                    RequestCallback<AppInfo> callback) throws WebSocketException;

    /**
     * Destroys application with pointed name from OpenShift.
     *
     * @param name
     *         application's name
     * @param vfsId
     *         virtual file system's id
     * @param projectId
     *         project's id
     * @param callback
     *         callback
     */
    public void destroyApplication(String name, String vfsId, String projectId, AsyncRequestCallback<String> callback)
            throws RequestException;

    /**
     * Get application's information.
     *
     * @param applicationName
     *         application name
     * @param vfsId
     *         virtual file system's id
     * @param projectId
     *         project's id
     * @param callback
     *         callback
     */
    public void getApplicationInfo(String applicationName, String vfsId, String projectId,
                                   AsyncRequestCallback<AppInfo> callback) throws RequestException;

    /**
     * Get types of allowed applications.
     *
     * @param callback
     *         callback
     */
    public void getApplicationTypes(AsyncRequestCallback<List<String>> callback) throws RequestException;

    /**
     * Start application.
     *
     * @param appName
     *         application name
     * @param callback
     *         callback
     * @throws RequestException
     */
    public void startApplication(String appName, AsyncRequestCallback<Void> callback) throws RequestException;

    /**
     * Stop application.
     *
     * @param appName
     *         application name
     * @param callback
     *         callback
     * @throws RequestException
     */
    public void stopApplication(String appName, AsyncRequestCallback<Void> callback) throws RequestException;

    /**
     * Restart application.
     *
     * @param appName
     *         application name
     * @param callback
     *         callback
     * @throws RequestException
     */
    public void restartApplication(String appName, AsyncRequestCallback<Void> callback) throws RequestException;

    /**
     * Get application state STARTED/STOPPED.
     *
     * @param appName
     *         application name
     * @param callback
     *         callback
     * @throws RequestException
     */
    public void getApplicationHealth(String appName, AsyncRequestCallback<StringBuilder> callback) throws RequestException;

    /**
     * Get available embedded cartridges
     *
     * @param callback
     *         callback
     * @throws RequestException
     */
    public void getCartridges(AsyncRequestCallback<List<String>> callback) throws RequestException;

    /**
     * Add cartridge to application
     *
     * @param appName
     *         application name
     * @param cartridgeName
     *         cartridge name
     * @param callback
     *         callback
     * @throws RequestException
     */
    public void addCartridge(String appName, String cartridgeName, AsyncRequestCallback<Void> callback) throws RequestException;

    /**
     * Delete cartridge from application
     *
     * @param appName
     *         application name
     * @param cartridgeName
     *         cartridge name
     * @param callback
     *         callback
     * @throws RequestException
     */
    public void deleteCartridge(String appName, String cartridgeName, AsyncRequestCallback<Void> callback) throws RequestException;

    /**
     * Start cartridge
     *
     * @param appName
     *         application name
     * @param cartridgeName
     *         cartridgeName
     * @param callback
     *         callback
     * @throws RequestException
     */
    public void startCartridge(String appName, String cartridgeName, AsyncRequestCallback<Void> callback) throws RequestException;

    /**
     * Stop cartridge
     *
     * @param appName
     *         application name
     * @param cartridgeName
     *         cartridgeName
     * @param callback
     *         callback
     * @throws RequestException
     */
    public void stopCartridge(String appName, String cartridgeName, AsyncRequestCallback<Void> callback) throws RequestException;

    /**
     * Restart cartridge
     *
     * @param appName
     *         application name
     * @param cartridgeName
     *         cartridgeName
     * @param callback
     *         callback
     * @throws RequestException
     */
    public void restartCartridge(String appName, String cartridgeName, AsyncRequestCallback<Void> callback) throws RequestException;

    /**
     * Reload cartridge
     *
     * @param appName
     *         application name
     * @param cartridgeName
     *         cartridgeName
     * @param callback
     *         callback
     * @throws RequestException
     */
    public void reloadCartridge(String appName, String cartridgeName, AsyncRequestCallback<Void> callback) throws RequestException;

    /**
     * Destroy all applications and namespace if need
     *
     * @param callback
     * @throws RequestException
     */
    public void destroyAllApplications(boolean alsoNamespace, String vfsId, String projectId, AsyncRequestCallback<Void> callback)
            throws RequestException;
}
