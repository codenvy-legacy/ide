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
package org.exoplatform.ide.git.server.github;


import org.exoplatform.ide.git.shared.Collaborators;
import org.exoplatform.ide.git.shared.GitHubUser;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: Collaborators.java Aug 6, 2012
 */
public class CollaboratorsImpl implements Collaborators {

    private List<GitHubUser> collaborators;

    /** @see org.exoplatform.ide.extension.samples.shared.Collaborators#getCollaborators() */
    @Override
    public List<GitHubUser> getCollaborators() {
        if (collaborators == null)
            collaborators = new ArrayList<GitHubUser>();
        return collaborators;
    }

    /** @see org.exoplatform.ide.extension.samples.shared.Collaborators#setCollaborators(java.util.List) */
    @Override
    public void setCollaborators(List<GitHubUser> collaborators) {
        this.collaborators = collaborators;
    }

}
