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
package com.codenvy.ide.ext.extensions.server;

import com.codenvy.api.vfs.server.MountPoint;
import com.codenvy.api.vfs.server.VirtualFile;
import com.codenvy.api.vfs.server.VirtualFileSystemProvider;
import com.codenvy.api.vfs.server.VirtualFileSystemRegistry;
import com.codenvy.api.vfs.server.exceptions.VirtualFileSystemException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * REST-service to unzip project templates.
 *
 * @author Artem Zatsarynnyy
 */
@Path("create-extension/{ws-name}")
public class CreateProjectFromTemplateService {
    @Inject
    VirtualFileSystemRegistry registry;
    @Inject
    @Named("extension-url")
    private String baseUrl;

    @Path("template/gist")
    @POST
    public void unzipGistTemplate(@QueryParam("vfsid") String vfsId, @QueryParam("name") String name)
            throws VirtualFileSystemException, IOException {
        unzipTemplate(vfsId, name, baseUrl + "/gist-extension.zip");
    }

    private void unzipTemplate(String vfsId, String projectName, String templatePath) throws VirtualFileSystemException, IOException {
        VirtualFileSystemProvider provider = registry.getProvider(vfsId);
        MountPoint mountPoint = provider.getMountPoint(false);
        VirtualFile root = mountPoint.getRoot();
        VirtualFile projectFolder = root.getChild(projectName);
        try (InputStream templateStream = new FileInputStream(new File(templatePath))) {
            projectFolder.unzip(templateStream, true);
        }
    }
}