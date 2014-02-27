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
package org.exoplatform.ide.extension.heroku.client.apps;

import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.extension.heroku.client.HerokuClientBundle;
import org.exoplatform.ide.extension.heroku.client.HerokuExtension;
import org.exoplatform.ide.extension.heroku.client.control.AbstractHerokuControl;

/**
 * Control for managing Heroku applications.
 *
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Mar 14, 2012 5:15:04 PM anya $
 */
@RolesAllowed({"workspace/developer"})
public class ManageApplicationsControl extends AbstractHerokuControl {

    public ManageApplicationsControl() {
        super(HerokuExtension.LOCALIZATION_CONSTANT.listApplicationsControlId());
        setTitle(HerokuExtension.LOCALIZATION_CONSTANT.listApplicationsControlTitle());
        setPrompt(HerokuExtension.LOCALIZATION_CONSTANT.listApplicationsControlPrompt());
        setEvent(new ManageApplicationsEvent());
        setImages(HerokuClientBundle.INSTANCE.applicationsList(), HerokuClientBundle.INSTANCE.applicationsListDisabled());
    }

    /** @see org.exoplatform.ide.client.framework.control.IDEControl#initialize() */
    @Override
    public void initialize() {
        IDE.addHandler(VfsChangedEvent.TYPE, this);

        setVisible(true);
    }

    protected void refresh() {
        setEnabled(vfsInfo != null);
    }

}
