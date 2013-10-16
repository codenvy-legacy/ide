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
package org.exoplatform.ide.vfs.impl.fs;

import com.codenvy.commons.env.EnvironmentContext;
import com.codenvy.ide.commons.shared.ProjectType;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.fileupload.FileItem;
import org.exoplatform.ide.vfs.server.ContentStream;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.VirtualFileSystemUser;
import org.exoplatform.ide.vfs.server.exceptions.HtmlErrorFormatter;
import org.exoplatform.ide.vfs.server.exceptions.InvalidArgumentException;
import org.exoplatform.ide.vfs.server.exceptions.ItemAlreadyExistException;
import org.exoplatform.ide.vfs.server.exceptions.ItemNotFoundException;
import org.exoplatform.ide.vfs.server.exceptions.NotSupportedException;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.server.observation.ChangeEvent;
import org.exoplatform.ide.vfs.server.observation.EventListenerList;
import org.exoplatform.ide.vfs.server.observation.ProjectUpdateListener;
import org.exoplatform.ide.vfs.server.util.LinksHelper;
import org.exoplatform.ide.vfs.shared.AccessControlEntry;
import org.exoplatform.ide.vfs.shared.File;
import org.exoplatform.ide.vfs.shared.FileImpl;
import org.exoplatform.ide.vfs.shared.Folder;
import org.exoplatform.ide.vfs.shared.FolderImpl;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.ItemList;
import org.exoplatform.ide.vfs.shared.ItemListImpl;
import org.exoplatform.ide.vfs.shared.ItemNode;
import org.exoplatform.ide.vfs.shared.ItemNodeImpl;
import org.exoplatform.ide.vfs.shared.ItemType;
import org.exoplatform.ide.vfs.shared.LockToken;
import org.exoplatform.ide.vfs.shared.LockTokenImpl;
import org.exoplatform.ide.vfs.shared.Principal;
import org.exoplatform.ide.vfs.shared.PrincipalImpl;
import org.exoplatform.ide.vfs.shared.Project;
import org.exoplatform.ide.vfs.shared.ProjectImpl;
import org.exoplatform.ide.vfs.shared.Property;
import org.exoplatform.ide.vfs.shared.PropertyFilter;
import org.exoplatform.ide.vfs.shared.PropertyImpl;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfoImpl;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static org.exoplatform.ide.vfs.server.observation.ChangeEvent.ChangeType;
import static org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo.ACLCapability;
import static org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo.BasicPermissions;
import static org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo.QueryCapability;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class LocalFileSystem implements VirtualFileSystem {
    private static final Log    LOG             = ExoLogger.getLogger(LocalFileSystem.class);
    private static final String FAKE_VERSION_ID = "0";

    final String            vfsId;
    final String            rootId;
    final URI               baseUri;
    final EventListenerList listeners;
    final MountPoint        mountPoint;
    final SearcherProvider  searcherProvider;

    private VirtualFileSystemInfoImpl vfsInfo;

    public LocalFileSystem(String vfsId,
                           URI baseUri,
                           EventListenerList listeners,
                           MountPoint mountPoint,
                           SearcherProvider searcherProvider) {
        this.vfsId = vfsId;
        this.baseUri = baseUri;
        this.listeners = listeners;
        this.mountPoint = mountPoint;
        this.searcherProvider = searcherProvider;
        try {
            this.rootId = Base64.encodeBase64URLSafeString((vfsId + ":root").getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            // Should never happen.
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    @Path("copy/{id}")
    @Override
    public Item copy(@PathParam("id") String id, //
                     @QueryParam("parentId") String parentId) throws VirtualFileSystemException {
        final VirtualFile virtualFileCopy = idToVirtualFile(id).copyTo(idToVirtualFile(parentId));
        final Item copy = fromVirtualFile(virtualFileCopy, false, PropertyFilter.ALL_FILTER);
        if (listeners != null) {
            listeners.notifyListeners(
                    new ChangeEvent(this, copy.getId(), copy.getPath(), copy.getMimeType(), ChangeType.CREATED,
                                    mountPoint.getCurrentVirtualFileSystemUser()));
        }
        return copy;
    }

    @Path("file/{parentId}")
    @Override
    public File createFile(@PathParam("parentId") String parentId, //
                           @QueryParam("name") String name, //
                           @DefaultValue(MediaType.APPLICATION_OCTET_STREAM) @HeaderParam("Content-Type") MediaType mediaType, //
                           InputStream content) throws VirtualFileSystemException {
        final VirtualFile newVirtualFile = idToVirtualFile(parentId)
                .createFile(name, mediaType != null ? mediaType.toString() : null, content);
        final File file = (File)fromVirtualFile(newVirtualFile, false, PropertyFilter.ALL_FILTER);
        if (listeners != null) {
            listeners.notifyListeners(
                    new ChangeEvent(this, file.getId(), file.getPath(), file.getMimeType(), ChangeType.CREATED,
                                    mountPoint.getCurrentVirtualFileSystemUser()));
        }
        return file;
    }

    @Path("folder/{parentId}")
    @Override
    public Folder createFolder(@PathParam("parentId") String parentId, //
                               @QueryParam("name") String name) throws VirtualFileSystemException {
        final VirtualFile newVirtualFile = idToVirtualFile(parentId).createFolder(name);
        final Folder folder = (Folder)fromVirtualFile(newVirtualFile, false, PropertyFilter.ALL_FILTER);
        if (listeners != null) {
            listeners.notifyListeners(
                    new ChangeEvent(this, folder.getId(), folder.getPath(), folder.getMimeType(), ChangeType.CREATED,
                                    mountPoint.getCurrentVirtualFileSystemUser()));
        }
        return folder;
    }

    @Path("project/{parentId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Override
    public Project createProject(@PathParam("parentId") String parentId, //
                                 @QueryParam("name") String name, //
                                 @QueryParam("type") String type, //
                                 List<Property> properties) throws VirtualFileSystemException {
        if (properties == null) {
            properties = new ArrayList<Property>(2);
        }
        if (type != null) {
            properties.add(new PropertyImpl("vfs:projectType", type));
        }
        properties.add(new PropertyImpl("vfs:mimeType", Project.PROJECT_MIME_TYPE));

        final VirtualFile newVirtualFile = idToVirtualFile(parentId).createProject(name, properties);
        final Project project = (Project)fromVirtualFile(newVirtualFile, false, PropertyFilter.ALL_FILTER);
        if (listeners != null) {
            listeners.notifyListeners(
                    new ChangeEvent(this, project.getId(), project.getPath(), project.getMimeType(), ChangeType.CREATED,
                                    mountPoint.getCurrentVirtualFileSystemUser()));
        }
        LOG.info("EVENT#project-created# PROJECT#{}# TYPE#{}#", name, project.getProjectType());
        return project;
    }

    @Path("delete/{id}")
    @Override
    public void delete(@PathParam("id") String id, //
                       @QueryParam("lockToken") String lockToken) throws VirtualFileSystemException {
        final VirtualFile virtualFile = idToVirtualFile(id);
        final String path = virtualFile.getPath();
        final String mediaType = virtualFile.getMediaType();
        String name = null;
        String projectType = null;
        boolean mavenModule = false;
        final boolean isProject = virtualFile.isProject();
        if (isProject) {
            name = virtualFile.getName();
            projectType = virtualFile.getPropertyValue("vfs:projectType");
            mavenModule = Boolean.parseBoolean(virtualFile.getPropertyValue("Maven Module"));
        }
        virtualFile.delete(lockToken);
        if (listeners != null) {
            listeners.notifyListeners(
                    new ChangeEvent(this, id, path, mediaType, ChangeType.DELETED, mountPoint.getCurrentVirtualFileSystemUser()));
        }
        if (isProject) {
            //For module from multi-module project no need to fire event for delete project
            if (!(mavenModule && ProjectType.fromValue(projectType) != ProjectType.MULTI_MODULE)) {
                LOG.info("EVENT#project-destroyed# PROJECT#{}# TYPE#{}#", name, projectType);
            }
        }
    }

    @Path("acl/{id}")
    @Override
    public List<AccessControlEntry> getACL(@PathParam("id") String id) throws VirtualFileSystemException {
        return idToVirtualFile(id).getACL();
    }

    @Path("children/{id}")
    @Override
    public ItemList<Item> getChildren(@PathParam("id") String folderId, //
                                      @DefaultValue("-1") @QueryParam("maxItems") int maxItems, //
                                      @QueryParam("skipCount") int skipCount, //
                                      @QueryParam("itemType") String itemType, //
                                      @DefaultValue("false") @QueryParam("includePermissions") Boolean includePermissions,
                                      @DefaultValue(PropertyFilter.NONE) @QueryParam("propertyFilter") PropertyFilter propertyFilter)
            throws VirtualFileSystemException {
        if (skipCount < 0) {
            throw new InvalidArgumentException("'skipCount' parameter is negative. ");
        }

        final ItemType itemTypeType;
        if (itemType != null) {
            try {
                itemTypeType = ItemType.fromValue(itemType);
            } catch (IllegalArgumentException e) {
                throw new InvalidArgumentException(String.format("Unknown type: %s", itemType));
            }
        } else {
            itemTypeType = null;
        }

        final VirtualFile virtualFile = idToVirtualFile(folderId);

        final List<VirtualFile> children = virtualFile.getChildren();
        int totalNumber = children.size();
        if (itemTypeType != null) {
            Iterator<VirtualFile> iterator = children.iterator();
            while (iterator.hasNext()) {
                VirtualFile next = iterator.next();
                if ((itemTypeType == ItemType.FILE && !next.isFile())
                    || (itemTypeType == ItemType.FOLDER && !next.isFolder())
                    || (itemTypeType == ItemType.PROJECT && !next.isProject())) {
                    iterator.remove();
                }
            }
        }
        if (skipCount > 0) {
            if (skipCount > children.size()) {
                throw new InvalidArgumentException("'skipCount' parameter is greater then total number of items. ");
            }
            children.subList(0, skipCount).clear();
        }

        final List<Item> list;
        final boolean hasMoreItems;
        if (maxItems > 0) {
            list = new ArrayList<Item>();
            Iterator<VirtualFile> iterator = children.iterator();
            for (int count = 0; count < maxItems && iterator.hasNext(); count++) {
                list.add(fromVirtualFile(iterator.next(), includePermissions, propertyFilter, false));
            }
            hasMoreItems = iterator.hasNext();
        } else {
            list = new ArrayList<Item>(children.size());
            for (VirtualFile aChildren : children) {
                list.add(fromVirtualFile(aChildren, includePermissions, propertyFilter, false));
            }
            hasMoreItems = false;
        }

        final ItemList<Item> result = new ItemListImpl<Item>(list);
        result.setNumItems(totalNumber);
        result.setHasMoreItems(hasMoreItems);

        return result;
    }

    @Path("tree/{id}")
    @Override
    public ItemNode getTree(@PathParam("id") String folderId,
                            @DefaultValue("-1") @QueryParam("depth") int depth,
                            @DefaultValue("false") @QueryParam("includePermissions") Boolean includePermissions,
                            @DefaultValue(PropertyFilter.NONE) @QueryParam("propertyFilter") PropertyFilter propertyFilter)
            throws VirtualFileSystemException {
        final VirtualFile virtualFile = idToVirtualFile(folderId);
        if (!virtualFile.isFolder()) {
            throw new InvalidArgumentException(
                    String.format("Unable get tree. Item '%s' is not a folder. ", virtualFile.getPath()));
        }
        return new ItemNodeImpl(fromVirtualFile(virtualFile, includePermissions, propertyFilter, false),
                                getTreeLevel(virtualFile, depth, includePermissions, propertyFilter));
    }

    private List<ItemNode> getTreeLevel(VirtualFile virtualFile, int depth, boolean includePermissions, PropertyFilter propertyFilter)
            throws VirtualFileSystemException {
        if (depth == 0 || !virtualFile.isFolder()) {
            return null;
        }
        final List<VirtualFile> children = virtualFile.getChildren();
        final List<ItemNode> level = new ArrayList<ItemNode>(children.size());
        for (VirtualFile i : children) {
            level.add(new ItemNodeImpl(fromVirtualFile(i, includePermissions, propertyFilter, false),
                                       getTreeLevel(i, depth - 1, includePermissions, propertyFilter)));
        }
        return level;
    }

    @Path("content/{id}")
    @Override
    public ContentStream getContent(@PathParam("id") String id) throws VirtualFileSystemException {
        return idToVirtualFile(id).getContent();
    }

    @Path("contentbypath/{path:.*}")
    @Override
    public ContentStream getContent(@PathParam("path") String path, //
                                    @QueryParam("versionId") String versionId) throws VirtualFileSystemException {
        return getVirtualFileByPath(path).getContent();
    }

    @Override
    public VirtualFileSystemInfo getInfo() throws VirtualFileSystemException {
        if (vfsInfo == null) {
            final BasicPermissions[] basicPermissions = BasicPermissions.values();
            final List<String> permissions = new ArrayList<String>(basicPermissions.length);
            for (BasicPermissions bp : basicPermissions) {
                permissions.add(bp.value());
            }
            vfsInfo =
                    new VirtualFileSystemInfoImpl(this.vfsId, false, true, VirtualFileSystemInfo.ANONYMOUS_PRINCIPAL,
                                                  VirtualFileSystemInfo.ANY_PRINCIPAL, permissions, ACLCapability.MANAGE,
                                                  searcherProvider == null ? QueryCapability.NONE : QueryCapability.FULLTEXT,
                                                  LinksHelper.createUrlTemplates(baseUri, EnvironmentContext.getCurrent().getVariable(EnvironmentContext.WORKSPACE_NAME).toString()),
                                                  (Folder)fromVirtualFile(mountPoint.getRoot(), true, PropertyFilter.ALL_FILTER));
        }
        return vfsInfo;
    }

    @Path("item/{id}")
    @Override
    public Item getItem(@PathParam("id") String id, //
                        @DefaultValue("false") @QueryParam("includePermissions") Boolean includePermissions,
                        @DefaultValue(PropertyFilter.ALL) @QueryParam("propertyFilter") PropertyFilter propertyFilter)
            throws VirtualFileSystemException {
        return fromVirtualFile(idToVirtualFile(id), includePermissions, propertyFilter);
    }

    @Path("itembypath/{path:.*}")
    @Override
    public Item getItemByPath(@PathParam("path") String path, //
                              @QueryParam("versionId") String versionId, //
                              @DefaultValue("false") @QueryParam("includePermissions") Boolean includePermissions,
                              @DefaultValue(PropertyFilter.ALL) @QueryParam("propertyFilter") PropertyFilter propertyFilter)
            throws VirtualFileSystemException {
        final VirtualFile virtualFile = getVirtualFileByPath(path);
        if (virtualFile.isFile()) {
            if (!(versionId == null || FAKE_VERSION_ID.equals(versionId))) {
                throw new NotSupportedException("Versioning is not supported. ");
            }
        } else if (versionId != null) {
            throw new InvalidArgumentException(
                    String.format("Object '%s' is not a file. Version ID must not be set. ", path));
        }

        return fromVirtualFile(virtualFile, includePermissions, propertyFilter);
    }

    @Path("version/{id}/{versionId}")
    @Override
    public ContentStream getVersion(@PathParam("id") String id, //
                                    @PathParam("versionId") String versionId) throws VirtualFileSystemException {
        if (!(versionId == null || FAKE_VERSION_ID.equals(versionId))) {
            throw new NotSupportedException("Versioning is not supported. ");
        }
        return idToVirtualFile(id).getContent();
    }

    @Path("version-history/{id}")
    @Override
    public ItemList<File> getVersions(@PathParam("id") String id, //
                                      @DefaultValue("-1") @QueryParam("maxItems") int maxItems, //
                                      @QueryParam("skipCount") int skipCount, //
                                      @DefaultValue(PropertyFilter.ALL) @QueryParam("propertyFilter") PropertyFilter propertyFilter //
                                     ) throws VirtualFileSystemException {
        if (skipCount < 0) {
            throw new InvalidArgumentException("'skipCount' parameter is negative. ");
        }
        if (skipCount > 1) {
            // Since we don't support versioning we always have only one version.
            throw new InvalidArgumentException("'skipCount' parameter is greater then total number of items. ");
        }

        final VirtualFile virtualFile = idToVirtualFile(id);
        if (!virtualFile.isFile()) {
            throw new InvalidArgumentException(String.format("Object '%s' is not a file. ", virtualFile.getName()));
        }
        ItemList<File> versions = (maxItems < 0 || (maxItems - skipCount) > 0)
                                  ? new ItemListImpl<File>(
                Collections.singletonList((File)fromVirtualFile(virtualFile, false, propertyFilter)))
                                  : new ItemListImpl<File>(Collections.<File>emptyList());
        versions.setHasMoreItems(false);
        return versions;
    }

    @Path("lock/{id}")
    @Override
    public LockToken lock(@PathParam("id") String id) throws NotSupportedException, VirtualFileSystemException {
        return new LockTokenImpl(idToVirtualFile(id).lock());
    }

    @Path("move/{id}")
    @Override
    public Item move(@PathParam("id") String id, //
                     @QueryParam("parentId") String parentId, //
                     @QueryParam("lockToken") String lockToken) throws VirtualFileSystemException {

        final VirtualFile origin = idToVirtualFile(id);
        final String oldPath = origin.getPath();
        final Item moved = fromVirtualFile(origin.moveTo(idToVirtualFile(parentId), lockToken), false, PropertyFilter.ALL_FILTER);
        if (listeners != null) {
            listeners.notifyListeners(
                    new ChangeEvent(this, moved.getId(), moved.getPath(), oldPath, moved.getMimeType(), ChangeType.MOVED,
                                    mountPoint.getCurrentVirtualFileSystemUser()));
        }
        return moved;
    }

    @Path("rename/{id}")
    @Override
    public Item rename(@PathParam("id") String id, //
                       @QueryParam("mediaType") MediaType newMediaType, //
                       @QueryParam("newname") String newName, //
                       @QueryParam("lockToken") String lockToken) throws VirtualFileSystemException {
        if ((newName == null || newName.isEmpty()) && newMediaType == null) {
            // Nothing to do. Return unchanged object.
            return getItem(id, false, PropertyFilter.ALL_FILTER);
        }

        final VirtualFile origin = idToVirtualFile(id);
        final boolean isProjectBefore = origin.isProject();
        final String oldPath = origin.getPath();
        final VirtualFile renamedVriVirtualFile = origin.rename(newName, newMediaType == null ? null : newMediaType.toString(), lockToken);
        final Item renamed = fromVirtualFile(renamedVriVirtualFile, false, PropertyFilter.ALL_FILTER);
        final boolean isProjectAfter = renamedVriVirtualFile.isProject();
        if (listeners != null) {
            listeners.notifyListeners(
                    new ChangeEvent(this, renamed.getId(), renamed.getPath(), oldPath, renamed.getMimeType(), ChangeType.RENAMED,
                                    mountPoint.getCurrentVirtualFileSystemUser()));
        }
        if (isProjectAfter && !isProjectBefore) {
            LOG.info("EVENT#project-created# PROJECT#{}# TYPE#{}#", renamed.getName(), ((Project)renamed).getProjectType());
        }
        return renamed;
    }

    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    @Override
    public ItemList<Item> search(MultivaluedMap<String, String> query, //
                                 @DefaultValue("-1") @QueryParam("maxItems") int maxItems, //
                                 @QueryParam("skipCount") int skipCount, //
                                 @DefaultValue(PropertyFilter.ALL) @QueryParam("propertyFilter") PropertyFilter propertyFilter //
                                ) throws NotSupportedException, VirtualFileSystemException {
        if (searcherProvider != null) {
            if (skipCount < 0) {
                throw new InvalidArgumentException("'skipCount' parameter is negative. ");
            }
            final QueryExpression expr = new QueryExpression()
                    .setPath(query.getFirst("path"))
                    .setName(query.getFirst("name"))
                    .setMediaType(query.getFirst("mediaType"))
                    .setText(query.getFirst("text"));

            final String[] result = searcherProvider.getSearcher(mountPoint, true).search(expr);
            if (skipCount > 0) {
                if (skipCount > result.length) {
                    throw new InvalidArgumentException("'skipCount' parameter is greater then total number of items. ");
                }
            }
            final int length = maxItems > 0 ? Math.min(result.length, maxItems) : result.length;
            final List<Item> items = new ArrayList<Item>(length);
            for (int i = skipCount; i < length; i++) {
                String path = result[i];
                try {
                    items.add(fromVirtualFile(getVirtualFileByPath(path), false, propertyFilter));
                } catch (ItemNotFoundException ignored) {
                }
            }

            ItemList<Item> itemList = new ItemListImpl<Item>(items);
            itemList.setNumItems(result.length);
            itemList.setHasMoreItems(length < result.length);
            return itemList;
        }
        throw new NotSupportedException("Not supported. ");
    }

    @Override
    public ItemList<Item> search(@QueryParam("statement") String statement, //
                                 @DefaultValue("-1") @QueryParam("maxItems") int maxItems, //
                                 @QueryParam("skipCount") int skipCount //
                                ) throws NotSupportedException, VirtualFileSystemException {
        // No plan to support SQL at the moment.
        throw new NotSupportedException("Not supported. ");
    }

    @Path("unlock/{id}")
    @Override
    public void unlock(@PathParam("id") String id, //
                       @QueryParam("lockToken") String lockToken //
                      ) throws NotSupportedException, VirtualFileSystemException {
        idToVirtualFile(id).unlock(lockToken);
    }

    @Path("acl/{id}")
    @Override
    public void updateACL(@PathParam("id") String id, //
                          List<AccessControlEntry> acl, //
                          @DefaultValue("false") @QueryParam("override") Boolean override, //
                          @QueryParam("lockToken") String lockToken //
                         ) throws VirtualFileSystemException {
        final VirtualFile virtualFile = idToVirtualFile(id).updateACL(acl, override, lockToken);
        if (listeners != null) {
            listeners.notifyListeners(
                    new ChangeEvent(this, virtualFileToId(virtualFile), virtualFile.getPath(), virtualFile.getMediaType(),
                                    ChangeType.ACL_UPDATED,
                                    mountPoint.getCurrentVirtualFileSystemUser()));
        }
    }

    @Path("content/{id}")
    @Override
    public void updateContent(
            @PathParam("id") String id, //
            @DefaultValue(MediaType.APPLICATION_OCTET_STREAM) @HeaderParam("Content-Type") MediaType mediaType,
            InputStream newContent, //
            @QueryParam("lockToken") String lockToken) throws VirtualFileSystemException {
        final VirtualFile virtualFile = idToVirtualFile(id)
                .updateContent(mediaType != null ? mediaType.toString() : null, newContent, lockToken);
        if (listeners != null) {
            listeners.notifyListeners(
                    new ChangeEvent(this, virtualFileToId(virtualFile), virtualFile.getPath(), virtualFile.getMediaType(),
                                    ChangeType.CONTENT_UPDATED, mountPoint.getCurrentVirtualFileSystemUser()));
        }
    }

    @Path("item/{id}")
    @Override
    public Item updateItem(@PathParam("id") String id, //
                           List<Property> properties, //
                           @QueryParam("lockToken") String lockToken) throws VirtualFileSystemException {
        final VirtualFile virtualFile = idToVirtualFile(id);
        final boolean isProjectBefore = virtualFile.isProject();
        virtualFile.updateProperties(properties, lockToken);
        final boolean isProjectAfter = virtualFile.isProject();
        final Item updated = fromVirtualFile(virtualFile, false, PropertyFilter.ALL_FILTER);
        if (listeners != null) {
            listeners.notifyListeners(
                    new ChangeEvent(this, updated.getId(), updated.getPath(), updated.getMimeType(), ChangeType.PROPERTIES_UPDATED,
                                    mountPoint.getCurrentVirtualFileSystemUser()));
        }
        if (isProjectAfter && !isProjectBefore) {
            //Filter nested modules from multi-module project. For them no need to generate creating event.
            Project project = ((Project)updated);
            if (!(Boolean.parseBoolean(project.getPropertyValue("Maven Module")) &&
                ProjectType.fromValue(project.getProjectType()) != ProjectType.MULTI_MODULE)) {
                LOG.info("EVENT#project-created# PROJECT#{}# TYPE#{}#", updated.getName(), ((Project)updated).getProjectType());
            }

        }

        boolean wasJRebelPropertyUpdated = false;
        for (Property p : properties) {
            if (p.getName().equals("jrebel")) {
                wasJRebelPropertyUpdated = true;
                break;
            }
        }
        if (wasJRebelPropertyUpdated) {
            final String projectType = updated.hasProperty("vfs:projectType") ? updated.getPropertyValue("vfs:projectType") : null;
            //TODO need to organize both ProjectType enums from server and client side to use one shared ProjectType
            if (projectType != null && ("Servlet/JSP".equals(projectType) || "Spring".equals(projectType))) {
                String jRebelUsage = updated.hasProperty("jrebel") ? updated.getPropertyValue("jrebel") : "false";
                VirtualFileSystemUser user = mountPoint.getCurrentVirtualFileSystemUser();
                LOG.info("EVENT#jrebel-usage# WS#"
                         + EnvironmentContext.getCurrent().getVariable(EnvironmentContext.WORKSPACE_NAME).toString() + "# USER#"
                         + user.getUserId() + "# PROJECT#" + updated.getName() + "# TYPE#" + projectType + "# JREBEL#" + jRebelUsage + "#");
            }
        }
        return updated;
    }

    @Path("export/{folderId}")
    @Override
    public ContentStream exportZip(@PathParam("folderId") String folderId) throws IOException, VirtualFileSystemException {
        return idToVirtualFile(folderId).zip();
    }

    @Path("import/{parentId}")
    @Override
    public void importZip(@PathParam("parentId") String parentId, //
                          InputStream in, //
                          @DefaultValue("false") @QueryParam("overwrite") Boolean overwrite //
                         ) throws VirtualFileSystemException, IOException {
        final VirtualFile parent = idToVirtualFile(parentId);
        final boolean isProjectBefore = parent.isProject();
        parent.unzip(in, overwrite);
        final boolean isProjectAfter = parent.isProject();
        if (!isProjectBefore && isProjectAfter) {
            LOG.info("EVENT#project-created# PROJECT#{}# TYPE#{}#", parent.getName(),
                     parent.getPropertyValue("vfs:projectType"));
        }
    }

    @Path("downloadfile/{id}")
    @Override
    public Response downloadFile(@PathParam("id") String id) throws VirtualFileSystemException {
        final ContentStream content = getContent(id);
        return Response
                .ok(content.getStream(), content.getMimeType())
                .lastModified(content.getLastModificationDate())
                .header(HttpHeaders.CONTENT_LENGTH, Long.toString(content.getLength()))
                .header("Content-Disposition", "attachment; filename=\"" + content.getFileName() + '"')
                .build();
    }

    @Path("uploadfile/{parentId}")
    @Override
    public Response uploadFile(@PathParam("parentId") String parentId, //
                               java.util.Iterator<FileItem> formData //
                              ) throws IOException, VirtualFileSystemException {
        try {
            FileItem contentItem = null;
            String mediaType = null;
            String name = null;
            boolean overwrite = false;

            while (formData.hasNext()) {
                FileItem item = formData.next();
                if (!item.isFormField()) {
                    if (contentItem == null) {
                        contentItem = item;
                    } else {
                        throw new InvalidArgumentException("More then one upload file is found but only one should be. ");
                    }
                } else if ("mimeType".equals(item.getFieldName())) {
                    mediaType = item.getString().trim();
                } else if ("name".equals(item.getFieldName())) {
                    name = item.getString().trim();
                } else if ("overwrite".equals(item.getFieldName())) {
                    overwrite = Boolean.parseBoolean(item.getString().trim());
                }
            }

            if (contentItem == null) {
                throw new InvalidArgumentException("Cannot find file for upload. ");
            }
            if (name == null || name.isEmpty()) {
                name = contentItem.getName();
            }
            if (mediaType == null || mediaType.isEmpty()) {
                mediaType = contentItem.getContentType();
            }

            try {
                createFile(
                        parentId,
                        name,
                        mediaType == null ? MediaType.APPLICATION_OCTET_STREAM_TYPE : MediaType.valueOf(mediaType),
                        contentItem.getInputStream()
                          );
            } catch (ItemAlreadyExistException e) {
                if (!overwrite) {
                    throw new ItemAlreadyExistException("Unable upload file. Item with the same name exists. ");
                }

                final VirtualFile file = getVirtualFileByPath(idToVirtualFile(parentId).getPath() + '/' + name)
                        .updateContent(mediaType, contentItem.getInputStream(), null);
                if (listeners != null) {
                    listeners.notifyListeners(
                            new ChangeEvent(this, virtualFileToId(file), file.getPath(), file.getMediaType(), ChangeType.CONTENT_UPDATED,
                                            mountPoint.getCurrentVirtualFileSystemUser()));
                }
            }

            return Response.ok("", MediaType.TEXT_HTML).build();
        } catch (VirtualFileSystemException e) {
            HtmlErrorFormatter.sendErrorAsHTML(e);
            // never thrown
            throw e;
        } catch (IOException e) {
            HtmlErrorFormatter.sendErrorAsHTML(e);
            // never thrown
            throw e;
        }
    }

    @Path("downloadzip/{folderId}")
    @Override
    public Response downloadZip(@PathParam("folderId") String folderId) throws IOException, VirtualFileSystemException {
        final ContentStream zip = exportZip(folderId);
        return Response //
                .ok(zip.getStream(), zip.getMimeType()) //
                .lastModified(zip.getLastModificationDate()) //
                .header(HttpHeaders.CONTENT_LENGTH, Long.toString(zip.getLength())) //
                .header("Content-Disposition", "attachment; filename=\"" + zip.getFileName() + '"') //
                .build();
    }

    @Path("uploadzip/{parentId}")
    @Override
    public Response uploadZip(@PathParam("parentId") String parentId, //
                              Iterator<FileItem> formData) throws IOException, VirtualFileSystemException {
        try {
            FileItem contentItem = null;
            boolean overwrite = false;
            while (formData.hasNext()) {
                FileItem item = formData.next();
                if (!item.isFormField()) {
                    if (contentItem == null) {
                        contentItem = item;
                    } else {
                        throw new InvalidArgumentException("More then one upload file is found but only one should be. ");
                    }
                } else if ("overwrite".equals(item.getFieldName())) {
                    overwrite = Boolean.parseBoolean(item.getString().trim());
                }
            }
            if (contentItem == null) {
                throw new InvalidArgumentException("Cannot find file for upload. ");
            }
            importZip(parentId, contentItem.getInputStream(), overwrite);
            return Response.ok("", MediaType.TEXT_HTML).build();
        } catch (VirtualFileSystemException e) {
            HtmlErrorFormatter.sendErrorAsHTML(e);
            // never thrown
            throw e;
        } catch (IOException e) {
            HtmlErrorFormatter.sendErrorAsHTML(e);
            // never thrown
            throw e;
        }
    }

    @Path("watch/start/{projectId}")
    @Override
    public void startWatchUpdates(@PathParam("projectId") String projectId) throws VirtualFileSystemException {
        if (listeners == null) {
            throw new VirtualFileSystemException("EventListenerList is not configured properly. ");
        }
        final VirtualFile project = idToVirtualFile(projectId);
        if (!project.isProject()) {
            throw new InvalidArgumentException(String.format("Item '%s' is not a project. ", project.getPath()));
        }
        if (listeners.addEventListener(
                ProjectUpdateEventFilter.newFilter(this, project), new ProjectUpdateListener(projectId))) {
            List<Property> properties = new ArrayList<Property>(1);
            properties.add(new PropertyImpl("vfs:lastUpdateTime", "0"));
            project.updateProperties(properties, null);
        }
    }

    @Path("watch/stop/{projectId}")
    @Override
    public void stopWatchUpdates(@PathParam("projectId") String projectId) throws VirtualFileSystemException {
        if (listeners != null) {
            final VirtualFile project = idToVirtualFile(projectId);
            if (!project.isProject()) {
                return;
            }
            if (!listeners.removeEventListener(ProjectUpdateEventFilter.newFilter(this, project),
                                               new ProjectUpdateListener(projectId))) {
                throw new InvalidArgumentException(
                        String.format("Project '%s' is not under watching. ", project.getPath()));
            }
        }
    }

   /* ==================================================================== */

    VirtualFile getVirtualFileByPath(String path) throws VirtualFileSystemException {
        return mountPoint.getVirtualFile(path);
    }

    VirtualFile idToVirtualFile(String id) throws VirtualFileSystemException {
        if (rootId.equals(id)) {
            return mountPoint.getRoot();
        }
        final String path;
        try {
            path = new String(Base64.decodeBase64(id), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // Should never happen.
            throw new IllegalStateException(e.getMessage(), e);
        }
        try {
            return getVirtualFileByPath(path.substring(vfsId.length() + 1)); // see virtualFileToId(VirtualFile)
        } catch (ItemNotFoundException e) {
            // re-throw to have correct message
            throw new ItemNotFoundException(String.format("Object '%s' does not exists. ", id));
        }
    }

    String virtualFileToId(VirtualFile virtualFile) throws VirtualFileSystemException {
        if (virtualFile.isRoot()) {
            return rootId;
        }
        try {
            return Base64.encodeBase64URLSafeString((vfsId + ':' + virtualFile.getPath()).getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            // Should never happen.
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    public void addToIndex(String id) throws VirtualFileSystemException {
        VirtualFile virtualFile = idToVirtualFile(id);
        if (searcherProvider != null) {
            try {
                searcherProvider.getSearcher(mountPoint, true).add(virtualFile);
            } catch (IOException e) {
                LOG.error("Unable add to index. " + e.getMessage(), e);
            }
        }
    }
    
    private Item fromVirtualFile(VirtualFile virtualFile, boolean includePermissions, PropertyFilter propertyFilter)
            throws VirtualFileSystemException {
        return fromVirtualFile(virtualFile, includePermissions, propertyFilter, true);
    }

    private Item fromVirtualFile(VirtualFile virtualFile, boolean includePermissions, PropertyFilter propertyFilter, boolean addLinks)
            throws VirtualFileSystemException {
        final String id = virtualFileToId(virtualFile);
        final String name = virtualFile.getName();
        final String path = virtualFile.getPath();
        final boolean isRoot = virtualFile.isFolder() && virtualFile.isRoot();
        final String parentId = isRoot ? null : virtualFileToId(virtualFile.getParent());
        final String mediaType = virtualFile.getMediaType();
        final long created = virtualFile.getCreationDate();

        Item item;
        if (virtualFile.isFile()) {
            final boolean locked = virtualFile.isLocked();
            final long length = virtualFile.getLength();
            final long modified = virtualFile.getLastModificationDate();
            item = new FileImpl(vfsId, id, name, path, parentId, created, modified, FAKE_VERSION_ID, mediaType, length, locked,
                                virtualFile.getProperties(propertyFilter),
                                addLinks ? LinksHelper.createFileLinks(baseUri, EnvironmentContext.getCurrent().getVariable(EnvironmentContext.WORKSPACE_NAME).toString(), id, id, path, mediaType, locked, parentId) : null);
        } else {
            if (virtualFile.isProject()) {
                final String projectType = virtualFile.getPropertyValue("vfs:projectType");
                item = new ProjectImpl(vfsId, id, name, mediaType, path, parentId, created, virtualFile.getProperties(propertyFilter),
                                       addLinks ? LinksHelper.createProjectLinks(baseUri, EnvironmentContext.getCurrent().getVariable(EnvironmentContext.WORKSPACE_NAME).toString(), id, parentId) : null,
                                       projectType == null ? "default" : projectType);
            } else {
                item = new FolderImpl(vfsId, id, name, mediaType, path, parentId, created, virtualFile.getProperties(propertyFilter),
                                      addLinks ? LinksHelper.createFolderLinks(baseUri, EnvironmentContext.getCurrent().getVariable(EnvironmentContext.WORKSPACE_NAME).toString(), id, isRoot, parentId) : null);
            }
        }

        if (includePermissions) {
            VirtualFileSystemUser user = mountPoint.getCurrentVirtualFileSystemUser();
            VirtualFile current = virtualFile;
            while (current != null) {
                final AccessControlList objectPermissions = mountPoint.getACL(current);
                if (!objectPermissions.isEmpty()) {
                    Set<String> userPermissions = new HashSet<String>(4);
                    Set<BasicPermissions> permissionsSet =
                            objectPermissions.getPermissions(new PrincipalImpl(user.getUserId(), Principal.Type.USER));
                    if (permissionsSet != null) {
                        for (BasicPermissions basicPermission : permissionsSet) {
                            userPermissions.add(basicPermission.value());
                        }
                    }
                    permissionsSet =
                            objectPermissions.getPermissions(new PrincipalImpl(VirtualFileSystemInfo.ANY_PRINCIPAL, Principal.Type.USER));
                    if (permissionsSet != null) {
                        for (BasicPermissions basicPermission : permissionsSet) {
                            userPermissions.add(basicPermission.value());
                        }
                    }
                    for (String group : user.getGroups()) {
                        permissionsSet = objectPermissions.getPermissions(new PrincipalImpl(group, Principal.Type.GROUP));
                        if (permissionsSet != null) {
                            for (BasicPermissions basicPermission : permissionsSet) {
                                userPermissions.add(basicPermission.value());
                            }
                        }
                    }
                    item.setPermissions(userPermissions);
                    break;
                } else {
                    current = current.getParent();
                }
            }
            if (item.getPermissions() == null) {
                item.setPermissions(new HashSet<String>(Arrays.asList(BasicPermissions.ALL.value())));
            }
        }

        return item;
    }

    @Override
    public String toString() {
        return "LocalFileSystem{" +
               "vfsId='" + vfsId + '\'' +
               ", baseUri=" + baseUri +
               '}';
    }
}
