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

import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.VirtualFileSystemRegistry;
import org.exoplatform.ide.vfs.server.exceptions.ItemNotFoundException;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.shared.Folder;
import org.exoplatform.ide.vfs.shared.PropertyFilter;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import java.io.IOException;
import java.io.InputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.URL;

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

    /**
     * Copy specified projects to a current workspace.
     * 
     * @param baseDownloadUrl base URL to download project
     * @param projectIds identifiers and names of projects to copy. String should be in the following format:
     *            <p/>
     * 
     *            <pre>
     *            project1_id:project1_name;
     *            project2_id:project2_name;
     * </pre>
     * @throws VirtualFileSystemException if any error occurred on vfs
     * @throws IOException if any error occurred while sending request
     */
    @POST
    @Path("projects")
    public void copyProjects(@QueryParam("downloadurl") String baseDownloadUrl, @QueryParam("projectid") String projectIds) throws VirtualFileSystemException,
                                                                                                                           IOException {
        VirtualFileSystem vfs = vfsRegistry.getProvider(EnvironmentContext.getCurrent().getVariable(EnvironmentContext.WORKSPACE_ID)
                                                                          .toString()).newInstance(null, null);

        if (CookieHandler.getDefault() == null) {
            CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
        }

        final String[] projectIdArray = projectIds.split(";");
        for (String projectInfo : projectIdArray) {
            String[] projectIdAndName = projectInfo.split(":");
            final String projectId = projectIdAndName[0];
            final String projectName = getNextItemName(vfs, projectIdAndName[1]);

            Folder folder = vfs.createFolder(vfs.getInfo().getRoot().getId(), projectName);
            final String projectUrl = baseDownloadUrl + projectId;

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
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException ex) {
                        // ignore
                    }
                }
            }
        }
    }

    private String getNextItemName(VirtualFileSystem vfs, String itemName) throws VirtualFileSystemException {
        String itemNameWithSuffix = itemName;
        try {
            for (int suffix = 1;; suffix++) {
                vfs.getItemByPath(itemNameWithSuffix, null, false, PropertyFilter.NONE_FILTER);
                itemNameWithSuffix = itemName + "_" + suffix;
            }
        } catch (ItemNotFoundException e) {
            return itemNameWithSuffix;
        }
    }
}
