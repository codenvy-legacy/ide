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
package org.exoplatform.ide.vfs.shared;

public class Lock {
    private String owner;

    private String lockToken;

    private int timeout;

    /**
     * @param owner
     *         user who is owner of the lock
     * @param lockToken
     *         lock token
     * @param timeout
     *         lock timeout
     */
    public Lock(String owner, String lockToken, int timeout) {
        this.owner = owner;
        this.lockToken = lockToken;
        this.timeout = timeout;
    }

    /** @return the owner */
    public String getOwner() {
        return owner;
    }

    /** @return the lockToken */
    public String getLockToken() {
        return lockToken;
    }

    /** @return the timeout */
    public int getTimeout() {
        return timeout;
    }

    /**
     * @param timeout
     *         the timeout to set
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}
