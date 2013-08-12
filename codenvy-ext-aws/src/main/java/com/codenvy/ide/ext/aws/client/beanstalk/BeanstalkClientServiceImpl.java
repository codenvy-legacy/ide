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
package com.codenvy.ide.ext.aws.client.beanstalk;

import com.codenvy.ide.ext.aws.client.AwsAsyncRequestCallback;
import com.codenvy.ide.ext.aws.dto.client.DtoClientImpls;
import com.codenvy.ide.ext.aws.shared.beanstalk.*;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.rest.AsyncRequest;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.HTTPHeader;
import com.codenvy.ide.rest.MimeType;
import com.codenvy.ide.ui.loader.Loader;
import com.codenvy.ide.util.Utils;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

/**
 * The implementation of {@link BeanstalkClientService}.
 *
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
@Singleton
public class BeanstalkClientServiceImpl implements BeanstalkClientService {
    private static final String BASE_URL                   = '/' + Utils.getWorkspaceName() + "/aws/beanstalk";
    private static final String LOGIN                      = BASE_URL + "/login";
    private static final String LOGOUT                     = BASE_URL + "/logout";
    private static final String SOLUTION_STACKS            = BASE_URL + "/system/solution-stacks";
    private static final String SOLUTION_STACK_OPTIONS     = BASE_URL + "/system/solution-stacks/options";
    private static final String APPLICATION_CREATE         = BASE_URL + "/apps/create";
    private static final String APPLICATION_INFO           = BASE_URL + "/apps/info";
    private static final String APPLICATION_DELETE         = BASE_URL + "/apps/delete";
    private static final String APPLICATION_UPDATE         = BASE_URL + "/apps/update";
    private static final String APPLICATIONS               = BASE_URL + "/apps";
    private static final String APPLICATION_EVENTS         = BASE_URL + "/apps/events";
    private static final String APPLICATION_TEMPLATE       = BASE_URL + "/apps/template";
    private static final String ENVIRONMENT_CREATE         = BASE_URL + "/environments/create";
    private static final String ENVIRONMENT_STOP           = BASE_URL + "/environments/stop/";
    private static final String ENVIRONMENT_REBUILD        = BASE_URL + "/environments/rebuild/";
    private static final String ENVIRONMENT_INFO           = BASE_URL + "/environments/info/";
    private static final String ENVIRONMENT_UPDATE         = BASE_URL + "/environments/update/";
    private static final String ENVIRONMENTS               = BASE_URL + "/environments";
    private static final String ENVIRONMENTS_CONFIGURATION = BASE_URL + "/environments/configuration";
    private static final String ENVIRONMENT_LOGS           = BASE_URL + "/environments/logs/";
    private static final String VERSIONS                   = BASE_URL + "/apps/versions";
    private static final String VERSION_DELETE             = BASE_URL + "/apps/versions/delete";
    private static final String VERSION_CREATE             = BASE_URL + "/apps/versions/create";
    private static final String SERVER_RESTART             = BASE_URL + "/server/restart/";
    private String restServiceContext;
    private Loader loader;

    /**
     * Create client service.
     *
     * @param restContext
     * @param loader
     */
    @Inject
    protected BeanstalkClientServiceImpl(@Named("restContext") String restContext, Loader loader) {
        this.loader = loader;
        this.restServiceContext = restContext;
    }

    /** {@inheritDoc} */
    @Override
    public void login(String accessKey, String secretKey, AsyncRequestCallback<Object> callback) throws RequestException {
        String url = restServiceContext + LOGIN;

        DtoClientImpls.CredentialsImpl credentialsBean = DtoClientImpls.CredentialsImpl.make();
        credentialsBean.setAccess_key(accessKey);
        credentialsBean.setSecret_key(secretKey);

        String credentials = credentialsBean.serialize();

        AsyncRequest.build(RequestBuilder.POST, url).loader(loader).data(credentials)
                    .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void logout(AsyncRequestCallback<Object> callback) throws RequestException {
        String url = restServiceContext + LOGOUT;

        AsyncRequest.build(RequestBuilder.POST, url).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void getAvailableSolutionStacks(AwsAsyncRequestCallback<JsonArray<SolutionStack>> callback)
            throws RequestException {
        String url = restServiceContext + SOLUTION_STACKS;

        AsyncRequest.build(RequestBuilder.GET, url).loader(loader).header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON)
                    .send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void getSolutionStackConfigurationOptions(SolutionStackConfigurationOptionsRequest request,
                                                     AsyncRequestCallback<JsonArray<ConfigurationOptionInfo>> callback)
            throws RequestException {
        String url = restServiceContext + SOLUTION_STACK_OPTIONS;
        DtoClientImpls.SolutionStackConfigurationOptionsRequestImpl requestImpl =
                DtoClientImpls.SolutionStackConfigurationOptionsRequestImpl.make();
        requestImpl.setSolutionStackName(request.getSolutionStackName());
        String data = requestImpl.serialize();

        AsyncRequest.build(RequestBuilder.POST, url).loader(loader)
                    .header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON)
                    .header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_JSON).data(data).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void createApplication(String vfsId, String projectId, CreateApplicationRequest createApplicationRequest,
                                  AsyncRequestCallback<ApplicationInfo> callback) throws RequestException {
        StringBuilder url = new StringBuilder(restServiceContext);
        url.append(APPLICATION_CREATE).append("?vfsid=").append(vfsId).append("&projectid=").append(projectId);
        DtoClientImpls.CreateApplicationRequestImpl createRequest = DtoClientImpls.CreateApplicationRequestImpl.make();
        createRequest.setApplicationName(createApplicationRequest.getApplicationName());
        createRequest.setDescription(createApplicationRequest.getDescription());
        createRequest.setS3Bucket(createApplicationRequest.getS3Bucket());
        createRequest.setS3Key(createApplicationRequest.getS3Key());
        createRequest.setWar(createApplicationRequest.getWar());

        String data = createRequest.serialize();

        AsyncRequest.build(RequestBuilder.POST, url.toString()).loader(loader)
                    .header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON)
                    .header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_JSON).data(data).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void updateApplication(String vfsId, String projectId, UpdateApplicationRequest updateApplicationRequest,
                                  AsyncRequestCallback<ApplicationInfo> callback) throws RequestException {
        StringBuilder url = new StringBuilder(restServiceContext);
        url.append(APPLICATION_UPDATE).append("?vfsid=").append(vfsId).append("&projectid=").append(projectId);

        DtoClientImpls.UpdateApplicationRequestImpl updateRequest = DtoClientImpls.UpdateApplicationRequestImpl.make();
        updateRequest.setApplicationName(updateApplicationRequest.getApplicationName());
        updateRequest.setDescription(updateApplicationRequest.getDescription());

        String data = updateRequest.serialize();

        AsyncRequest.build(RequestBuilder.POST, url.toString()).loader(loader)
                    .header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON)
                    .header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_JSON).data(data).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void getApplicationInfo(String vfsId, String projectId, AsyncRequestCallback<ApplicationInfo> callback)
            throws RequestException {
        StringBuilder url = new StringBuilder(restServiceContext);
        url.append(APPLICATION_INFO).append("?vfsid=").append(vfsId).append("&").append("projectid=").append(projectId);

        AsyncRequest.build(RequestBuilder.GET, url.toString()).loader(loader)
                    .header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void deleteApplication(String vfsId, String projectId, AsyncRequestCallback<Object> callback)
            throws RequestException {
        StringBuilder url = new StringBuilder(restServiceContext);
        url.append(APPLICATION_DELETE).append("?vfsid=").append(vfsId).append("&").append("projectid=").append(projectId);

        AsyncRequest.build(RequestBuilder.POST, url.toString()).loader(loader).send(callback);

    }

    /** {@inheritDoc} */
    @Override
    public void getApplications(AsyncRequestCallback<JsonArray<ApplicationInfo>> callback) throws RequestException {
        String url = restServiceContext + APPLICATIONS;

        AsyncRequest.build(RequestBuilder.GET, url).loader(loader).header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON)
                    .send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void getApplicationEvents(String vfsId, String projectId, ListEventsRequest listEventsRequest,
                                     AsyncRequestCallback<EventsList> callback) throws RequestException {
        StringBuilder url = new StringBuilder(restServiceContext);
        url.append(APPLICATION_EVENTS).append("?vfsid=").append(vfsId).append("&projectid=").append(projectId);

        DtoClientImpls.ListEventsRequestImpl dtoListEventsRequest = (DtoClientImpls.ListEventsRequestImpl)listEventsRequest;
        String data = dtoListEventsRequest.serialize();

        AsyncRequest.build(RequestBuilder.POST, url.toString()).header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON)
                    .header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_JSON).data(data).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void getConfigurationTemplate(String vfsId, String projectId, ConfigurationRequest configurationRequest,
                                         AsyncRequestCallback<Configuration> callback) throws RequestException {
        StringBuilder url = new StringBuilder(restServiceContext);
        url.append(APPLICATION_TEMPLATE).append("?vfsid=").append(vfsId).append("&projectid=").append(projectId);
        DtoClientImpls.ConfigurationRequestImpl configurationRequestImpl = DtoClientImpls.ConfigurationRequestImpl.make();
        configurationRequestImpl.setApplicationName(configurationRequest.getApplicationName());
        configurationRequestImpl.setTemplateName(configurationRequest.getTemplateName());
        configurationRequestImpl.setEnvironmentName(configurationRequest.getEnvironmentName());

        String data = configurationRequestImpl.serialize();

        AsyncRequest.build(RequestBuilder.POST, url.toString()).header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON)
                    .loader(loader).header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_JSON).data(data).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void getEnvironments(String vfsId, String projectId, AsyncRequestCallback<JsonArray<EnvironmentInfo>> callback)
            throws RequestException {
        StringBuilder url = new StringBuilder(restServiceContext);
        url.append(ENVIRONMENTS).append("?vfsid=").append(vfsId).append("&projectid=").append(projectId);

        AsyncRequest.build(RequestBuilder.GET, url.toString()).loader(loader)
                    .header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void getEnvironmentConfigurations(String vfsId, String projectId, ConfigurationRequest configurationRequest,
                                             AsyncRequestCallback<JsonArray<Configuration>> callback) throws RequestException {
        StringBuilder url = new StringBuilder(restServiceContext);
        url.append(ENVIRONMENTS_CONFIGURATION).append("?vfsid=").append(vfsId).append("&projectid=").append(projectId);
        DtoClientImpls.ConfigurationRequestImpl configurationRequestImpl = DtoClientImpls.ConfigurationRequestImpl.make();
        configurationRequestImpl.setApplicationName(configurationRequest.getApplicationName());
        configurationRequestImpl.setTemplateName(configurationRequest.getTemplateName());
        configurationRequestImpl.setEnvironmentName(configurationRequest.getEnvironmentName());

        String data = configurationRequestImpl.serialize();

        AsyncRequest.build(RequestBuilder.POST, url.toString()).header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON)
                    .loader(loader).header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_JSON).data(data).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void createEnvironment(String vfsId, String projectId, CreateEnvironmentRequest createEnvironmentRequest,
                                  AwsAsyncRequestCallback<EnvironmentInfo> callback) throws RequestException {
        StringBuilder url = new StringBuilder(restServiceContext);
        url.append(ENVIRONMENT_CREATE).append("?vfsid=").append(vfsId).append("&projectid=").append(projectId);
        DtoClientImpls.CreateEnvironmentRequestImpl createEnvironmentRequestImpl = DtoClientImpls.CreateEnvironmentRequestImpl.make();
        createEnvironmentRequestImpl.setEnvironmentName(createEnvironmentRequest.getEnvironmentName());
        createEnvironmentRequestImpl.setApplicationName(createEnvironmentRequest.getApplicationName());
        createEnvironmentRequestImpl.setTemplateName(createEnvironmentRequest.getTemplateName());
        createEnvironmentRequestImpl.setVersionLabel(createEnvironmentRequest.getVersionLabel());
        createEnvironmentRequestImpl.setDescription(createEnvironmentRequest.getDescription());
        createEnvironmentRequestImpl.setOptions(createEnvironmentRequest.getOptions());
        createEnvironmentRequestImpl.setSolutionStackName(createEnvironmentRequest.getSolutionStackName());

        String data = createEnvironmentRequestImpl.serialize();

        AsyncRequest.build(RequestBuilder.POST, url.toString()).loader(loader)
                    .header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON)
                    .header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_JSON).data(data).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void stopEnvironment(String environmentId, AwsAsyncRequestCallback<EnvironmentInfo> callback)
            throws RequestException {
        String url = restServiceContext + ENVIRONMENT_STOP + environmentId;

        AsyncRequest.build(RequestBuilder.GET, url).loader(loader)
                    .header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void rebuildEnvironment(String environmentId, AwsAsyncRequestCallback<Object> callback)
            throws RequestException {
        String url = restServiceContext + ENVIRONMENT_REBUILD + environmentId;

        AsyncRequest.build(RequestBuilder.GET, url).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void getEnvironmentInfo(String environmentId, AsyncRequestCallback<EnvironmentInfo> callback)
            throws RequestException {
        String url = restServiceContext + ENVIRONMENT_INFO + environmentId;

        AsyncRequest.build(RequestBuilder.GET, url).header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON)
                    .send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void updateEnvironment(String environmentId, UpdateEnvironmentRequest updateEnvironmentRequest,
                                  AsyncRequestCallback<EnvironmentInfo> callback) throws RequestException {
        String url = restServiceContext + ENVIRONMENT_UPDATE + environmentId;
        DtoClientImpls.UpdateEnvironmentRequestImpl updateRequest = DtoClientImpls.UpdateEnvironmentRequestImpl.make();
        updateRequest.setOptions(updateEnvironmentRequest.getOptions());
        updateRequest.setDescription(updateEnvironmentRequest.getDescription());
        updateRequest.setVersionLabel(updateEnvironmentRequest.getVersionLabel());
        updateRequest.setTemplateName(updateEnvironmentRequest.getTemplateName());

        String data = updateRequest.serialize();

        AsyncRequest.build(RequestBuilder.POST, url).loader(loader)
                    .header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON)
                    .header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_JSON).data(data).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void getEnvironmentLogs(String environmentId, AsyncRequestCallback<JsonArray<InstanceLog>> callback)
            throws RequestException {
        String url = restServiceContext + ENVIRONMENT_LOGS + environmentId;

        AsyncRequest.build(RequestBuilder.GET, url).loader(loader).header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON)
                    .send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void getVersions(String vfsId, String projectId, AsyncRequestCallback<JsonArray<ApplicationVersionInfo>> callback)
            throws RequestException {
        StringBuilder url = new StringBuilder(restServiceContext);
        url.append(VERSIONS).append("?vfsid=").append(vfsId).append("&projectid=").append(projectId);

        AsyncRequest.build(RequestBuilder.GET, url.toString()).loader(loader)
                    .header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void deleteVersion(String vfsId, String projectId, String applicationName, String versionLabel,
                              boolean isDeleteS3Bundle, AsyncRequestCallback<Object> callback) throws RequestException {
        StringBuilder url = new StringBuilder(restServiceContext);
        url.append(VERSION_DELETE).append("?vfsid=").append(vfsId).append("&projectid=").append(projectId);
        DtoClientImpls.DeleteApplicationVersionRequestImpl deleteRequest = DtoClientImpls.DeleteApplicationVersionRequestImpl.make();
        deleteRequest.setVersionLabel(versionLabel);
        deleteRequest.setApplicationName(applicationName);
        deleteRequest.setIsDeleteS3Bundle(isDeleteS3Bundle);

        String data = deleteRequest.serialize();

        AsyncRequest.build(RequestBuilder.POST, url.toString()).loader(loader)
                    .header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_JSON).data(data).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void createVersion(String vfsId, String projectId,
                              CreateApplicationVersionRequest createApplicationVersionRequest,
                              AwsAsyncRequestCallback<ApplicationVersionInfo> callback) throws RequestException {
        StringBuilder url = new StringBuilder(restServiceContext);
        url.append(VERSION_CREATE).append("?vfsid=").append(vfsId).append("&projectid=").append(projectId);

        DtoClientImpls.CreateApplicationVersionRequestImpl createVersionRequest = DtoClientImpls.CreateApplicationVersionRequestImpl.make();
        createVersionRequest.setApplicationName(createApplicationVersionRequest.getApplicationName());
        createVersionRequest.setVersionLabel(createApplicationVersionRequest.getVersionLabel());
        createVersionRequest.setDescription(createApplicationVersionRequest.getDescription());
        createVersionRequest.setS3Bucket(createApplicationVersionRequest.getS3Bucket());
        createVersionRequest.setS3Key(createApplicationVersionRequest.getS3Key());
        createVersionRequest.setWar(createApplicationVersionRequest.getWar());

        String data = createVersionRequest.serialize();

        AsyncRequest.build(RequestBuilder.POST, url.toString()).loader(loader)
                    .header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON)
                    .header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_JSON).data(data).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void restartApplicationServer(String environmentId, AsyncRequestCallback<Object> callback)
            throws RequestException {
        String url = restServiceContext + SERVER_RESTART + environmentId;

        AsyncRequest.build(RequestBuilder.GET, url).loader(loader).send(callback);
    }
}
