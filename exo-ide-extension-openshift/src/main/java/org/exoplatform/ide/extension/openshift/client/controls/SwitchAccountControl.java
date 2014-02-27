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
package org.exoplatform.ide.extension.openshift.client.controls;

import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.extension.openshift.client.OpenShiftClientBundle;
import org.exoplatform.ide.extension.openshift.client.OpenShiftExtension;
import org.exoplatform.ide.extension.openshift.client.login.SwitchAccountEvent;

/**
 * Control to switch OpenShift account.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.com">Artem Zatsarynnyy</a>
 * @version $Id: SwitchAccountControl.java Feb 7, 2012 2:11:44 PM azatsarynnyy $
 */
@RolesAllowed({"workspace/developer"})
public class SwitchAccountControl extends AbstractOpenShiftControl {

    public SwitchAccountControl() {
        super(OpenShiftExtension.LOCALIZATION_CONSTANT.switchAccountControlId());
        setTitle(OpenShiftExtension.LOCALIZATION_CONSTANT.switchAccountControlSwitchTitle());
        setPrompt(OpenShiftExtension.LOCALIZATION_CONSTANT.switchAccountControlSwitchPrompt());
        setEvent(new SwitchAccountEvent());
        setImages(OpenShiftClientBundle.INSTANCE.switchAccount(), OpenShiftClientBundle.INSTANCE.switchAccountDisabled());
    }

    /** @see org.exoplatform.ide.client.framework.control.IDEControl#initialize() */
    @Override
    public void initialize() {
        IDE.addHandler(VfsChangedEvent.TYPE, this);

        setVisible(true);
    }

    /**
     *
     */
    protected void refresh() {
        setEnabled(vfsInfo != null);
    }

}
