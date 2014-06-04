/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
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
