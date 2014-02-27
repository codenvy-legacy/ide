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
import com.codenvy.commons.user.User;

import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.VirtualFileSystemRegistry;
import org.exoplatform.ide.vfs.server.exceptions.ItemNotFoundException;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.shared.Folder;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.*;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.security.ConversationState;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/** Service which perform copying projects that passed through "Copy to my workspace" action. */
@Path("{ws-name}/copy")
public class CopyProjectService {
    private static final Log LOG = ExoLogger.getLogger(CopyProjectService.class);

    @Inject
    private VirtualFileSystemRegistry vfsRegistry;

    @PathParam("ws-name")
    private String wsName;

    /**
     * Copy specified projects to a current workspace.
     * </pre>
     *
     * @return List of copied projects to client.
     * @param baseDownloadUrl
     *         base URL to download project
     * @param projects
     *         identifiers and names of projects to copy. String should be in the following format:
     *         <p/>
     *         <p/>
     *         <pre>
     *                                            project1_id:project1_name;
     *                                            project2_id:project2_name;
     *                                 </pre>
     * @throws VirtualFileSystemException
     *         if any error occurred on vfs
     * @throws IOException
     *         if any error occurred while sending request
     */
    @POST
    @Path("projects")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Item> copyProjects(@QueryParam("downloadurl") String baseDownloadUrl, List<String> projects)
            throws VirtualFileSystemException,
                   IOException {
        VirtualFileSystem vfs = vfsRegistry.getProvider(EnvironmentContext.getCurrent().getWorkspaceId()).newInstance(null, null);

        String tmpWorkspace = baseDownloadUrl.substring(baseDownloadUrl.indexOf("tmp")).substring(0, baseDownloadUrl
                .substring(baseDownloadUrl.indexOf("tmp")).indexOf("/"));

        String authToken = null;
        User user = EnvironmentContext.getCurrent().getUser();
        if (user != null && user.getToken() != null) {
            authToken = user.getToken();
        }

        List<Item> importedProjects = new ArrayList<>();

        for (String projectInfo : projects) {
            String[] projectIdAndName = projectInfo.split(":");
            final String projectId = projectIdAndName[0];
            final String projectName = getNextItemName(vfs, projectIdAndName[1]);

            Folder folder = vfs.createFolder(vfs.getInfo().getRoot().getId(), projectName);
            final String projectUrl = baseDownloadUrl + projectId;

            HttpURLConnection connection = null;
            InputStream inputStream = null;
            try {
                UriBuilder ub = UriBuilder.fromUri(projectUrl);
                if (authToken != null) {
                    ub.queryParam("token", authToken);
                }
                URL url = ub.build().toURL();
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
                importedProjects.add(project);
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

        return importedProjects;
    }

    private String getNextItemName(VirtualFileSystem vfs, String itemName) throws VirtualFileSystemException {
        String itemNameWithSuffix = itemName;
        try {
            for (int suffix = 1; ; suffix++) {
                vfs.getItemByPath(itemNameWithSuffix, null, false, PropertyFilter.NONE_FILTER);
                itemNameWithSuffix = itemName + "_" + suffix;
            }
        } catch (ItemNotFoundException e) {
            return itemNameWithSuffix;
        }
    }
}
