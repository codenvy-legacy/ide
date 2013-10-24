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
package com.codenvy.ide.factory.shared;

/**
 * Describe parameters for Codenvy Factory feature. Version of specification Codenvy Factory 1.0
 *
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: CodenvyFactorySpec10.java Nov 30, 2012 vetal $
 */
public interface FactorySpec10 {

    String FACTORY_VERSION    = "v";
    String VCS_TYPE           = "vcs";
    String VCS_URL            = "vcsurl";
    String COMMIT_ID          = "commitid";
    String VCS_INFO           = "vcsinfo";
    String VCS_BRANCH         = "vcsbranch";
    String PROJECT_TYPE       = "ptype";
    String PROJECT_NAME       = "pname";
    String OPEN_FILE          = "openfile";
    String ACTION             = "action";
    String WORKSPACE          = "wname";
    String ORG_ID             = "orgid";
    String AFFILIATE_ID       = "affiliateid";
    String PROFILE_ATTRIBUTES = "projectattributes";

    String CURRENT_VERSION = "1.0";

    interface ACTION_VALUES {
        String OPEN_PROJECT = "openproject";
    }
}
