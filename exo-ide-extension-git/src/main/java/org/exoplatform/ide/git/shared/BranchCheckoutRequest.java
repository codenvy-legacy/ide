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
 * Request to checkout a branch to the working tree.
 * 
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: BranchCheckoutRequest.java 21623 2011-03-17 12:14:42Z andrew00x $
 */
public class BranchCheckoutRequest extends GitRequest {
    /** Name of branch to checkout. */
    private String  name;

    /**
     * The name of a commit at which to start the new branch. If <code>null</code> the HEAD will be used. Has sense if {@link #createNew} is
     * <code>true</code>.
     */
    private String  startPoint;

    /**
     * If <code>true</code> then create a new branch named {@link #name} and start it at {@link #startPoint}. If <code>false</code> and
     * there is no branch with name {@link #name} corresponding exception will be thrown.
     */
    private boolean createNew;

    /**
     * @param name name of branch to checkout
     * @param startPoint name of a commit at which to start the new branch
     * @param createNew if <code>true</code> then create a new branch named {@link #name} and start it at {@link #startPoint} or to the HEAD
     *            if {@link #startPoint} is not set. If <code>false</code> and there is no branch with name {@link #name} corresponding
     *            exception will be thrown
     */
    public BranchCheckoutRequest(String name, String startPoint, boolean createNew) {
        this.name = name;
        this.startPoint = startPoint;
        this.createNew = createNew;
    }

    /**
     * "Empty" request to checkout branch. Corresponding setters used to setup required behavior.
     */
    public BranchCheckoutRequest() {
    }

    /** @return name of branch to checkout */
    public String getName() {
        return name;
    }

    /**
     * @param name name of branch to checkout
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return name of a commit at which to start the new branch. If <code>null</code> the HEAD will be used
     */
    public String getStartPoint() {
        return startPoint;
    }

    /**
     * @param startPoint name of a commit at which to start the new branch. If <code>null</code> the HEAD will be used
     */
    public void setStartPoint(String startPoint) {
        this.startPoint = startPoint;
    }

    /**
     * @return if <code>true</code> then create a new branch named {@link #name} and start it at {@link #startPoint} or to the HEAD if
     *         {@link #startPoint} is not set. If <code>false</code> and there is no branch with name {@link #name} corresponding exception
     *         will be thrown
     */
    public boolean isCreateNew() {
        return createNew;
    }

    /**
     * @param createNew should create new branch named {@link #name} and checkout it or not. Exception will be thrown in there in no branch
     *            named {@link #name}
     */
    public void setCreateNew(boolean createNew) {
        this.createNew = createNew;
    }
}
