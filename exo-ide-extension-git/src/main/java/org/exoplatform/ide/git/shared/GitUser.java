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
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: GitUser.java 22811 2011-03-22 07:28:35Z andrew00x $
 */
public class GitUser {
    private String name;
    private String email;

    /**
     * @param name name
     * @param email email
     */
    public GitUser(String name, String email) {
        if (name == null)
            throw new NullPointerException("name");
        this.name = name;
        this.email = email;
    }

    /** @param name */
    public GitUser(String name) {
        this(name, "");
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    /** @see java.lang.Object#hashCode() */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = hash * 31 + ((email == null) ? 0 : email.hashCode());
        hash = hash * 31 + name.hashCode();
        return hash;
    }

    /** @see java.lang.Object#equals(java.lang.Object) */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        GitUser other = (GitUser)obj;
        if (email == null) {
            if (other.email != null)
                return false;
        } else if (!email.equals(other.email))
            return false;
        return name.equals(other.name);
    }

    /** @see java.lang.Object#toString() */
    @Override
    public String toString() {
        return "GitUser [name=" + name + ", email=" + email + "]";
    }
}
