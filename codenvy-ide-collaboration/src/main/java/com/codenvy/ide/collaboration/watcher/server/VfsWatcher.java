/*
 * Copyright (C) 2013 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.codenvy.ide.collaboration.watcher.server;

import com.codenvy.ide.collaboration.dto.Item.ItemType;
import com.codenvy.ide.collaboration.dto.ProjectOpenedDto;
import com.codenvy.ide.collaboration.dto.server.DtoServerImpls.*;
import com.google.gson.internal.Pair;

import org.everrest.websockets.WSConnection;
import org.everrest.websockets.WSConnectionContext;
import org.everrest.websockets.message.ChannelBroadcastMessage;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.VirtualFileSystemRegistry;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.server.observation.*;
import org.exoplatform.ide.vfs.server.observation.ChangeEvent.ChangeType;
import org.exoplatform.ide.vfs.server.observation.EventListener;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.Link;
import org.exoplatform.ide.vfs.shared.Property;
import org.exoplatform.ide.vfs.shared.PropertyFilter;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.picocontainer.Startable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class VfsWatcher implements Startable {
    private class WSConnectionListener implements org.everrest.websockets.WSConnectionListener {
        @Override
        public void onOpen(WSConnection connection) {
        }

        @Override
        public void onClose(WSConnection connection) {
            String userId = connection.getHttpSession().getId();
            sessionDestroyed(userId);
        }
    }

    private class EventListenerImpl implements EventListener {

        private String projectId;

        private EventListenerImpl(String projectId) {
            this.projectId = projectId;
        }

        @Override
        public void handleEvent(ChangeEvent event) throws VirtualFileSystemException {
            notifyUsers(event);
        }

        private void notifyUsers(ChangeEvent event) {

            //TODO when switch to FS VFS remove this
            String path = event.getItemPath();
            path = path.substring(0, path.indexOf("/", 1));
            try {
                Item project = event.getVirtualFileSystem().getItemByPath(path, null, false, PropertyFilter.ALL_FILTER);
                if (!projectId.equals(project.getId())) {
                    return;
                }
            } catch (VirtualFileSystemException e) {
                LOG.error("Can't find project: " + path, e);
                return;
            }
            String message;
            switch (event.getType()) {
                case CREATED:
                    ItemCreatedDtoImpl createdDto = ItemCreatedDtoImpl.make();
                    createdDto.setItem(getDtoItem(event.getVirtualFileSystem(), event.getItemId()));
                    createdDto.setUserId(event.getUser().getUserId());
                    message = createdDto.toJson();
                    break;
                case DELETED:
                    ItemDeletedDtoImpl deletedDto = ItemDeletedDtoImpl.make();
                    deletedDto.setFileId(event.getItemId());
                    deletedDto.setFilePath(event.getItemPath());
                    deletedDto.setUserId(event.getUser().getUserId());
                    message = deletedDto.toJson();
                    break;
                case MOVED:
                    ItemMovedDtoImpl movedDto = ItemMovedDtoImpl.make();
                    movedDto.setOldPath(event.getOldItemPath());
                    movedDto.setMovedItem(getDtoItem(event.getVirtualFileSystem(), event.getItemId()));
                    movedDto.setUserId(event.getUser().getUserId());
                    message = movedDto.toJson();
                    break;
                case RENAMED:
                    ItemRenamedDtoImpl renamedDto = ItemRenamedDtoImpl.make();
                    renamedDto.setOldPath(event.getOldItemPath());
                    renamedDto.setRenamedItem(getDtoItem(event.getVirtualFileSystem(), event.getItemId()));
                    renamedDto.setUserId(event.getUser().getUserId());
                    message = renamedDto.toJson();
                    break;
                default:
                    return;
            }
            Set<String> clientIds = new HashSet<String>(projectUsers.getProjectUsers(projectId));
            clientIds.removeAll(projectUsers.getClientIds(event.getUser().getUserId()));
            if (!clientIds.isEmpty() && message != null) {
                broadcastToClients(message, clientIds);
            }
        }
    }

    private ItemImpl getDtoItem(VirtualFileSystem vfs, String itemId) {
        try {
            Item item = vfs.getItem(itemId, false, PropertyFilter.ALL_FILTER);
            ItemImpl dtoItem = ItemImpl.make();
            dtoItem.setId(item.getId());
            dtoItem.setItemType(ItemType.fromValue(item.getItemType().value()));
            dtoItem.setMimeType(item.getMimeType());
            dtoItem.setName(item.getName());
            dtoItem.setPath(item.getPath());
            dtoItem.setParentId(item.getParentId());
            dtoItem.setProperties(convertProperties(item.getProperties()));
            dtoItem.setLinks(convertLinks(item.getLinks()));
            return dtoItem;
        } catch (VirtualFileSystemException e) {
            LOG.error("Can't find item: " + itemId, e);
        }
        return null;
    }

    private Map<String, LinkImpl> convertLinks(Map<String, Link> links) {
        Map<String, LinkImpl> converted = new HashMap<String, LinkImpl>(links.size());

        for (String key : links.keySet()) {
            LinkImpl link = LinkImpl.make();
            Link l = links.get(key);
            link.setHref(l.getHref());
            link.setRel(l.getRel());
            link.setTypeLink(l.getType());
            converted.put(key, link);
        }
        return converted;
    }

    private List<PropertyImpl> convertProperties(List<Property> properties) {
        List<PropertyImpl> prop = new ArrayList<PropertyImpl>(properties.size());
        for (Property p : properties) {
            PropertyImpl property = PropertyImpl.make();
            property.setName(p.getName());
            property.setValue(p.getValue());
            prop.add(property);
        }
        return prop;
    }

    private static final Log LOG = ExoLogger.getLogger(VfsWatcher.class);

    /** Map of per project listener. */
    private final ConcurrentMap<String, Pair<ChangeEventFilter, EventListener>> vfsListeners =
            new ConcurrentHashMap<String, Pair<ChangeEventFilter, EventListener>>();

    private WSConnectionListener listener;

    private VirtualFileSystemRegistry vfsRegistry;

    private EventListenerList listeners;

    private ProjectUsers projectUsers;

    public VfsWatcher(VirtualFileSystemRegistry vfsRegistry, EventListenerList listeners, ProjectUsers projectUsers) {
        this.vfsRegistry = vfsRegistry;

        this.listeners = listeners;
        this.projectUsers = projectUsers;
    }

    public void openProject(String clientId, String userId, ProjectOpenedDto dto) {

        if (!projectUsers.hasProject(dto.projectId())) {
            addListenerToProject(dto.projectId(), dto.vfsId(), dto.projectPath());
        }
        projectUsers.addProjectUser(dto.projectId(), clientId, userId);
    }

    private void addListenerToProject(String projectId, String vfsId, String projectPath) {
        LOG.debug("Add VFS listener for {} project", projectPath);
        EventListenerImpl eventListener = new EventListenerImpl(projectId);

        ChangeEventFilter filter = ChangeEventFilter.createAndFilter(new VfsIDFilter(vfsId),
                                                                     new PathFilter(projectPath + "/.*"),
                                                                     ChangeEventFilter.createOrFilter( // created, deleted, renamed or moved
                                                                                                       new TypeFilter(ChangeType.CREATED),//
                                                                                                       new TypeFilter(ChangeType.DELETED),//
                                                                                                       new TypeFilter(ChangeType.RENAMED),//
                                                                                                       new TypeFilter(ChangeType.MOVED)));

        listeners.addEventListener(filter, eventListener);
        Pair<ChangeEventFilter, EventListener> pair = new Pair<ChangeEventFilter, EventListener>(filter, eventListener);
        vfsListeners.putIfAbsent(projectId, pair);

    }

    public void closeProject(String clientId, String userId, String projectId) {
        if (projectUsers.hasProject(projectId)) {
            projectUsers.removeProjectUser(projectId, clientId, userId);
            if (!projectUsers.hasProject(projectId)) {
                LOG.debug("Remove VFS listener for {} project", projectId);
                Pair<ChangeEventFilter, EventListener> pair = vfsListeners.remove(projectId);
                if(pair != null){
                   listeners.removeEventListener(pair.first, pair.second);
                }
            }

        }
    }


    public static void broadcastToClients(String message, Set<String> collaborators) {
        for (String collaborator : collaborators) {
            ChannelBroadcastMessage broadcastMessage = new ChannelBroadcastMessage();
            broadcastMessage.setChannel("vfs_watcher." + collaborator);
            broadcastMessage.setBody(message);
            try {
                WSConnectionContext.sendMessage(broadcastMessage);
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }

    public void sessionDestroyed(String clientId) {
        String projectId = projectUsers.getProjectId(clientId);
        String userId = projectUsers.getUserId(clientId);
        if (projectId != null && userId != null) {
            closeProject(clientId, userId, projectId);
        }
    }

    @Override
    public void start() {
        listener = new WSConnectionListener();
        WSConnectionContext.registerConnectionListener(listener);
    }

    @Override
    public void stop() {
        WSConnectionContext.removeConnectionListener(listener);
    }
}
