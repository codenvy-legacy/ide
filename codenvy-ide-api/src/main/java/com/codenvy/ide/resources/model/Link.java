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
package com.codenvy.ide.resources.model;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: Link.java 79579 2012-02-17 13:27:25Z andrew00x $
 */
public class Link {
    // Folder
    public static String REL_CHILDREN = "children";

    public static String REL_TREE = "tree";

    public static String REL_CREATE_PROJECT = "create-project";

    public static String REL_CREATE_FOLDER = "create-folder";

    public static String REL_CREATE_FILE = "create-file";

    public static String REL_UPLOAD_FILE = "upload-file";

    public static String REL_EXPORT = "export";

    public static String REL_IMPORT = "import";

    public static String REL_DOWNLOAD_ZIP = "download-zip";

    public static String REL_UPLOAD_ZIP = "upload-zip";

    // File
    public static String REL_CURRENT_VERSION = "current-version";

    public static String REL_VERSION_HISTORY = "version-history";

    public static String REL_CONTENT = "content";

    public static String REL_DOWNLOAD_FILE = "download-file";

    public static String REL_CONTENT_BY_PATH = "content-by-path";

    public static String REL_UNLOCK = "unlock";

    public static String REL_LOCK = "lock";

    // Common
    public static String REL_PARENT = "parent";

    public static String REL_DELETE = "delete";

    public static String REL_MOVE = "move";

    public static String REL_COPY = "copy";

    public static String REL_SELF = "self";

    public static String REL_ITEM = "item";

    public static String REL_ITEM_BY_PATH = "item-by-path";

    public static String REL_ACL = "acl";

    public static String REL_RENAME = "rename";

    public static String REL_SEARCH = "search";

    public static String REL_SEARCH_FORM = "search-form";

    /** URL of resource. */
    private String href;

    /** Produced media type of resource described by this link. */
    private String type;

    /** Relation attribute of link. Client may use it for choice links to retrieve specific info about resource. */
    private String rel;

    public Link(String href, String rel, String type) {
        this.href = href;
        this.rel = rel;
        this.type = type;
    }

    public Link() {
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getRel() {
        return rel;
    }

    public void setRel(String rel) {
        this.rel = rel;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    /** @see java.lang.Object#toString() */
    @Override
    public String toString() {
        return "Link [href=" + href + ", type=" + type + ", rel=" + rel + ']';
    }
}
