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
package org.exoplatform.ide.extension.heroku.client.control;

import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.project.ProjectClosedEvent;
import org.exoplatform.ide.client.framework.project.ProjectClosedHandler;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedHandler;
import org.exoplatform.ide.client.framework.project.api.PropertiesChangedEvent;
import org.exoplatform.ide.client.framework.project.api.PropertiesChangedHandler;
import org.exoplatform.ide.extension.heroku.client.HerokuClientBundle;
import org.exoplatform.ide.extension.heroku.client.HerokuExtension;
import org.exoplatform.ide.extension.heroku.client.create.CreateApplicationEvent;
import org.exoplatform.ide.vfs.client.model.ProjectModel;

/**
 * Control for creating new application on Heroku.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: May 26, 2011 2:27:45 PM anya $
 */
@RolesAllowed("developer")
public class CreateApplicationControl extends AbstractHerokuControl implements ProjectOpenedHandler, ProjectClosedHandler,
                                                                   PropertiesChangedHandler {

    public CreateApplicationControl() {
        super(HerokuExtension.LOCALIZATION_CONSTANT.createApplicationControlId());
        setTitle(HerokuExtension.LOCALIZATION_CONSTANT.createApplicationControlTitle());
        setPrompt(HerokuExtension.LOCALIZATION_CONSTANT.createApplicationControlPrompt());
        setEvent(new CreateApplicationEvent());
        setImages(HerokuClientBundle.INSTANCE.createApplication(),
                  HerokuClientBundle.INSTANCE.createApplicationDisabled());
    }

    /** @see org.exoplatform.ide.extension.heroku.client.control.AbstractHerokuControl#initialize() */
    @Override
    public void initialize() {
        IDE.addHandler(ProjectOpenedEvent.TYPE, this);
        IDE.addHandler(ProjectClosedEvent.TYPE, this);
        IDE.addHandler(PropertiesChangedEvent.TYPE, this);
        setVisible(true);
    }

    /**
     * @see org.exoplatform.ide.client.framework.project.ProjectClosedHandler#onProjectClosed(org.exoplatform.ide.client.framework
     *      .project.ProjectClosedEvent)
     */
    @Override
    public void onProjectClosed(ProjectClosedEvent event) {
        setEnabled(false);
    }

    /**
     * @see org.exoplatform.ide.client.framework.project.ProjectOpenedHandler#onProjectOpened(org.exoplatform.ide.client.framework
     *      .project.ProjectOpenedEvent)
     */
    @Override
    public void onProjectOpened(ProjectOpenedEvent event) {
        setEnabled(event.getProject() != null && HerokuExtension.canBeDeployedToHeroku(event.getProject()));
    }

    @Override
    public void onPropertiesChanged(PropertiesChangedEvent event) {
        ProjectModel project = event.getProject();
        while (project.getProject() != null) {
            project = project.getProject();
        }
        setEnabled(HerokuExtension.canBeDeployedToHeroku(project));
    }
}
