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
package org.exoplatform.ide.extension.cloudbees.client.account;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.extension.cloudbees.client.CloudBeesClientBundle;
import org.exoplatform.ide.extension.cloudbees.client.CloudBeesExtension;
import org.exoplatform.ide.extension.cloudbees.client.login.LoginEvent;

/**
 * Control for switching between CloudBees accounts.
 *
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Sep 5, 2012 5:02:29 PM anya $
 */
@RolesAllowed({"workspace/developer"})
public class SwitchAccountControl extends SimpleControl implements IDEControl, VfsChangedHandler {
    private static final String ID = CloudBeesExtension.LOCALIZATION_CONSTANT.controlSwitchAccountId();

    private static final String TITLE = CloudBeesExtension.LOCALIZATION_CONSTANT.controlSwitchAccountTitle();

    private static final String PROMPT = CloudBeesExtension.LOCALIZATION_CONSTANT.controlSwitchAccountPrompt();

    /** @param id */
    public SwitchAccountControl() {
        super(ID);
        setTitle(TITLE);
        setPrompt(PROMPT);
        setImages(CloudBeesClientBundle.INSTANCE.switchAccount(), CloudBeesClientBundle.INSTANCE.switchAccountDisabled());
        setEvent(new LoginEvent(null, null));
    }

    /** @see org.exoplatform.ide.client.framework.control.IDEControl#initialize() */
    @Override
    public void initialize() {
        IDE.addHandler(VfsChangedEvent.TYPE, this);

        setVisible(true);
    }

    /** @see org.exoplatform.ide.client.framework.application.event.VfsChangedHandler#onVfsChanged(org.exoplatform.ide.client.framework
     * .application.event.VfsChangedEvent) */
    @Override
    public void onVfsChanged(VfsChangedEvent event) {
        setEnabled(event.getVfsInfo() != null);
    }

}
