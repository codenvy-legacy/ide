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
import org.exoplatform.ide.git.client.GitClientBundle;
import org.exoplatform.ide.git.client.GitExtension;
import org.exoplatform.ide.git.client.pull.PullEvent;

/**
 * Control for pulling data from remote repository.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Apr 20, 2011 12:03:32 PM anya $
 */
@RolesAllowed({"workspace/developer"})
public class PullControl extends GitControl {
    public PullControl() {
        super(GitExtension.MESSAGES.pullControlId());
        setTitle(GitExtension.MESSAGES.pullControlTitle());
        setPrompt(GitExtension.MESSAGES.pullControlPrompt());
        setEvent(new PullEvent());
        setImages(GitClientBundle.INSTANCE.pull(), GitClientBundle.INSTANCE.pullDisabled());
    }

}
