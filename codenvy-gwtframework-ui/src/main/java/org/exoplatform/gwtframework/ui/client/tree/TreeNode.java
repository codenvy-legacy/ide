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
package org.exoplatform.gwtframework.ui.client.tree;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class TreeNode {

    /** List of children */
    private List<TreeNode> children = new ArrayList<TreeNode>();

    /**
     * URL to Icon
     * If icon is not set then default icons are used from TreeRecord.Images ( for collections and simple items )
     */
    private String icon;

    /** Name of a tree node */
    private String name;

    /** Is Folder status */
    private boolean isFolder = true;

    /** Entry used for storing any object which is binded with this tree node */
    private Object entry;

    public TreeNode(String name) {
        this.name = name;
    }

    public TreeNode(String name, String icon) {
        this.name = name;
        this.icon = icon;
    }

    public TreeNode(String name, Object entry) {
        this.name = name;
        this.entry = entry;
    }

    public TreeNode(String name, Object entry, boolean isFolder) {
        this.name = name;
        this.entry = entry;
        this.isFolder = isFolder;
    }

    public TreeNode(String name, String icon, Object entry) {
        this.name = name;
        this.icon = icon;
        this.entry = entry;
    }

    /** @return list of children */
    public List<TreeNode> getChildren() {
        return children;
    }

    /** @return url to icon */
    public String getIcon() {
        return icon;
    }

    /** @return name of tree node */
    public String getName() {
        return name;
    }

    /** @return is folder status */
    public boolean isFolder() {
        return isFolder;
    }

    /**
     * Set Is Folder status
     *
     * @param isFolder
     */
    public void setIsFolder(boolean isFolder) {
        this.isFolder = isFolder;
    }

    /** @return entry */
    public Object getEntry() {
        return entry;
    }

    /**
     * Set entry
     *
     * @param entry
     */
    public void setEntry(Object entry) {
        this.entry = entry;
    }

}
