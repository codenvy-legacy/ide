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
 * Request to create new tag.
 * 
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: TagCreateRequest.java 22811 2011-03-22 07:28:35Z andrew00x $
 */
public class TagCreateRequest extends GitRequest {
    /** Name of tag to create. */
    private String  name;

    /** Commit to make tag. If <code>null</code> then HEAD will be used. */
    private String  commit;

    /** Message for the tag. */
    private String  message;

    /** Force create tag. If tag with the same exists it will be replaced. */
    private boolean force;

    /**
     * @param name name of tag to create
     * @param commit commit to make tag
     * @param message message for the tag
     * @param forceUpdate force create tag operation
     */
    public TagCreateRequest(String name, String commit, String message, boolean forceUpdate) {
        this.name = name;
        this.commit = commit;
        this.message = message;
        this.force = forceUpdate;
    }

    /**
     * @param name name of tag to create
     * @param commit commit to make tag
     * @param message message for the tag
     */
    public TagCreateRequest(String name, String commit, String message) {
        this.name = name;
        this.commit = commit;
        this.message = message;
    }

    /**
     * "Empty" create tag request. Corresponding setters used to setup required parameters.
     */
    public TagCreateRequest() {
    }

    /** @return name of tag to create */
    public String getName() {
        return name;
    }

    /**
     * @param name name of tag to create
     */
    public void setName(String name) {
        this.name = name;
    }

    /** @return commit to make tag. If <code>null</code> then HEAD is used */
    public String getCommit() {
        return commit;
    }

    /**
     * @param commit commit to make tag. If <code>null</code> then HEAD is used
     */
    public void setCommit(String commit) {
        this.commit = commit;
    }

    /** @return message for tag */
    public String getMessage() {
        return message;
    }

    /**
     * @param message message for tag
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return force create tag operation
     * @see #force
     */
    public boolean isForce() {
        return force;
    }

    /**
     * @param force if <code>true</code> force create tag operation
     * @see #force
     */
    public void setForce(boolean force) {
        this.force = force;
    }
}
