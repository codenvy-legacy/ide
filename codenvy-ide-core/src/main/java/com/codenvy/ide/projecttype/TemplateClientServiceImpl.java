/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 *  [2012] - [2014] Codenvy, S.A.
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
package com.codenvy.ide.projecttype;

import com.codenvy.api.project.shared.dto.ProjectTemplateDescriptor;
import com.codenvy.api.project.shared.dto.ProjectTypeDescriptor;
import com.codenvy.ide.api.template.TemplateClientService;
import com.codenvy.ide.rest.AsyncRequest;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.ui.loader.Loader;
import com.codenvy.ide.util.Utils;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import static com.codenvy.ide.MimeType.APPLICATION_JSON;
import static com.codenvy.ide.rest.HTTPHeader.ACCEPT;
import static com.google.gwt.http.client.RequestBuilder.POST;

/**
 * The implementation of {@link TemplateClientService}.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class TemplateClientServiceImpl implements TemplateClientService {
    private static final String GET_TEMPLATE_DESCRIPTORS = "/project-template/" + Utils.getWorkspaceId() + "/get";
    private static final String CREATE_PROJECT           = "/project-template/" + Utils.getWorkspaceId() + "/create";
    private String restContext;
    private Loader loader;

    @Inject
    protected TemplateClientServiceImpl(@Named("restContext") String restContext, Loader loader) {
        this.restContext = restContext;
        this.loader = loader;
    }

    @Override
    public void getTemplates(ProjectTypeDescriptor projectTypeDescriptor, AsyncRequestCallback<String> callback) throws RequestException {
        final String requestUrl = restContext + GET_TEMPLATE_DESCRIPTORS;
        final String param = "?projectTypeId=" + projectTypeDescriptor.getProjectTypeId();
        AsyncRequest.build(RequestBuilder.GET, requestUrl + param).header(ACCEPT, APPLICATION_JSON).loader(loader).send(callback);
    }

    @Override
    public void createProject(String projectName, ProjectTemplateDescriptor projectTemplateDescriptor,
                              AsyncRequestCallback<String> callback) throws RequestException {
        final String requestUrl = restContext + CREATE_PROJECT;
        final String param = "?name=" + projectName + "&projectTypeId=" + projectTemplateDescriptor.getProjectTypeId() + "&templateId=" +
                             projectTemplateDescriptor.getTemplateId();

        loader.setMessage("Creating new project...");
        AsyncRequest.build(POST, requestUrl + param).header(ACCEPT, APPLICATION_JSON).loader(loader).send(callback);
    }
}
