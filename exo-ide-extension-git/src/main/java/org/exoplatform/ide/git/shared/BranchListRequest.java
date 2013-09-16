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
 * @version $Id: BranchListRequest.java 22811 2011-03-22 07:28:35Z andrew00x $
 */
public class BranchListRequest extends GitRequest {
    /**
     * Show both remote and local branches. <br/>
     * Corresponds to -a option in C git.
     */
    public static final String LIST_ALL    = "a";
    /**
     * Show both remote branches. <br/>
     * Corresponds to -r option in C git.
     */
    public static final String LIST_REMOTE = "r";

    public static final String LIST_LOCAL  = null;

    /** Branches list mode. */
    private String             listMode;

    /** Create list branches request that will retrieved local branches. */
    public BranchListRequest() {
    }

    /**
     * Create list branch request with specified list mode. Parameter <code>listMode</code> may be either 'a' or 'r'. If 'a' is specified
     * then all branches (local and remote) will be displayed. If 'r' is specified then remote branches only will be displayed. May be
     * <code>null</code> also in this case only local branches displayed. This is default behavior.
     * 
     * @param listMode list branch mode
     */
    public BranchListRequest(String listMode) {
        setListMode(listMode);
    }

    /** @return branches list mode */
    public String getListMode() {
        return listMode;
    }

    /**
     * @param listMode may be either 'a' or 'r'. If 'a' is specified then all branches (local and remote) will be displayed. If 'r' is
     *            specified then remote branches only will be displayed. May be <code>null</code> also in this case only local branches
     *            displayed
     */
    public void setListMode(String listMode) {
        this.listMode = listMode;
    }
}
