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
 * Request to pull (fetch and merge) changes from remote repository to local branch.
 * 
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: PullRequest.java 22811 2011-03-22 07:28:35Z andrew00x $
 */
public class PullRequest extends GitRequest {
    // Refspec for pull.
    private String refSpec;

    /** Remote name. If <code>null</code> then 'origin' will be used. */
    private String remote;

    /**
     * Time (in seconds) to wait without data transfer occurring before aborting fetching data from remote repository.
     */
    private int    timeout;

    /**
     * @param timeout time (in seconds) to wait without data transfer occurring before aborting fetching data from remote repository
     */
    public PullRequest(String remote, String refSpec, int timeout) {
        this.remote = remote;
        this.refSpec = refSpec;
        this.timeout = timeout;
    }

    /**
     * "Empty" pull request. Corresponding setters used to setup required parameters.
     */
    public PullRequest() {
    }

    /**
     * @return refspec to fetch
     * @see #refSpec
     */
    public String getRefSpec() {
        return refSpec;
    }

    /**
     * @param refSpec refspec to fetch
     * @see #refSpec
     */
    public void setRefSpec(String refSpec) {
        this.refSpec = refSpec;
    }

    /** @return remote name. If <code>null</code> then 'origin' will be used */
    public String getRemote() {
        return remote;
    }

    /**
     * @param remote remote name
     */
    public void setRemote(String remote) {
        this.remote = remote;
    }

    /**
     * @return time (in seconds) to wait without data transfer occurring before aborting fetching data from remote repository
     */
    public int getTimeout() {
        return timeout;
    }

    /**
     * @param timeout time (in seconds) to wait without data transfer occurring before aborting fetching data from remote repository
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}
