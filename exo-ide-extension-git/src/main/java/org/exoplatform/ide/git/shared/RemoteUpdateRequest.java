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
package org.exoplatform.ide.git.shared;

/**
 * Request to update tracked repositories.
 * 
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: RemoteUpdateRequest.java 67651 2011-03-25 16:15:36Z andrew00x $
 */
public class RemoteUpdateRequest extends GitRequest {
    /** Remote name. */
    private String   name;

    /**
     * Updates for list of currently tracked branches.
     * 
     * @see #addBranches
     */
    private String[] branches;

    /**
     * If <code>true</code> {@link #branches} instead of replacing the list of currently tracked branches, added to that list.
     */
    private boolean  addBranches;

    /** Remote URLs to be added. */
    private String[] addUrl;

    /** Remote URLs to be removed. */
    private String[] removeUrl;

    /** Remote push URLs to be added. */
    private String[] addPushUrl;

    /** Remote push URLs to be removed. */
    private String[] removePushUrl;

    /**
     * @param name name of remote
     * @param branches updates for list of currently tracked branches
     * @param addBranches if <code>true</code> then <code>branches</code> instead of replacing the list of currently tracked branches, added
     *            to that list
     * @param addUrl remote URLs to be added
     * @param removeUrl remote URLs to be removed
     * @param addPushUrl remote push URLs to be added
     * @param removePushUrl remote push URLs to be removed
     */
    public RemoteUpdateRequest(String name, String[] branches, boolean addBranches, String[] addUrl, String[] removeUrl,
                               String[] addPushUrl, String[] removePushUrl) {
        this.name = name;
        this.branches = branches;
        this.addBranches = addBranches;
        this.addUrl = addUrl;
        this.removeUrl = removeUrl;
        this.addPushUrl = addPushUrl;
        this.removePushUrl = removePushUrl;
    }

    /**
     * "Empty" request for update remote. Corresponding setters used to setup required parameters.
     */
    public RemoteUpdateRequest() {
    }

    /**
     * @return remote name
     * @see #name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name remote name
     * @see #name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return list tracked branches
     * @see #branches
     */
    public String[] getBranches() {
        return branches;
    }

    /**
     * @param branches list tracked branches
     * @see #branches
     */
    public void setBranches(String[] branches) {
        this.branches = branches;
    }

    /**
     * @return if <code>true</code> then {@link #branches} instead of replacing the list of currently tracked branches, added to that list
     * @see #addBranches
     */
    public boolean isAddBranches() {
        return addBranches;
    }

    /**
     * @param addBranches if <code>true</code> then {@link #branches} instead of replacing the list of currently tracked branches, added to
     *            that list
     * @see #addBranches
     */
    public void setAddBranches(boolean addBranches) {
        this.addBranches = addBranches;
    }

    /** @return remote URLs to be added */
    public String[] getAddUrl() {
        return addUrl;
    }

    public void setAddUrl(String[] addUrl) {
        this.addUrl = addUrl;
    }

    /** @return remote URLs to be removed */
    public String[] getRemoveUrl() {
        return removeUrl;
    }

    /**
     * @param removeUrl URLs to be removed
     */
    public void setRemoveUrl(String[] removeUrl) {
        this.removeUrl = removeUrl;
    }

    /** @return remote push URLs to be added */
    public String[] getAddPushUrl() {
        return addPushUrl;
    }

    /**
     * @param addPushUrl push URLs to be added
     */
    public void setAddPushUrl(String[] addPushUrl) {
        this.addPushUrl = addPushUrl;
    }

    /** @return remote push URLs to be removed */
    public String[] getRemovePushUrl() {
        return removePushUrl;
    }

    /**
     * @param removePushUrl remote push URLs to be removed
     */
    public void setRemovePushUrl(String[] removePushUrl) {
        this.removePushUrl = removePushUrl;
    }
}
