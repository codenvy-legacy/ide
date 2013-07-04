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
package org.exoplatform.ide.extension.aws.client.beanstalk;

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
import org.exoplatform.ide.extension.aws.client.AWSExtension;
import org.exoplatform.ide.extension.aws.client.AwsAsyncRequestCallback;
import org.exoplatform.ide.extension.aws.client.login.Credentials;
import org.exoplatform.ide.extension.aws.shared.beanstalk.*;

import java.util.List;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Sep 14, 2012 1:13:05 PM anya $
 */
public class BeanstalkClientServiceImpl extends BeanstalkClientService {

    private static final String BASE_URL = Utils.getWorkspaceName() + "/aws/beanstalk";

    private static final String LOGIN = BASE_URL + "/login";

    private static final String LOGOUT = BASE_URL + "/logout";

    private static final String SOLUTION_STACKS = BASE_URL + "/system/solution-stacks";

    private static final String SOLUTION_STACK_OPTIONS = BASE_URL + "/system/solution-stacks/options";

    private static final String APPLICATION_CREATE = BASE_URL + "/apps/create";

    private static final String APPLICATION_INFO = BASE_URL + "/apps/info";

    private static final String APPLICATION_DELETE = BASE_URL + "/apps/delete";

    private static final String APPLICATION_UPDATE = BASE_URL + "/apps/update";

    private static final String APPLICATIONS = BASE_URL + "/apps";

    private static final String APPLICATION_EVENTS = BASE_URL + "/apps/events";

    private static final String APPLICATION_TEMPLATE = BASE_URL + "/apps/template";

    private static final String ENVIRONMENT_CREATE = BASE_URL + "/environments/create";

    private static final String ENVIRONMENT_STOP = BASE_URL + "/environments/stop/";

    private static final String ENVIRONMENT_REBUILD = BASE_URL + "/environments/rebuild/";

    private static final String ENVIRONMENT_INFO = BASE_URL + "/environments/info/";

    private static final String ENVIRONMENT_UPDATE = BASE_URL + "/environments/update/";

    private static final String ENVIRONMENTS = BASE_URL + "/environments";

    private static final String ENVIRONMENTS_CONFIGURATION = BASE_URL + "/environments/configuration";

    private static final String ENVIRONMENT_LOGS = BASE_URL + "/environments/logs/";

    private static final String VERSIONS = BASE_URL + "/apps/versions";

    private static final String VERSION_DELETE = BASE_URL + "/apps/versions/delete";

    private static final String VERSION_CREATE = BASE_URL + "/apps/versions/create";

    private static final String SERVER_RESTART = BASE_URL + "/server/restart/";

    /** REST service context. */
    private String restServiceContext;

    /** Loader to be displayed. */
    private Loader loader;

    public BeanstalkClientServiceImpl(Loader loader) {
        this.loader = loader;
        this.restServiceContext = Utils.getRestContext();
    }

    /**
     * @see org.exoplatform.ide.extension.aws.client.beanstalk.BeanstalkClientService#login(java.lang.String, java.lang.String,
     *      org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
     */
    @Override
    public void login(String accessKey, String secretKey, AsyncRequestCallback<Object> callback) throws RequestException {
        String url = restServiceContext + LOGIN;
        Credentials credentialsBean = AWSExtension.AUTO_BEAN_FACTORY.credentials().as();
        credentialsBean.setAccess_key(accessKey);
        credentialsBean.setSecret_key(secretKey);

        String credentials = AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(credentialsBean)).getPayload();

        AsyncRequest.build(RequestBuilder.POST, url).loader(loader).data(credentials)
                    .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
    }

    /** @see org.exoplatform.ide.extension.aws.client.beanstalk.BeanstalkClientService#logout(org.exoplatform.gwtframework.commons.rest
     * .AsyncRequestCallback) */
    @Override
    public void logout(AsyncRequestCallback<Object> callback) throws RequestException {
        String url = restServiceContext + LOGOUT;

        AsyncRequest.build(RequestBuilder.POST, url).loader(loader).send(callback);
    }

    /** @see org.exoplatform.ide.extension.aws.client.beanstalk.BeanstalkClientService#getAvailableSolutionStacks(org.exoplatform
     * .gwtframework.commons.rest.AsyncRequestCallback) */
    @Override
    public void getAvailableSolutionStacks(AwsAsyncRequestCallback<List<SolutionStack>> callback)
            throws RequestException {
        String url = restServiceContext + SOLUTION_STACKS;

        AsyncRequest.build(RequestBuilder.GET, url).loader(loader).header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON)
                    .send(callback);
    }

    /**
     * @see org.exoplatform.ide.extension.aws.client.beanstalk.BeanstalkClientService#getSolutionStackConfigurationOptions(
     *org.exoplatform.ide.extension.aws.shared.beanstalk.SolutionStackConfigurationOptionsRequest,
     *      org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
     */
    @Override
    public void getSolutionStackConfigurationOptions(SolutionStackConfigurationOptionsRequest request,
                                                     AsyncRequestCallback<List<ConfigurationOptionInfo>> callback) throws RequestException {
        String url = restServiceContext + SOLUTION_STACK_OPTIONS;
        String data = AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(request)).getPayload();

        AsyncRequest.build(RequestBuilder.POST, url.toString()).loader(loader)
                    .header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON)
                    .header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_JSON).data(data).send(callback);
    }

    /**
     * @see org.exoplatform.ide.extension.aws.client.beanstalk.BeanstalkClientService#createApplication(java.util.Map,
     *      org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
     */
    @Override
    public void createApplication(String vfsId, String projectId, CreateApplicationRequest createApplicationRequest,
                                  AsyncRequestCallback<ApplicationInfo> callback) throws RequestException {
        StringBuilder url = new StringBuilder(restServiceContext);
        url.append(APPLICATION_CREATE).append("?vfsid=").append(vfsId).append("&projectid=").append(projectId);
        String data = AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(createApplicationRequest)).getPayload();

        AsyncRequest.build(RequestBuilder.POST, url.toString()).loader(loader)
                    .header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON)
                    .header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_JSON).data(data).send(callback);
    }

    /**
     * @see org.exoplatform.ide.extension.aws.client.beanstalk.BeanstalkClientService#updateApplication(java.lang.String,
     *      java.lang.String, com.amazonaws.services.elasticbeanstalk.model.UpdateApplicationRequest,
     *      org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
     */
    @Override
    public void updateApplication(String vfsId, String projectId, UpdateApplicationRequest updateApplicationRequest,
                                  AsyncRequestCallback<ApplicationInfo> callback) throws RequestException {
        StringBuilder url = new StringBuilder(restServiceContext);
        url.append(APPLICATION_UPDATE).append("?vfsid=").append(vfsId).append("&projectid=").append(projectId);
        String data = AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(updateApplicationRequest)).getPayload();

        AsyncRequest.build(RequestBuilder.POST, url.toString()).loader(loader)
                    .header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON)
                    .header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_JSON).data(data).send(callback);
    }

    /**
     * @see org.exoplatform.ide.extension.aws.client.beanstalk.BeanstalkClientService#getApplicationInfo(java.lang.String,
     *      java.lang.String, org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
     */
    @Override
    public void getApplicationInfo(String vfsId, String projectId, AsyncRequestCallback<ApplicationInfo> callback)
            throws RequestException {
        StringBuilder url = new StringBuilder(restServiceContext);
        url.append(APPLICATION_INFO).append("?vfsid=").append(vfsId).append("&").append("projectid=").append(projectId);

        AsyncRequest.build(RequestBuilder.GET, url.toString()).loader(loader)
                    .header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON).send(callback);
    }

    /**
     * @see org.exoplatform.ide.extension.aws.client.beanstalk.BeanstalkClientService#deleteApplication(java.lang.String,
     *      java.lang.String, org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
     */
    @Override
    public void deleteApplication(String vfsId, String projectId, AsyncRequestCallback<Object> callback)
            throws RequestException {
        StringBuilder url = new StringBuilder(restServiceContext);
        url.append(APPLICATION_DELETE).append("?vfsid=").append(vfsId).append("&").append("projectid=").append(projectId);

        AsyncRequest.build(RequestBuilder.POST, url.toString()).loader(loader).send(callback);

    }

    /** @see org.exoplatform.ide.extension.aws.client.beanstalk.BeanstalkClientService#getApplications(org.exoplatform.gwtframework
     * .commons.rest.AsyncRequestCallback) */
    @Override
    public void getApplications(AsyncRequestCallback<List<ApplicationInfo>> callback) throws RequestException {
        String url = restServiceContext + APPLICATIONS;

        AsyncRequest.build(RequestBuilder.GET, url).loader(loader).header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON)
                    .send(callback);
    }

    /**
     * @see org.exoplatform.ide.extension.aws.client.beanstalk.BeanstalkClientService#getApplicationEvents(java.lang.String,
     *      java.lang.String, org.exoplatform.ide.extension.aws.shared.beanstalk.ListEventsRequest,
     *      org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
     */
    @Override
    public void getApplicationEvents(String vfsId, String projectId, ListEventsRequest listEventsRequest,
                                     AsyncRequestCallback<EventsList> callback) throws RequestException {
        StringBuilder url = new StringBuilder(restServiceContext);
        url.append(APPLICATION_EVENTS).append("?vfsid=").append(vfsId).append("&projectid=").append(projectId);
        String data = AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(listEventsRequest)).getPayload();

        AsyncRequest.build(RequestBuilder.POST, url.toString()).header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON)
                    .header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_JSON).data(data).send(callback);
    }

    /**
     * @see org.exoplatform.ide.extension.aws.client.beanstalk.BeanstalkClientService#getConfigurationTemplate(java.lang.String,
     *      java.lang.String, org.exoplatform.ide.extension.aws.shared.beanstalk.ConfigurationRequest,
     *      org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
     */
    @Override
    public void getConfigurationTemplate(String vfsId, String projectId, ConfigurationRequest configurationRequest,
                                         AsyncRequestCallback<Configuration> callback) throws RequestException {
        StringBuilder url = new StringBuilder(restServiceContext);
        url.append(APPLICATION_TEMPLATE).append("?vfsid=").append(vfsId).append("&projectid=").append(projectId);
        String data = AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(configurationRequest)).getPayload();

        AsyncRequest.build(RequestBuilder.POST, url.toString()).header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON)
                    .loader(loader).header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_JSON).data(data).send(callback);
    }

    /**
     * @see org.exoplatform.ide.extension.aws.client.beanstalk.BeanstalkClientService#getEnvironments(java.lang.String,
     *      java.lang.String, org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
     */
    @Override
    public void getEnvironments(String vfsId, String projectId, AsyncRequestCallback<List<EnvironmentInfo>> callback)
            throws RequestException {
        StringBuilder url = new StringBuilder(restServiceContext);
        url.append(ENVIRONMENTS).append("?vfsid=").append(vfsId).append("&projectid=").append(projectId);

        AsyncRequest.build(RequestBuilder.GET, url.toString()).loader(loader)
                    .header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON).send(callback);
    }

    /**
     * @see org.exoplatform.ide.extension.aws.client.beanstalk.BeanstalkClientService#getEnvironmentConfigurations(java.lang.String,
     *      java.lang.String, org.exoplatform.ide.extension.aws.shared.beanstalk.ConfigurationRequest,
     *      org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
     */
    @Override
    public void getEnvironmentConfigurations(String vfsId, String projectId, ConfigurationRequest configurationRequest,
                                             AsyncRequestCallback<List<Configuration>> callback) throws RequestException {
        StringBuilder url = new StringBuilder(restServiceContext);
        url.append(ENVIRONMENTS_CONFIGURATION).append("?vfsid=").append(vfsId).append("&projectid=").append(projectId);
        String data = AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(configurationRequest)).getPayload();

        AsyncRequest.build(RequestBuilder.POST, url.toString()).header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON)
                    .loader(loader).header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_JSON).data(data).send(callback);
    }

    /**
     * @see org.exoplatform.ide.extension.aws.client.beanstalk.BeanstalkClientService#createEnvironment(java.lang.String,
     *      java.lang.String, org.exoplatform.ide.extension.aws.shared.beanstalk.CreateEnvironmentRequest,
     *      org.exoplatform.ide.extension.aws.client.AwsAsyncRequestCallback)
     */
    @Override
    public void createEnvironment(String vfsId, String projectId, CreateEnvironmentRequest createEnvironmentRequest,
                                  AwsAsyncRequestCallback<EnvironmentInfo> callback) throws RequestException {
        StringBuilder url = new StringBuilder(restServiceContext);
        url.append(ENVIRONMENT_CREATE).append("?vfsid=").append(vfsId).append("&projectid=").append(projectId);
        String data = AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(createEnvironmentRequest)).getPayload();

        AsyncRequest.build(RequestBuilder.POST, url.toString()).loader(loader)
                    .header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON)
                    .header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_JSON).data(data).send(callback);
    }

    /**
     * @see org.exoplatform.ide.extension.aws.client.beanstalk.BeanstalkClientService#stopEnvironment(java.lang.String,
     *      org.exoplatform.ide.extension.aws.client.AwsAsyncRequestCallback)
     */
    @Override
    public void stopEnvironment(String environmentId, AwsAsyncRequestCallback<EnvironmentInfo> callback)
            throws RequestException {
        String url = restServiceContext + ENVIRONMENT_STOP + environmentId;

        AsyncRequest.build(RequestBuilder.GET, url.toString()).loader(loader)
                    .header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON).send(callback);
    }

    /**
     * @see org.exoplatform.ide.extension.aws.client.beanstalk.BeanstalkClientService#rebuildEnvironment(java.lang.Object,
     *      org.exoplatform.ide.extension.aws.client.AwsAsyncRequestCallback)
     */
    @Override
    public void rebuildEnvironment(String environmentId, AwsAsyncRequestCallback<Object> callback)
            throws RequestException {
        String url = restServiceContext + ENVIRONMENT_REBUILD + environmentId;

        AsyncRequest.build(RequestBuilder.GET, url.toString()).loader(loader).send(callback);
    }

    /**
     * @see org.exoplatform.ide.extension.aws.client.beanstalk.BeanstalkClientService#getEnvironmentInfo(java.lang.String,
     *      org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
     */
    @Override
    public void getEnvironmentInfo(String environmentId, AsyncRequestCallback<EnvironmentInfo> callback)
            throws RequestException {
        String url = restServiceContext + ENVIRONMENT_INFO + environmentId;

        AsyncRequest.build(RequestBuilder.GET, url.toString()).header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON)
                    .send(callback);
    }

    /**
     * @see org.exoplatform.ide.extension.aws.client.beanstalk.BeanstalkClientService#updateEnvironment(java.lang.String,
     *      org.exoplatform.ide.extension.aws.shared.beanstalk.UpdateEnvironmentRequest,
     *      org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
     */
    @Override
    public void updateEnvironment(String environmentId, UpdateEnvironmentRequest updateEnvironmentRequest,
                                  AsyncRequestCallback<EnvironmentInfo> callback) throws RequestException {
        String url = restServiceContext + ENVIRONMENT_UPDATE + environmentId;
        String data = AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(updateEnvironmentRequest)).getPayload();

        AsyncRequest.build(RequestBuilder.POST, url.toString()).loader(loader)
                    .header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON)
                    .header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_JSON).data(data).send(callback);
    }

    /**
     * @see org.exoplatform.ide.extension.aws.client.beanstalk.BeanstalkClientService#getEnvironmentLogs(java.lang.String,
     *      org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
     */
    @Override
    public void getEnvironmentLogs(String environmentId, AsyncRequestCallback<List<InstanceLog>> callback)
            throws RequestException {
        String url = restServiceContext + ENVIRONMENT_LOGS + environmentId;

        AsyncRequest.build(RequestBuilder.GET, url).loader(loader).header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON)
                    .send(callback);
    }

    /**
     * @see org.exoplatform.ide.extension.aws.client.beanstalk.BeanstalkClientService#getVersions(java.lang.String,
     *      java.lang.String, org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
     */
    @Override
    public void getVersions(String vfsId, String projectId, AsyncRequestCallback<List<ApplicationVersionInfo>> callback)
            throws RequestException {
        StringBuilder url = new StringBuilder(restServiceContext);
        url.append(VERSIONS).append("?vfsid=").append(vfsId).append("&projectid=").append(projectId);

        AsyncRequest.build(RequestBuilder.GET, url.toString()).loader(loader)
                    .header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON).send(callback);
    }

    /**
     * @see org.exoplatform.ide.extension.aws.client.beanstalk.BeanstalkClientService#deleteVersion(java.lang.String,
     *      java.lang.String, java.lang.String, java.lang.String, boolean,
     *      org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
     */
    @Override
    public void deleteVersion(String vfsId, String projectId, String applicationName, String versionLabel,
                              boolean isDeleteS3Bundle, AsyncRequestCallback<Object> callback) throws RequestException {
        StringBuilder url = new StringBuilder(restServiceContext);
        url.append(VERSION_DELETE).append("?vfsid=").append(vfsId).append("&projectid=").append(projectId);

        DeleteApplicationVersionRequest deleteApplicationVersionRequest =
                AWSExtension.AUTO_BEAN_FACTORY.deleteVersionRequest().as();
        deleteApplicationVersionRequest.setApplicationName(applicationName);
        deleteApplicationVersionRequest.setVersionLabel(versionLabel);
        deleteApplicationVersionRequest.setDeleteS3Bundle(isDeleteS3Bundle);
        String data = AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(deleteApplicationVersionRequest)).getPayload();

        AsyncRequest.build(RequestBuilder.POST, url.toString()).loader(loader)
                    .header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_JSON).data(data).send(callback);
    }

    /**
     * @see org.exoplatform.ide.extension.aws.client.beanstalk.BeanstalkClientService#createVersion(java.lang.String,
     *      java.lang.String, org.exoplatform.ide.extension.aws.shared.beanstalk.CreateApplicationVersionRequest,
     *      org.exoplatform.ide.extension.aws.client.beanstalk.BeanstalkAsyncRequestCallback)
     */
    @Override
    public void createVersion(String vfsId, String projectId,
                              CreateApplicationVersionRequest createApplicationVersionRequest,
                              AwsAsyncRequestCallback<ApplicationVersionInfo> callback) throws RequestException {
        StringBuilder url = new StringBuilder(restServiceContext);
        url.append(VERSION_CREATE).append("?vfsid=").append(vfsId).append("&projectid=").append(projectId);

        String data = AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(createApplicationVersionRequest)).getPayload();

        AsyncRequest.build(RequestBuilder.POST, url.toString()).loader(loader)
                    .header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON)
                    .header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_JSON).data(data).send(callback);
    }

    /**
     * @see org.exoplatform.ide.extension.aws.client.beanstalk.BeanstalkClientService#restartApplicationServer(java.lang.Object,
     *      org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
     */
    @Override
    public void restartApplicationServer(String environmentId, AsyncRequestCallback<Object> callback)
            throws RequestException {
        String url = restServiceContext + SERVER_RESTART + environmentId;

        AsyncRequest.build(RequestBuilder.GET, url.toString()).loader(loader).send(callback);
    }

}
