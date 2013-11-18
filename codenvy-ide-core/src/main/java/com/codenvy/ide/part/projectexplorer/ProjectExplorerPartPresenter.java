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
package com.codenvy.ide.part.projectexplorer;

import com.codenvy.ide.Resources;
import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.api.event.ProjectActionEvent;
import com.codenvy.ide.api.event.ProjectActionHandler;
import com.codenvy.ide.api.event.ResourceChangedEvent;
import com.codenvy.ide.api.event.ResourceChangedHandler;
import com.codenvy.ide.api.parts.ProjectExplorerPart;
import com.codenvy.ide.api.parts.base.BasePresenter;
import com.codenvy.ide.api.resources.FileEvent;
import com.codenvy.ide.api.resources.FileEvent.FileOperation;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.selection.Selection;
import com.codenvy.ide.contexmenu.ContextMenuPresenter;
import com.codenvy.ide.resources.model.File;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.resources.model.Resource;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;


/**
 * Project Explorer display Project Model in a dedicated Part (view).
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a>
 */
@Singleton
public class ProjectExplorerPartPresenter extends BasePresenter implements ProjectExplorerView.ActionDelegate, ProjectExplorerPart {
    protected ProjectExplorerView  view;
    protected EventBus             eventBus;
    private   Resources            resources;
    private   ResourceProvider     resourceProvider;
    private   ContextMenuPresenter contextMenuPresenter;

    /**
     * Instantiates the ProjectExplorer Presenter
     *
     * @param view
     * @param eventBus
     * @param resources
     * @param resourceProvider
     * @param contextMenuPresenter
     */
    @Inject
    public ProjectExplorerPartPresenter(ProjectExplorerView view, EventBus eventBus, Resources resources,
                                        ResourceProvider resourceProvider, ContextMenuPresenter contextMenuPresenter) {
        this.view = view;
        this.eventBus = eventBus;
        this.resources = resources;
        this.resourceProvider = resourceProvider;
        this.view.setTitle("Project Explorer");
        this.contextMenuPresenter = contextMenuPresenter;

        bind();
    }

    /** {@inheritDoc} */
    @Override
    public void go(AcceptsOneWidget container) {
        container.setWidget(view);
    }

    /**
     * Sets content.
     *
     * @param resource
     */
    public void setContent(@NotNull Resource resource) {
        view.setItems(resource);
    }

    /** Adds behavior to view components */
    protected void bind() {
        view.setDelegate(this);
        eventBus.addHandler(ProjectActionEvent.TYPE, new ProjectActionHandler() {
            @Override
            public void onProjectOpened(ProjectActionEvent event) {
                setContent(event.getProject().getParent());
            }

            @Override
            public void onProjectDescriptionChanged(ProjectActionEvent event) {
            }

            @Override
            public void onProjectClosed(ProjectActionEvent event) {
                setContent(null);
            }
        });

        eventBus.addHandler(ResourceChangedEvent.TYPE, new ResourceChangedHandler() {
            @Override
            public void onResourceRenamed(ResourceChangedEvent event) {
                // TODO handle it
            }

            @Override
            public void onResourceMoved(ResourceChangedEvent event) {
                // TODO handle it
            }

            @Override
            public void onResourceDeleted(ResourceChangedEvent event) {
                setContent(event.getResource().getProject().getParent());
            }

            @Override
            public void onResourceCreated(ResourceChangedEvent event) {
                setContent(event.getResource().getProject().getParent());
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public String getTitle() {
        return "Project Explorer";
    }

    /** {@inheritDoc} */
    @Override
    public ImageResource getTitleImage() {
        return resources.projectExplorer();
    }

    /** {@inheritDoc} */
    @Override
    public String getTitleToolTip() {
        return "This View helps you to do basic operation with your projects. Following features are currently available:"
               + "\n\t- view project's tree" + "\n\t- select and open project's file";
    }

    @Override
    public void onResourceSelected(@NotNull Resource resource) {
        setSelection(new Selection<Resource>(resource));
    }

    /** {@inheritDoc} */
    @Override
    public void onResourceAction(@NotNull Resource resource) {
        // open file
        if (resource.isFile()) {
            eventBus.fireEvent(new FileEvent((File)resource, FileOperation.OPEN));
        }
        //open project
        if (resource.getResourceType().equals(Project.TYPE) && resourceProvider.getActiveProject() == null) {
            resourceProvider.getProject(resource.getName(), new AsyncCallback<Project>() {
                @Override
                public void onSuccess(Project result) {
                }

                @Override
                public void onFailure(Throwable caught) {
                    Log.error(ProjectExplorerPartPresenter.class, "Can not get project", caught);
                }
            });
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onContextMenu(int mouseX, int mouseY) {
        contextMenuPresenter.show(mouseX, mouseY);
    }
}