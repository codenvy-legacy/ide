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
package com.codenvy.ide.api.resources.model;

import com.codenvy.api.core.rest.shared.dto.Link;
import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.api.project.shared.dto.ItemReference;
import com.codenvy.ide.api.event.ResourceChangedEvent;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.AsyncRequestFactory;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.rest.StringUnmarshaller;
import com.codenvy.ide.rest.Unmarshallable;
import com.codenvy.ide.ui.loader.EmptyLoader;
import com.codenvy.ide.ui.loader.Loader;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents Project model.
 *
 * @author Nikolay Zamosenchuk
 */
public class Project extends Folder {
    public static final String PROJECT_MIME_TYPE = "text/vnd.ideproject+directory";
    public static final String TYPE              = "project";
    protected final EventBus                  eventBus;
    protected final AsyncRequestFactory       asyncRequestFactory;
    private final   DtoUnmarshallerFactory    dtoUnmarshallerFactory;
    protected       Map<String, List<String>> attributes;
    protected       Loader                    loader;
    private         ProjectDescription        description;
    private         ProjectServiceClient      projectServiceClient;
    private         String                    projectTypeId;
    private         String                    visibility;

    private List<String> currentUserPermissions;

    /**
     * Constructor for empty project. Used for serialization only.
     * <p/>
     * Not intended to be used by client.
     */
    public Project(EventBus eventBus,
                   AsyncRequestFactory asyncRequestFactory,
                   ProjectServiceClient projectServiceClient,
                   DtoUnmarshallerFactory dtoUnmarshallerFactory) {
        super(TYPE, PROJECT_MIME_TYPE);
        this.projectServiceClient = projectServiceClient;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.description = new ProjectDescription(this);
        this.attributes = new HashMap<>();
        this.eventBus = eventBus;
        this.asyncRequestFactory = asyncRequestFactory;
        this.loader = new EmptyLoader();
    }

    public void init(ItemReference itemReference) {
        name = itemReference.getName();
        mimeType = itemReference.getMediaType();
        setLinks(itemReference.getLinks());
    }

    @Override
    public void init(JSONObject itemObject) {
        name = itemObject.get("name").isString().stringValue();
        mimeType = itemObject.get("mimeType").isString().stringValue();
    }

    public ProjectDescription getDescription() {
        return description;
    }

    /**
     * Other attributes.
     *
     * @return attributes
     */
    public Map<String, List<String>> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, List<String>> attributes) {
        this.attributes = attributes;
    }

    /**
     * Check if the given attribute is present.
     *
     * @param name
     *         name of the attribute
     * @return <code>true</code> if attribute is present and <code>false</code> otherwise
     */
    public boolean hasAttribute(String name) {
        return getAttributeValues(name) != null;
    }

    /**
     * Get value of attribute <code>name</code>. It is shortcut for:
     *
     * @param attributeName
     *         attribute name
     * @return value of attribute with specified name or <code>null</code> if attribute does not exists
     */
    public String getAttributeValue(String attributeName) {
        List<String> attributeValues = getAttributeValues(attributeName);
        if (attributeValues != null && !attributeValues.isEmpty()) {
            return attributeValues.get(0);
        }
        return null;
    }

    /**
     * Get attribute values.
     *
     * @param attributeName
     *         attribute name
     * @return {@link List} of attribute values or <code>null</code> if attribute does not exists
     * @see #getAttributeValue(String)
     */
    public List<String> getAttributeValues(String attributeName) {
        return attributes.get(attributeName);
    }

    /**
     * @return the visibility
     */
    public String getVisibility() {
        return visibility;
    }

    /**
     * @param visibility
     *         the visibility to set
     */
    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    // management methods

    /**
     * Create new file.
     *
     * @param parent
     *         parent folder
     * @param name
     *         file name
     * @param content
     *         file content
     * @param mimeType
     *         file content media type
     * @param callback
     *         callback
     */
    public void createFile(final Folder parent, final String name, String content, String mimeType, final AsyncCallback<File> callback) {
        try {
            checkItemValid(parent);

            projectServiceClient.createFile(parent.getPath(), name, content, mimeType, new AsyncRequestCallback<Void>() {
                @Override
                protected void onSuccess(Void result) {
                    refreshChildren(parent, new AsyncCallback<Folder>() {
                        @Override
                        public void onSuccess(Folder result) {
                            File file = (File)result.findChildByName(name);
                            eventBus.fireEvent(ResourceChangedEvent.createResourceCreatedEvent(file));
                            callback.onSuccess(file);
                        }

                        @Override
                        public void onFailure(Throwable caught) {
                            Log.error(Project.class, callback);
                        }
                    });
                }

                @Override
                protected void onFailure(Throwable exception) {
                    callback.onFailure(exception);
                }
            });
        } catch (Exception e) {
            callback.onFailure(e);
        }
    }

    /**
     * Create new Folder.
     *
     * @param parent
     *         parent folder
     * @param name
     *         new folder name
     * @param callback
     *         callback
     */
    public void createFolder(final Folder parent, final String name, final AsyncCallback<Folder> callback) {
        try {
            checkItemValid(parent);

            projectServiceClient.createFolder(parent.getPath() + '/' + name, new AsyncRequestCallback<Void>() {
                @Override
                protected void onSuccess(Void result) {
                    refreshChildren(parent, new AsyncCallback<Folder>() {
                        @Override
                        public void onSuccess(Folder result) {
                            Folder folder = (Folder)result.findChildByName(name);
                            eventBus.fireEvent(ResourceChangedEvent.createResourceCreatedEvent(folder));
                            callback.onSuccess(folder);
                        }

                        @Override
                        public void onFailure(Throwable caught) {
                            Log.error(Project.class, callback);
                        }
                    });
                }

                @Override
                protected void onFailure(Throwable exception) {
                    callback.onFailure(exception);
                }
            });
        } catch (Exception e) {
            callback.onFailure(e);
        }
    }

    /**
     * Refreshes project's children.
     *
     * @param callback
     *         callback
     */
    public void refreshChildren(final AsyncCallback<Project> callback) {
        refreshChildren(this, new AsyncCallback<Folder>() {
            @Override
            public void onSuccess(Folder result) {
                eventBus.fireEvent(ResourceChangedEvent.createResourceTreeRefreshedEvent(Project.this));
                callback.onSuccess(Project.this);
            }

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }
        });
    }

    /**
     * Refreshes folder's children.
     *
     * @param folderToRefresh
     *         folder to refresh
     * @param callback
     *         callback
     */
    public void refreshChildren(final Folder folderToRefresh, final AsyncCallback<Folder> callback) {
        final Unmarshallable<Array<ItemReference>> unmarshaller = dtoUnmarshallerFactory.newArrayUnmarshaller(ItemReference.class);
        projectServiceClient.getChildren(folderToRefresh.getPath(), new AsyncRequestCallback<Array<ItemReference>>(unmarshaller) {
            @Override
            protected void onSuccess(Array<ItemReference> children) {
                folderToRefresh.setChildren(Collections.<Resource>createArray());
                for (ItemReference child : children.asIterable()) {
                    // skip hidden items
                    if (child.getName().startsWith(".")) {
                        continue;
                    }
                    switch (child.getType()) {
                        case File.TYPE:
                            File file = new File(child);
                            file.setProject(Project.this);
                            folderToRefresh.addChild(file);
                            break;
                        case Folder.TYPE:
                            Folder folder = new Folder(child);
                            folder.setProject(Project.this);
                            folderToRefresh.addChild(folder);
                            break;
                        default:
                            Log.error(Project.class, "Unsupported resource type: " + child.getType());
                    }
                }
                callback.onSuccess(folderToRefresh);
            }

            @Override
            protected void onFailure(Throwable throwable) {
                callback.onFailure(throwable);
            }
        });
    }

    /**
     * Recursively looks for the {@link Resource}.
     *
     * @param path
     *         resource's path to find (e.g. /project/folder/file)
     * @param callback
     *         callback
     */
    public void findResourceByPath(String path, AsyncCallback<Resource> callback) {
        findResourceByPath(this, path, callback);
    }

    /**
     * Recursively looks for the {@link Resource}.
     *
     * @param rootFolder
     *         root folder
     * @param path
     *         resource's path to find (e.g. /project/folder/file)
     * @param callback
     *         callback
     */
    public void findResourceByPath(Folder rootFolder, final String path, final AsyncCallback<Resource> callback) {
        // Avoid redundant requests. Use cached project structure.
        if (!rootFolder.getChildren().isEmpty()) {
            for (Resource child : rootFolder.getChildren().asIterable()) {
                if (path.equals(child.getPath())) {
                    callback.onSuccess(child);
                    return;
                } else if (path.startsWith(child.getPath().endsWith("/") ? child.getPath() : child.getPath() + "/")) {
                    // we're on the right way
                    findResourceByPath((Folder)child, path, callback);
                    return;
                }
            }
            callback.onFailure(new Exception("Resource not found"));
        } else {
            refreshChildren(rootFolder, new AsyncCallback<Folder>() {
                @Override
                public void onSuccess(Folder result) {
                    if (result.getChildren().isEmpty()) {
                        callback.onFailure(new Exception("Resource not found"));
                    } else {
                        findResourceByPath(result, path, callback);
                    }
                }

                @Override
                public void onFailure(Throwable caught) {
                    callback.onFailure(caught);
                }
            });
        }
    }

    /**
     * Delete child resource.
     *
     * @param resource
     *         resource to delete its child
     * @param callback
     *         callback
     */
    public void deleteChild(final Resource resource, final AsyncCallback<Void> callback) {
        try {
            checkItemValid(resource);

            projectServiceClient.delete(resource.getPath(), new AsyncRequestCallback<Void>() {
                @Override
                protected void onSuccess(Void result) {
                    // remove from the list of child
                    resource.getParent().removeChild(resource);
                    eventBus.fireEvent(ResourceChangedEvent.createResourceDeletedEvent(resource));
                    callback.onSuccess(result);
                }

                @Override
                protected void onFailure(Throwable exception) {
                    callback.onFailure(exception);
                }
            });
        } catch (Exception e) {
            callback.onFailure(e);
        }
    }

    /**
     * Get file content.
     *
     * @param file
     *         file to get its content
     * @param callback
     *         callback
     */
    public void getContent(final File file, final AsyncCallback<File> callback) {
        try {
            checkItemValid(file);

            // content already present
            if (file.getContent() != null) {
                callback.onSuccess(file);
                return;
            }

            projectServiceClient.getFileContent(file.getPath(), new AsyncRequestCallback<String>(new StringUnmarshaller()) {
                @Override
                protected void onSuccess(String result) {
                    file.setContent(result);
                    callback.onSuccess(file);
                }

                @Override
                protected void onFailure(Throwable exception) {
                    callback.onFailure(exception);
                }
            });
        } catch (Exception e) {
            callback.onFailure(e);
        }
    }

    /**
     * Update file content.
     *
     * @param file
     *         file to update its content
     * @param callback
     *         callback
     */
    public void updateContent(final File file, final AsyncCallback<File> callback) {
        try {
            checkItemValid(file);

            projectServiceClient.updateFile(file.getPath(), file.getContent(), file.getMimeType(), new AsyncRequestCallback<Void>() {
                @Override
                protected void onSuccess(Void result) {
                    callback.onSuccess(file);
                }

                @Override
                protected void onFailure(Throwable exception) {
                    callback.onFailure(exception);
                }
            });
        } catch (Exception e) {
            callback.onFailure(e);
        }
    }

    /**
     * Move resource to new destination.
     *
     * @param source
     *         source resource to move
     * @param destination
     *         destination for moved resource
     * @param lockToken
     *         lock token
     * @param callback
     *         callback
     */
    public void move(final Resource source, final Folder destination, String lockToken, final AsyncCallback<Resource> callback) {
        callback.onFailure(new Exception("Move operation not currently supported"));
    }

    /**
     * Copy resource.
     *
     * @param source
     *         resource to copy
     * @param destination
     *         destination folder
     * @param callback
     *         callback
     */
    public void copy(final Resource source, final Folder destination, final AsyncCallback<Resource> callback) {
        callback.onFailure(new Exception("Copy operation not currently supported"));
    }

    /**
     * Rename resource.
     *
     * @param resource
     *         resource to rename
     * @param newName
     *         new name for resource
     * @param callback
     *         callback
     */
    public void rename(final Resource resource, final String newName, final AsyncCallback<Resource> callback) {
        try {
            checkItemValid(resource);

            projectServiceClient.rename(resource.getPath(), newName, resource.getMimeType(), new AsyncRequestCallback<Void>() {
                @Override
                protected void onSuccess(Void result) {
                    final Folder folderToRefresh = (resource instanceof Project && resource.getParent().getName().equals(""))
                                                   ? (Project)resource : resource.getParent();
                    if (resource instanceof Project && resource.getParent().getName().equals("")) {
                        resource.setName(newName);
                    }

                    refreshChildren(folderToRefresh, new AsyncCallback<Folder>() {
                        @Override
                        public void onSuccess(Folder result) {
                            Resource renamed = (resource instanceof Project && resource.getParent().getName().equals(""))
                                               ? resource : result.findChildByName(newName);
                            eventBus.fireEvent(ResourceChangedEvent.createResourceRenamedEvent(renamed));
                            callback.onSuccess(renamed);
                        }

                        @Override
                        public void onFailure(Throwable caught) {
                            Log.error(Project.class, callback);
                        }
                    });
                }

                @Override
                protected void onFailure(Throwable exception) {
                    callback.onFailure(exception);
                }
            });
        } catch (Exception e) {
            callback.onFailure(e);
        }
    }

    /**
     * @param callback
     *         callback
     */
    public void search(final AsyncCallback<Array<Resource>> callback) {
        callback.onFailure(new Exception("Operation not currently supported"));
    }

    /**
     * Check if resource belongs to this project
     *
     * @param resource
     *         resource to check
     */
    protected void checkItemValid(final Resource resource) throws Exception {
        if (resource == null) {
            throw new Exception("A Java Resource object is null.  This is a Codenvy error.  Please contact support.");
        }
        if (resource.getProject() != this) {
            throw new Exception("Resource is out of the project's scope. Project : " +
                                getName() + ", resource path is : " + resource.getPath() + ".");
        }
    }

    public void setProjectType(String projectTypeId) {
        this.projectTypeId = projectTypeId;
    }

    String getProjectTypeId() {
        return projectTypeId;
    }

    public List<String> getCurrentUserPermissions() {
        return currentUserPermissions;
    }

    public void setCurrentUserPermissions(List<String> currentUserPermissions) {
        this.currentUserPermissions = currentUserPermissions;
    }

}
