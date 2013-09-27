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
package org.exoplatform.ide.extension.cloudfoundry.client;

import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.client.framework.websocket.WebSocketException;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryExtension.PAAS_PROVIDER;
import org.exoplatform.ide.extension.cloudfoundry.shared.CloudFoundryApplication;
import org.exoplatform.ide.extension.cloudfoundry.shared.CloudfoundryServices;
import org.exoplatform.ide.extension.cloudfoundry.shared.Framework;
import org.exoplatform.ide.extension.cloudfoundry.shared.ProvisionedService;
import org.exoplatform.ide.extension.cloudfoundry.shared.SystemInfo;

import java.util.List;

/**
 * Client service for CloudFoundry.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: CloudFoundryClientService.java Jul 12, 2011 10:24:53 AM vereshchaka $
 */
public abstract class CloudFoundryClientService {

    private static CloudFoundryClientService instance;

    public static CloudFoundryClientService getInstance() {
        return instance;
    }

    protected CloudFoundryClientService() {
        instance = this;
    }

    /**
     * Get the list of available frameworks for CloudFoundry.
     * 
     * @param paasProvider CloudFoundry provider like CloudFoundry, Tier3 Web Fabric, etc.
     * @param callback - callback, that client has to implement to receive response
     */
    public abstract void getFrameworks(String server, PAAS_PROVIDER paasProvider, AsyncRequestCallback<List<Framework>> callback)
                                                                                                     throws RequestException;

    /**
     * Create application on CloudFoundry.
     * 
     * @param server location of Cloud Foundry instance where application must be created, e.g. http://api.cloudfoundry.com
     * @param name application name. This parameter is mandatory.
     * @param type the type of application (name of framework). Can be <code>null</code> (will try to detect automatically)
     * @param url the url (can be null - than will use default)
     * @param instances the number of instances of application
     * @param memory memory (in MB) allocated for application (optional). If less of equals zero then use default value which is dependents
     *            to framework type
     * @param nostart is start application after creating
     * @param vfsId current virtual file system id
     * @param projectId id of the project with the source code or compiled and packed java web application
     * @param war URL to pre-builded war file. May be present for java (spring, grails, java-web) applications ONLY
     * @param paasProvider CloudFoundry provider like CloudFoundry, Tier3 Web Fabric, etc.
     * @param callback callback, that client has to implement to receive response
     */
    public abstract void create(String server, String name, String type, String url, int instances, int memory,
                                boolean nostart, String vfsId, String projectId, String war, PAAS_PROVIDER paasProvider,
                                CloudFoundryAsyncRequestCallback<CloudFoundryApplication> callback) throws RequestException;

    /**
     * Create application on CloudFoundry. Sends request over WebSocket.
     * 
     * @param server location of Cloud Foundry instance where application must be created, e.g. http://api.cloudfoundry.com
     * @param name application name. This parameter is mandatory.
     * @param type the type of application (name of framework). Can be <code>null</code> (will try to detect automatically)
     * @param url the url (can be null - than will use default)
     * @param instances the number of instances of application
     * @param memory memory (in MB) allocated for application (optional). If less of equals zero then use default value which is dependents
     *            to framework type
     * @param nostart is start application after creating
     * @param vfsId current virtual file system id
     * @param projectId id of the project with the source code or compiled and packed java web application
     * @param war URL to pre-builded war file. May be present for java (spring, grails, java-web) applications ONLY
     * @param paasProvider CloudFoundry provider like CloudFoundry, Tier3 Web Fabric, etc.
     * @param callback callback, that client has to implement to receive response
     */
    public abstract void createWS(String server, String name, String type, String url, int instances, int memory,
                                  boolean nostart, String vfsId, String projectId, String war, PAAS_PROVIDER paasProvider,
                                  CloudFoundryRESTfulRequestCallback<CloudFoundryApplication> callback) throws WebSocketException;

    /**
     * Log in CloudFoundry account.
     * 
     * @param server location of Cloud Foundry instance where to log in
     * @param email user's email (login)
     * @param password user's password
     * @param paasProvider CloudFoundry provider like CloudFoundry, Tier3 Web Fabric, etc.
     * @param callback callback, that client has to implement to receive response
     */
    public abstract void login(String server,
                               String email,
                               String password,
                               PAAS_PROVIDER paasProvider,
                               AsyncRequestCallback<String> callback)
                                                                     throws RequestException;

    /**
     * Log out CloudFoundry account.
     * 
     * @param server location of Cloud Foundry instance from which to log out
     * @param paasProvider CloudFoundry provider like CloudFoundry, Tier3 Web Fabric, etc.
     * @param callback callback, that client has to implement to receive response
     */
    public abstract void logout(String server, PAAS_PROVIDER paasProvider, AsyncRequestCallback<String> callback) throws RequestException;

    /**
     * Get the application's information.
     * 
     * @param vfsId current virtual file system id
     * @param projectId id of the project with the source code or compiled and packed java web application
     * @param appId application's id
     * @param server location of Cloud Foundry instance, where application is located
     * @param callback callback, that client has to implement
     */
    public abstract void getApplicationInfo(String vfsId, String projectId, String appId, String server,
                                            CloudFoundryAsyncRequestCallback<CloudFoundryApplication> callback) throws RequestException;

    /**
     * Delete application from CloudFoundry.
     * 
     * @param vfsId current virtual file system id
     * @param projectId id of the project with the source code or compiled and packed java web application
     * @param appId application's id
     * @param server location of Cloud Foundry instance, where application is located
     * @param paasProvider CloudFoundry provider like CloudFoundry, Tier3 Web Fabric, etc.
     * @param deleteServices if <code>true</code> - delete application's services
     * @param callback - callback, that client has to implement
     */
    public abstract void deleteApplication(String vfsId,
                                           String projectId,
                                           String appId,
                                           String server,
                                           PAAS_PROVIDER paasProvider,
                                           boolean deleteServices,
                                           CloudFoundryAsyncRequestCallback<String> callback)
                                                                                             throws RequestException;

    /**
     * Start application.
     * 
     * @param vfsId current virtual file system id
     * @param projectId id of the project with the source code or compiled and packed java web application
     * @param name - application name
     * @param server location of Cloud Foundry instance, where application is located
     * @param paasProvider CloudFoundry provider like CloudFoundry, Tier3 Web Fabric, etc.
     * @param callback callback, that client has to implement to receive response from server.
     */
    public abstract void startApplication(String vfsId, String projectId, String name, String server, PAAS_PROVIDER paasProvider,
                                          CloudFoundryAsyncRequestCallback<CloudFoundryApplication> callback) throws RequestException;

    /**
     * Stop application.
     * 
     * @param vfsId current virtual file system id
     * @param projectId id of the project with the source code or compiled and packed java web application
     * @param name - application name
     * @param server location of Cloud Foundry instance, where application is located
     * @param paasProvider CloudFoundry provider like CloudFoundry, Tier3 Web Fabric, etc.
     * @param callback callback, that client has to implement to receive response from server.
     */
    public abstract void stopApplication(String vfsId, String projectId, String name, String server, PAAS_PROVIDER paasProvider,
                                         CloudFoundryAsyncRequestCallback<String> callback) throws RequestException;

    /**
     * Restart application.
     * 
     * @param vfsId current virtual file system id
     * @param projectId id of the project with the source code or compiled and packed java web application
     * @param name application's name
     * @param server location of Cloud Foundry instance, where application is located
     * @param paasProvider CloudFoundry provider like CloudFoundry, Tier3 Web Fabric, etc.
     * @param callback callback, that client has to implement to receive response from server.
     */
    public abstract void restartApplication(String vfsId, String projectId, String name, String server, PAAS_PROVIDER paasProvider,
                                            CloudFoundryAsyncRequestCallback<CloudFoundryApplication> callback) throws RequestException;

    /**
     * Update existing application.
     * 
     * @param vfsId current virtual file system id
     * @param projectId id of the project with the source code or compiled and packed java web application
     * @param name application's name
     * @param server location of Cloud Foundry instance, where application is located
     * @param war location of war file (Java applications only)
     * @param callback callback, that client has to implement to handle response from server
     */
    public abstract void updateApplication(String vfsId,
                                           String projectId,
                                           String name,
                                           String server,
                                           String war,
                                           CloudFoundryAsyncRequestCallback<String> callback) throws RequestException;

    public abstract void renameApplication(String vfsId, String projectId, String name, String server, String newName,
                                           CloudFoundryAsyncRequestCallback<String> callback) throws RequestException;

    /**
     * Map new URL of the application.
     * 
     * @param vfsId current virtual file system id
     * @param projectId id of the project with the source code or compiled and packed java web application
     * @param name application's name
     * @param server location of Cloud Foundry instance, where application is located
     * @param url URL to map
     * @param callback callback, that client has to implement to handle response from server.
     */
    public abstract void mapUrl(String vfsId, String projectId, String name, String server, String url,
                                CloudFoundryAsyncRequestCallback<String> callback) throws RequestException;

    /**
     * Unmap URL from the application.
     * 
     * @param vfsId current virtual file system id
     * @param projectId id of the project with the source code or compiled and packed java web application
     * @param name application's name
     * @param server location of Cloud Foundry instance, where application is located
     * @param url URL to unmap
     * @param callback callback, that client has to implement to handle response from server
     */
    public abstract void unmapUrl(String vfsId, String projectId, String name, String server, String url,
                                  CloudFoundryAsyncRequestCallback<Object> callback) throws RequestException;

    /**
     * Update the memory size.
     * 
     * @param vfsId current virtual file system id
     * @param projectId id of the project with the source code or compiled and packed java web application
     * @param name application's name
     * @param server location of Cloud Foundry instance, where to update memoryw
     * @param mem mememory size
     * @param callback callback, that client has to implement to handle response from server
     */
    public abstract void updateMemory(String vfsId, String projectId, String name, String server, int mem,
                                      CloudFoundryAsyncRequestCallback<String> callback) throws RequestException;

    /**
     * Update the number of instances for the application.
     * 
     * @param vfsId current virtual file system id
     * @param projectId id of the project with the source code or compiled and packed java web application
     * @param name application's name
     * @param server location of Cloud Foundry instance, where to update instances
     * @param expression expression for instances updating
     * @param callback callback, that client has to implement to handle response from server
     */
    public abstract void updateInstances(String vfsId,
                                         String projectId,
                                         String name,
                                         String server,
                                         String expression,
                                         CloudFoundryAsyncRequestCallback<String> callback) throws RequestException;

    /**
     * Validate action before building project.
     * 
     * @param action he name of action (create, update)
     * @param server location of Cloud Foundry instance, where to validate action, e.g. http://api.cloudfoundry.com
     * @param appName the name of application (if create - than required, if update - <code>null</code>)
     * @param framework he name of application framework (can be <code>null</code>)
     * @param url application's URL
     * @param vfsId current virtual file system id
     * @param projectId id of the project with the source code or compiled and packed java web application
     * @param paasProvider CloudFoundry provider like CloudFoundry, Tier3 Web Fabric, etc.
     * @param instances number of instances
     * @param memory memory size
     * @param nostart
     * @param callback callback, that client has to implement to handle response from server.
     */
    public abstract void validateAction(String action,
                                        String server,
                                        String appName,
                                        String framework,
                                        String url,
                                        String vfsId,
                                        String projectId,
                                        PAAS_PROVIDER paasProvider,
                                        int instances,
                                        int memory,
                                        boolean nostart,
                                        CloudFoundryAsyncRequestCallback<String> callback) throws RequestException;

    /**
     * Get list of deployed applications.
     * 
     * @param server location of Cloud Foundry instance, where applications are located
     * @param paasProvider CloudFoundry provider like CloudFoundry, Tier3 Web Fabric, etc.
     * @param callback callback, that client has to implement to handle response from server.
     */
    public abstract void getApplicationList(String server, PAAS_PROVIDER paasProvider,
                                            CloudFoundryAsyncRequestCallback<List<CloudFoundryApplication>> callback)
                                                                                                                     throws RequestException;

    /**
     * Get Cloud Foundry system information.
     * 
     * @param server location of Cloud Foundry instance
     * @param paasProvider CloudFoundry provider like CloudFoundry, Tier3 Web Fabric, etc.
     * @param callback callback, that client has to implement to handle response from server
     */
    public abstract void getSystemInfo(String server, PAAS_PROVIDER paasProvider, AsyncRequestCallback<SystemInfo> callback) throws RequestException;

    /**
     * Get the list of available targets for user.
     * 
     * @param paasProvider CloudFoundry provider like CloudFoundry, Tier3 Web Fabric, etc.
     * @param callback callback, that client has to implement to handle response from server
     */
    public abstract void getTargets(PAAS_PROVIDER paasProvider, AsyncRequestCallback<List<String>> callback) throws RequestException;

    /**
     * @param paasProvider CloudFoundry provider like CloudFoundry, Tier3 Web Fabric, etc.
     * @param callback callback, that client has to implement to handle response from server
     */
    public abstract void getTarget(PAAS_PROVIDER paasProvider, AsyncRequestCallback<StringBuilder> callback) throws RequestException;

    /**
     * Getting logs for CloudFoundry Application
     * 
     * @param vfsId
     * @param projectId
     * @param callback
     * @throws RequestException
     */
    public abstract void getLogs(String vfsId, String projectId, AsyncRequestCallback<StringBuilder> callback)
                                                                                                              throws RequestException;

    /**
     * Get the list of services available and provisioned.
     * 
     * @param server server's name (may be <code>null</code>)
     * @param paasProvider CloudFoundry provider like CloudFoundry, Tier3 Web Fabric, etc.
     * @param callback
     * @throws RequestException
     */
    public abstract void services(String server, PAAS_PROVIDER paasProvider, AsyncRequestCallback<CloudfoundryServices> callback)
                                                                                                     throws RequestException;

    /**
     * Create new provisioned service.
     * 
     * @param server server's name
     * @param type service's type
     * @param name service's name
     * @param application application's name
     * @param vfsId virtual file system id
     * @param projectId project's id
     * @param callback callback
     * @throws RequestException
     */
    public abstract void createService(String server,
                                       String type,
                                       String name,
                                       String application,
                                       String vfsId,
                                       String projectId,
                                       CloudFoundryAsyncRequestCallback<ProvisionedService> callback)
                                                                                                     throws RequestException;

    /**
     * Delete provisioned service.
     * 
     * @param server server's name
     * @param name service's name
     * @param paasProvider CloudFoundry provider like CloudFoundry, Tier3 Web Fabric, etc.
     * @param callback callback
     * @throws RequestException
     */
    public abstract void deleteService(String server, String name, PAAS_PROVIDER paasProvider, CloudFoundryAsyncRequestCallback<Object> callback)
                                                                                                                     throws RequestException;

    /**
     * Bind service to application.
     * 
     * @param server server's name
     * @param name service's name
     * @param application application's name
     * @param vfsId virtual file system id
     * @param projectId project's id
     * @param callback callback
     * @throws RequestException
     */
    public abstract void bindService(String server,
                                     String name,
                                     String application,
                                     String vfsId,
                                     String projetcId,
                                     CloudFoundryAsyncRequestCallback<Object> callback) throws RequestException;

    /**
     * Unbind service from application.
     * 
     * @param server server's name
     * @param name service's name
     * @param application application's name
     * @param vfsId virtual file system id
     * @param projectId project's id
     * @param callback callback
     * @throws RequestException
     */
    public abstract void unbindService(String server,
                                       String name,
                                       String application,
                                       String vfsId,
                                       String projetcId,
                                       CloudFoundryAsyncRequestCallback<Object> callback) throws RequestException;
}
