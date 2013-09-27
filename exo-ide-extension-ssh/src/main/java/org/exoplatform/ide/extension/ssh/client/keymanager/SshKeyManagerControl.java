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
package org.exoplatform.ide.extension.ssh.client.keymanager;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.extension.ssh.client.SshClientBundle;
import org.exoplatform.ide.extension.ssh.client.keymanager.event.ShowSshKeyManagerEvent;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: SshKeyManagerControl May 17, 2011 5:22:50 PM evgen $
 */
@RolesAllowed({"developer"})
public class SshKeyManagerControl extends SimpleControl implements IDEControl {
    /** Control ID. */
    public static final String ID = "Window/Ssh Key Manager";

    /** Control's title. */
    public static final String TITLE = "Ssh Key Manager";

    /** Control's prompt, when user hovers the mouse on it. */
    public static final String PROMPT = "Open Ssh Key Manager";

    public SshKeyManagerControl() {
        super(ID);
        setTitle(TITLE);
        setPrompt(PROMPT);
        setEvent(new ShowSshKeyManagerEvent());
        setImages(SshClientBundle.INSTANCE.sshKeyManager(), SshClientBundle.INSTANCE.sshKeyManagerDisabled());
    }

    /** @see org.exoplatform.ide.client.framework.control.IDEControl#initialize() */
    @Override
    public void initialize() {
        setVisible(true);
        setEnabled(true);
    }

}
