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
import org.exoplatform.ide.git.client.remove.RemoveFilesEvent;

/**
 * Control is used to remove files from commit (added by add command) and work tree.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Apr 12, 2011 3:33:56 PM anya $
 */
@RolesAllowed({"workspace/developer"})
public class RemoveFilesControl extends GitControl {
    public RemoveFilesControl() {
        super(GitExtension.MESSAGES.removeControlId());
        setTitle(GitExtension.MESSAGES.removeControlTitle());
        setPrompt(GitExtension.MESSAGES.removeControlPrompt());
        setImages(GitClientBundle.INSTANCE.removeFiles(), GitClientBundle.INSTANCE.removeFilesDisabled());
        setEvent(new RemoveFilesEvent());
        setGroupName(GroupNames.COMMANDS);
    }
}
