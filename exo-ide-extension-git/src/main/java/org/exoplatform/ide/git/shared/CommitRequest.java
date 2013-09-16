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
 * Request to commit current state of index in new commit.
 * 
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: CommitRequest.java 22811 2011-03-22 07:28:35Z andrew00x $
 */
public class CommitRequest extends GitRequest {
    /** Commit message. */
    private String  message;

    /**
     * Need automatically stage files that have been modified and deleted, but not new files.
     */
    private boolean all;

    /** Parameter responsible for amending of previous commit. */
    private boolean amend;

    /**
     * @param message commit message
     */
    public CommitRequest(String message, boolean all, boolean amend) {
        this.message = message;
        this.all = all;
        this.amend = amend;
    }

    /**
     * @param message commit message
     */
    public CommitRequest(String message) {
        this.message = message;
    }

    /**
     * "Empty" commit request. Corresponding setters used to setup required parameters.
     */
    public CommitRequest() {
    }

    /** @return commit message */
    public String getMessage() {
        return message;
    }

    /**
     * @param message commit message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return <code>true</code> if need automatically stage files that have been modified and deleted
     */
    public boolean isAll() {
        return all;
    }

    /**
     * @param all if <code>true</code> automatically stage files that have been modified and deleted
     */
    public void setAll(boolean all) {
        this.all = all;
    }

    /** @return <code>true</code> in case when commit is amending a previous commit. */
    public boolean isAmend() {
        return amend;
    }

    /**
     * @param amend if <code>true</code> it means that previous commit must be amended.
     */
    public void setAmend(boolean amend) {
        this.amend = amend;
    }
}
