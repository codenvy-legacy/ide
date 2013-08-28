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
package org.exoplatform.ide.extension.heroku.client.project;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.navigation.event.FolderRefreshedEvent;
import org.exoplatform.ide.client.framework.navigation.event.FolderRefreshedHandler;
import org.exoplatform.ide.client.framework.project.ProjectClosedEvent;
import org.exoplatform.ide.client.framework.project.ProjectClosedHandler;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedHandler;
import org.exoplatform.ide.extension.heroku.client.HerokuClientBundle;
import org.exoplatform.ide.vfs.client.model.ProjectModel;

/**
 * Control for user to manage project deployed on Heroku.
 *
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Dec 2, 2011 2:17:30 PM anya $
 */
@RolesAllowed("developer")
public class HerokuControl extends SimpleControl implements IDEControl, 
            ProjectClosedHandler, ProjectOpenedHandler, FolderRefreshedHandler {
    
    /** Control ID. */
    public static final String ID = "Project/PaaS/Heroku";

    /** Control's title. */
    public static final String TITLE = "Heroku";

    /** Control's prompt, when user hovers the mouse on it. */
    public static final String PROMPT = "Heroku";

    public HerokuControl() {
        super(ID);
        setTitle(TITLE);
        setPrompt(PROMPT);
        setImages(HerokuClientBundle.INSTANCE.heroku(), HerokuClientBundle.INSTANCE.herokuDisabled());
        setEvent(new ManageHerokuProjectEvent());
    }

    /** @see org.exoplatform.ide.client.framework.control.IDEControl#initialize() */
    @Override
    public void initialize() {
        IDE.addHandler(ProjectOpenedEvent.TYPE, this);
        IDE.addHandler(ProjectClosedEvent.TYPE, this);
        IDE.addHandler(FolderRefreshedEvent.TYPE, this);
    }

    /** @see org.exoplatform.ide.client.framework.project.ProjectOpenedHandler#onProjectOpened(org.exoplatform.ide.client.framework
     * .project.ProjectOpenedEvent) */
    @Override
    public void onProjectOpened(ProjectOpenedEvent event) {
        boolean isHerokuProject = isHeroku(event.getProject());
        setVisible(isHerokuProject);
        setEnabled(isHerokuProject);
    }

    /** @see org.exoplatform.ide.client.framework.project.ProjectClosedHandler#onProjectClosed(org.exoplatform.ide.client.framework
     * .project.ProjectClosedEvent) */
    @Override
    public void onProjectClosed(ProjectClosedEvent event) {
        setVisible(false);
        setEnabled(false);
    }

    /** @see org.exoplatform.ide.client.framework.navigation.event.FolderRefreshedHandler#onFolderRefreshed(org.exoplatform.ide.client
     * .framework.navigation.event.FolderRefreshedEvent) */
    @Override
    public void onFolderRefreshed(FolderRefreshedEvent event) {
        if (event.getFolder() instanceof ProjectModel) {
            boolean enabled = isHeroku((ProjectModel)event.getFolder());
            setVisible(enabled);
            setEnabled(enabled);
        }
    }

    private boolean isHeroku(ProjectModel project) {
        return (project.getProperty("heroku-application") != null &&  !project.getPropertyValues("heroku-application").isEmpty());
    }
    
}
