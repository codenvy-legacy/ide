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
package org.exoplatform.ide.project;

import org.exoplatform.ide.vfs.server.LocalPathResolver;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.VirtualFileSystemRegistry;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.ItemList;
import org.exoplatform.ide.vfs.shared.Property;
import org.exoplatform.ide.vfs.shared.PropertyFilter;
import org.xml.sax.SAXException;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.util.List;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
@Path("{ws-name}/project")
@RolesAllowed("developer")
public class ProjectPrepareService {
    @Inject
    private LocalPathResolver localPathResolver;

    @Inject
    private VirtualFileSystemRegistry vfsRegistry;

    @Path("prepare")
    @POST
    @Consumes("application/json")
    public void prepareProject(@QueryParam("folderid") String folderId,
                               @QueryParam("vfsid") String vfsId,
                               List<Property> properties)
            throws VirtualFileSystemException, ProjectPrepareException {
        if (folderId == null || vfsId == null) {
            throw new ProjectPrepareException(500, "Missing folderId or vfsId parameter");
        }

        VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null, null);
        if (vfs == null) {
            throw new VirtualFileSystemException(
                    "Can't resolve path on the Local File System : Virtual file system not initialized");
        }

        ProjectPrepare newProjectPrepare = new ProjectPrepare(vfs);
        try {
            newProjectPrepare.doPrepare(folderId);
        } catch (ParserConfigurationException e) {
            throw new ProjectPrepareException(e.getMessage(), e);
        } catch (SAXException e) {
            throw new ProjectPrepareException(e.getMessage(), e);
        } catch (XPathExpressionException e) {
            throw new ProjectPrepareException(e.getMessage(), e);
        } catch (IOException e) {
            throw new ProjectPrepareException(e.getMessage(), e);
        }
    }

    @Path("addModule")
    @GET
    public void addModule(@QueryParam("vfsId") String vfsId,
                          @QueryParam("projectId") String projectId,
                          @QueryParam("moduleName") String moduleName)
            throws VirtualFileSystemException, ProjectPrepareException {
        if (vfsId == null || vfsId.isEmpty() || projectId == null || projectId.isEmpty() || moduleName == null || moduleName.isEmpty()) {
            throw new ProjectPrepareException(500, "Missing projectId or moduleName parameter");
        }

        VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null, null);
        if (vfs == null) {
            throw new VirtualFileSystemException(
                    "Can't resolve path on the Local File System : Virtual file system not initialized");
        }

        try {
            ItemList<Item> children = vfs.getChildren(projectId, -1, 0, null, false, PropertyFilter.ALL_FILTER);
            for (Item item : children.getItems()) {
                if ("pom.xml".equals(item.getName())) {
                    MultiModuleProjectProcessor processor = new MultiModuleProjectProcessor(vfs);
                    processor.addModule(item, moduleName);
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
