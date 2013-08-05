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

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.VirtualFileSystemRegistry;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.shared.Folder;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.Project;
import org.exoplatform.ide.vfs.shared.PropertyFilter;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import java.io.IOException;
import java.io.InputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
@Path("{ws-name}/copy")
public class CopyProjectService {
    @Inject
    private VirtualFileSystemRegistry vfsRegistry;

    @PathParam("ws-name")
    private String                    wsName;

    @POST
    @Path("project")
    @Produces(MimeType.APPLICATION_JSON)
    public Project copyProject(@QueryParam("projecturl") String projectUrl, @QueryParam("projectname") String projectName) throws VirtualFileSystemException,
                                                                                                                          IOException {
        VirtualFileSystem vfs =
                                vfsRegistry.getProvider(EnvironmentContext.getCurrent().getVariable(EnvironmentContext.WORKSPACE_ID)
                                                                          .toString()).newInstance(null, null);
        Folder folder = vfs.createFolder(vfs.getInfo().getRoot().getId(), projectName);
        if (CookieHandler.getDefault() == null)
            CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
        HttpURLConnection connection = null;

        InputStream inputStream = null;
        try {
            URL url = new URL(projectUrl);
            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestProperty("Referer", projectUrl);
            connection.setAllowUserInteraction(false);
            connection.setRequestMethod("GET");

            inputStream = connection.getInputStream();
            vfs.importZip(folder.getId(), inputStream, true);
            return (Project)vfs.getItem(folder.getId(), false, PropertyFilter.ALL_FILTER);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
    }

    @POST
    @Path("projects")
    public void copyProjects(@QueryParam("vfsid") String vfsId) throws VirtualFileSystemException, IOException {
        VirtualFileSystem sourceVfs = vfsRegistry.getProvider(vfsId).newInstance(null, null);
        VirtualFileSystem vfs = vfsRegistry.getProvider(EnvironmentContext.getCurrent().getVariable(EnvironmentContext.WORKSPACE_ID)
                                                                          .toString()).newInstance(null, null);

        List<Item> projects = sourceVfs.getChildren(sourceVfs.getInfo().getRoot().getId(), -1, 0, "project", false,
                                                    PropertyFilter.NONE_FILTER).getItems();
        for (Item project : projects) {
            final String projectName = project.getName();
            Folder folder = vfs.createFolder(vfs.getInfo().getRoot().getId(), projectName);
            InputStream stream = sourceVfs.exportZip(project.getId()).getStream();
            vfs.importZip(folder.getId(), stream, true);
        }
    }
}
