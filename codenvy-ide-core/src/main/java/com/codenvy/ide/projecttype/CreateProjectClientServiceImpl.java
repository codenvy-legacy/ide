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

import com.codenvy.api.project.shared.dto.ProjectDescriptor;
import com.codenvy.api.project.shared.dto.ProjectTypeDescriptor;
import com.codenvy.ide.api.resources.CreateProjectClientService;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.rest.AsyncRequest;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.ui.loader.Loader;
import com.codenvy.ide.util.Utils;
import com.google.gwt.http.client.RequestException;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import java.util.List;
import java.util.Map;

import static com.codenvy.ide.rest.HTTPHeader.CONTENT_TYPE;
import static com.google.gwt.http.client.RequestBuilder.POST;

/**
 * //
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class CreateProjectClientServiceImpl implements CreateProjectClientService {
    private static final String CREATE_PROJECT = "/project/" + Utils.getWorkspaceName() + "/create";
    private DtoFactory dtoFactory;
    private String     restContext;
    private Loader     loader;

    @Inject
    protected CreateProjectClientServiceImpl(@Named("restContext") String restContext, Loader loader, DtoFactory dtoFactory) {
        this.restContext = restContext;
        this.loader = loader;
        this.dtoFactory = dtoFactory;
    }

    @Override
    public void createProject(String projectName, ProjectTypeDescriptor projectTypeDescriptor, Map<String, List<String>> attributes,
                              AsyncRequestCallback<Void> callback) throws RequestException {
        String param = "?name=" + projectName;
        String requestUrl = restContext + CREATE_PROJECT + param;

        ProjectDescriptor descriptor =
                dtoFactory.createDto(ProjectDescriptor.class).withProjectTypeId(projectTypeDescriptor.getProjectTypeId())
                          .withProjectTypeName(projectTypeDescriptor.getProjectTypeName()).withAttributes(attributes);

        loader.setMessage("Creating new project...");

        AsyncRequest.build(POST, requestUrl).data(dtoFactory.toJson(descriptor)).header(CONTENT_TYPE, "application/json").loader(loader)
                    .send(callback);
    }

}
