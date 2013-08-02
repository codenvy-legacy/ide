/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package com.codenvy.ide.factory.server;

import com.codenvy.commons.env.EnvironmentContext;

import org.everrest.websockets.WSConnection;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.VirtualFileSystemRegistry;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.shared.Folder;
import org.exoplatform.ide.vfs.shared.Project;
import org.exoplatform.ide.vfs.shared.PropertyFilter;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.security.ConversationState;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
@Path("{ws-name}/copy")
public class CopyProjectService {
    private static final Log LOG = ExoLogger.getLogger(CopyProjectService.class);

    @Inject
    private VirtualFileSystemRegistry vfsRegistry;
    @PathParam("ws-name")
    private String                    wsName;

    @POST
    @Path("project")
    @Produces(MimeType.APPLICATION_JSON)
    public Project copyProject(@QueryParam("projecturl") String projectUrl,
                               @QueryParam("projectname") String projectName, @Context WSConnection wsConnection)
            throws VirtualFileSystemException, IOException {

        VirtualFileSystem vfs = vfsRegistry
                .getProvider(EnvironmentContext.getCurrent().getVariable(EnvironmentContext.WORKSPACE_ID).toString())
                .newInstance(null, null);
        Folder folder = vfs.createFolder(vfs.getInfo().getRoot().getId(), projectName);
        if (CookieHandler.getDefault() == null)
            CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        String user = ConversationState.getCurrent().getIdentity().getUserId();
        String sessionId = wsConnection.getHttpSession().getId();
        try {
            URL url = new URL(projectUrl);
            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestProperty("Referer", projectUrl);
            connection.setAllowUserInteraction(false);
            connection.setRequestMethod("GET");

            inputStream = connection.getInputStream();
            vfs.importZip(folder.getId(), inputStream, true);
            Project project = (Project)vfs.getItem(folder.getId(), false, PropertyFilter.ALL_FILTER);
            LOG.info("EVENT#factory-project-imported# SESSION-ID#" + sessionId + "# WS#" + wsName + "# USER#" + user +
                     "# PROJECT#" + project.getName() + "# TYPE#" + project.getProjectType() + "#");
            return project;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    //ignore
                }
            }
        }
    }
}
