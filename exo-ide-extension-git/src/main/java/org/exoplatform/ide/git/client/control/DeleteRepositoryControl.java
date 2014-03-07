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
import org.exoplatform.ide.git.client.delete.DeleteRepositoryEvent;

/**
 * Control for deleting Fit repository (only folder ".git" and its contents is deleted).
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Jun 21, 2011 5:40:27 PM anya $
 */
@RolesAllowed({"workspace/developer"})
public class DeleteRepositoryControl extends GitControl {

    public DeleteRepositoryControl() {
        super(GitExtension.MESSAGES.deleteControlId());
        setTitle(GitExtension.MESSAGES.deleteControlTitle());
        setPrompt(GitExtension.MESSAGES.deleteControlPrompt());
        setImages(GitClientBundle.INSTANCE.deleteRepo(), GitClientBundle.INSTANCE.deleteRepoDisabled());
        setEvent(new DeleteRepositoryEvent());
        setVisible(true);
        setGroupName(GroupNames.ACTIONS);
    }

}
