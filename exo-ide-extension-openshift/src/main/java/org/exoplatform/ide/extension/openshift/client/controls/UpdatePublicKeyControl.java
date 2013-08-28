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
import org.exoplatform.ide.extension.openshift.client.key.UpdatePublicKeyEvent;

/**
 * Control is used for updating SSH public key on OpenShift.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Jun 21, 2011 12:15:28 PM anya $
 */
@RolesAllowed("developer")
public class UpdatePublicKeyControl extends AbstractOpenShiftControl {

    public UpdatePublicKeyControl() {
        super(OpenShiftExtension.LOCALIZATION_CONSTANT.updatePublicKeyControlId());
        setTitle(OpenShiftExtension.LOCALIZATION_CONSTANT.updatePublicKeyControlTitle());
        setPrompt(OpenShiftExtension.LOCALIZATION_CONSTANT.updatePublicKeyControlPrompt());
        setImages(OpenShiftClientBundle.INSTANCE.updateKeyControl(),
                  OpenShiftClientBundle.INSTANCE.updateKeyControlDisabled());
        setEvent(new UpdatePublicKeyEvent());
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
