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
package com.codenvy.ide.factory.client;

/**
 * Describe parameters for Codenvy Factory feature. Version of specification Codenvy Factory 1.0
 *
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: CodenvyFactorySpec10.java Nov 30, 2012 vetal $
 */
public class FactorySpec10 {

    /** Mandatory parameter. Version for Codenvy Factory API. */
    public final static String VERSION_PARAMETER = "v";

    public final static String CURRENT_VERSION   = "1.0";

    /** Mandatory parameter. Locations of sources in Version Control System. */
    public final static String VCS_URL           = "vcsurl";

    /** Mandatory parameter. Version Control System. */
    public final static String VCS               = "vcs";

    /** Default value of VCS parameter. */
    public final static String DEFAULT_VCS       = "Git";

    /** Mandatory parameter. Latest commit ID. */
    public final static String COMMIT_ID         = "idcommit";

    /**
     * Optional parameter for project name in file system, if not set we try detect it from VCS_URL param.
     * <p/>
     * e.g. for "git@github.com:exodev/ide.git" project name will be "ide".
     */
    public final static String PROJECT_NAME      = "pname";

    /** Mandatory parameter. Workspace name. */
    public final static String WORKSPACE_NAME    = "wname";

    /** Optional parameter. By default will be use {@link DEFAULT_ACTION} value. */
    public final static String ACTION_PARAMETER  = "action";

    public final static String DEFAULT_ACTION    = "openproject";

    /** Optional parameter for project type, if not set we try detect it. */
    public final static String PROJECT_TYPE      = "ptype";

    /** Optional parameter for opening specified file after cloning.  */
    public final static String FILE_TO_OPEN      = "openfile";

    /** Optional parameter for keeping vcs information.  */
    public final static String KEEP_VCS_INFO      = "keepvcsinfo";

    /** Optional parameter for checkout branches */
    public final static String BRANCH_TO_CHECKOUT = "gitbranch";

}
