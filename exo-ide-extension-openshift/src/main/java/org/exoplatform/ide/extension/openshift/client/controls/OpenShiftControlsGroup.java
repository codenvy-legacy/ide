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

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.extension.openshift.client.OpenShiftClientBundle;
import org.exoplatform.ide.extension.openshift.client.OpenShiftExtension;

/**
 * Group control for OpenShift controls
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Jun 9, 2011 4:58:20 PM anya $
 */
@RolesAllowed({"workspace/developer"})
public class OpenShiftControlsGroup extends SimpleControl implements IDEControl {

    public OpenShiftControlsGroup() {
        super(OpenShiftExtension.LOCALIZATION_CONSTANT.openShiftControlId());
        setTitle(OpenShiftExtension.LOCALIZATION_CONSTANT.openShiftControlTitle());
        setPrompt(OpenShiftExtension.LOCALIZATION_CONSTANT.openShiftControlPrompt());
        setImages(OpenShiftClientBundle.INSTANCE.openShiftControl(),
                  OpenShiftClientBundle.INSTANCE.openShiftControlDisabled());
    }

    /** @see org.exoplatform.ide.client.framework.control.IDEControl#initialize() */
    @Override
    public void initialize() {
        setVisible(true);
        setEnabled(true);
    }

}
