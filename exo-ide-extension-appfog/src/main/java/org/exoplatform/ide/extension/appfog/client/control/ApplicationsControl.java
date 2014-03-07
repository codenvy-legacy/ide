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
package org.exoplatform.ide.extension.appfog.client.control;

import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.extension.appfog.client.AppfogClientBundle;
import org.exoplatform.ide.extension.appfog.client.AppfogExtension;
import org.exoplatform.ide.extension.appfog.client.apps.ShowApplicationsEvent;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
@RolesAllowed({"workspace/developer"})
public class ApplicationsControl extends AbstractAppfogControl {
    public ApplicationsControl() {
        super(AppfogExtension.LOCALIZATION_CONSTANT.appsControlId());
        setTitle(AppfogExtension.LOCALIZATION_CONSTANT.appsControlTitle());
        setPrompt(AppfogExtension.LOCALIZATION_CONSTANT.appsControlPrompt());
        setEvent(new ShowApplicationsEvent());
        setImages(AppfogClientBundle.INSTANCE.appsList(), AppfogClientBundle.INSTANCE.appsListDisabled());
    }

    @Override
    public void initialize() {
        IDE.addHandler(VfsChangedEvent.TYPE, this);

        setVisible(true);
    }

    @Override
    protected void refresh() {
        setEnabled(vfsInfo != null);
    }
}
