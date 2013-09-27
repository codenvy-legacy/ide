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
package com.codenvy.ide.factory.server;

import com.codenvy.commons.env.EnvironmentContext;

import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.VirtualFileSystemRegistry;
import org.exoplatform.ide.vfs.server.exceptions.ItemNotFoundException;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.shared.Folder;
import org.exoplatform.ide.vfs.shared.Project;
import org.exoplatform.ide.vfs.shared.PropertyFilter;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.security.ConversationState;

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
    private static final Log LOG = ExoLogger.getLogger(CopyProjectService.class);

    @Inject
    private VirtualFileSystemRegistry vfsRegistry;

    @PathParam("ws-name")
    private String wsName;

    static {
        if (CookieHandler.getDefault() == null) {
            CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
        }
    }

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
    public void copyProjects(@QueryParam("downloadurl") String baseDownloadUrl, @QueryParam("projectid") String projectIds)
            throws VirtualFileSystemException,
                   IOException {
        VirtualFileSystem vfs = vfsRegistry.getProvider(EnvironmentContext.getCurrent().getVariable(EnvironmentContext.WORKSPACE_ID)
                                                                          .toString()).newInstance(null, null);

        String tmpWorkspace = baseDownloadUrl.substring(baseDownloadUrl.indexOf("tmp")).substring(0, baseDownloadUrl
                .substring(baseDownloadUrl.indexOf("tmp")).indexOf("/"));

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
                Project project = (Project)vfs.getItem(folder.getId(), false, PropertyFilter.ALL_FILTER);
                LOG.info("EVENT#factory-project-imported# WS#" + tmpWorkspace + "# USER#" +
                         ConversationState.getCurrent().getIdentity().getUserId() + "# PROJECT#" + project.getName() + "# TYPE#" +
                         project.getProjectType() + "#");
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