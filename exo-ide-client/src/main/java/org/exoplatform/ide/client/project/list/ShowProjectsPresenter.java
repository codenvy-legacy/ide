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

package org.exoplatform.ide.client.project.list;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.project.*;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.marshal.ChildrenUnmarshaller;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.ItemType;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ShowProjectsPresenter implements ShowProjectsHandler, ViewClosedHandler,
        ProjectOpenedHandler, ProjectClosedHandler, VfsChangedHandler {

    public interface Display extends IsView {

        ListGridItem<ProjectModel> getProjectsListGrid();

        List<ProjectModel> getSelectedItems();

        HasClickHandlers getOpenButton();

        HasClickHandlers getCancelButton();

        void setOpenButtonEnabled(boolean enabled);

    }

    /** Instance of opened {@link Display}. */
    private Display display;

    /** Current opened project. */
    private ProjectModel openedProject;

    /** Virtual File System info. */
    private VirtualFileSystemInfo vfsInfo;

    /** Creates new instance of this presenter. */
    public ShowProjectsPresenter() {
        IDE.getInstance().addControl(new ShowProjectsControl());

        IDE.addHandler(ShowProjectsEvent.TYPE, this);
        IDE.addHandler(ViewClosedEvent.TYPE, this);
        IDE.addHandler(VfsChangedEvent.TYPE, this);
        IDE.addHandler(ProjectOpenedEvent.TYPE, this);
        IDE.addHandler(ProjectClosedEvent.TYPE, this);
    }

    /**
     * Handles {@link ShowProjectsEvent} and opens {@link ShowProjectsView}.
     *
     * @see org.exoplatform.ide.client.project.list.ShowProjectsHandler#onShowProjects(org.exoplatform.ide.client.project.list
     * .ShowProjectsEvent)
     */
    @Override
    public void onShowProjects(ShowProjectsEvent event) {
        if (display != null || vfsInfo == null) {
            return;
        }

        getProjectList();
    }

    /** Creates and binds display. */
    private void createAndBindDisplay() {
        display = GWT.create(Display.class);
        IDE.getInstance().openView(display.asView());

        display.getOpenButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                openProject();
            }
        });

        display.getCancelButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                IDE.getInstance().closeView(display.asView().getId());
            }
        });

        display.getProjectsListGrid().addDoubleClickHandler(new DoubleClickHandler() {
            @Override
            public void onDoubleClick(DoubleClickEvent event) {
                openProject();
            }
        });

        display.getProjectsListGrid().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Scheduler.get().scheduleDeferred(openButtonEnablingChecker);
            }
        });

        display.setOpenButtonEnabled(false);
    }

    /** Enables or disables Open button. */
    private ScheduledCommand openButtonEnablingChecker = new ScheduledCommand() {
        @Override
        public void execute() {
            if (display == null) {
                return;
            }

            if (display.getSelectedItems().size() != 1) {
                display.setOpenButtonEnabled(false);
                return;
            }

            ProjectModel selectedProject = display.getSelectedItems().get(0);
            if (openedProject == null) {
                display.setOpenButtonEnabled(true);
                return;
            }

            if (selectedProject.getId().equals(openedProject.getId())) {
                display.setOpenButtonEnabled(false);
            } else {
                display.setOpenButtonEnabled(true);
            }
        }
    };

    /** Refreshes the list of available projects and opens new {@link ShowProjectsView}. */
    private void getProjectList() {
        HashMap<String, String> query = new HashMap<String, String>();

        query.put("nodeType", "vfs:project");

        try {
            VirtualFileSystem.getInstance().getChildren(VirtualFileSystem.getInstance().getInfo().getRoot(),
                                                        ItemType.PROJECT, new AsyncRequestCallback<List<Item>>(
                    new ChildrenUnmarshaller(new ArrayList<Item>())) {
                @Override
                protected void onSuccess(List<Item> result) {
                    List<ProjectModel> projects = new ArrayList<ProjectModel>();
                    for (Item item : result) {
                        if (item instanceof ProjectModel) {
                            projects.add((ProjectModel)item);
                        }
                    }

                    createAndBindDisplay();
                    display.getProjectsListGrid().setValue(projects);
                }

                @Override
                protected void onFailure(Throwable exception) {
                    IDE.fireEvent(new ExceptionThrownEvent(exception, "Searching of projects failed."));
                }
            });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e, "Searching of projects failed."));
        }
    }

    /**
     * Handle {@link ViewClosedEvent} and reset instance of {@link Display}.
     *
     * @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api
     * .event.ViewClosedEvent)
     */
    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (event.getView() instanceof Display) {
            display = null;
        }
    }

    @Override
    public void onVfsChanged(VfsChangedEvent event) {
        vfsInfo = event.getVfsInfo();
    }

    /** Receives the name of the currently opened project. */
    @Override
    public void onProjectOpened(ProjectOpenedEvent event) {
        openedProject = event.getProject();

        if (display != null) {
            IDE.getInstance().closeView(display.asView().getId());
        }
    }

    /** Opens selected project. First, close all opened files. */
    private void openProject() {
        if (display.getSelectedItems().size() == 0) {
            return;
        }

        if (openedProject != null) {
            IDE.fireEvent(new CloseProjectEvent());
            return;
        }

        ProjectModel project = (ProjectModel)display.getSelectedItems().get(0);
        IDE.fireEvent(new OpenProjectEvent(project));
        IDE.getInstance().closeView(display.asView().getId());
    }

    @Override
    public void onProjectClosed(ProjectClosedEvent event) {
        openedProject = null;

        if (display == null) {
            return;
        }

        ProjectModel project = (ProjectModel)display.getSelectedItems().get(0);
        IDE.fireEvent(new OpenProjectEvent(project));
        IDE.getInstance().closeView(display.asView().getId());
    }

}
