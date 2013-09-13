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

package org.exoplatform.ide.client.project;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;

import org.exoplatform.gwtframework.ui.client.dialog.BooleanValueReceivedHandler;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.event.RefreshBrowserEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.project.CloseProjectEvent;
import org.exoplatform.ide.client.framework.project.OpenProjectEvent;
import org.exoplatform.ide.client.framework.project.ProjectClosedEvent;
import org.exoplatform.ide.client.framework.project.ProjectClosedHandler;
import org.exoplatform.ide.client.framework.project.ProjectCreatedEvent;
import org.exoplatform.ide.client.framework.project.ProjectCreatedHandler;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedHandler;
import org.exoplatform.ide.vfs.client.model.ProjectModel;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ProjectCreatedEventHandler implements ProjectCreatedHandler, ProjectOpenedHandler, ProjectClosedHandler {

    private ProjectModel openedProject;

    private ProjectModel projectToBeOpened;

    public ProjectCreatedEventHandler() {
        IDE.addHandler(ProjectCreatedEvent.TYPE, this);
        IDE.addHandler(ProjectOpenedEvent.TYPE, this);
        IDE.addHandler(ProjectClosedEvent.TYPE, this);
    }

    @Override
    public void onProjectOpened(ProjectOpenedEvent event) {
        openedProject = event.getProject();
    }

    @Override
    public void onProjectCreated(final ProjectCreatedEvent event) {
        projectToBeOpened = null;

        if (openedProject == null) {
            openProject(event.getProject());
            return;
        }
        if (openedProject.getId().equals(event.getProject().getId())) {
            // disallow to reopened current project. if this appears then we update folder contents
            IDE.fireEvent(new RefreshBrowserEvent(event.getProject()));
            return;
        }

        Dialogs.getInstance().ask("IDE", "Open project " + event.getProject().getName() + " ?",
                                  new BooleanValueReceivedHandler() {
                                      @Override
                                      public void booleanValueReceived(Boolean value) {
                                          if (value != null && true == value) {
                                              projectToBeOpened = event.getProject();
                                              IDE.fireEvent(new CloseProjectEvent());
                                          }
                                      }
                                  });
    }

    @Override
    public void onProjectClosed(ProjectClosedEvent event) {
        openedProject = null;

        if (projectToBeOpened != null) {
            openProject(projectToBeOpened);
            projectToBeOpened = null;
        }
    }

    private void openProject(final ProjectModel project) {
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                IDE.fireEvent(new OpenProjectEvent(project));
            }
        });
    }

}
