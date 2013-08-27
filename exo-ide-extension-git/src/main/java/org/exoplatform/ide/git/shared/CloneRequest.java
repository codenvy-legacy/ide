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
 * Clone repository to {@link #workingDir}.
 * 
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: CloneRequest.java 22817 2011-03-22 09:17:52Z andrew00x $
 */
public class CloneRequest extends GitRequest {
    /** URI of repository to be cloned. */
    private String   remoteUri;

    /**
     * List of refspec to fetch in cloned repository.
     * <p/>
     * Expected form is "refs/heads/featured".
     */
    private String[] branchesToFetch;

    /** Work directory for cloning. */
    private String   workingDir;

    /** Remote name. If <code>null</code> then 'origin' will be used. */
    private String   remoteName;

    /**
     * Time (in seconds) to wait without data transfer occurring before aborting fetching data from remote repository.
     */
    private int      timeout;

    /**
     * @param remoteUri URI of repository to be cloned
     * @param branchesToFetch list of remote branches to fetch in cloned repository
     * @param workingDir work directory for cloning
     * @param remoteName remote name
     * @param timeout time (in seconds) to wait without data transfer occurring before aborting fetching data from remote repository
     */
    public CloneRequest(String remoteUri, String[] branchesToFetch, String workingDir, String remoteName, int timeout) {
        this.remoteUri = remoteUri;
        this.branchesToFetch = branchesToFetch;
        this.workingDir = workingDir;
        this.remoteName = remoteName;
        this.timeout = timeout;
    }

    /**
     * @param remoteUri URI of repository to be cloned
     * @param workingDir work directory for cloning
     */
    public CloneRequest(String remoteUri, String workingDir) {
        this.remoteUri = remoteUri;
        this.workingDir = workingDir;
    }

    /**
     * "Empty" request to clone repository. Corresponding setters used to setup required behavior.
     */
    public CloneRequest() {
    }

    /** @return URI of repository to be cloned */
    public String getRemoteUri() {
        return remoteUri;
    }

    /**
     * @param remoteUri URI of repository to be cloned
     */
    public void setRemoteUri(String remoteUri) {
        this.remoteUri = remoteUri;
    }

    /** @return list of remote branches to fetch in cloned repository */
    public String[] getBranchesToFetch() {
        return branchesToFetch;
    }

    /**
     * @param branchesToFetch list of remote branches to fetch in cloned repository
     */
    public void setBranchesToFetch(String[] branchesToFetch) {
        this.branchesToFetch = branchesToFetch;
    }

    /** @return work directory for cloning */
    public String getWorkingDir() {
        return workingDir;
    }

    /**
     * @param workingDir work directory for cloning
     */
    public void setWorkingDir(String workingDir) {
        this.workingDir = workingDir;
    }

    /** @return remote name. If <code>null</code> then 'origin' will be used */
    public String getRemoteName() {
        return remoteName;
    }

    /**
     * @param remoteName remote name. If <code>null</code> then 'origin' will be used
     */
    public void setRemoteName(String remoteName) {
        this.remoteName = remoteName;
    }

    /**
     * @return time (in seconds) to wait without data transfer occurring before aborting fetching data from remote repository. If 0 then
     *         default timeout may be used. This is implementation specific
     */
    public int getTimeout() {
        return timeout;
    }

    /**
     * @param timeout time (in seconds) to wait without data transfer occurring before aborting fetching data from remote repository. If 0
     *            then default timeout may be used. This is implementation specific
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}
