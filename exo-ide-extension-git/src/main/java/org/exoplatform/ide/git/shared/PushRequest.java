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
 * Request to update remote refs using local refs. In other words send changes from local repository to remote one.
 * 
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: PushRequest.java 22817 2011-03-22 09:17:52Z andrew00x $
 */
public class PushRequest extends GitRequest {
    /**
     * List of refspec to push.
     * <p/>
     * Expected form is "refs/heads/master:refs/heads/test". Push changes from local 'master' to remote 'test'.
     */
    private String[] refSpec;

    /**
     * Remote repository. URI or name is acceptable. If not specified then 'origin' will be used.
     */
    private String   remote;

    /**
     * Usually, the command refuses to update a remote ref that is not an ancestor of the local ref used to overwrite it. If this attribute
     * <code>true</code> disables the check. This can cause the remote repository to lose commits
     */
    private boolean  force;

    /**
     * Time (in seconds) to wait without data transfer occurring before aborting pushing data to remote repository.
     */
    private int      timeout;

    /**
     * @param refSpec list of refspec to push
     * @param remote remote repository. URI or name is acceptable. If not specified then 'origin' will be used
     * @param force force push operation
     * @param timeout time (in seconds) to wait without data transfer occurring before aborting pushing data to remote repository
     * @see #refSpec
     * @see #force
     */
    public PushRequest(String[] refSpec, String remote, boolean force, int timeout) {
        this.refSpec = refSpec;
        this.remote = remote;
        this.force = force;
        this.timeout = timeout;
    }

    /**
     * "Empty" push request. Corresponding setters used to setup required parameters.
     */
    public PushRequest() {
    }

    /**
     * @return list of refspec to push
     * @see #refSpec
     */
    public String[] getRefSpec() {
        return refSpec;
    }

    /**
     * @param refSpec list of refspec to push
     * @see #refSpec
     */
    public void setRefSpec(String[] refSpec) {
        this.refSpec = refSpec;
    }

    /**
     * @return remote repository. URI or name is acceptable. If not specified then 'origin' will be used
     */
    public String getRemote() {
        return remote;
    }

    /**
     * @param remote remote repository. URI or name is acceptable. If not specified then 'origin' will be used
     */
    public void setRemote(String remote) {
        this.remote = remote;
    }

    /**
     * @return force or not push operation
     * @see #force
     */
    public boolean isForce() {
        return force;
    }

    /**
     * @param force <code>true</code> if force push operation and <code>false</code> otherwise
     * @see #force
     */
    public void setForce(boolean force) {
        this.force = force;
    }

    /**
     * @param timeout time (in seconds) to wait without data transfer occurring before aborting pushing data to remote repository
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    /**
     * @return time (in seconds) to wait without data transfer occurring before aborting pushing data to remote repository
     */
    public int getTimeout() {
        return timeout;
    }
}
