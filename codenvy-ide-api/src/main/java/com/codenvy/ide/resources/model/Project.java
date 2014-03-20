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
package com.codenvy.ide.resources.model;

import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.api.project.shared.dto.ItemReference;
import com.codenvy.ide.MimeType;
import com.codenvy.ide.api.event.ProjectActionEvent;
import com.codenvy.ide.api.event.ResourceChangedEvent;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.resources.marshal.JSONDeserializer;
import com.codenvy.ide.resources.marshal.JSONSerializer;
import com.codenvy.ide.resources.marshal.PropertyUnmarshaller;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.AsyncRequestFactory;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.rest.HTTPHeader;
import com.codenvy.ide.rest.StringUnmarshaller;
import com.codenvy.ide.rest.Unmarshallable;
import com.codenvy.ide.ui.loader.EmptyLoader;
import com.codenvy.ide.ui.loader.Loader;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.http.client.URL;
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
    protected       Array<Property>           properties;
    protected       Map<String, List<String>> attributes;
    protected       Loader                    loader;
    protected       VirtualFileSystemInfo     vfsInfo;
    private         ProjectDescription        description;
    private         ProjectServiceClient      projectServiceClient;
    private         String                    projectTypeId;

    /**
     * Constructor for empty project. Used for serialization only.
     * <p/>
     * Not intended to be used by client.
     */
    public Project(EventBus eventBus, AsyncRequestFactory asyncRequestFactory, ProjectServiceClient projectServiceClient,
                   DtoUnmarshallerFactory dtoUnmarshallerFactory) {
        super(TYPE, PROJECT_MIME_TYPE);
        this.projectServiceClient = projectServiceClient;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.description = new ProjectDescription(this);
        this.properties = Collections.<Property>createArray();
        this.attributes = new HashMap<>();
        this.eventBus = eventBus;
        this.asyncRequestFactory = asyncRequestFactory;
        this.loader = new EmptyLoader();
    }

    public void init(ItemReference itemReference) {
        id = itemReference.getId();
        name = itemReference.getName();
        mimeType = itemReference.getMediaType();
    }

    @Override
    public void init(JSONObject itemObject) {
        id = itemObject.get("id").isString().stringValue();
        name = itemObject.get("name").isString().stringValue();
        mimeType = itemObject.get("mimeType").isString().stringValue();
        properties = JSONDeserializer.PROPERTY_DESERIALIZER.toList(itemObject.get("properties"));
        links = JSONDeserializer.LINK_DESERIALIZER.toMap(itemObject.get("links"));
    }

    public void setVFSInfo(VirtualFileSystemInfo vfsInfo) {
        this.vfsInfo = vfsInfo;
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
     * @param name
     *         attribute name
     * @return value of attribute with specified name or <code>null</code> if attribute does not exists
     */
    public String getAttributeValue(String attributeName) {
        List<String> attributeValues = getAttributeValues(attributeName);
        if (attributeValues != null) {
            return attributeValues.get(0);
        }
        return null;
    }

    /**
     * Get attribute values.
     *
     * @param name
     *         attribute name
     * @return {@link List} of attribute values or <code>null</code> if attribute does not exists
     * @see #getAttributeValue(String)
     */
    public List<String> getAttributeValues(String attributeName) {
        return attributes.get(attributeName);
    }

    /**
     * Other properties.
     *
     * @return properties. If there is no properties then empty list returned, never <code>null</code>
     */
    public Array<Property> getProperties() {
        if (properties == null) {
            properties = Collections.<Property>createArray();
        }
        return properties;
    }

    /**
     * Get single property with specified name.
     *
     * @param name
     *         name of property
     * @return property or <code>null</code> if there is not property with specified name
     */
    public Property getProperty(String name) {
        Array<Property> props = getProperties();
        for (int i = 0; i < props.size(); i++) {
            Property p = props.get(i);
            if (p.getName().equals(name)) {
                return p;
            }
        }
        return null;
    }

    /**
     * Check does item has property with specified name.
     *
     * @param name
     *         name of property
     * @return <code>true</code> if item has property <code>name</code> and <code>false</code> otherwise
     */
    public boolean hasProperty(String name) {
        return getProperty(name) != null;
    }

    /**
     * Get value of property <code>name</code>. It is shortcut for:
     * <pre>
     *    String name = ...
     *    Item item = ...
     *    Property property = item.getProperty(name);
     *    Object value;
     *    if (property != null)
     *       value = property.getValue().get(0);
     *    else
     *       value = null;
     * </pre>
     *
     * @param name
     *         property name
     * @return value of property with specified name or <code>null</code>
     */
    public Object getPropertyValue(String name) {
        Property p = getProperty(name);
        if (p != null) {
            return p.getValue().get(0);
        }
        return null;
    }

    /**
     * Get set of property values
     *
     * @param name
     *         property name
     * @return set of property values or <code>null</code> if property does not exists
     * @see #getPropertyValue(String)
     */
    public Array<String> getPropertyValues(String name) {
        Property p = getProperty(name);
        if (p != null) {
            return p.getValue().copy();
        }
        return null;
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
                            file.getParent().setTag(parent.getTag());
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
                            Log.error(this.getClass(), "Unsupported resource type: " + child.getType());
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
        callback.onFailure(new Exception("Rename operation not currently supported"));
    }

    /** @param callback */
    public void flushProjectProperties(final AsyncCallback<Project> callback) {
        try {
            AsyncRequestCallback<Void> internalCallback = new AsyncRequestCallback<Void>() {
                @Override
                protected void onSuccess(Void result) {
                    eventBus.fireEvent(ProjectActionEvent.createProjectDescriptionChangedEvent(Project.this));
                    callback.onSuccess(Project.this);
                }

                @Override
                protected void onFailure(Throwable exception) {
                    callback.onFailure(exception);
                }
            };

            String url = this.getLinkByRelation(Link.REL_SELF).getHref();
            loader.setMessage("Updating item...");
            asyncRequestFactory.createPostRequest(url, null)
                               .data(JSONSerializer.PROPERTY_SERIALIZER.fromCollection(getProperties()).toString())
                               .header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_JSON)
                               .header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON).loader(loader).send(internalCallback);
        } catch (Exception e) {
            callback.onFailure(e);
        }
    }

    /**
     * Reads or Refreshes all project properties.
     *
     * @param callback
     *         callback
     */
    public void refreshProperties(final AsyncCallback<Project> callback) {
        final Array<Property> currentProperties = properties;

        AsyncRequestCallback<Array<Property>> internalCallback =
                new AsyncRequestCallback<Array<Property>>(new PropertyUnmarshaller()) {
                    @Override
                    protected void onSuccess(Array<Property> properties) {
                        // Update properties on client-side Object
                        currentProperties.clear();
                        currentProperties.addAll(properties);

                        eventBus.fireEvent(ProjectActionEvent.createProjectDescriptionChangedEvent(project));

                        callback.onSuccess(Project.this);
                    }

                    @Override
                    protected void onFailure(Throwable exception) {
                        callback.onFailure(exception);
                    }
                };

        // get JSON for this Project
        String url = vfsInfo.getUrlTemplates().get(Link.REL_ITEM).getHref();
        url = URL.decode(url).replace("[id]", id);
        asyncRequestFactory.createGetRequest(URL.encode(url)).loader(loader).send(internalCallback);
    }

    /**
     * @param callback
     *         callback
     */
    public void search(final AsyncCallback<Array<Resource>> callback) {
        callback.onFailure(new Exception("Operation not currently supported"));
    }

    // ====================================================================================================

    /**
     * Check if resource belongs to this project
     *
     * @param resource
     *         resource to check
     */
    protected void checkItemValid(final Resource resource) throws Exception {
        if (resource == null) {
            throw new Exception("Resource is null.");
        }
        if (resource.getProject() != this) {
            throw new Exception("Resource is out of the project's scope. Project : " + getName() + ", resource path is : "
                                + resource.getPath());
        }
    }

    public void setProjectType(String projectTypeId) {
        this.projectTypeId = projectTypeId;
    }

    String getProjectTypeId() {
        return projectTypeId;
    }
}
