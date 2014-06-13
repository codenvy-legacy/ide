/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 ******************************************************************************/

package com.codenvy.ide.extension.maven.server;

import com.codenvy.api.project.server.Project;
import com.codenvy.api.project.server.ProjectManager;
import com.codenvy.ide.extension.maven.shared.MavenAttributes;
import com.codenvy.ide.maven.tools.MavenUtils;
import com.codenvy.vfs.impl.fs.VirtualFileImpl;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import org.apache.maven.model.Model;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.io.File;

/**
 * @author Evgen Vidolob
 */
@Path("maven/pom/{ws-id}")
public class MavenPomReaderService {
    private static final Gson gson = new GsonBuilder().disableHtmlEscaping().serializeNulls().create();

    @PathParam("ws-id")
    @Inject
    private String wsId;

    @Inject
    private ProjectManager projectManager;

    @Path("read")
    @GET
    @Produces("application/json")
    public String readPomAttributes(@QueryParam("projectpath") String projectPath) throws Exception {
        Project project = projectManager.getProject(wsId, projectPath);
        VirtualFileImpl virtualFile = (VirtualFileImpl)project.getBaseFolder().getVirtualFile();

        File pomFile = new File(virtualFile.getIoFile(), "pom.xml");
        if (pomFile.exists()) {
            Model model = MavenUtils.readModel(pomFile);
            JsonObject object = new JsonObject();
            object.addProperty(MavenAttributes.MAVEN_ARTIFACT_ID, model.getArtifactId());
            object.addProperty(MavenAttributes.MAVEN_GROUP_ID, model.getGroupId());
            object.addProperty(MavenAttributes.MAVEN_VERSION, model.getVersion());
            object.addProperty(MavenAttributes.MAVEN_PACKAGING, model.getPackaging());
            return gson.toJson(object);
        } else {
            throw new IllegalArgumentException("There is no pom.xml file in project: " + projectPath);
        }
    }
}
