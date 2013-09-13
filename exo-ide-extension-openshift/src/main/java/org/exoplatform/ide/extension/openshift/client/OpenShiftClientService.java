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
package org.exoplatform.ide.extension.openshift.client;

import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.client.framework.websocket.WebSocketException;
import org.exoplatform.ide.client.framework.websocket.rest.RequestCallback;
import org.exoplatform.ide.extension.openshift.shared.AppInfo;
import org.exoplatform.ide.extension.openshift.shared.RHUserInfo;

import java.util.List;
import java.util.Set;

/**
 * OpenShift client service.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Jun 6, 2011 5:49:43 PM anya $
 */
public abstract class OpenShiftClientService {
    /** OpenShift client service. */
    private static OpenShiftClientService instance;

    /** @return {@link OpenShiftClientService} OpenShiftClientService client service */
    public static OpenShiftClientService getInstance() {
        return instance;
    }

    protected OpenShiftClientService() {
        instance = this;
    }

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
    public abstract void login(String login, String password, AsyncRequestCallback<String> callback)
            throws RequestException;

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
    public abstract void createDomain(String name, boolean alter, AsyncRequestCallback<String> callback)
            throws RequestException;

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
     * @param workdir
     *         application's working directory
     * @param callback
     *         callback
     */
    public abstract void createApplication(String name, String vfsId, String projectId, String type, boolean scale,
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
     * @param workdir
     *         application's working directory
     * @param callback
     *         callback
     */
    public abstract void createApplicationWS(String name, String vfsId, String projectId, String type, boolean scale,
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
    public abstract void destroyApplication(String name, String vfsId, String projectId,
                                            AsyncRequestCallback<String> callback) throws RequestException;

    /**
     * Get user's information. You can view the Framework Type, Creation Date, GitURL and PublicURL details for each application.
     *
     * @param appsInfo
     *         if <code>true</code>, then list applications
     * @param callback
     *         callback
     */
    public abstract void getUserInfo(boolean appsInfo, AsyncRequestCallback<RHUserInfo> callback)
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
    public abstract void getApplicationInfo(String applicationName, String vfsId, String projectId,
                                            AsyncRequestCallback<AppInfo> callback) throws RequestException;

    /**
     * Get types of allowed applications.
     *
     * @param callback
     *         callback
     */
    public abstract void getApplicationTypes(AsyncRequestCallback<List<String>> callback) throws RequestException;

    /**
     * Get application state STARTED/STOPPED.
     *
     * @param appName
     *         application name
     * @param callback
     *         callback
     * @throws RequestException
     */
    public abstract void getApplicationHealth(String appName, AsyncRequestCallback<StringBuilder> callback) throws RequestException;

    /**
     * Start application.
     *
     * @param appName
     *         application name
     * @param callback
     *         calback
     * @throws RequestException
     */
    public abstract void startApplication(String appName, AsyncRequestCallback<Void> callback) throws RequestException;

    /**
     * Stop application.
     *
     * @param appName
     *         application name
     * @param callback
     *         callback
     * @throws RequestException
     */
    public abstract void stopApplication(String appName, AsyncRequestCallback<Void> callback) throws RequestException;

    /**
     * Restart application.
     *
     * @param appName
     *         application name
     * @param callback
     *         callback
     * @throws RequestException
     */
    public abstract void restartApplication(String appName, AsyncRequestCallback<Void> callback) throws RequestException;

    /**
     * Get available embedded cartridges
     *
     * @param callback
     *         callback
     * @throws RequestException
     */
    public abstract void getCartridges(AsyncRequestCallback<List<String>> callback) throws RequestException;

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
    public abstract void addCartridge(String appName, String cartridgeName, AsyncRequestCallback<Void> callback) throws RequestException;

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
    public abstract void deleteCartridge(String appName, String cartridgeName, AsyncRequestCallback<Void> callback) throws RequestException;

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
    public abstract void startCartridge(String appName, String cartridgeName, AsyncRequestCallback<Void> callback) throws RequestException;

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
    public abstract void stopCartridge(String appName, String cartridgeName, AsyncRequestCallback<Void> callback) throws RequestException;

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
    public abstract void restartCartridge(String appName, String cartridgeName, AsyncRequestCallback<Void> callback)
            throws RequestException;

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
    public abstract void reloadCartridge(String appName, String cartridgeName, AsyncRequestCallback<Void> callback) throws RequestException;

    /**
     * Destroy all applications and namespace if need
     *
     * @param callback
     * @throws RequestException
     */
    public abstract void destroyAllApplications(boolean alsoNamespace, String vfsId, String projectId, AsyncRequestCallback<Void> callback)
            throws RequestException;
}
