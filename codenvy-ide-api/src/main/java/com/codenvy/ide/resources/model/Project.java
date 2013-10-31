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

import com.codenvy.ide.api.event.ProjectActionEvent;
import com.codenvy.ide.api.event.ResourceChangedEvent;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.resources.marshal.*;
import com.codenvy.ide.rest.AsyncRequest;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.HTTPHeader;
import com.codenvy.ide.rest.MimeType;
import com.codenvy.ide.ui.loader.EmptyLoader;
import com.codenvy.ide.ui.loader.Loader;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.resources.client.ResourceException;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;


/**
 * Represents Project  model. Responsinble for deserialization of JSon String to generate it' own project model
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a>
 */
public class Project extends Folder {
    public static final String PROJECT_MIME_TYPE = "text/vnd.ideproject+directory";
    public static final String TYPE              = "project";
    private         ProjectDescription    description;
    /** Properties. */
    protected       JsonArray<Property>   properties;
    protected       Loader                loader;
    protected final EventBus              eventBus;
    protected       VirtualFileSystemInfo vfsInfo;

    /**
     * Constructor for empty project. Used for serialization only.
     * <p/>
     * Not intended to be used by client.
     */
    public Project(EventBus eventBus) {
        super(TYPE, PROJECT_MIME_TYPE);
        this.description = new ProjectDescription(this);
        this.properties = JsonCollections.<Property>createArray();
        this.eventBus = eventBus;
        // TODO : receive it in some way
        this.loader = new EmptyLoader();
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
        //projectType = (itemObject.get("projectType") != null) ? itemObject.get("projectType").isString().stringValue() : null;
        // TODO Unmarshall children
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
    public JsonArray<Property> getProperties() {
        if (properties == null) {
            properties = JsonCollections.<Property>createArray();
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
        JsonArray<Property> props = getProperties();
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
    public JsonArray<String> getPropertyValues(String name) {
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
     * @param name
     * @param content
     * @param mimeType
     * @param callback
     * @throws ResourceException
     */
    public void createFile(final Folder parent, final String name, String content, String mimeType, final AsyncCallback<File> callback) {
        try {
            checkItemValid(parent);

            // create internal wrapping Request Callback with proper Unmarshaller
            AsyncRequestCallback<File> internalCallback = new AsyncRequestCallback<File>(new FileUnmarshaller()) {
                @Override
                protected void onSuccess(final File newFile) {
                    if (name.contains("/")) {
                        // refresh tree, cause additional hierarchy folders my have been created
                        refreshTree(parent, new AsyncCallback<Folder>() {
                            @Override
                            public void onSuccess(Folder result) {
                                eventBus.fireEvent(ResourceChangedEvent.createResourceCreatedEvent(newFile));
                                callback.onSuccess(newFile);
                            }

                            @Override
                            public void onFailure(Throwable exception) {
                                callback.onFailure(exception);
                            }
                        });
                    } else {
                        // add to the list of items
                        parent.addChild(newFile);
                        // set proper parent project
                        newFile.setProject(Project.this);
                        eventBus.fireEvent(ResourceChangedEvent.createResourceCreatedEvent(newFile));
                        callback.onSuccess(newFile);
                    }
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
            AsyncRequest.build(RequestBuilder.POST, url).data(content).header(HTTPHeader.CONTENT_TYPE, mimeType)
                        .loader(loader).send(internalCallback);
        } catch (Exception e) {
            callback.onFailure(e);
        }
    }

    /**
     * Create new Folder.
     *
     * @param parent
     * @param name
     * @param callback
     * @throws ResourceException
     */
    public void createFolder(final Folder parent, final String name, final AsyncCallback<Folder> callback) {
        try {
            checkItemValid(parent);

            // create internal wrapping Request Callback with proper Unmarshaller
            AsyncRequestCallback<Folder> internalCallback = new AsyncRequestCallback<Folder>(new FolderUnmarshaller()) {
                @Override
                protected void onSuccess(final Folder folder) {
                    if (name.contains("/")) {
                        // refresh tree, cause additional hierarchy folders my have been created
                        refreshTree(parent, new AsyncCallback<Folder>() {
                            @Override
                            public void onSuccess(Folder result) {
                                Folder newFolder = (Folder)result.findResourceById(folder.getId());
                                eventBus.fireEvent(ResourceChangedEvent.createResourceCreatedEvent(newFolder));
                                callback.onSuccess(newFolder);
                            }

                            @Override
                            public void onFailure(Throwable exception) {
                                callback.onFailure(exception);
                            }
                        });
                    } else {
                        // add to the list of items
                        parent.addChild(folder);
                        // set proper parent project
                        folder.setProject(Project.this);
                        eventBus.fireEvent(ResourceChangedEvent.createResourceCreatedEvent(folder));
                        callback.onSuccess(folder);
                    }
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
            AsyncRequest.build(RequestBuilder.POST, urlString).loader(loader).send(internalCallback);
        } catch (Exception e) {
            callback.onFailure(e);
        }
    }

    /**
     * Reads or Refreshes full Project Structure tree. This can be a costly operation, since
     *
     * @param callback
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
     * @param newFolderId
     * @param callback
     */
    protected void refreshTree(final Folder root, final AsyncCallback<Folder> callback) {
        try {
            // create internal wrapping Request Callback with proper Unmarshaller
            AsyncRequestCallback<Folder> internalCallback =
                    new AsyncRequestCallback<Folder>(new FolderTreeUnmarshaller(root, root.getProject())) {
                        @Override
                        protected void onSuccess(Folder refreshedRoot) {
                            callback.onSuccess(refreshedRoot);
                        }

                        @Override
                        protected void onFailure(Throwable exception) {
                            callback.onFailure(exception);
                        }
                    };

            String url = vfsInfo.getUrlTemplates().get(Link.REL_TREE).getHref();
            url = URL.decode(url).replace("[id]", root.getId());
            AsyncRequest.build(RequestBuilder.GET, URL.encode(url)).loader(loader).send(internalCallback);
        } catch (Exception e) {
            callback.onFailure(e);
        }
    }

    /**
     * Delete child resource
     *
     * @param resource
     * @param callback
     * @throws ResourceException
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
            AsyncRequest.build(RequestBuilder.POST, url).loader(loader).send(internalCallback);
        } catch (Exception e) {
            callback.onFailure(e);
        }
    }

    /**
     * @param file
     * @param callback
     * @throws ResourceException
     */
    public void getContent(File file, final AsyncCallback<File> callback) {
        try {
            checkItemValid(file);

            // content already present
            if (file.getContent() != null) {
                callback.onSuccess(file);
                return;
            }

            // create internal wrapping Request Callback with proper Unmarshaller
            AsyncRequestCallback<File> internalCallback = new AsyncRequestCallback<File>(new FileContentUnmarshaller(file)) {
                @Override
                protected void onSuccess(File result) {
                    callback.onSuccess(result);
                }

                @Override
                protected void onFailure(Throwable exception) {
                    callback.onFailure(exception);
                }
            };

            String url = file.getLinkByRelation(Link.REL_CONTENT).getHref();
            loader.setMessage("Loading content...");
            AsyncRequest.build(RequestBuilder.GET, url).loader(loader).send(internalCallback);
        } catch (Exception e) {
            callback.onFailure(e);
        }
    }

    /**
     * @param file
     * @param callback
     * @throws ResourceException
     */
    public void updateContent(File file, final AsyncCallback<File> callback) {
        try {
            checkItemValid(file);
            // create internal wrapping Request Callback with proper Unmarshaller
            AsyncRequestCallback<File> internalCallback = new AsyncRequestCallback<File>(new FileContentUnmarshaller(file)) {
                @Override
                protected void onSuccess(File result) {
                    callback.onSuccess(result);
                }

                @Override
                protected void onFailure(Throwable exception) {
                    callback.onFailure(exception);
                }
            };

            // TODO check with lock
            String url = file.getLinkByRelation(Link.REL_CONTENT).getHref();
            url += (file.isLocked()) ? "?lockToken=" + file.getLock().getLockToken() : "";
            loader.setMessage("Updating content...");
            AsyncRequest.build(RequestBuilder.POST, url).header(HTTPHeader.CONTENT_TYPE, file.getMimeType())
                        .data(file.getContent()).loader(loader).send(internalCallback);
        } catch (Exception e) {
            callback.onFailure(e);
        }
    }

    /**
     * @param file
     * @param callback
     * @throws ResourceException
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
            AsyncRequest.build(RequestBuilder.POST, url).loader(loader).send(internalCallback);
        } catch (Exception e) {
            callback.onFailure(e);
        }
    }

    /**
     * @param file
     * @param lockToken
     * @param callback
     * @throws ResourceException
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
            AsyncRequest.build(RequestBuilder.POST, url).loader(loader).send(internalCallback);
        } catch (Exception e) {
            callback.onFailure(e);
        }
    }

    /**
     * @param source
     * @param destination
     * @param lockToken
     * @param callback
     * @throws ResourceException
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
            AsyncRequest.build(RequestBuilder.POST, url).loader(loader).send(internalCallback);
        } catch (Exception e) {
            callback.onFailure(e);
        }
    }

    /**
     * @param source
     * @param destination
     * @param callback
     * @throws ResourceException
     */
    public void copy(final Resource source, final Folder destination, final AsyncCallback<Resource> callback) {
        callback.onFailure(new Exception("copy not supported"));
    }

    /**
     * @param item
     * @param mediaType
     * @param newname
     * @param lockToken
     * @param callback
     * @throws RequestException
     */
    public void rename(final Resource resource, final String newname, String lockToken, final AsyncCallback<Resource> callback) {
        try {
            checkItemValid(resource);

            // internal call back
            AsyncRequestCallback<Void> internalCallback = new AsyncRequestCallback<Void>() {
                @Override
                protected void onSuccess(Void result) {
                    // TODO : check consistency
                    resource.setName(newname);
                    eventBus.fireEvent(ResourceChangedEvent.createResourceRenamedEvent(resource));
                    callback.onSuccess(resource);
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
                    (newname != null && !newname.isEmpty()) ? url.replace("[newname]", newname) : url.replace(
                            "newname=[newname]", "");

            if (File.TYPE.equals(resource.getResourceType()) && ((File)resource).isLocked()) {
                url = URL.decode(url).replace("[lockToken]", ((File)resource).getLock().getLockToken());
            }

            url = url.replace("?&", "?");
            url = url.replaceAll("&&", "&");
            url = URL.encode(url);
            loader.setMessage("Renaming item...");
            AsyncRequest.build(RequestBuilder.POST, url).loader(loader).send(internalCallback);
        } catch (Exception e) {
            callback.onFailure(e);
        }
    }

    /**
     * @param callback
     * @throws ResourceException
     */
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
            AsyncRequest.build(RequestBuilder.POST, url)
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
     */
    public void refreshProperties(final AsyncCallback<Project> callback) {
        try {
            final JsonArray<Property> currentProperties = properties;

            AsyncRequestCallback<JsonArray<Property>> internalCallback =
                    new AsyncRequestCallback<JsonArray<Property>>(new PropertyUnmarshaller()) {
                        @Override
                        protected void onSuccess(JsonArray<Property> properties) {
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
            String url = vfsInfo.getUrlTemplates().get((Link.REL_ITEM)).getHref();
            url = URL.decode(url).replace("[id]", id);
            AsyncRequest.build(RequestBuilder.GET, URL.encode(url)).loader(loader).send(internalCallback);
        } catch (RequestException e) {
            callback.onFailure(e);
        }
    }

    /**
     * @param source
     * @param destination
     * @param callback
     * @throws ResourceException
     */
    public void search(final AsyncCallback<JsonArray<Resource>> callback) {
        callback.onFailure(new Exception("Operation not currently supported"));
    }

    // ====================================================================================================

    /**
     * Check if resource belongs to this project
     *
     * @param resource
     * @throws ResourceException
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
}
