/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.project;

import org.exoplatform.ide.vfs.server.LocalPathResolver;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.VirtualFileSystemRegistry;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.ItemList;
import org.exoplatform.ide.vfs.shared.Property;
import org.exoplatform.ide.vfs.shared.PropertyFilter;

import javax.inject.Inject;
import javax.ws.rs.*;
import java.util.List;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
@Path("{ws-name}/project")
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

        ProjectPrepare project = new ProjectPrepare(vfs);
        project.doPrepare(localPathResolver.resolve(vfs, folderId), folderId, properties);
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
            ItemList<Item> children = vfs.getChildren(projectId, -1, 0, null, PropertyFilter.ALL_FILTER);
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
