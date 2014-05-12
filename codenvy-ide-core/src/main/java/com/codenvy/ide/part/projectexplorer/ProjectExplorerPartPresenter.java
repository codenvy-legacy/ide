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

import com.codenvy.ide.Constants;
import com.codenvy.ide.CoreLocalizationConstant;
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
import com.codenvy.ide.projecttype.SelectProjectTypePresenter;
import com.codenvy.ide.api.resources.model.File;
import com.codenvy.ide.api.resources.model.Folder;
import com.codenvy.ide.api.resources.model.Project;
import com.codenvy.ide.api.resources.model.Resource;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import javax.validation.constraints.NotNull;

/**
 * Project Explorer display Project Model in a dedicated Part (view).
 *
 * @author Nikolay Zamosenchuk
 */
@Singleton
public class ProjectExplorerPartPresenter extends BasePresenter implements ProjectExplorerView.ActionDelegate, ProjectExplorerPart {
    protected ProjectExplorerView        view;
    protected EventBus                   eventBus;
    private   ResourceProvider           resourceProvider;
    private   ContextMenuPresenter       contextMenuPresenter;
    private   SelectProjectTypePresenter selectProjectTypePresenter;
    private   CoreLocalizationConstant   coreLocalizationConstant;

    /**
     * Instantiates the ProjectExplorer Presenter.
     *
     * @param view
     * @param eventBus
     * @param resourceProvider
     * @param contextMenuPresenter
     * @param selectProjectTypePresenter
     * @param coreLocalizationConstant
     */
    @Inject
    public ProjectExplorerPartPresenter(ProjectExplorerView view,
                                        EventBus eventBus,
                                        ResourceProvider resourceProvider,
                                        ContextMenuPresenter contextMenuPresenter,
                                        SelectProjectTypePresenter selectProjectTypePresenter,
                                        CoreLocalizationConstant coreLocalizationConstant) {
        this.view = view;
        this.coreLocalizationConstant = coreLocalizationConstant;
        this.eventBus = eventBus;
        this.resourceProvider = resourceProvider;
        this.contextMenuPresenter = contextMenuPresenter;
        this.selectProjectTypePresenter = selectProjectTypePresenter;
        this.view.setTitle(coreLocalizationConstant.projectExplorerTitleBarText());

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
    private void setContent(@NotNull Resource resource) {
        view.setItems(resource);
        onResourceSelected(null);
    }

    /** Adds behavior to view components */
    protected void bind() {
        view.setDelegate(this);

        eventBus.addHandler(ProjectActionEvent.TYPE, new ProjectActionHandler() {
            @Override
            public void onProjectOpened(ProjectActionEvent event) {
                setContent(event.getProject().getParent());
                
                //List of projects is displaying (need find better sign, that the list of projects is shown):
                if (event.getProject().getProject() == null) {
                    view.hideProjectHeader();
                } else {
                    view.setProjectHeader(event.getProject());
                }
            }

            @Override
            public void onProjectDescriptionChanged(ProjectActionEvent event) {
            }

            @Override
            public void onProjectClosed(ProjectActionEvent event) {
                setContent(null);
                view.hideProjectHeader();
            }
        });

        eventBus.addHandler(ResourceChangedEvent.TYPE, new ResourceChangedHandler() {
            @Override
            public void onResourceRenamed(ResourceChangedEvent event) {
                if (event.getResource() instanceof Project &&
                    event.getResource().getParent().getId().equals(resourceProvider.getRootId())) {
                    setContent(event.getResource().getParent());
                } else {
                    updateItem(event.getResource().getParent());
                }
            }

            @Override
            public void onResourceMoved(ResourceChangedEvent event) {
            }

            @Override
            public void onResourceDeleted(ResourceChangedEvent event) {
                if (!(event.getResource() instanceof Project)) {
                    updateItem(event.getResource().getParent());
                }
            }

            @Override
            public void onResourceCreated(ResourceChangedEvent event) {
                updateItem(event.getResource().getParent());
            }

            @Override
            public void onResourceTreeRefreshed(ResourceChangedEvent event) {
                final Resource resource = event.getResource();

                if (resource.getProject() == null) {
                    if (resource.getId().equals(resourceProvider.getRootId())) {
                        setContent(resource);
                        view.hideProjectHeader();
                    }
                    return;
                }

                if (resource instanceof Project && resource.getProject() != null) {
                    view.updateItem(resource.getProject(), resource);
                } else if (resource instanceof Folder && ((Folder)resource).getChildren().isEmpty()) {
                    return;
                } else if (resource.getProject() != null) {
                    Resource oldResource = resource.getProject().findResourceById(resource.getId());
                    if (oldResource != null) {
                        view.updateItem(oldResource, resource);
                    }
                }
            }
        });
    }

    /**
     * Update item in the project explorer.
     *
     * @param resource
     *         the resource that need to be updated
     */
    private void updateItem(@NotNull Resource resource) {
        Project project = resource.getProject();
        Resource oldResource;
        if (resource.getParent().getId().equals(resourceProvider.getRootId())) {
            oldResource = project;
        } else {
            oldResource = project.findResourceById(resource.getId());
        }
        view.updateItem(oldResource, resource);
    }

    /** {@inheritDoc} */
    @Override
    public String getTitle() {
        return coreLocalizationConstant.projectExplorerButtonTitle();
    }

    /** {@inheritDoc} */
    @Override
    public ImageResource getTitleImage() {
        return null;
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
        if (resource != null) {
            resourceProvider.setActiveProject(resource.getProject());
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onResourceAction(@NotNull Resource resource) {
        // open file
        if (resource.isFile()) {
            eventBus.fireEvent(new FileEvent((File)resource, FileOperation.OPEN));
        }
        // open project
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

    /** {@inheritDoc} */
    @Override
    public void onResourceOpened(final Resource resource) {
        if (resource instanceof Folder && (((Folder)resource).getChildren().isEmpty())) {
            if (resource.getResourceType().equals(Project.TYPE)) {
                checkProjectType((Project)resource, new AsyncCallback<Project>() {
                    @Override
                    public void onSuccess(Project result) {
                        refreshChildren(result);
                    }

                    @Override
                    public void onFailure(Throwable caught) {
                        Log.error(ProjectExplorerPartPresenter.class, "Can not set project type.", caught);
                    }
                });
            } else {
                refreshChildren((Folder)resource);
            }
        }
    }

    /**
     * Check, whether project type is "unknown" and call {@link SelectProjectTypePresenter} to set it.
     *
     * @param project
     *         project to check it's type
     * @param callback
     *         callback
     */
    private void checkProjectType(final Project project, final AsyncCallback<Project> callback) {
        if (Constants.NAMELESS_ID.equals(project.getDescription().getProjectTypeId())) {
            selectProjectTypePresenter.showDialog(project, callback);
        } else {
            callback.onSuccess(project);
        }
    }

    private void refreshChildren(Folder folder) {
        folder.getProject().refreshChildren(folder, new AsyncCallback<Folder>() {
            @Override
            public void onSuccess(Folder result) {
                eventBus.fireEvent(ResourceChangedEvent.createResourceTreeRefreshedEvent(result));
            }

            @Override
            public void onFailure(Throwable caught) {
                Log.error(ProjectExplorerPartPresenter.class, "Can not refresh project tree.", caught);
            }
        });
    }

}
