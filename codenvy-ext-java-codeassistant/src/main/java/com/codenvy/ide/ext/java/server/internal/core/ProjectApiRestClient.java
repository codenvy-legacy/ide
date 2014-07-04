/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/

package com.codenvy.ide.ext.java.server.internal.core;

import com.codenvy.api.core.ConflictException;
import com.codenvy.api.core.ForbiddenException;
import com.codenvy.api.core.NotFoundException;
import com.codenvy.api.core.ServerException;
import com.codenvy.api.core.UnauthorizedException;
import com.codenvy.api.core.rest.HttpJsonHelper;
import com.codenvy.api.project.shared.dto.ProjectDescriptor;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * @author Evgen Vidolob
 */
@Singleton
public class ProjectApiRestClient {

    private static final Logger LOG =
            LoggerFactory.getLogger(ProjectApiRestClient.class);

    private String projectApiUrl;

    @Inject
    public ProjectApiRestClient(@Named("api.endpoint") String apiUrl) {
        this.projectApiUrl = apiUrl +"/project";
    }

    public ProjectDescriptor getProject(String wsId, String path) {
        try {
            return HttpJsonHelper.request(ProjectDescriptor.class, projectApiUrl + "/" + wsId + "/" + path, "GET", null, null);
        } catch (IOException | ServerException | UnauthorizedException | ForbiddenException | NotFoundException | ConflictException e) {
            LOG.error("Can't read project: " + path + " in workspace: " + wsId, e);
        }
        return null;
    }

    public List<ProjectDescriptor> getModules(String wsId, String path) {
        try {
            return HttpJsonHelper.requestArray(ProjectDescriptor.class, projectApiUrl + "/" + wsId + "/modules/" + path, "GET", null, null);
        } catch (IOException | ServerException | UnauthorizedException | ForbiddenException | NotFoundException | ConflictException e) {
            LOG.error("Can't read modules of project: " + path + " in workspace: " + wsId, e);
        }
        return Collections.emptyList();
    }
}
