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
import org.exoplatform.ide.git.client.remote.ShowRemotesEvent;

/**
 * Control is used to view the list of remote repositories.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Apr 18, 2011 10:21:02 AM anya $
 */
@RolesAllowed("developer")
public class RemotesControl extends GitControl {
    public RemotesControl() {
        super(GitExtension.MESSAGES.remotesControlId());
        setTitle(GitExtension.MESSAGES.remotesControlTitle());
        setPrompt(GitExtension.MESSAGES.remotesControlPrompt());
        setImages(GitClientBundle.INSTANCE.remotes(), GitClientBundle.INSTANCE.remotesDisabled());
        setEvent(new ShowRemotesEvent());
    }
}
