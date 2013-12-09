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
package com.codenvy.ide.resources;

import com.codenvy.ide.api.event.ProjectActionEvent;
import com.codenvy.ide.api.event.ResourceChangedEvent;
import com.codenvy.ide.api.resources.FileType;
import com.codenvy.ide.api.resources.ModelProvider;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.collections.IntegerMap;
import com.codenvy.ide.collections.IntegerMap.IterationCallback;
import com.codenvy.ide.collections.StringMap;
import com.codenvy.ide.collections.StringSet;
import com.codenvy.ide.core.Component;
import com.codenvy.ide.core.ComponentException;
import com.codenvy.ide.resources.marshal.ChildNamesUnmarshaller;
import com.codenvy.ide.resources.marshal.FolderUnmarshaller;
import com.codenvy.ide.resources.marshal.JSONSerializer;
import com.codenvy.ide.resources.marshal.ProjectModelProviderAdapter;
import com.codenvy.ide.resources.marshal.ProjectModelUnmarshaller;
import com.codenvy.ide.resources.marshal.VFSInfoUnmarshaller;
import com.codenvy.ide.resources.model.*;
import com.codenvy.ide.rest.AsyncRequest;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.HTTPHeader;
import com.codenvy.ide.ui.loader.Loader;
import com.codenvy.ide.util.Utils;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.core.client.Callback;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.URL;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.resources.client.ResourceException;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.web.bindery.event.shared.EventBus;


/**
 * Implementation of Resource Provider
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a>
 */
@Singleton
public class ResourceProviderComponent implements ResourceProvider, Component {
    /** Used for compatibility with IDE-VFS 1.x */
    private static final String DEPRECATED_PROJECT_TYPE = "deprecated.project.type";
    /** Fully qualified URL to root folder of VFS */
    private final   String                   workspaceURL;
    private         Loader                   loader;
    private final   StringMap<ModelProvider> modelProviders;
    private final   StringMap<ProjectNature> natures;
    private final   IntegerMap<FileType>     fileTypes;
    protected       VirtualFileSystemInfo    vfsInfo;
    protected final ModelProvider            genericModelProvider;
    @SuppressWarnings("unused")
    private boolean initialized = false;
    private       Project  activeProject;
    private final EventBus eventBus;
    private final FileType defaultFile;

    /**
     * Resources API for client application.
     * It deals with VFS to retrieve the content of  the files
     *
     * @throws ResourceException
     */
    @Inject
    public ResourceProviderComponent(ModelProvider genericModelProvider, Loader loader, EventBus eventBus,
                                     @Named("defaultFileType") FileType defaultFile,
                                     @Named("restContext") String restContext) {
        super();
        this.genericModelProvider = genericModelProvider;
        this.eventBus = eventBus;
        this.defaultFile = defaultFile;
        this.workspaceURL = restContext + '/' + Utils.getWorkspaceName() + "/vfs/v2";
        this.modelProviders = Collections.<ModelProvider>createStringMap();
        this.natures = Collections.<ProjectNature>createStringMap();
        this.fileTypes = Collections.createIntegerMap();
        this.loader = loader;
    }

    @Override
    public void start(final Callback<Component, ComponentException> callback) {
        AsyncRequestCallback<VirtualFileSystemInfo> internalCallback =
                new AsyncRequestCallback<VirtualFileSystemInfo>(new VFSInfoUnmarshaller()) {
                    @Override
                    protected void onSuccess(VirtualFileSystemInfo result) {
                        vfsInfo = result;
                        initialized = true;
                        // notify Component started
                        callback.onSuccess(ResourceProviderComponent.this);
                    }

                    @Override
                    protected void onFailure(Throwable exception) {
                        // notify Component failed
                        callback.onFailure(new ComponentException(
                                "Failed to start Resource Manager. Cause:" + exception.getMessage(),
                                ResourceProviderComponent.this));
                        Log.error(ResourceProviderComponent.class, exception);
                    }
                };

        this.vfsInfo = internalCallback.getPayload();
        try {
            AsyncRequest.build(RequestBuilder.GET, workspaceURL).send(internalCallback);
        } catch (RequestException exception) {
            // notify Component failed
            callback.onFailure(new ComponentException("Failed to start Resource Manager. Cause:" + exception.getMessage(),
                                                      ResourceProviderComponent.this));
        }
    }

    /** {@inheritDoc} */
    @Override
    public void getProject(final String name, final AsyncCallback<Project> callback) {
        // create internal wrapping Request Callback with proper Unmarshaller
        AsyncRequestCallback<ProjectModelProviderAdapter> internalCallback =
                new AsyncRequestCallback<ProjectModelProviderAdapter>(new ProjectModelUnmarshaller(this)) {
                    @Override
                    protected void onSuccess(ProjectModelProviderAdapter result) {
                        Folder rootFolder = vfsInfo.getRoot();

                        Project project = result.getProject();
                        project.setParent(rootFolder);
                        project.setProject(project);
                        project.setVFSInfo(vfsInfo);

                        rootFolder.getChildren().clear();
                        rootFolder.addChild(project);

                        activeProject = project;

                        // get project structure
                        project.refreshTree(new AsyncCallback<Project>() {
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
                    protected void onFailure(Throwable exception) {
                        callback.onFailure(exception);
                    }
                };

        try {
            // get Project Item by path
            String url = vfsInfo.getUrlTemplates().get((Link.REL_ITEM_BY_PATH)).getHref() + "?itemType=" + Project.TYPE;
            url = URL.decode(url).replace("[path]", name);
            AsyncRequest.build(RequestBuilder.GET, URL.encode(url)).loader(loader).send(internalCallback);
        } catch (RequestException e) {
            callback.onFailure(e);
        }
    }

    public void getFolder(final Folder folder, final AsyncCallback<Folder> callback) {
        // create internal wrapping Request Callback with proper Unmarshaller
        AsyncRequestCallback<Folder> internalCallback =
                new AsyncRequestCallback<Folder>(new FolderUnmarshaller()) {
                    @Override
                    protected void onSuccess(Folder result) {
                        result.setParent(folder.getParent());
                        result.setProject(folder.getProject());
                        activeProject.refreshTree(result, new AsyncCallback<Folder>() {
                            @Override
                            public void onSuccess(Folder folder) {
                                eventBus.fireEvent(ResourceChangedEvent.createResourceTreeRefreshedEvent(folder));
                                callback.onSuccess(folder);
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
                };

        try {
            String url = folder.getLinkByRelation(Link.REL_SELF).getHref();
            AsyncRequest.build(RequestBuilder.GET, URL.encode(url)).loader(loader).send(internalCallback);
        } catch (RequestException e) {
            callback.onFailure(e);
        }
    }
    
    /** {@inheritDoc} */
    @Override
    public void listProjects(final AsyncCallback<Array<String>> callback) {
        // internal callback
        AsyncRequestCallback<Array<String>> internalCallback =
                new AsyncRequestCallback<Array<String>>(new ChildNamesUnmarshaller()) {
                    @Override
                    protected void onSuccess(Array<String> result) {
                        callback.onSuccess(result);
                    }

                    @Override
                    protected void onFailure(Throwable exception) {
                        callback.onFailure(exception);
                    }
                };

        try {
            String param = "propertyFilter=*& itemType=" + Project.TYPE;
            AsyncRequest
                    .build(RequestBuilder.GET, vfsInfo.getRoot().getLinkByRelation(Link.REL_CHILDREN).getHref() + "?" + param)
                    .loader(loader).send(internalCallback);
        } catch (RequestException e) {
            callback.onFailure(e);
        }

    }

    /** {@inheritDoc} */
    @Override
    public void createProject(String name, Array<Property> properties, final AsyncCallback<Project> callback) {
        final Folder rootFolder = vfsInfo.getRoot();

        // create internal wrapping Request Callback with proper Unmarshaller
        AsyncRequestCallback<ProjectModelProviderAdapter> internalCallback =
                new AsyncRequestCallback<ProjectModelProviderAdapter>(new ProjectModelUnmarshaller(this)) {
                    @Override
                    protected void onSuccess(ProjectModelProviderAdapter result) {
                        Project project = result.getProject();
                        project.setParent(rootFolder);
                        rootFolder.getChildren().clear();
                        rootFolder.addChild(project);
                        project.setProject(project);
                        project.setVFSInfo(vfsInfo);
                        activeProject = project;

                        // get project structure
                        project.refreshTree(new AsyncCallback<Project>() {
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
                    protected void onFailure(Throwable exception) {
                        callback.onFailure(exception);
                    }
                };

        // perform request
        String url = rootFolder.getLinkByRelation(Link.REL_CREATE_PROJECT).getHref();
        url = URL.decode(url).replace("[name]", name);
        // DEPRECATED type not used anymore in 2.0
        url = url.replace("[type]", DEPRECATED_PROJECT_TYPE);
        url = URL.encode(url);
        loader.setMessage("Creating new project...");
        try {
            AsyncRequest.build(RequestBuilder.POST, url)
                        .data(JSONSerializer.PROPERTY_SERIALIZER.fromCollection(properties).toString())
                        .header(HTTPHeader.CONTENT_TYPE, "application/json").loader(loader).send(internalCallback);
        } catch (RequestException e) {
            callback.onFailure(e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void registerModelProvider(String primaryNature, ModelProvider modelProvider) {
        modelProviders.put(primaryNature, modelProvider);
    }

    /** {@inheritDoc} */
    @Override
    public ModelProvider getModelProvider(String primaryNature) {
        if (primaryNature != null) {
            ModelProvider modelProvider = modelProviders.get(primaryNature);
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
    public void registerNature(ProjectNature nature) {
        if (nature != null) {
            natures.put(nature.getNatureId(), nature);
        }

    }

    /** {@inheritDoc} */
    @Override
    public ProjectNature getNature(String natureId) {
        return natures.get(natureId);
    }

    /** {@inheritDoc} */
    @Override
    public void applyNature(final Project project, final String natureId, final AsyncCallback<Project> callback) {
        ProjectNature nature = natures.get(natureId);
        try {
            validate(project, nature);
        } catch (IllegalStateException e) {
            callback.onFailure(e);
            // break process
            return;
        }
        // Call ProjectNature.configure()
        nature.configure(project, new AsyncCallback<Project>() {
            @Override
            public void onSuccess(Project result) {
                // finally add property and flush settings
                project.getProperty(ProjectDescription.PROPERTY_MIXIN_NATURES).getValue().add(natureId);
                project.flushProjectProperties(new AsyncCallback<Project>() {

                    @Override
                    public void onSuccess(Project result) {
                        callback.onSuccess(result);
                    }

                    @Override
                    public void onFailure(Throwable caught) {
                        callback.onFailure(caught);
                    }
                });
            }

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }
        });
    }

    /**
     * Validate, if nature can be applied on project
     *
     * @param project
     * @param nature
     */
    protected void validate(Project project, ProjectNature nature) throws IllegalStateException {
        // Nature can't be null
        if (nature == null) {
            throw new IllegalStateException("Nature can't be null");
        }
        // check nature not primary
        if (nature.getNatureCategories().contains(ProjectNature.PRIMARY_NATURE_CATEGORY)) {
            throw new IllegalStateException("Can't set primary nature in runtime");
        }

        StringSet natureCategories = nature.getNatureCategories();
        StringSet requiredNatureIds = nature.getRequiredNatureIds();

        StringSet appliedNatureIds = project.getDescription().getNatures();
        // checj already applied
        if (appliedNatureIds.contains(nature.getNatureId())) {
            throw new IllegalStateException("Nature aready applied");
        }

        // check dependencies
        for (String requiredNatureId : requiredNatureIds.getKeys().asIterable()) {
            if (!appliedNatureIds.contains(requiredNatureId)) {
                throw new IllegalStateException("Missing required Nature on the project: " + requiredNatureId);
            }
        }

        // check ONE-OF-CATEGORY constraint
        for (String appliedNatureId : appliedNatureIds.getKeys().asIterable()) {
            ProjectNature appliedNature = natures.get(appliedNatureId);

            for (String appliedNatureCategory : appliedNature.getNatureCategories().getKeys().asIterable()) {
                if (natureCategories.contains(appliedNatureCategory)) {
                    throw new IllegalStateException("New Nature conflict with: " + appliedNatureId
                                                    + ", cause of the following category: " + appliedNatureCategory);
                }
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public void registerFileType(FileType fileType) {
        fileTypes.put(fileType.getId(), fileType);
    }

    /** {@inheritDoc} */
    @Override
    public FileType getFileType(File file) {
        String mimeType = file.getMimeType();
        final String name = file.getName();
        final Array<FileType> filtered = Collections.createArray();
        final Array<FileType> nameMatch = Collections.createArray();
        fileTypes.iterate(new IterationCallback<FileType>() {

            @Override
            public void onIteration(int key, FileType val) {
                if (val.getNamePattern() != null) {
                    RegExp regExp = RegExp.compile(val.getNamePattern());
                    if (regExp.test(name)) {
                        nameMatch.add(val);
                    }
                } else {
                    filtered.add(val);
                }
            }
        });
        if (!nameMatch.isEmpty()) {
            //TODO what if name matches more than one
            return nameMatch.get(0);
        }
        for (FileType type : filtered.asIterable()) {
            if (type.getMimeTypes().contains(mimeType)) {
                return type;
            }
        }
        String extension = getFileExtension(name);
        if (extension != null) {
            for (FileType type : filtered.asIterable()) {
                if (extension.equals(type.getExtension())) {
                    return type;
                }
            }
        }
        return defaultFile;

    }

    /**
     * @param name
     * @return
     */
    private String getFileExtension(String name) {
        int lastDotPos = name.lastIndexOf('.');
        //file has no extension
        if (lastDotPos < 0) {
            return null;
        }
        return name.substring(lastDotPos + 1);
    }

    /** {@inheritDoc} */
    @Override
    public String getVfsId() {
        return vfsInfo.getId();
    }

    /** {@inheritDoc} */
    @Override
    public String getRootId() {
        return vfsInfo.getRoot().getId();
    }

    /** {@inheritDoc} */
    @Override
    public void delete(final Resource item, final AsyncCallback<String> callback) {
        final Folder parent = item.getParent();
        activeProject.deleteChild(item, new AsyncCallback<Void>() {

            @Override
            public void onFailure(Throwable caught) {
                caught.printStackTrace();
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(Void result) {
                if (item instanceof Project) {
                    showListProjects();
                    //TODO onSuccess
                    callback.onSuccess(item.toString());
                } else if (parent instanceof Project) {
                    getProject(activeProject.getName(), new AsyncCallback<Project>() {
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
                            callback.onSuccess(result.toString());
                        }

                        @Override
                        public void onFailure(Throwable exception) {
                            callback.onFailure(exception);
                        }
                    });
                } 
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void showListProjects() {
        if (activeProject != null) {
            eventBus.fireEvent(ProjectActionEvent.createProjectClosedEvent(activeProject));
        }
        activeProject = null;

        final Folder rootFolder = vfsInfo.getRoot();
        rootFolder.getChildren().clear();

        listProjects(new AsyncCallback<Array<String>>() {
            @Override
            public void onSuccess(Array<String> result) {
                for (String projectName : result.asIterable()) {
                    Project project = new Project(eventBus);
                    project.setName(projectName);
                    rootFolder.addChild(project);
                    eventBus.fireEvent(ProjectActionEvent.createProjectOpenedEvent(project));
                }
            }

            @Override
            public void onFailure(Throwable caught) {
                Log.error(ResourceProviderComponent.class, "Can not get list of projects", caught);
            }
        });
    }
}