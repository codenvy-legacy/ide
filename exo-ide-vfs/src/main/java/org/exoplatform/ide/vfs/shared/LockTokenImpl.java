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

/**
 * Representation of LockToken used to interaction with client via JSON. A lock token is a opaque token, which
 * identifies a
 * particular lock. Is used for replace existed lock or performing actions on locked object.
 *
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: LockTokenImpl.java 80595 2012-03-27 09:12:25Z azatsarynnyy $
 */
public class LockTokenImpl implements LockToken {
    private String token;

    public LockTokenImpl() {
    }

    /**
     * @param token
     *         string representation of lock token
     * @throws IllegalArgumentException
     *         if <code>token == null</code>
     */
    public LockTokenImpl(String token) {
        if (token == null) {
            throw new IllegalArgumentException("Lock token may not be null. ");
        }
        this.token = token;
    }

    /** @see org.exoplatform.ide.vfs.shared.LockToken#getLockToken() */
    @Override
    public String getLockToken() {
        return token;
    }

    /** @see org.exoplatform.ide.vfs.shared.LockToken#setLockToken(java.lang.String) */
    @Override
    public void setLockToken(String token) {
        if (token == null) {
            throw new IllegalArgumentException("Lock token may not be null. ");
        }
        this.token = token;
    }

    /** @see java.lang.Object#equals(java.lang.Object) */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if ((obj == null) || (obj.getClass() != this.getClass()) || (token == null)) {
            return false;
        }
        LockTokenImpl otherLockToken = (LockTokenImpl)obj;
        return token.equals(otherLockToken.token);
    }

    /** @see java.lang.Object#hashCode() */
    @Override
    public int hashCode() {
        int hash = 8;
        hash = hash * 31 + token.hashCode();
        return hash;
    }

    /** @see java.lang.Object#toString() */
    @Override
    public String toString() {
        return "LockToken [token=" + token + ']';
    }
}
