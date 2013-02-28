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
package org.exoplatform.ide.vfs.server.util;

import org.exoplatform.ide.vfs.server.VirtualFileSystemFactory;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.shared.Link;
import org.exoplatform.ide.vfs.shared.LinkImpl;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

/**
 * Helper class for creation links. See {@link org.exoplatform.ide.vfs.shared.Item#getLinks()}
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class LinksHelper
{
   public static Map<String, Link> createFileLinks(URI baseUri,
                                                   String vfsId,
                                                   String itemId,
                                                   String latestVersionId,
                                                   String itemPath,
                                                   String mediaType,
                                                   boolean locked,
                                                   String parentId) throws VirtualFileSystemException
   {
      final Map<String, Link> links = new LinkedHashMap<String, Link>();
      final UriBuilder baseUriBuilder = UriBuilder.fromUri(baseUri).path(VirtualFileSystemFactory.class, "getFileSystem");

      links.put(Link.REL_SELF, //
         new LinkImpl(createURI(baseUriBuilder.clone(), vfsId, "item", itemId), Link.REL_SELF, MediaType.APPLICATION_JSON));

      links.put(Link.REL_ACL, //
         new LinkImpl(createURI(baseUriBuilder.clone(), vfsId, "acl", itemId), Link.REL_ACL, MediaType.APPLICATION_JSON));

      links.put(Link.REL_CONTENT, //
         new LinkImpl(createURI(baseUriBuilder.clone(), vfsId, "content", itemId), Link.REL_CONTENT, mediaType));

      links.put(Link.REL_DOWNLOAD_FILE, //
         new LinkImpl(createURI(baseUriBuilder.clone(), vfsId, "downloadfile", itemId), Link.REL_DOWNLOAD_FILE, mediaType));

      links.put(Link.REL_CONTENT_BY_PATH, //
         new LinkImpl(createURI(baseUriBuilder.clone(), vfsId, "contentbypath", itemPath.substring(1)),
            Link.REL_CONTENT_BY_PATH, mediaType));

      links.put(Link.REL_VERSION_HISTORY, //
         new LinkImpl(createURI(baseUriBuilder.clone(), vfsId, "version-history", itemId), Link.REL_VERSION_HISTORY,
            MediaType.APPLICATION_JSON));

      // Always have only one versioning since is not supported.
      links.put(Link.REL_CURRENT_VERSION, //
         new LinkImpl(createURI(baseUriBuilder.clone(), vfsId, "item", latestVersionId), Link.REL_CURRENT_VERSION,
            MediaType.APPLICATION_JSON));

      if (locked)
      {
         links.put(Link.REL_UNLOCK, //
            new LinkImpl(createURI(baseUriBuilder.clone(), vfsId, "unlock", itemId, "lockToken", "[lockToken]"),
               Link.REL_UNLOCK, null));
      }
      else
      {
         links.put(Link.REL_LOCK, //
            new LinkImpl(createURI(baseUriBuilder.clone(), vfsId, "lock", itemId), Link.REL_LOCK, MediaType.APPLICATION_JSON));
      }

      links.put(Link.REL_DELETE, //
         new LinkImpl(locked
            ? createURI(baseUriBuilder.clone(), vfsId, "delete", itemId, "lockToken", "[lockToken]")
            : createURI(baseUriBuilder.clone(), vfsId, "delete", itemId),
            Link.REL_DELETE, null));

      links.put(Link.REL_COPY, //
         new LinkImpl(createURI(baseUriBuilder.clone(), vfsId, "copy", itemId, "parentId", "[parentId]"), Link.REL_COPY,
            MediaType.APPLICATION_JSON));

      links.put(Link.REL_MOVE, //
         new LinkImpl(locked
            ? createURI(baseUriBuilder.clone(), vfsId, "move", itemId, "parentId", "[parentId]", "lockToken", "[lockToken]")
            : createURI(baseUriBuilder.clone(), vfsId, "move", itemId, "parentId", "[parentId]"),
            Link.REL_MOVE, MediaType.APPLICATION_JSON));

      links.put(Link.REL_PARENT, //
         new LinkImpl(createURI(baseUriBuilder.clone(), vfsId, "item", parentId), Link.REL_PARENT, MediaType.APPLICATION_JSON));

      links.put(Link.REL_RENAME, //
         new LinkImpl(locked
            ? createURI(baseUriBuilder.clone(), vfsId, "rename", itemId, "newname", "[newname]", "mediaType", "[mediaType]", "lockToken", "[lockToken]")
            : createURI(baseUriBuilder.clone(), vfsId, "rename", itemId, "newname", "[newname]", "mediaType", "[mediaType]"),
            Link.REL_RENAME, MediaType.APPLICATION_JSON));

      return links;
   }

   public static Map<String, Link> createFolderLinks(URI baseUri,
                                                     String vfsId,
                                                     String itemId,
                                                     boolean isRoot,
                                                     String parentId)
      throws VirtualFileSystemException
   {
      final UriBuilder baseUriBuilder = UriBuilder.fromUri(baseUri).path(VirtualFileSystemFactory.class, "getFileSystem");
      final Map<String, Link> links = createBaseFolderLinks(baseUriBuilder, vfsId, itemId, isRoot, parentId);
      links.put(Link.REL_CREATE_PROJECT, //
         new LinkImpl(createURI(baseUriBuilder.clone(), vfsId, "project", itemId, "name", "[name]", "type", "[type]"),
            Link.REL_CREATE_PROJECT, MediaType.APPLICATION_JSON));
      return links;
   }

   public static Map<String, Link> createProjectLinks(URI baseUri,
                                                      String vfsId,
                                                      String itemId,
                                                      String parentId)
   {
      final UriBuilder baseUriBuilder = UriBuilder.fromUri(baseUri).path(VirtualFileSystemFactory.class, "getFileSystem");
      return createBaseFolderLinks(baseUriBuilder, vfsId, itemId, false, parentId);
   }

   private static Map<String, Link> createBaseFolderLinks(UriBuilder baseUriBuilder,
                                                          String vfsId,
                                                          String id,
                                                          boolean isRoot,
                                                          String parentId)
   {
      final Map<String, Link> links = new LinkedHashMap<String, Link>();

      links.put(Link.REL_SELF, //
         new LinkImpl(createURI(baseUriBuilder.clone(), vfsId, "item", id), Link.REL_SELF, MediaType.APPLICATION_JSON));

      links.put(Link.REL_ACL, //
         new LinkImpl(createURI(baseUriBuilder.clone(), vfsId, "acl", id), Link.REL_ACL, MediaType.APPLICATION_JSON));

      if (!isRoot)
      {
         links.put(Link.REL_PARENT, //
            new LinkImpl(createURI(baseUriBuilder.clone(), vfsId, "item", parentId), Link.REL_PARENT, MediaType.APPLICATION_JSON));

         links.put(Link.REL_DELETE, //
            new LinkImpl(createURI(baseUriBuilder.clone(), vfsId, "delete", id), Link.REL_DELETE, null));

         links.put(Link.REL_COPY, //
            new LinkImpl(createURI(baseUriBuilder.clone(), vfsId, "copy", id, "parentId", "[parentId]"), Link.REL_COPY,
               MediaType.APPLICATION_JSON));

         links.put(Link.REL_MOVE, //
            new LinkImpl(createURI(baseUriBuilder.clone(), vfsId, "move", id, "parentId", "[parentId]"), Link.REL_MOVE,
               MediaType.APPLICATION_JSON));

         links.put(
            Link.REL_RENAME, //
            new LinkImpl(createURI(baseUriBuilder.clone(), vfsId, "rename", id, "newname", "[newname]", "mediaType", "[mediaType]"),
               Link.REL_RENAME, MediaType.APPLICATION_JSON));
      }

      links.put(Link.REL_CHILDREN, //
         new LinkImpl(createURI(baseUriBuilder.clone(), vfsId, "children", id), Link.REL_CHILDREN, MediaType.APPLICATION_JSON));

      links.put(Link.REL_TREE, //
         new LinkImpl(createURI(baseUriBuilder.clone(), vfsId, "tree", id), Link.REL_TREE, MediaType.APPLICATION_JSON));

      links.put(Link.REL_CREATE_FOLDER, //
         new LinkImpl(createURI(baseUriBuilder.clone(), vfsId, "folder", id, "name", "[name]"), Link.REL_CREATE_FOLDER,
            MediaType.APPLICATION_JSON));

      links.put(Link.REL_CREATE_FILE, //
         new LinkImpl(createURI(baseUriBuilder.clone(), vfsId, "file", id, "name", "[name]"), Link.REL_CREATE_FILE,
            MediaType.APPLICATION_JSON));

      links.put(Link.REL_UPLOAD_FILE, //
         new LinkImpl(createURI(baseUriBuilder.clone(), vfsId, "uploadfile", id), Link.REL_UPLOAD_FILE, MediaType.TEXT_HTML));

      links.put(Link.REL_EXPORT, //
         new LinkImpl(createURI(baseUriBuilder.clone(), vfsId, "export", id), Link.REL_EXPORT, "application/zip"));

      links.put(Link.REL_IMPORT, //
         new LinkImpl(createURI(baseUriBuilder.clone(), vfsId, "import", id), Link.REL_IMPORT, "application/zip"));

      links.put(Link.REL_DOWNLOAD_ZIP, //
         new LinkImpl(createURI(baseUriBuilder.clone(), vfsId, "downloadzip", id), Link.REL_DOWNLOAD_ZIP, "application/zip"));

      links.put(Link.REL_UPLOAD_ZIP, //
         new LinkImpl(createURI(baseUriBuilder.clone(), vfsId, "uploadzip", id), Link.REL_UPLOAD_ZIP, MediaType.TEXT_HTML));

      return links;
   }

   public static Map<String, Link> createUrlTemplates(URI baseUri, String vfsId)
   {
      final Map<String, Link> templates = new LinkedHashMap<String, Link>();
      final UriBuilder baseUriBuilder = UriBuilder.fromUri(baseUri).path(VirtualFileSystemFactory.class, "getFileSystem");

      templates.put(Link.REL_ITEM,
         new LinkImpl(createURI(baseUriBuilder.clone(), vfsId, "item", "[id]"), Link.REL_ITEM, MediaType.APPLICATION_JSON));

      templates.put(Link.REL_ITEM_BY_PATH,
         new LinkImpl(createURI(baseUriBuilder.clone(), vfsId, "itembypath", "[path]"), Link.REL_ITEM_BY_PATH,
            MediaType.APPLICATION_JSON));

      templates.put(Link.REL_TREE,
         new LinkImpl(createURI(baseUriBuilder.clone(), vfsId, "tree", "[id]"), Link.REL_TREE, MediaType.APPLICATION_JSON));

      templates.put(Link.REL_CREATE_FILE,
         new LinkImpl(createURI(baseUriBuilder.clone(), vfsId, "file", "[parentId]", "name", "[name]"),
            Link.REL_CREATE_FILE, MediaType.APPLICATION_JSON));

      templates.put(Link.REL_CREATE_FOLDER,
         new LinkImpl(createURI(baseUriBuilder.clone(), vfsId, "folder", "[parentId]", "name", "[name]"),
            Link.REL_CREATE_FOLDER, MediaType.APPLICATION_JSON));

      templates.put(Link.REL_CREATE_PROJECT,
         new LinkImpl(createURI(baseUriBuilder.clone(), vfsId, "project", "[parentId]", "name", "[name]", "type", "[type]"),
            Link.REL_CREATE_PROJECT, MediaType.APPLICATION_JSON));

      templates.put(Link.REL_COPY,
         new LinkImpl(createURI(baseUriBuilder.clone(), vfsId, "copy", "[id]", "parentId", "[parentId]"),
            Link.REL_COPY, MediaType.APPLICATION_JSON));

      templates.put(Link.REL_MOVE,
         new LinkImpl(createURI(baseUriBuilder.clone(), vfsId, "move", "[id]", "parentId", "[parentId]", "lockToken",
            "[lockToken]"), Link.REL_MOVE, MediaType.APPLICATION_JSON));

      templates.put(Link.REL_LOCK,
         new LinkImpl(createURI(baseUriBuilder.clone(), vfsId, "lock", "[id]"), Link.REL_LOCK, MediaType.APPLICATION_JSON));

      templates.put(Link.REL_UNLOCK,
         new LinkImpl(createURI(baseUriBuilder.clone(), vfsId, "unlock", "[id]", "lockToken", "[lockToken]"),
            Link.REL_UNLOCK, null));

      templates.put(
         Link.REL_SEARCH_FORM,
         new LinkImpl(createURI(baseUriBuilder.clone(), vfsId, "search", null, "maxItems", "[maxItems]", "skipCount",
            "[skipCount]", "propertyFilter", "[propertyFilter]"), Link.REL_SEARCH_FORM, MediaType.APPLICATION_JSON));

      templates.put(
         Link.REL_SEARCH,
         new LinkImpl(createURI(baseUriBuilder.clone(), vfsId, "search", null, "statement", "[statement]", "maxItems",
            "[maxItems]", "skipCount", "[skipCount]"), Link.REL_SEARCH, MediaType.APPLICATION_JSON));

      return templates;
   }

   private static String createURI(UriBuilder baseUriBuilder, String vfsId, String rel, String id, String... query)
   {
      final UriBuilder myUriBuilder = baseUriBuilder.path(rel);
      if (id != null)
      {
         myUriBuilder.path(id);
      }
      if (query != null && query.length > 0)
      {
         for (int i = 0; i < query.length; i++)
         {
            String name = query[i];
            String value = i < query.length ? query[++i] : "";
            myUriBuilder.queryParam(name, value);
         }
      }

      return myUriBuilder.build(vfsId).toString();
   }

   private static String createURI(UriBuilder baseUriBuilder, String vfsId, String rel, String id)
   {
      final UriBuilder myUriBuilder = baseUriBuilder.path(rel);
      if (id != null)
      {
         myUriBuilder.path(id);
      }
      return myUriBuilder.build(vfsId).toString();
   }

   private LinksHelper()
   {
   }
}
