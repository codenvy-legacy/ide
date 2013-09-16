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
import org.exoplatform.ide.git.client.push.PushToRemoteEvent;

/**
 * Control for pushing changes from local repository to remote one.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Apr 4, 2011 9:27:56 AM anya $
 */
@RolesAllowed("developer")
public class PushToRemoteControl extends GitControl {
    public PushToRemoteControl() {
        super(GitExtension.MESSAGES.pushControlId());
        setTitle(GitExtension.MESSAGES.pushControlTitle());
        setPrompt(GitExtension.MESSAGES.pushControlPrompt());
        setImages(GitClientBundle.INSTANCE.push(), GitClientBundle.INSTANCE.pushDisabled());
        setEvent(new PushToRemoteEvent());
        setDelimiterBefore(true);
    }
}
