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
 * Request to delete branch.
 * 
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: BranchDeleteRequest.java 22811 2011-03-22 07:28:35Z andrew00x $
 */
public class BranchDeleteRequest extends GitRequest {
    /** Name of branch to delete. */
    private String  name;

    /**
     * If <code>true</code> delete branch {@link #name} even if it is not fully merged. It is corresponds to -D options in C git.
     */
    private boolean force;

    /**
     * @param name name of branch to delete
     * @param force if <code>true</code> delete branch {@link #name} even if it is not fully merged
     */
    public BranchDeleteRequest(String name, boolean force) {
        this.name = name;
        this.force = force;
    }

    /**
     * "Empty" request to delete branch. Corresponding setters used to setup required behavior.
     */
    public BranchDeleteRequest() {
    }

    /** @return name of branch to delete */
    public String getName() {
        return name;
    }

    /**
     * @param name name of branch to delete
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return if <code>true</code> then delete branch {@link #name} even if it is not fully merged
     */
    public boolean isForce() {
        return force;
    }

    /**
     * @param force if <code>true</code> delete branch {@link #name} even if it is not fully merged
     */
    public void setForce(boolean force) {
        this.force = force;
    }
}
