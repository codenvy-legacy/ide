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
package org.exoplatform.ide.extension.aws.client.beanstalk;

import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.extension.aws.client.AwsAsyncRequestCallback;
import org.exoplatform.ide.extension.aws.shared.beanstalk.*;

import java.util.List;

/**
 * @author <a href="mailto:azatsarynnyy@exoplatform.com">Artem Zatsarynnyy</a>
 * @version $Id: BeanstalkClientService.java Sep 28, 2012 3:00:04 PM azatsarynnyy $
 */
public abstract class BeanstalkClientService {
    private static BeanstalkClientService instance;

    public static BeanstalkClientService getInstance() {
        return instance;
    }

    protected BeanstalkClientService() {
        instance = this;
    }

    /**
     * Log in AWS.
     *
     * @param accessKey
     * @param secretKey
     * @param callback
     * @throws RequestException
     */
    public abstract void login(String accessKey, String secretKey, AsyncRequestCallback<Object> callback)
            throws RequestException;

    /**
     * Log out AWS.
     *
     * @param callback
     * @throws RequestException
     */
    public abstract void logout(AsyncRequestCallback<Object> callback) throws RequestException;

    /**
     * Returns available solution stacks.
     *
     * @param callback
     * @throws RequestException
     */
    public abstract void getAvailableSolutionStacks(AwsAsyncRequestCallback<List<SolutionStack>> callback)
            throws RequestException;

    /**
     * Returns configuration options of the solution stack.
     *
     * @param request
     * @param callback
     * @throws RequestException
     */
    public abstract void getSolutionStackConfigurationOptions(SolutionStackConfigurationOptionsRequest request,
                                                              AsyncRequestCallback<List<ConfigurationOptionInfo>> callback)
            throws RequestException;

    /**
     * Create application.
     *
     * @param params
     * @param callback
     * @throws RequestException
     */
    public abstract void createApplication(String vfsId, String projectId,
                                           CreateApplicationRequest createApplicationRequest,
                                           AsyncRequestCallback<ApplicationInfo> callback)
            throws RequestException;

    /**
     * Update application info.
     *
     * @param vfsId
     * @param projectId
     * @param updateApplicationRequest
     * @param callback
     * @throws RequestException
     */
    public abstract void updateApplication(String vfsId, String projectId,
                                           UpdateApplicationRequest updateApplicationRequest,
                                           AsyncRequestCallback<ApplicationInfo> callback)
            throws RequestException;

    /**
     * Returns application's information.
     *
     * @param vfsId
     * @param projectId
     * @param callback
     * @throws RequestException
     */
    public abstract void getApplicationInfo(String vfsId, String projectId,
                                            AsyncRequestCallback<ApplicationInfo> callback) throws RequestException;

    /**
     * Deletes specified application.
     *
     * @param vfsId
     * @param projectId
     * @param callback
     * @throws RequestException
     */
    public abstract void deleteApplication(String vfsId, String projectId, AsyncRequestCallback<Object> callback)
            throws RequestException;

    /**
     * Returns the list of applications.
     *
     * @param callback
     * @throws RequestException
     */
    public abstract void getApplications(AsyncRequestCallback<List<ApplicationInfo>> callback) throws RequestException;

    /**
     * Returns list of application events.
     *
     * @param vfsId
     * @param projectId
     * @param listEventsRequest
     * @param callback
     * @throws RequestException
     */
    public abstract void getApplicationEvents(String vfsId, String projectId, ListEventsRequest listEventsRequest,
                                              AsyncRequestCallback<EventsList> callback) throws RequestException;

    /**
     * Returns configuration template.
     *
     * @param vfsId
     * @param projectId
     * @param configurationRequest
     * @param callback
     * @throws RequestException
     */
    public abstract void getConfigurationTemplate(String vfsId, String projectId,
                                                  ConfigurationRequest configurationRequest, AsyncRequestCallback<Configuration> callback)
            throws RequestException;

    /**
     * Returns the list of environments.
     *
     * @param vfsId
     * @param projectId
     * @param callback
     * @throws RequestException
     */
    public abstract void getEnvironments(String vfsId, String projectId,
                                         AsyncRequestCallback<List<EnvironmentInfo>> callback) throws RequestException;

    /**
     * Returns the list of the environment configurations.
     *
     * @param vfsId
     * @param projectId
     * @param configurationRequest
     * @param callback
     * @throws RequestException
     */
    public abstract void getEnvironmentConfigurations(String vfsId, String projectId,
                                                      ConfigurationRequest configurationRequest,
                                                      AsyncRequestCallback<List<Configuration>> callback)
            throws RequestException;

    /**
     * Create new environment.
     *
     * @param vfsId
     * @param projectId
     * @param createEnvironmentRequest
     * @param callback
     * @throws RequestException
     */
    public abstract void createEnvironment(String vfsId, String projectId,
                                           CreateEnvironmentRequest createEnvironmentRequest,
                                           AwsAsyncRequestCallback<EnvironmentInfo> callback)
            throws RequestException;

    /**
     * Terminates the specified environment.
     *
     * @param environmentId
     * @param callback
     * @throws RequestException
     */
    public abstract void stopEnvironment(String environmentId, AwsAsyncRequestCallback<EnvironmentInfo> callback)
            throws RequestException;

    /**
     * Rebuild the specified environment (deletes and recreates all of the AWS resources (for example: the Auto
     * Scaling group, load balancer, etc.) for a specified environment and forces a restart).
     *
     * @param environmentId
     * @param callback
     * @throws RequestException
     */
    public abstract void rebuildEnvironment(String environmentId, AwsAsyncRequestCallback<Object> callback)
            throws RequestException;

    /**
     * Get info about specified environment.
     *
     * @param environmentId
     *         environment identifier
     * @param callback
     * @throws RequestException
     */
    public abstract void getEnvironmentInfo(String environmentId, AsyncRequestCallback<EnvironmentInfo> callback)
            throws RequestException;

    /**
     * Update specified environment.
     *
     * @param environmentId
     * @param updateEnvironmentRequest
     * @param callback
     * @throws RequestException
     */
    public abstract void updateEnvironment(String environmentId, UpdateEnvironmentRequest updateEnvironmentRequest,
                                           AsyncRequestCallback<EnvironmentInfo> callback) throws RequestException;

    /**
     * Returns the list of web servers startup logs for a specified environment.
     *
     * @param environmentId
     * @param callback
     * @throws RequestException
     */
    public abstract void getEnvironmentLogs(String environmentId, AsyncRequestCallback<List<InstanceLog>> callback)
            throws RequestException;

    /**
     * Returns the list of application versions.
     *
     * @param vfsId
     * @param projectId
     * @param callback
     * @throws RequestException
     */
    public abstract void getVersions(String vfsId, String projectId,
                                     AsyncRequestCallback<List<ApplicationVersionInfo>> callback) throws RequestException;

    /**
     * Delete the specified application's version.
     *
     * @param vfsId
     * @param projectId
     * @param applicationName
     * @param versionLabel
     * @param isDeleteS3Bundle
     * @param callback
     * @throws RequestException
     */
    public abstract void deleteVersion(String vfsId, String projectId, String applicationName, String versionLabel,
                                       boolean isDeleteS3Bundle, AsyncRequestCallback<Object> callback) throws RequestException;

    /**
     * Create new version of application.
     *
     * @param vfsId
     *         VFS identifier
     * @param projectId
     *         project identifier
     * @param createApplicationVersionRequest
     *         {@link CreateApplicationVersionRequest}
     * @param callback
     * @throws RequestException
     */
    public abstract void createVersion(String vfsId, String projectId,
                                       CreateApplicationVersionRequest createApplicationVersionRequest,
                                       AwsAsyncRequestCallback<ApplicationVersionInfo> callback) throws RequestException;

    /**
     * Restart an application server associated with the specified environment.
     *
     * @param environmentId
     * @param callback
     * @throws RequestException
     */
    public abstract void restartApplicationServer(String environmentId, AsyncRequestCallback<Object> callback)
            throws RequestException;

}