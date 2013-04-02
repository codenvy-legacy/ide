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
package org.exoplatform.ide.vfs.shared;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface Link {
    // Folder
    String REL_CHILDREN        = "children";
    String REL_TREE            = "tree";
    String REL_CREATE_PROJECT  = "create-project";
    String REL_CREATE_FOLDER   = "create-folder";
    String REL_CREATE_FILE     = "create-file";
    String REL_UPLOAD_FILE     = "upload-file";
    String REL_EXPORT          = "export";
    String REL_IMPORT          = "import";
    String REL_DOWNLOAD_ZIP    = "download-zip";
    String REL_UPLOAD_ZIP      = "upload-zip";
    // File
    String REL_CURRENT_VERSION = "current-version";
    String REL_VERSION_HISTORY = "version-history";
    String REL_CONTENT         = "content";
    String REL_DOWNLOAD_FILE   = "download-file";
    String REL_CONTENT_BY_PATH = "content-by-path";
    String REL_UNLOCK          = "unlock";
    String REL_LOCK            = "lock";
    // Common
    String REL_PARENT          = "parent";
    String REL_DELETE          = "delete";
    String REL_MOVE            = "move";
    String REL_COPY            = "copy";
    String REL_SELF            = "self";
    String REL_ITEM            = "item";
    String REL_ITEM_BY_PATH    = "item-by-path";
    String REL_ACL             = "acl";
    String REL_RENAME          = "rename";
    String REL_SEARCH          = "search";
    String REL_SEARCH_FORM     = "search-form";

    String getHref();

    void setHref(String href);

    String getRel();

    void setRel(String rel);

    String getType();

    void setType(String type);
}
