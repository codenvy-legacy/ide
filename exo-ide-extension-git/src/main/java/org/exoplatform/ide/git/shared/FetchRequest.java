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
 * Request to fetch data from remote repository.
 * 
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: FetchRequest.java 22817 2011-03-22 09:17:52Z andrew00x $
 */
public class FetchRequest extends GitRequest {
    /**
     * List of refspec to fetch.
     * <p/>
     * Expected form is:
     * <ul>
     * <li>refs/heads/featured:refs/remotes/origin/featured - branch 'featured' from remote repository will be fetched to
     * 'refs/remotes/origin/featured'.</li>
     * <li>featured - remote branch name. In this case {@link #remote} must be remote name but not uri.</li>
     * </ul>
     * <p/>
     * If either <code>null</code> or empty array then default remote settings of repository will be used.
     */
    private String[] refSpec;

    /** Remote name or uri. If <code>null</code> then 'origin' will be used. */
    private String   remote;

    /** Remove refs in local branch if they are removed in remote branch. */
    private boolean  removeDeletedRefs;

    /**
     * Time (in seconds) to wait without data transfer occurring before aborting fetching data from remote repository.
     */
    private int      timeout;

    /**
     * @param refSpec list of refspec to fetch
     * @param remote remote name. If <code>null</code> then 'origin' will be used
     * @param removeDeletedRefs remove or not refs in local branch if they are removed in remote branch
     * @param timeout time (in seconds) to wait without data transfer occurring before aborting fetching data from remote repository
     */
    public FetchRequest(String[] refSpec, String remote, boolean removeDeletedRefs, int timeout) {
        this.refSpec = refSpec;
        this.remote = remote;
        this.removeDeletedRefs = removeDeletedRefs;
        this.timeout = timeout;
    }

    /**
     * "Empty" fetch request. Corresponding setters used to setup required parameters.
     */
    public FetchRequest() {
    }

    /** @return list of refspec to fetch */
    public String[] getRefSpec() {
        return refSpec;
    }

    /**
     * @param refSpec list of refspec to fetch
     */
    public void setRefSpec(String[] refSpec) {
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
     * @return <code>true</code> if local refs must be deleted if they deleted in remote repository and <code>false</code> otherwise
     */
    public boolean isRemoveDeletedRefs() {
        return removeDeletedRefs;
    }

    /**
     * @param removeDeletedRefs <code>true</code> if local refs must be deleted if they deleted in remote repository and <code>false</code>
     *            otherwise
     */
    public void setRemoveDeletedRefs(boolean removeDeletedRefs) {
        this.removeDeletedRefs = removeDeletedRefs;
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
