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
package com.codenvy.ide.ext.appfog.client;

import com.codenvy.ide.ext.appfog.shared.*;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.websocket.WebSocketException;
import com.google.gwt.http.client.RequestException;

/**
 * Client service for AppFog.
 *
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 */
public interface AppfogClientService {
    /**
     * Get the list of available frameworks for AppFog.
     *
     * @param server
     *         location of AppFog instance
     * @param callback
     *         - callback, that client has to implement to receive response
     */
    void getFrameworks(String server, AsyncRequestCallback<JsonArray<Framework>> callback) throws RequestException;

    /**
     * Create application on AppFog.
     *
     * @param server
     *         location of AppFog instance where application must be created, e.g. https://api.appfog.com
     * @param name
     *         application name. This parameter is mandatory.
     * @param type
     *         the type of application (name of framework). Can be <code>null</code> (will try to detect automatically)
     * @param url
     *         the url (can be null - than will use default)
     * @param instances
     *         the number of instances of application
     * @param memory
     *         memory (in MB) allocated for application (optional). If less of equals zero then use default value which is
     *         dependents to framework type
     * @param nostart
     *         is start application after creating
     * @param vfsId
     *         current virtual file system id
     * @param projectId
     *         id of the project with the source code or compiled and packed java web application
     * @param war
     *         URL to pre-builded war file. May be present for java (spring, grails, java-web) applications ONLY
     * @param callback
     *         callback, that client has to implement to receive response
     */
    void create(String server, String name, String type, String url, int instances, int memory, boolean nostart, String vfsId,
                String projectId, String war, String infra, AppfogAsyncRequestCallback<AppfogApplication> callback) throws RequestException;

    /**
     * Create application on AppFog. Sends request over WebSocket.
     *
     * @param server
     *         location of AppFog instance where application must be created, e.g. https://api.appfog.com
     * @param name
     *         application name. This parameter is mandatory.
     * @param type
     *         the type of application (name of framework). Can be <code>null</code> (will try to detect automatically)
     * @param url
     *         the url (can be null - than will use default)
     * @param instances
     *         the number of instances of application
     * @param memory
     *         memory (in MB) allocated for application (optional). If less of equals zero then use default value which is
     *         dependents to framework type
     * @param nostart
     *         is start application after creating
     * @param vfsId
     *         current virtual file system id
     * @param projectId
     *         id of the project with the source code or compiled and packed java web application
     * @param war
     *         URL to pre-builded war file. May be present for java (spring, grails, java-web) applications ONLY
     * @param callback
     *         callback, that client has to implement to receive response
     */
    void createWS(String server, String name, String type, String url, int instances, int memory, boolean nostart, String vfsId,
                  String projectId, String war, String infra, AppfogRESTfulRequestCallback<AppfogApplication> callback)
            throws WebSocketException;

    /**
     * Log in AppFog account.
     *
     * @param server
     *         location of AppFog instance where to log in
     * @param email
     *         user's email (login)
     * @param password
     *         user's password
     * @param callback
     *         callback, that client has to implement to receive response
     */
    void login(String server, String email, String password, AsyncRequestCallback<String> callback) throws RequestException;

    /**
     * Log out AppFog account.
     *
     * @param server
     *         location of AppFog instance from which to log out
     * @param callback
     *         callback, that client has to implement to receive response
     */
    void logout(String server, AsyncRequestCallback<String> callback) throws RequestException;

    /**
     * Get the application's information.
     *
     * @param vfsId
     *         current virtual file system id
     * @param projectId
     *         id of the project with the source code or compiled and packed java web application
     * @param appId
     *         application's id
     * @param server
     *         location of AppFog instance, where application is located
     * @param callback
     *         callback, that client has to implement
     */
    void getApplicationInfo(String vfsId, String projectId, String appId, String server,
                            AppfogAsyncRequestCallback<AppfogApplication> callback) throws RequestException;

    /**
     * Delete application from AppFog.
     *
     * @param vfsId
     *         current virtual file system id
     * @param projectId
     *         id of the project with the source code or compiled and packed java web application
     * @param appId
     *         application's id
     * @param server
     *         location of AppFog instance, where application is located
     * @param deleteServices
     *         if <code>true</code> - delete application's services
     * @param callback
     *         - callback, that client has to implement
     */
    void deleteApplication(String vfsId, String projectId, String appId, String server, boolean deleteServices,
                           AppfogAsyncRequestCallback<String> callback) throws RequestException;

    /**
     * Start application.
     *
     * @param vfsId
     *         current virtual file system id
     * @param projectId
     *         id of the project with the source code or compiled and packed java web application
     * @param name
     *         - application name
     * @param server
     *         location of AppFog instance, where application is located
     * @param callback
     *         callback, that client has to implement to receive response from server.
     */
    void startApplication(String vfsId, String projectId, String name, String server,
                          AppfogAsyncRequestCallback<AppfogApplication> callback) throws RequestException;

    /**
     * Stop application.
     *
     * @param vfsId
     *         current virtual file system id
     * @param projectId
     *         id of the project with the source code or compiled and packed java web application
     * @param name
     *         - application name
     * @param server
     *         location of AppFog instance, where application is located
     * @param callback
     *         callback, that client has to implement to receive response from server.
     */
    void stopApplication(String vfsId, String projectId, String name, String server, AppfogAsyncRequestCallback<String> callback)
            throws RequestException;

    /**
     * Restart application.
     *
     * @param vfsId
     *         current virtual file system id
     * @param projectId
     *         id of the project with the source code or compiled and packed java web application
     * @param name
     *         application's name
     * @param server
     *         location of AppFog instance, where application is located
     * @param callback
     *         callback, that client has to implement to receive response from server.
     */
    void restartApplication(String vfsId, String projectId, String name, String server,
                            AppfogAsyncRequestCallback<AppfogApplication> callback) throws RequestException;

    /**
     * Update existing application.
     *
     * @param vfsId
     *         current virtual file system id
     * @param projectId
     *         id of the project with the source code or compiled and packed java web application
     * @param name
     *         application's name
     * @param server
     *         location of AppFog instance, where application is located
     * @param war
     *         location of war file (Java applications only)
     * @param callback
     *         callback, that client has to implement to handle response from server
     */
    void updateApplication(String vfsId, String projectId, String name, String server, String war,
                           AppfogAsyncRequestCallback<String> callback) throws RequestException;

    /**
     * Rename existing application.
     *
     * @param vfsId
     *         current virtual file system id
     * @param projectId
     *         id of the project with the source code or compiled and packed java web application
     * @param name
     *         application's name
     * @param server
     *         location of AppFog instance, where application is located
     * @param newName
     *         new application's name
     * @param callback
     *         callback, that client has to implement to handle response from server
     * @throws RequestException
     */
    void renameApplication(String vfsId, String projectId, String name, String server, String newName,
                           AppfogAsyncRequestCallback<String> callback) throws RequestException;

    /**
     * Map new URL of the application.
     *
     * @param vfsId
     *         current virtual file system id
     * @param projectId
     *         id of the project with the source code or compiled and packed java web application
     * @param name
     *         application's name
     * @param server
     *         location of AppFog instance, where application is located
     * @param url
     *         URL to map
     * @param callback
     *         callback, that client has to implement to handle response from server.
     */
    void mapUrl(String vfsId, String projectId, String name, String server, String url, AppfogAsyncRequestCallback<String> callback)
            throws RequestException;

    /**
     * Unmap URL from the application.
     *
     * @param vfsId
     *         current virtual file system id
     * @param projectId
     *         id of the project with the source code or compiled and packed java web application
     * @param name
     *         application's name
     * @param server
     *         location of AppFog instance, where application is located
     * @param url
     *         URL to unmap
     * @param callback
     *         callback, that client has to implement to handle response from server
     */
    void unmapUrl(String vfsId, String projectId, String name, String server, String url, AppfogAsyncRequestCallback<Object> callback)
            throws RequestException;

    /**
     * Update the memory size.
     *
     * @param vfsId
     *         current virtual file system id
     * @param projectId
     *         id of the project with the source code or compiled and packed java web application
     * @param name
     *         application's name
     * @param server
     *         location of AppFog instance, where to update memoryw
     * @param mem
     *         mememory size
     * @param callback
     *         callback, that client has to implement to handle response from server
     */
    void updateMemory(String vfsId, String projectId, String name, String server, int mem, AppfogAsyncRequestCallback<String> callback)
            throws RequestException;

    /**
     * Update the number of instances for the application.
     *
     * @param vfsId
     *         current virtual file system id
     * @param projectId
     *         id of the project with the source code or compiled and packed java web application
     * @param name
     *         application's name
     * @param server
     *         location of AppFog instance, where to update instances
     * @param expression
     *         expression for instances updating
     * @param callback
     *         callback, that client has to implement to handle response from server
     */
    void updateInstances(String vfsId, String projectId, String name, String server, String expression,
                         AppfogAsyncRequestCallback<String> callback) throws RequestException;

    /**
     * Validate action before building project.
     *
     * @param action
     *         he name of action (create, update)
     * @param server
     *         location of AppFog instance, where to validate action, e.g. https://api.appfog.com
     * @param appName
     *         the name of application (if create - than required, if update - <code>null</code>)
     * @param framework
     *         he name of application framework (can be <code>null</code>)
     * @param url
     *         application's URL
     * @param vfsId
     *         current virtual file system id
     * @param projectId
     *         id of the project with the source code or compiled and packed java web application
     * @param instances
     *         number of instances
     * @param memory
     *         memory size
     * @param nostart
     * @param callback
     *         callback, that client has to implement to handle response from server.
     */
    void validateAction(String action, String server, String appName, String framework, String url, String vfsId, String projectId,
                        int instances, int memory, boolean nostart, AppfogAsyncRequestCallback<String> callback) throws RequestException;

    /**
     * Get list of deployed applications.
     *
     * @param server
     *         location of AppFog instance, where applications are located
     * @param callback
     *         callback, that client has to implement to handle response from server.
     */
    void getApplicationList(String server, AppfogAsyncRequestCallback<JsonArray<AppfogApplication>> callback) throws RequestException;

    /**
     * Get AppFog system information.
     *
     * @param server
     *         location of AppFog instance
     * @param callback
     *         callback, that client has to implement to handle response from server
     */
    void getSystemInfo(String server, AsyncRequestCallback<SystemInfo> callback) throws RequestException;

    /**
     * Get the list of available targets for user.
     *
     * @param callback
     *         callback, that client has to implement to handle response from server
     */
    void getTargets(AsyncRequestCallback<JsonArray<String>> callback) throws RequestException;

    /**
     * Get target.
     *
     * @param callback
     *         callback, that client has to implement to handle response from server
     */
    void getTarget(AsyncRequestCallback<StringBuilder> callback) throws RequestException;

    /**
     * Getting logs for AppFog Application
     *
     * @param vfsId
     *         current virtual file system id
     * @param projectId
     *         id of the project with the source code or compiled and packed java web application
     * @param callback
     *         callback, that client has to implement to handle response from server
     * @throws RequestException
     */
    void getLogs(String vfsId, String projectId, AsyncRequestCallback<String> callback) throws RequestException;

    /**
     * Get the list of services available and provisioned.
     *
     * @param server
     *         server's name (may be <code>null</code>)
     * @param callback
     * @throws RequestException
     */
    void services(String server, AsyncRequestCallback<AppfogServices> callback) throws RequestException;

    /**
     * Create new provisioned service.
     *
     * @param server
     *         location of AppFog instance
     * @param type
     *         service's type
     * @param name
     *         service's name
     * @param application
     *         application's name
     * @param vfsId
     *         virtual file system id
     * @param projectId
     *         id of the project with the source code or compiled and packed java web application
     * @param callback
     *         callback, that client has to implement to handle response from server
     * @throws RequestException
     */
    void createService(String server, String type, String name, String application, String vfsId, String projectId, String infra,
                       AppfogAsyncRequestCallback<AppfogProvisionedService> callback) throws RequestException;

    /**
     * Delete provisioned service.
     *
     * @param server
     *         location of AppFog instance
     * @param name
     *         service's name
     * @param callback
     *         callback, that client has to implement to handle response from server
     * @throws RequestException
     */
    void deleteService(String server, String name, AppfogAsyncRequestCallback<Object> callback) throws RequestException;

    /**
     * Bind service to application.
     *
     * @param server
     *         location of AppFog instance
     * @param name
     *         service's name
     * @param application
     *         application's name
     * @param vfsId
     *         virtual file system id
     * @param projectId
     *         id of the project with the source code or compiled and packed java web application
     * @param callback
     *         callback, that client has to implement to handle response from server
     * @throws RequestException
     */
    void bindService(String server, String name, String application, String vfsId, String projectId,
                     AppfogAsyncRequestCallback<Object> callback) throws RequestException;

    /**
     * Unbind service from application.
     *
     * @param server
     *         location of AppFog instance
     * @param name
     *         service's name
     * @param application
     *         application's name
     * @param vfsId
     *         virtual file system id
     * @param projectId
     *         id of the project with the source code or compiled and packed java web application
     * @param callback
     *         callback, that client has to implement to handle response from server
     * @throws RequestException
     */
    void unbindService(String server, String name, String application, String vfsId, String projectId,
                       AppfogAsyncRequestCallback<Object> callback) throws RequestException;

    /**
     * Get the list of infrastructure.
     *
     * @param server
     *         location of AppFog instance
     * @param vfsId
     *         virtual file system id
     * @param projectId
     *         id of the project with the source code or compiled and packed java web application
     * @param callback
     *         callback, that client has to implement to handle response from server
     * @throws RequestException
     */
    void infras(String server, String vfsId, String projectId, AsyncRequestCallback<JsonArray<InfraDetail>> callback)
            throws RequestException;
}