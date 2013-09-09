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
 * Request to add remote configuration {@link #name} for repository at {@link #url}.
 * 
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: RemoteAddRequest.java 67651 2011-03-25 16:15:36Z andrew00x $
 */
public class RemoteAddRequest extends GitRequest {
    /** Remote name. */
    private String   name;

    /** Repository url. */
    private String   url;

    /**
     * List of tracked branches in remote repository. If not set then track all branches.
     */
    private String[] branches;

    /**
     * @param name remote name
     * @param url repository url
     * @param branches list of tracked branches in remote repository. If not set then track all branches
     */
    public RemoteAddRequest(String name, String url, String[] branches) {
        this.name = name;
        this.url = url;
        this.branches = branches;
    }

    /**
     * @param name remote name
     * @param url repository url
     */
    public RemoteAddRequest(String name, String url) {
        this.name = name;
        this.url = url;
    }

    /**
     * "Empty" request for create remote configuration. Corresponding setters used to setup required parameters.
     */
    public RemoteAddRequest() {
    }

    /** @return remote name */
    public String getName() {
        return name;
    }

    /**
     * @param name remote name
     */
    public void setName(String name) {
        this.name = name;
    }

    /** @return repository url */
    public String getUrl() {
        return url;
    }

    /**
     * @param url repository url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return list of tracked branches in remote repository
     * @see #branches
     */
    public String[] getBranches() {
        return branches;
    }

    /**
     * @param branches list of tracked branches in remote repository
     * @see #branches
     */
    public void setBranches(String[] branches) {
        this.branches = branches;
    }
}
