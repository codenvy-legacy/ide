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
package org.exoplatform.ide.git.client.control;

import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.control.GroupNames;
import org.exoplatform.ide.git.client.GitClientBundle;
import org.exoplatform.ide.git.client.GitExtension;
import org.exoplatform.ide.git.client.branch.ShowBranchesEvent;

/**
 * Control is responsible to open view with branches, where user can create new, checkout or delete.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Apr 7, 2011 5:39:13 PM anya $
 */
@RolesAllowed({"workspace/developer"})
public class BranchesControl extends GitControl {
    public BranchesControl() {
        super(GitExtension.MESSAGES.branchesControlId());
        setTitle(GitExtension.MESSAGES.branchesControlTitle());
        setPrompt(GitExtension.MESSAGES.branchesControlPrompt());
        setEvent(new ShowBranchesEvent());
        setImages(GitClientBundle.INSTANCE.branches(), GitClientBundle.INSTANCE.branchesDisabled());
        setGroupName(GroupNames.COMMANDS);
    }
}
