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
import org.exoplatform.ide.git.client.clone.CloneRepositoryEvent;

/**
 * Control for cloning remote repository to local one.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Mar 22, 2011 3:49:23 PM anya $
 */
@RolesAllowed("developer")
public class CloneRepositoryControl extends GitControl {

    public CloneRepositoryControl() {
        super(GitExtension.MESSAGES.cloneControlId());
        setTitle(GitExtension.MESSAGES.cloneControlTitle());
        setPrompt(GitExtension.MESSAGES.cloneControlPrompt());
        setImages(GitClientBundle.INSTANCE.cloneRepo(), GitClientBundle.INSTANCE.cloneRepoDisabled());
        setEvent(new CloneRepositoryEvent());
        setEnableState(EnableState.BEFORE_INIT);
        setGroupName(GroupNames.ACTIONS);
    }


    @Override
    protected void updateControlState() {
        setEnabled(vfsInfo != null);
        setVisible(vfsInfo != null);
    }

}
