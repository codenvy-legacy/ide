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
import org.exoplatform.ide.extension.openshift.client.OpenShiftClientBundle;
import org.exoplatform.ide.extension.openshift.client.OpenShiftExtension;
import org.exoplatform.ide.extension.openshift.client.create.CreateApplicationEvent;

/**
 * Control for creating new application on OpenShift.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Jun 7, 2011 5:35:09 PM anya $
 */
@RolesAllowed("developer")
public class CreateApplicationControl extends AbstractOpenShiftControl {

    public CreateApplicationControl() {
        super(OpenShiftExtension.LOCALIZATION_CONSTANT.createApplicationControlId());
        setTitle(OpenShiftExtension.LOCALIZATION_CONSTANT.createApplicationControlTitle());
        setPrompt(OpenShiftExtension.LOCALIZATION_CONSTANT.createApplicationControlPrompt());
        setImages(OpenShiftClientBundle.INSTANCE.createApplicationControl(),
                  OpenShiftClientBundle.INSTANCE.createApplicationControlDisabled());
        setEvent(new CreateApplicationEvent());
    }

    @Override
    public void initialize() {
        super.initialize();

        //Temporary hide menu for feature considering if create application functionality will be in next version of IDE
        setVisible(false);
    }
}
