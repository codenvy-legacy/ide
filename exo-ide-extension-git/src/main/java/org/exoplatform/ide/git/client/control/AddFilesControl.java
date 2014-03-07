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
import org.exoplatform.ide.git.client.add.AddFilesEvent;

/**
 * Control for adding changes to index (temporary storage).
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Mar 29, 2011 4:23:20 PM anya $
 */
@RolesAllowed({"workspace/developer"})
public class AddFilesControl extends GitControl {
    public AddFilesControl() {
        super(GitExtension.MESSAGES.addControlId());
        setTitle(GitExtension.MESSAGES.addControlTitle());
        setPrompt(GitExtension.MESSAGES.addControlTitle());
        setEvent(new AddFilesEvent());
        setImages(GitClientBundle.INSTANCE.addToIndex(), GitClientBundle.INSTANCE.addToIndexDisabled());
        setDelimiterBefore(true);
        setGroupName(GroupNames.COMMANDS);
    }
}
