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
package com.codenvy.ide.collaboration.watcher.client;

import com.codenvy.ide.client.util.logging.Log;
import com.codenvy.ide.collaboration.dto.Item;
import com.codenvy.ide.collaboration.dto.ItemCreatedDto;
import com.codenvy.ide.collaboration.dto.ItemDeletedDto;
import com.codenvy.ide.collaboration.dto.ItemMovedDto;
import com.codenvy.ide.collaboration.dto.ItemRenamedDto;
import com.codenvy.ide.collaboration.dto.Link;
import com.codenvy.ide.collaboration.dto.ProjectOpenedResponseDto;
import com.codenvy.ide.collaboration.dto.ProjectOperationNotification;
import com.codenvy.ide.collaboration.dto.Property;
import com.codenvy.ide.collaboration.dto.RoutingTypes;
import com.codenvy.ide.collaboration.dto.client.DtoClientImpls.ProjectClosedDtoImpl;
import com.codenvy.ide.collaboration.dto.client.DtoClientImpls.ProjectOpenedDtoImpl;
import com.codenvy.ide.dtogen.shared.ServerError;
import com.codenvy.ide.json.shared.JsonArray;
import com.codenvy.ide.json.shared.JsonStringMap;
import com.codenvy.ide.json.shared.JsonStringMap.IterationCallback;
import com.codenvy.ide.notification.Notification;
import com.codenvy.ide.notification.NotificationManager;
import com.google.gwt.event.shared.HandlerManager;

import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.project.ProjectClosedEvent;
import org.exoplatform.ide.client.framework.project.ProjectClosedHandler;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedHandler;
import org.exoplatform.ide.client.framework.project.api.IDEProject;
import org.exoplatform.ide.client.framework.websocket.FrontendApi;
import org.exoplatform.ide.client.framework.websocket.MessageFilter;
import org.exoplatform.ide.client.framework.websocket.MessageFilter.MessageRecipient;
import org.exoplatform.ide.client.framework.websocket.events.ConnectionOpenedHandler;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.client.model.ItemContext;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.LinkImpl;
import org.exoplatform.ide.vfs.shared.PropertyImpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class VfsWatcher implements ProjectOpenedHandler, ProjectClosedHandler {

    public static final int DURATION = 7000;

    private CollaborationApi    collaborationApi;
    private NotificationManager notificationManager;

    private ProjectModel project;

    public VfsWatcher(MessageFilter messageFilter, HandlerManager handlerManager, CollaborationApi collaborationApi,
                      NotificationManager notificationManager) {
        this.collaborationApi = collaborationApi;
        this.notificationManager = notificationManager;
        handlerManager.addHandler(ProjectClosedEvent.TYPE, this);
        handlerManager.addHandler(ProjectOpenedEvent.TYPE, this);
        messageFilter.registerMessageRecipient(RoutingTypes.ITEM_MOVED, new MessageRecipient<ItemMovedDto>() {
            @Override
            public void onMessageReceived(ItemMovedDto message) {
                IDEProject ideProject = (IDEProject)project;
                ideProject.removeItem(message.oldPath());
                Item messageItem = message.movedItem();
                ideProject.addItem(convertDto2VfsItem(messageItem));
                Notification notification =
                        new Notification("User " + message.getUserId() + " move: " + message.oldPath() + " to " + messageItem.getPath(),
                                         DURATION);
                NotificationManager.get().addNotification(notification);
            }
        });

        messageFilter.registerMessageRecipient(RoutingTypes.ITEM_RENAMED, new MessageRecipient<ItemRenamedDto>() {
            @Override
            public void onMessageReceived(ItemRenamedDto message) {
                IDEProject ideProject = (IDEProject)project;
                org.exoplatform.ide.vfs.shared.Item resource = ideProject.getResource(message.oldPath());
                Item messageItem = message.renamedItem();
                resource.setPath(messageItem.getPath());
                resource.setName(messageItem.getName());
                resource.setId(messageItem.getId());
                resource.getLinks().putAll(convertDto2VfsLinks(messageItem.getLinks()));
                String mimeType = messageItem.getMimeType();
                if (mimeType.contains(";")) {
                    mimeType = mimeType.substring(0, mimeType.indexOf(';'));
                }
                resource.setMimeType(mimeType);
                ideProject.notifyFolderChanged(((ItemContext)resource).getParent().getPath());
                Notification notification =
                        new Notification("User " + message.getUserId() + " rename: " + message.oldPath() + " to " + messageItem.getName(),
                                         DURATION);
                NotificationManager.get().addNotification(notification);
            }
        });

        messageFilter.registerMessageRecipient(RoutingTypes.ITEM_CREATED, new MessageRecipient<ItemCreatedDto>() {
            @Override
            public void onMessageReceived(ItemCreatedDto message) {
                Item messageItem = message.getItem();
                Notification notification = new Notification("User " + message.getUserId() + " created: " + message.getItem().getPath(),
                                                             DURATION);
                NotificationManager.get().addNotification(notification);
                ((IDEProject)project).addItem(convertDto2VfsItem(messageItem));
            }
        });

        messageFilter.registerMessageRecipient(RoutingTypes.ITEM_DELETED, new MessageRecipient<ItemDeletedDto>() {
            @Override
            public void onMessageReceived(ItemDeletedDto message) {
                ((IDEProject)project).removeItem(message.getFilePath());
                Notification notification = new Notification("User " + message.getUserId() + " deleted: " + message.getFilePath(),
                                                             DURATION);
                NotificationManager.get().addNotification(notification);
            }
        });
        messageFilter.registerMessageRecipient(RoutingTypes.PROJECT_OPERATION_NOTIFICATION,
                                               new MessageRecipient<ProjectOperationNotification>() {
                                                   @Override
                                                   public void onMessageReceived(ProjectOperationNotification message) {
                                                       showProjectNotification(message);
                                                   }
                                               });
        IDE.messageBus().setOnOpenHandler(new ConnectionOpenedHandler() {
            @Override
            public void onConnectionOpened() {
                if (project != null) {
                    addVfsListener();
                }
            }
        });
    }

    private void showProjectNotification(ProjectOperationNotification message) {
        notificationManager.addNotification(new Notification(message.message(), -1));
    }

    private org.exoplatform.ide.vfs.shared.Item convertDto2VfsItem(Item messageItem) {
        org.exoplatform.ide.vfs.shared.Item item = null;
        switch (messageItem.getItemType()) {
            case FILE:
                //hack for isPersisted()
                item = new FileModel(new FileModel());
                break;
            case FOLDER:
                item = new FolderModel();
                break;
            case PROJECT:
                item = new ProjectModel();
                break;
        }
        String mimeType = messageItem.getMimeType();
        if (mimeType != null) {
            if (mimeType.contains(";")) {
                mimeType = mimeType.substring(0, mimeType.indexOf(';'));
            }
            item.setMimeType(mimeType);
        }
        item.setId(messageItem.getId());
        item.setParentId(messageItem.getParentId());
        item.setName(messageItem.getName());
        item.setPath(messageItem.getPath());
        item.getLinks().putAll(convertDto2VfsLinks(messageItem.getLinks()));
        item.getProperties().addAll(convertDto2VfsProperties(messageItem.getProperties()));
        return item;
    }

    private Collection<org.exoplatform.ide.vfs.shared.Property> convertDto2VfsProperties(JsonArray<Property> properties) {
        List<org.exoplatform.ide.vfs.shared.Property> vfsProperties = new ArrayList<org.exoplatform.ide.vfs.shared.Property>(
                properties.size());
        for (Property p : properties.asIterable()) {
            PropertyImpl property = new PropertyImpl(p.getName(), copyIterator(p.getValue().asIterable().iterator()));
            vfsProperties.add(property);
        }
        return vfsProperties;
    }

    public static <T> List<T> copyIterator(Iterator<T> iter) {
        List<T> copy = new ArrayList<T>();
        while (iter.hasNext()) {
            copy.add(iter.next());
        }
        return copy;
    }

    private Map<String, org.exoplatform.ide.vfs.shared.Link> convertDto2VfsLinks(JsonStringMap<Link> links) {
        final Map<String, org.exoplatform.ide.vfs.shared.Link> vfsLinks = //
                new HashMap<String, org.exoplatform.ide.vfs.shared.Link>(links.size());

        links.iterate(new IterationCallback<Link>() {
            @Override
            public void onIteration(String key, Link value) {
                LinkImpl li = new LinkImpl(value.getHref(), value.getRel(), value.getTypeLink());
                vfsLinks.put(key, li);
            }
        });

        return vfsLinks;
    }

    @Override
    public void onProjectClosed(ProjectClosedEvent event) {
        ProjectClosedDtoImpl dto = ProjectClosedDtoImpl.make();
        dto.setProjectPath(project.getPath());
        dto.setVfsId(VirtualFileSystem.getInstance().getInfo().getId());
        dto.setProjectId(project.getId());
        collaborationApi.PROJECT_CLOSED.send(dto);
        project = null;
    }

    @Override
    public void onProjectOpened(ProjectOpenedEvent event) {
        project = event.getProject();
        addVfsListener();
    }

    private void addVfsListener() {
        ProjectOpenedDtoImpl dto = ProjectOpenedDtoImpl.make();
        dto.setProjectPath(project.getPath());
        dto.setVfsId(VirtualFileSystem.getInstance().getInfo().getId());
        dto.setProjectId(project.getId());
        collaborationApi.PROJECT_OPEN.send(dto, new FrontendApi.ApiCallback<ProjectOpenedResponseDto>() {
            @Override
            public void onFail(ServerError.FailureReason reason) {
                Log.error(VfsWatcher.class, reason);
            }

            @Override
            public void onMessageReceived(ProjectOpenedResponseDto message) {
                IDE.fireEvent(new ProjectParticipantsReceivedEvent(message.projectParticipants()));
            }
        });
    }
}
