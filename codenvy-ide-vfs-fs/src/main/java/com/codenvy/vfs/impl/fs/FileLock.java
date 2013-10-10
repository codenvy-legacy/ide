/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 *  [2012] - [2013] Codenvy, S.A. 
 *  All Rights Reserved.
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
package com.codenvy.vfs.impl.fs;

/**
 * Lock of VirtualFile.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 */
public class FileLock {
    private final String lockToken;
    private final long   expired;
    private final int    hash;

    public FileLock(String lockToken, long expired) {
        this.lockToken = lockToken;
        this.expired = expired;
        int hash = 7;
        hash = hash * 31 + lockToken.hashCode();
        this.hash = hash;
    }

    public String getLockToken() {
        return lockToken;
    }

    public long getExpired() {
        return expired;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FileLock)) {
            return false;
        }
        return lockToken.equals(((FileLock)o).lockToken);
    }

    @Override
    public int hashCode() {
        return hash;
    }

    @Override
    public String toString() {
        return "FileLock{" +
               "lockToken='" + lockToken + '\'' +
               ", expired=" + expired +
               '}';
    }
}
