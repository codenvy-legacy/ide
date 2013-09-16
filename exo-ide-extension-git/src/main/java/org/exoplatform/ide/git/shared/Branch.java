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
 * Git branch description.
 * 
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: Branch.java 22811 2011-03-22 07:28:35Z andrew00x $
 */
public class Branch {
    /** Full name of branch, e.g. 'refs/heads/master'. */
    private String  name;

    /** Display name of branch, e.g. 'refs/heads/master' -> 'master'. */
    private String  displayName;

    /** <code>true</code> if branch is checked out in working tree (active) */
    private boolean active;

    /** <code>true</code> if branch is a remote branch */
    private boolean remote;

    /**
     * @param name the name of branch
     * @param active indicate is current branch active or not
     * @param displayName short name of branch. Full name 'refs/heads/master' may be represented by short name 'master'
     */
    public Branch(String name, boolean active, String displayName, boolean remote) {
        this.name = name;
        this.active = active;
        this.displayName = displayName;
        this.remote = remote;
    }

    /** Corresponding setters used to setup required fields. */
    public Branch() {
    }

    /** @return full name of branch, e.g. 'refs/heads/master' */
    public String getName() {
        return name;
    }

    /** @return <code>true</code> if branch is checked out and false otherwise */
    public boolean isActive() {
        return active;
    }

    /** @return display name of branch, e.g. 'refs/heads/master' -> 'master' */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * @param name full name of branch, e.g. 'refs/heads/master'
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @param displayName name of branch, e.g. 'refs/heads/master' -> 'master'
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * @param active <code>true</code> if branch is checked out and false otherwise
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * @return <code>true</code> if branch is a remote branch
     */
    public boolean isRemote() {
        return remote;
    }

    /**
     * @param remote <code>true</code> if branch is a remote branch
     */
    public void setRemote(boolean remote) {
        this.remote = remote;
    }

    /** @see java.lang.Object#toString() */
    @Override
    public String toString() {
        return "Branch [displayName=" + displayName + ", name=" + name + ", active=" + active + ", remote=" + remote + "]";
    }
}
