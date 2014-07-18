/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.resources;

import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.api.project.shared.dto.ProjectDescriptor;
import com.codenvy.api.project.shared.dto.ProjectReference;
import com.codenvy.ide.api.AppContext;
import com.codenvy.ide.api.CurrentProject;
import com.codenvy.ide.api.event.ProjectActionEvent;
import com.codenvy.ide.api.event.ResourceChangedEvent;
import com.codenvy.ide.api.resources.FileEvent;
import com.codenvy.ide.api.resources.ModelProvider;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.resources.model.File;
import com.codenvy.ide.api.resources.model.Folder;
import com.codenvy.ide.api.resources.model.Project;
import com.codenvy.ide.api.resources.model.Resource;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.collections.StringMap;
import com.codenvy.ide.core.Component;
import com.codenvy.ide.core.ComponentException;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.AsyncRequestFactory;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.rest.Unmarshallable;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import java.util.List;

import static com.codenvy.ide.api.resources.model.ProjectDescription.LANGUAGE_ATTRIBUTE;

/**
 * Implementation of Resource Provider
 *
 * @author Nikolay Zamosenchuk
 */
@Singleton
public class ResourceProviderComponent implements ResourceProvider, Component {
    protected final ModelProvider            genericModelProvider;
    /** Fully qualified URL to root folder of VFS */
    private final   StringMap<ModelProvider> modelProviders;
    private final   EventBus                 eventBus;
    private final   DtoUnmarshallerFactory   dtoUnmarshallerFactory;
    private final   AsyncRequestFactory      asyncRequestFactory;
    private final   ProjectServiceClient     projectServiceClient;
    private AppContext appContext;
    private Project activeProject;

    /** Resources API for client application. */
    @Inject
    public ResourceProviderComponent(ModelProvider genericModelProvider,
                                     EventBus eventBus,
                                     DtoUnmarshallerFactory dtoUnmarshallerFactory,
                                     AsyncRequestFactory asyncRequestFactory,
                                     ProjectServiceClient projectServiceClient,
                                     AppContext appContext) {
        super();
        this.genericModelProvider = genericModelProvider;
        this.eventBus = eventBus;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.asyncRequestFactory = asyncRequestFactory;
        this.projectServiceClient = projectServiceClient;
        this.appContext = appContext;
        this.modelProviders = Collections.createStringMap();
    }

    @Override
    public void start(final Callback<Component, ComponentException> callback) {
        // notify Component started
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                callback.onSuccess(ResourceProviderComponent.this);
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void getProject(final String name, final AsyncCallback<Project> callback) {
        final Unmarshallable<ProjectDescriptor> unmarshaller = dtoUnmarshallerFactory.newUnmarshaller(ProjectDescriptor.class);
        projectServiceClient.getProject(name, new AsyncRequestCallback<ProjectDescriptor>(unmarshaller) {
            @Override
            protected void onSuccess(ProjectDescriptor result) {
                // do post actions
                Folder rootFolder = getRoot();
                appContext.setCurrentProject(new CurrentProject(result));
                List<String> attr = result.getAttributes().get(LANGUAGE_ATTRIBUTE);
                String language = null;
                if (attr != null && !attr.isEmpty())
                    language = result.getAttributes().get(LANGUAGE_ATTRIBUTE).get(0);
                final Project project = getModelProvider(language).createProjectInstance();

                project.setAttributes(result.getAttributes());
                project.setProjectType(result.getProjectTypeId());
                project.setName(result.getName());
                project.setParent(rootFolder);
                project.setProject(project);
                project.setVisibility(result.getVisibility());

                rootFolder.getChildren().clear();
                rootFolder.addChild(project);
                if (activeProject != null) {
                    try {
                        eventBus.fireEvent(ProjectActionEvent.createProjectClosedEvent(activeProject));
                    } catch (Exception e) {
                        Log.error(ResourceProviderComponent.class, "An error occurred while firing ProjectClosedEvent", e);
                    }
                }

                activeProject = project;

                project.refreshChildren(new AsyncCallback<Project>() {
                    @Override
                    public void onSuccess(Project result) {
                        try {
                            eventBus.fireEvent(ProjectActionEvent.createProjectOpenedEvent(project));
                        } catch (Exception e) {
                            Log.error(ResourceProviderComponent.class, "An error occurred while firing ProjectOpenedEvent", e);
                        }
                        callback.onSuccess(project);
                    }

                    @Override
                    public void onFailure(Throwable exception) {
                        callback.onFailure(exception);
                    }
                });
            }

            @Override
            protected void onFailure(Throwable exception) {
                callback.onFailure(exception);
            }
        });
    }

    public void getFolder(final Folder folder, final AsyncCallback<Folder> callback) {
        activeProject.refreshChildren(folder, new AsyncCallback<Folder>() {
            @Override
            public void onSuccess(Folder result) {
                eventBus.fireEvent(ResourceChangedEvent.createResourceTreeRefreshedEvent(result));
                Resource f = result.findChildByName(folder.getName());
                if (f != null && !f.getPath().equals(result.getPath())) {
                    eventBus.fireEvent(ResourceChangedEvent.createResourceTreeRefreshedEvent(f));
                }
            }

            @Override
            public void onFailure(Throwable exception) {
                callback.onFailure(exception);
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void createProject(final String name, ProjectDescriptor projectDescriptor, final AsyncCallback<Project> callback) {
        final Unmarshallable<ProjectDescriptor> unmarshaller = dtoUnmarshallerFactory.newUnmarshaller(ProjectDescriptor.class);
        projectServiceClient.createProject(name, projectDescriptor, new AsyncRequestCallback<ProjectDescriptor>(unmarshaller) {
            @Override
            protected void onSuccess(ProjectDescriptor result) {
                Log.info(ResourceProviderComponent.class, " :1111: " + result.getName());
                Folder rootFolder = getRoot();
                List<String> attr = result.getAttributes().get(LANGUAGE_ATTRIBUTE);
                String language = null;
                if (attr != null && !attr.isEmpty())
                    language = result.getAttributes().get(LANGUAGE_ATTRIBUTE).get(0);
                final Project project = getModelProvider(language).createProjectInstance();
                Log.info(ResourceProviderComponent.class, " :: " + project.getName());
                project.setAttributes(result.getAttributes());
                project.setProjectType(result.getProjectTypeId());
                project.setName(name);
                project.setParent(rootFolder);
                project.setProject(project);
                project.setVisibility(result.getVisibility());

                rootFolder.getChildren().clear();
                rootFolder.addChild(project);

                if (activeProject != null) {
                    try {
                        eventBus.fireEvent(ProjectActionEvent.createProjectClosedEvent(activeProject));
                    } catch (Exception e) {
                        Log.error(ResourceProviderComponent.class, "An error occurred while firing ProjectClosedEvent", e);
                    }
                }
                activeProject = project;

                appContext.setCurrentProject(new CurrentProject(result));

                // get project structure
                project.refreshChildren(new AsyncCallback<Project>() {
                    @Override
                    public void onSuccess(Project project) {
                        eventBus.fireEvent(ProjectActionEvent.createProjectOpenedEvent(project));
                        callback.onSuccess(project);
                    }

                    @Override
                    public void onFailure(Throwable exception) {
                        callback.onFailure(exception);
                    }
                });
            }

            @Override
            protected void onFailure(Throwable throwable) {
                callback.onFailure(throwable);
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void registerModelProvider(String language, ModelProvider modelProvider) {
        modelProviders.put(language, modelProvider);
    }

    /** {@inheritDoc} */
    @Override
    public ModelProvider getModelProvider(String language) {
        if (language != null) {
            ModelProvider modelProvider = modelProviders.get(language);
            if (modelProvider != null) {
                return modelProvider;
            }
        }
        // return generic model provider
        return genericModelProvider;
    }

    /** {@inheritDoc} */
    @Override
    public Project getActiveProject() {
        return activeProject;
    }

    /** {@inheritDoc} */
    @Override
    public void setActiveProject(Project project) {
        this.activeProject = project;
    }

    @Override
    public Folder getRoot() { //TODO: need rework logic and remove this method
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", new JSONString(getRootId()));
        jsonObject.put("name", new JSONString(""));
        jsonObject.put("mimeType", new JSONString("text/directory"));
        return new Folder(jsonObject);
    }

    @Override
    public String getRootId() { //TODO: need rework logic and remove this method
        return "_root_";
    }

    /** {@inheritDoc} */
    @Override
    public void delete(final Resource item, final AsyncCallback<String> callback) {
        final Folder parent = item.getParent();
        if (activeProject == null) {
            projectServiceClient.delete(item.getPath(), new AsyncRequestCallback<Void>() {
                @Override
                protected void onSuccess(Void result) {
                    // remove from the list of child
                    parent.removeChild(item);

                    refreshRoot();

                    eventBus.fireEvent(ResourceChangedEvent.createResourceDeletedEvent(item));
                    callback.onSuccess(item.getName());
                }

                @Override
                protected void onFailure(Throwable exception) {
                    callback.onFailure(exception);
                }
            });
        } else {
            activeProject.deleteChild(item, new AsyncCallback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    if (item instanceof Project && !(parent instanceof Project)) {
                        refreshRoot();

                        callback.onSuccess(item.toString());
                    } else if (parent instanceof Project) {
                        getProject(parent.getName(), new AsyncCallback<Project>() {
                            @Override
                            public void onSuccess(Project result) {
                                callback.onSuccess(result.toString());
                            }

                            @Override
                            public void onFailure(Throwable caught) {
                                callback.onFailure(caught);
                            }
                        });
                    } else {
                        getFolder(parent, new AsyncCallback<Folder>() {
                            @Override
                            public void onSuccess(Folder result) {
                                if (item instanceof File) {
                                    eventBus.fireEvent(new FileEvent((File)item, FileEvent.FileOperation.CLOSE));
                                }
                                callback.onSuccess(result.toString());
                            }

                            @Override
                            public void onFailure(Throwable exception) {
                                callback.onFailure(exception);
                            }
                        });
                    }
                }

                @Override
                public void onFailure(Throwable caught) {
                    caught.printStackTrace();
                    callback.onFailure(caught);
                }
            });
        }
    }

    @Override
    public void refreshRoot() {
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                if (activeProject != null) {
                    eventBus.fireEvent(ProjectActionEvent.createProjectClosedEvent(activeProject));
                    activeProject = null;
                }

                projectServiceClient.getProjects(
                        new AsyncRequestCallback<Array<ProjectReference>>(dtoUnmarshallerFactory.newArrayUnmarshaller(ProjectReference.class)) {
                            @Override
                            protected void onSuccess(Array<ProjectReference> result) {
                                Folder root = getRoot();
                                Log.debug(this.getClass(), ">>> " + result.toString());
                                for (ProjectReference item : result.asIterable()) {
                                    Project project = new Project(eventBus, asyncRequestFactory, projectServiceClient, dtoUnmarshallerFactory);
                                    project.setName(item.getName());
                                    project.setProjectType(item.getProjectTypeId());
                                    root.addChild(project);
                                }
                                eventBus.fireEvent(ResourceChangedEvent.createResourceTreeRefreshedEvent(root));
                            }

                            @Override
                            protected void onFailure(Throwable exception) {
                                Log.error(ResourceProviderComponent.class, "Unable to get the list of projects", exception);
                            }
                        });
            }
        });
    }
}
