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
 * Request to create new branch.
 * 
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: BranchCreateRequest.java 22811 2011-03-22 07:28:35Z andrew00x $
 */
public class BranchCreateRequest extends GitRequest {
    /** Name of branch to create. */
    private String name;

    /**
     * The name of a commit at which to start the new branch. If <code>null</code> the HEAD will be used.
     */
    private String startPoint;

    /**
     * @param name name of branch to be created
     * @param startPoint name of a commit at which to start the new branch. If <code>null</code> the HEAD will be used
     */
    public BranchCreateRequest(String name, String startPoint) {
        this.name = name;
        this.startPoint = startPoint;
    }

    /**
     * "Empty" request to create branch. Corresponding setters used to setup required behavior.
     */
    public BranchCreateRequest() {
    }

    /** @return name of branch to be created */
    public String getName() {
        return name;
    }

    /**
     * @param name name of branch to be created
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
}
