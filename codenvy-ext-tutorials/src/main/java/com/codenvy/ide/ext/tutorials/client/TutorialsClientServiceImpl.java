/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 *  [2012] - [2013] Codenvy, S.A. 
 *  All Rights Reserved.
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
package com.codenvy.ide.ext.tutorials.client;

import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.rest.AsyncRequest;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.ui.loader.Loader;
import com.codenvy.ide.util.Utils;
import com.google.gwt.http.client.RequestException;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import javax.validation.constraints.NotNull;

import static com.google.gwt.http.client.RequestBuilder.POST;

/**
 * Implementation of {@link TutorialsClientService}.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class TutorialsClientServiceImpl implements TutorialsClientService {
    private static final String BASE_URL                            = "/create-tutorial/" + Utils.getWorkspaceName();
    private static final String UNPACK_NOTIFICATION_TUTORIAL        = BASE_URL + "/notification";
    private static final String UNPACK_ACTION_TUTORIAL              = BASE_URL + "/action";
    private static final String UNPACK_WIZARD_TUTORIAL              = BASE_URL + "/wizard";
    private static final String UNPACK_NEW_PROJECT_WIZARD_TUTORIAL  = BASE_URL + "/newproject";
    private static final String UNPACK_NEW_RESOURCE_WIZARD_TUTORIAL = BASE_URL + "/newresource";
    private static final String UNPACK_PARTS_TUTORIAL               = BASE_URL + "/parts";
    private static final String UNPACK_EDITOR_TUTORIAL              = BASE_URL + "/editor";
    private static final String UNPACK_GIN_TUTORIAL                 = BASE_URL + "/gin";
    private static final String UNPACK_WYSIWYG_TUTORIAL             = BASE_URL + "/wysiwyg";
    /** REST-service context. */
    private String           restContext;
    /** Loader to be displayed. */
    private Loader           loader;
    /** Provider of Codenvy IDE resources. */
    private ResourceProvider resourceProvider;

    /**
     * Creates service.
     *
     * @param restContext
     *         REST-service context
     * @param loader
     *         loader to show on server request
     * @param resourceProvider
     *         provider of IDE resources
     */
    @Inject
    protected TutorialsClientServiceImpl(@Named("restContext") String restContext, Loader loader, ResourceProvider resourceProvider) {
        this.loader = loader;
        this.restContext = restContext;
        this.resourceProvider = resourceProvider;
    }

    /**
     * Send request for given request url.
     *
     * @param requestUrl
     *         url where request need to be sent
     * @param projectName
     *         name of the project to create
     * @param callback
     *         callback
     * @throws RequestException
     */
    private void sendRequest(@NotNull String requestUrl, @NotNull String projectName, @NotNull AsyncRequestCallback<Void> callback)
            throws RequestException {
        final String param = "?vfsid=" + resourceProvider.getVfsInfo().getId() + "&name=" + projectName;
        loader.setMessage("Unpacking from template...");
        AsyncRequest.build(POST, requestUrl + param).loader(loader).send(callback);
    }

    @Override
    public void unzipNotificationTutorial(@NotNull String projectName, @NotNull AsyncRequestCallback<Void> callback)
            throws RequestException {
        final String requestUrl = restContext + UNPACK_NOTIFICATION_TUTORIAL;
        sendRequest(requestUrl, projectName, callback);
    }

    @Override
    public void unzipActionTutorial(@NotNull String projectName, @NotNull AsyncRequestCallback<Void> callback) throws RequestException {
        final String requestUrl = restContext + UNPACK_ACTION_TUTORIAL;
        sendRequest(requestUrl, projectName, callback);
    }

    @Override
    public void unzipWizardTutorial(@NotNull String projectName, @NotNull AsyncRequestCallback<Void> callback) throws RequestException {
        final String requestUrl = restContext + UNPACK_WIZARD_TUTORIAL;
        sendRequest(requestUrl, projectName, callback);
    }

    @Override
    public void unzipNewProjectWizardTutorial(@NotNull String projectName, @NotNull AsyncRequestCallback<Void> callback)
            throws RequestException {
        final String requestUrl = restContext + UNPACK_NEW_PROJECT_WIZARD_TUTORIAL;
        sendRequest(requestUrl, projectName, callback);
    }

    @Override
    public void unzipNewResourceWizardTutorial(@NotNull String projectName, @NotNull AsyncRequestCallback<Void> callback)
            throws RequestException {
        final String requestUrl = restContext + UNPACK_NEW_RESOURCE_WIZARD_TUTORIAL;
        sendRequest(requestUrl, projectName, callback);
    }

    @Override
    public void unzipPartsTutorial(@NotNull String projectName, @NotNull AsyncRequestCallback<Void> callback) throws RequestException {
        final String requestUrl = restContext + UNPACK_PARTS_TUTORIAL;
        sendRequest(requestUrl, projectName, callback);
    }

    @Override
    public void unzipEditorTutorial(@NotNull String projectName, @NotNull AsyncRequestCallback<Void> callback) throws RequestException {
        final String requestUrl = restContext + UNPACK_EDITOR_TUTORIAL;
        sendRequest(requestUrl, projectName, callback);
    }

    @Override
    public void unzipGinTutorial(@NotNull String projectName, @NotNull AsyncRequestCallback<Void> callback) throws RequestException {
        final String requestUrl = restContext + UNPACK_GIN_TUTORIAL;
        sendRequest(requestUrl, projectName, callback);
    }

    @Override
    public void unzipWYSIWYGEditorTutorial(@NotNull String projectName, @NotNull AsyncRequestCallback<Void> callback)
            throws RequestException {
        final String requestUrl = restContext + UNPACK_WYSIWYG_TUTORIAL;
        sendRequest(requestUrl, projectName, callback);
    }
}