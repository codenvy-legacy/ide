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
import com.codenvy.api.project.shared.dto.TreeElement;
import com.codenvy.ide.MimeType;
import com.codenvy.ide.api.event.ProjectActionEvent;
import com.codenvy.ide.api.event.ResourceChangedEvent;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.resources.marshal.FileUnmarshaller;
import com.codenvy.ide.resources.marshal.FolderTreeUnmarshaller;
import com.codenvy.ide.resources.marshal.FolderUnmarshaller;
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

import java.util.List;
import java.util.Map;

/**
 * Represents Project model. Responsible for deserialization of JSon String to generate it' own project model.
 *
 * @author Nikolay Zamosenchuk
 */
public class Project extends Folder {
    public static final String PROJECT_MIME_TYPE = "text/vnd.ideproject+directory";
    public static final String TYPE              = "project";
    protected final EventBus                  eventBus;
    protected final AsyncRequestFactory       asyncRequestFactory;
    private final   DtoUnmarshallerFactory    dtoUnmarshallerFactory;
    /** Properties. */
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
    public Project(EventBus eventBus,
                   AsyncRequestFactory asyncRequestFactory,
                   ProjectServiceClient projectServiceClient,
                   DtoUnmarshallerFactory dtoUnmarshallerFactory) {
        super(TYPE, PROJECT_MIME_TYPE);
        this.projectServiceClient = projectServiceClient;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.description = new ProjectDescription(this);
        this.properties = Collections.<Property>createArray();
        this.eventBus = eventBus;
        this.asyncRequestFactory = asyncRequestFactory;
        // TODO : receive it in some way
        this.loader = new EmptyLoader();
    }

    public void setAttributes(Map<String, List<String>> attributes) {
        this.attributes = attributes;
    }

    public String getAttributeValue(String attributeName) {
        return attributes.get(attributeName).get(0);
    }

    public List<String> getAttributeValues(String attributeName) {
        return attributes.get(attributeName);
    }

    public void init(ItemReference itemReference) {
        name = itemReference.getName();
        mimeType = itemReference.getMediaType();
    }

    @Override
    public void init(JSONObject itemObject) {
        id = itemObject.get("id").isString().stringValue();
        name = itemObject.get("name").isString().stringValue();
        mimeType = itemObject.get("mimeType").isString().stringValue();
        //path = itemObject.get("path").isString().stringValue();
        //parentId = itemObject.get("parentId").isString().stringValue();
        creationDate = (long)itemObject.get("creationDate").isNumber().doubleValue();
        properties = JSONDeserializer.PROPERTY_DESERIALIZER.toList(itemObject.get("properties"));
        links = JSONDeserializer.LINK_DESERIALIZER.toMap(itemObject.get("links"));
//        projectType = (itemObject.get("projectType") != null) ? itemObject.get("projectType").isString().stringValue() : null;
    }

    public void setVFSInfo(VirtualFileSystemInfo vfsInfo) {
        this.vfsInfo = vfsInfo;
    }

    public ProjectDescription getDescription() {
        return description;
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

            // create internal wrapping Request Callback with proper Unmarshaller
            AsyncRequestCallback<File> internalCallback = new AsyncRequestCallback<File>(new FileUnmarshaller()) {
                @Override
                protected void onSuccess(final File newFile) {
                    refreshTree(parent, new AsyncCallback<Folder>() {
                        @Override
                        public void onSuccess(Folder result) {
                            File file = (File)result.findResourceById(newFile.getId());
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
            };

            String url = parent.getLinkByRelation(Link.REL_CREATE_FILE).getHref();
            url = URL.decode(url).replace("[name]", name);
            url = URL.encode(url);
            loader.setMessage("Creating new file...");
            asyncRequestFactory.createPostRequest(url, null).data(content).header(HTTPHeader.CONTENT_TYPE, mimeType)
                               .loader(loader).send(internalCallback);
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

            // create internal wrapping Request Callback with proper Unmarshaller
            AsyncRequestCallback<Folder> internalCallback = new AsyncRequestCallback<Folder>(new FolderUnmarshaller()) {
                @Override
                protected void onSuccess(final Folder newFolder) {
                    refreshTree(parent, new AsyncCallback<Folder>() {
                        @Override
                        public void onSuccess(Folder result) {
                            Folder folder = (Folder)result.findResourceById(newFolder.getId());
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
            };

            String url = parent.getLinkByRelation(Link.REL_CREATE_FOLDER).getHref();
            String urlString = URL.decode(url).replace("[name]", name);
            urlString = URL.encode(urlString);
            loader.setMessage("Creating new folder...");
            asyncRequestFactory.createPostRequest(urlString, null).loader(loader).send(internalCallback);
        } catch (Exception e) {
            callback.onFailure(e);
        }
    }

    /**
     * Reads or Refreshes full Project Structure tree. This can be a costly operation.
     *
     * @param callback
     *         callback
     */
    public void refreshTree(final AsyncCallback<Project> callback) {
        refreshTree(this, new AsyncCallback<Folder>() {
            @Override
            public void onSuccess(Folder result) {
                eventBus.fireEvent(ResourceChangedEvent.createResourceTreeRefreshedEvent(Project.this));
                callback.onSuccess(Project.this);
            }

            @Override
            public void onFailure(Throwable exception) {
                callback.onFailure(exception);
            }
        });
    }

    /**
     * If new folder created with relative path, but not the name, i.e. "new_parent/parent/parentC/newFolder", then
     * need to refresh the tree of the folders, since new folders may have been created by the server-side.
     *
     * @param root
     *         root folder to refresh
     * @param callback
     *         callback
     */
    public void refreshTree(final Folder root, final AsyncCallback<Folder> callback) {
        projectServiceClient.getTree(root.getPath(), -1,
                                     new AsyncRequestCallback<TreeElement>(dtoUnmarshallerFactory.newUnmarshaller(TreeElement.class)) {
                                         @Override
                                         protected void onSuccess(TreeElement result) {
                                             FolderTreeUnmarshaller unmarshaller = new FolderTreeUnmarshaller(root, root.getProject());
                                             unmarshaller.unmarshal(result);
                                             callback.onSuccess(unmarshaller.getPayload());
                                         }

                                         @Override
                                         protected void onFailure(Throwable exception) {
                                             callback.onFailure(exception);
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
            final Folder parent = resource.getParent();
            // create internal wrapping Request Callback with proper Unmarshaller
            AsyncRequestCallback<Void> internalCallback = new AsyncRequestCallback<Void>() {
                @Override
                protected void onSuccess(Void result) {
                    // remove from the list of child
                    parent.removeChild(resource);
                    eventBus.fireEvent(ResourceChangedEvent.createResourceDeletedEvent(resource));
                    callback.onSuccess(result);
                }

                @Override
                protected void onFailure(Throwable exception) {
                    callback.onFailure(exception);
                }
            };

            // TODO check with lock
            String url = resource.getLinkByRelation(Link.REL_DELETE).getHref();

            if (File.TYPE.equals(resource.getResourceType()) && ((File)resource).isLocked()) {
                url = URL.decode(url).replace("[lockToken]", ((File)resource).getLock().getLockToken());
            }
            loader.setMessage("Deleting item...");
            asyncRequestFactory.createPostRequest(url, null).loader(loader).send(internalCallback);
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
     * Lock file.
     *
     * @param file
     *         file to lock
     * @param callback
     *         callback
     */
    public void lock(File file, final AsyncCallback<String> callback) {
        try {
            checkItemValid(file);
            // create internal wrapping Request Callback with proper Unmarshaller
            AsyncRequestCallback<String> internalCallback = new AsyncRequestCallback<String>(new StringUnmarshaller()) {
                @Override
                protected void onSuccess(String result) {
                    callback.onSuccess(result);
                }

                @Override
                protected void onFailure(Throwable exception) {
                    callback.onFailure(exception);
                }
            };

            String url = file.getLinkByRelation(Link.REL_LOCK).getHref();
            loader.setMessage("Locking file...");
            asyncRequestFactory.createPostRequest(url, null).loader(loader).send(internalCallback);
        } catch (Exception e) {
            callback.onFailure(e);
        }
    }

    /**
     * Unlock file.
     *
     * @param file
     *         file to unlock
     * @param lockToken
     *         lock token
     * @param callback
     *         callback
     */
    public void unlock(File file, String lockToken, final AsyncCallback<Void> callback) {
        try {
            checkItemValid(file);
            // create internal wrapping Request Callback with proper Unmarshaller
            AsyncRequestCallback<Void> internalCallback = new AsyncRequestCallback<Void>() {
                @Override
                protected void onSuccess(Void result) {
                    callback.onSuccess(result);
                }

                @Override
                protected void onFailure(Throwable exception) {
                    callback.onFailure(exception);
                }
            };

            String url = file.getLinkByRelation(Link.REL_UNLOCK).getHref();
            url = URL.decode(url).replace("[lockToken]", lockToken);
            loader.setMessage("Unlocking file...");
            asyncRequestFactory.createPostRequest(url, null).loader(loader).send(internalCallback);
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
        try {
            checkItemValid(source);
            checkItemValid(destination);

            AsyncRequestCallback<Void> internalCallback = new AsyncRequestCallback<Void>() {
                @Override
                protected void onSuccess(Void result) {
                    // TODO : check consistency
                    source.getParent().removeChild(source);
                    destination.addChild(source);

                    eventBus.fireEvent(ResourceChangedEvent.createResourceMovedEvent(source));
                    callback.onSuccess(source);
                }

                @Override
                protected void onFailure(Throwable exception) {
                    callback.onFailure(exception);
                }
            };

            // TODO check with locks
            String url = source.getLinkByRelation(Link.REL_MOVE).getHref();
            url = URL.decode(url).replace("[parentId]", destination.getId());
            if (File.TYPE.equals(source.getResourceType()) && ((File)source).isLocked()) {
                url = URL.decode(url).replace("[lockToken]", ((File)source).getLock().getLockToken());
            }
            url = URL.encode(url);
            loader.setMessage("Moving item...");
            asyncRequestFactory.createPostRequest(url, null).loader(loader).send(internalCallback);
        } catch (Exception e) {
            callback.onFailure(e);
        }
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
        callback.onFailure(new Exception("copy not supported"));
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
            Unmarshallable<Resource> unmarshaller =
                    (Unmarshallable<Resource>)((resource instanceof File) ? new FileUnmarshaller() : new FolderUnmarshaller());
            // internal call back
            AsyncRequestCallback<Resource> internalCallback = new AsyncRequestCallback<Resource>(unmarshaller) {
                @Override
                protected void onSuccess(Resource result) {
                    final String id = result.getId();
                    final Folder folderToRefresh =
                            (resource instanceof Project && resource.getParent().getId().equals(vfsInfo.getRoot().getId()))
                            ? (Project)resource : resource.getParent();
                    //Renamed the project:
                    if (resource instanceof Project && resource.getParent().getId().equals(vfsInfo.getRoot().getId())) {
                        ((Project)resource).setName(result.getName());
                        ((Project)resource).setId(result.getId());
                        ((Project)resource).getLinks().putAll(result.getLinks());
                    }

                    refreshTree(folderToRefresh, new AsyncCallback<Folder>() {
                        @Override
                        public void onSuccess(Folder result) {
                            Resource renamed =
                                    (resource instanceof Project && resource.getParent().getId().equals(vfsInfo.getRoot().getId()))
                                    ? resource : result.findResourceById(id);
                            renamed.getParent().setTag(folderToRefresh.getTag());
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
            };

            String url = resource.getLinkByRelation(Link.REL_RENAME).getHref();
            url = URL.decode(url);
            url = url.replace("mediaType=[mediaType]", "");
            url =
                    (newName != null && !newName.isEmpty()) ? url.replace("[newname]", newName) : url.replace(
                            "newname=[newname]", "");

            if (File.TYPE.equals(resource.getResourceType()) && ((File)resource).isLocked()) {
                url = URL.decode(url).replace("[lockToken]", ((File)resource).getLock().getLockToken());
            }

            url = url.replace("?&", "?");
            url = url.replaceAll("&&", "&");
            url = URL.encode(url);
            loader.setMessage("Renaming item...");
            asyncRequestFactory.createPostRequest(url, null).loader(loader).send(internalCallback);
        } catch (Exception e) {
            callback.onFailure(e);
        }
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

    // TODO
    public void setProjectType(String projectTypeId) {
        this.projectTypeId = projectTypeId;
    }

    String getProjectTypeId() {
        return projectTypeId;
    }
}
