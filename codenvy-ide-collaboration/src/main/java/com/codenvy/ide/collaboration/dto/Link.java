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
package com.codenvy.ide.collaboration.dto;

import com.codenvy.ide.dtogen.shared.*;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id: $
 */
@RoutingType(type = RoutableDto.NON_ROUTABLE_TYPE)
public interface Link extends ServerToClientDto, CompactJsonDto {
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

    @SerializationIndex(1)
    String getHref();

    @SerializationIndex(2)
    String getRel();

    @SerializationIndex(3)
    String getTypeLink();

}
