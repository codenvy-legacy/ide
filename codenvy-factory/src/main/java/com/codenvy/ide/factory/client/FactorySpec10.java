/*
 * Copyright (C) 2012 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
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

}
