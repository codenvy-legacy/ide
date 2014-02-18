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
package org.exoplatform.ide.extension.googleappengine.client.create;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.project.ProjectClosedEvent;
import org.exoplatform.ide.client.framework.project.ProjectClosedHandler;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedHandler;
import org.exoplatform.ide.client.framework.project.api.PropertiesChangedEvent;
import org.exoplatform.ide.client.framework.project.api.PropertiesChangedHandler;
import org.exoplatform.ide.extension.googleappengine.client.GAEClientBundle;
import org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineExtension;
import org.exoplatform.ide.vfs.client.model.ProjectModel;

/**
 * Control for creating new application on Google App Engine.
 * 
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: May 21, 2012 2:11:53 PM anya $
 */
@RolesAllowed({"workspace/developer"})
public class CreateApplicationControl extends SimpleControl implements IDEControl, ProjectOpenedHandler, ProjectClosedHandler,
                                                           PropertiesChangedHandler {

    private static final String ID     = "PaaS/Google App Engine/Create";

    private static final String TITLE  = GoogleAppEngineExtension.GAE_LOCALIZATION.createApplicationControlTitle();

    private static final String PROMPT = GoogleAppEngineExtension.GAE_LOCALIZATION.createApplicationControlPrompt();

    public CreateApplicationControl() {
        super(ID);
        IDE.addHandler(ProjectClosedEvent.TYPE, this);
        IDE.addHandler(ProjectOpenedEvent.TYPE, this);
        IDE.addHandler(PropertiesChangedEvent.TYPE, this);
        setTitle(TITLE);
        setPrompt(PROMPT);
        setImages(GAEClientBundle.INSTANCE.createApplicationConrtol(), GAEClientBundle.INSTANCE.createApplicationConrtolDisabled());
        setEvent(new CreateApplicationEvent());
    }

    /** @see org.exoplatform.ide.client.framework.control.IDEControl#initialize() */
    @Override
    public void initialize() {
        setVisible(true);
        setEnabled(false);
    }

    @Override
    public void onProjectClosed(ProjectClosedEvent event) {
        setEnabled(false);
    }

    @Override
    public void onProjectOpened(ProjectOpenedEvent event) {
        if (event.getProject() != null && GoogleAppEngineExtension.isAppEngineProject(event.getProject())) {
            setEnabled(true);
        }
    }

    @Override
    public void onPropertiesChanged(PropertiesChangedEvent event) {
        ProjectModel project = event.getProject();
        while (project.getProject() != null) {
            project = project.getProject();
        }
        setEnabled(GoogleAppEngineExtension.isAppEngineProject(project));
    }
}
