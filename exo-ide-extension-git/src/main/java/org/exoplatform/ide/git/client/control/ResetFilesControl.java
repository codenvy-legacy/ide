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
import org.exoplatform.ide.git.client.reset.ResetFilesEvent;

/**
 * Control resets files from index (added by add command).
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Apr 13, 2011 4:00:45 PM anya $
 */
@RolesAllowed("developer")
public class ResetFilesControl extends GitControl {
    public ResetFilesControl() {
        super(GitExtension.MESSAGES.resetFilesControlId());
        setTitle(GitExtension.MESSAGES.resetFilesControlTitle());
        setPrompt(GitExtension.MESSAGES.resetFilesControlPrompt());
        setImages(GitClientBundle.INSTANCE.reset(), GitClientBundle.INSTANCE.resetDisabled());
        setEvent(new ResetFilesEvent());
        setGroupName(GroupNames.COMMANDS);
    }
}
