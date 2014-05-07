/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 *  [2012] - [2014] Codenvy, S.A.
 *  All Rights Reserved.
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
package com.codenvy.vfs.impl.fs;

import com.codenvy.api.core.notification.EventService;
import com.codenvy.api.core.notification.EventSubscriber;
import com.codenvy.api.vfs.server.MountPoint;
import com.codenvy.api.vfs.server.VirtualFileSystemRegistry;
import com.codenvy.api.vfs.server.exceptions.VirtualFileSystemException;
import com.codenvy.api.workspace.server.observation.CreateWorkspaceEvent;
import com.codenvy.api.workspace.server.observation.DeleteWorkspaceEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * This class provide execution required actions in virtual file system when workspace was created or deleted
 *
 * @author Sergii Leschenko
 */
@Singleton
public class ConsistentProvider {
    private final EventService          eventService;
    private final List<EventSubscriber> subscribers;

    /** Create a root folder for VFS, when workspace was created */
    @Singleton
    private static class VFSRootCreator implements EventSubscriber<CreateWorkspaceEvent> {
        private static final Logger LOG = LoggerFactory.getLogger(VFSRootCreator.class);

        private final LocalFSMountStrategy mountStrategy;

        @Inject
        VFSRootCreator(LocalFSMountStrategy mountStrategy) {
            this.mountStrategy = mountStrategy;
        }

        @Override
        public void onEvent(CreateWorkspaceEvent event) {
            try {
                File wsPath = mountStrategy.getMountPath(event.getWorkspaceId());
                if (!wsPath.exists()) {
                    if (!wsPath.mkdirs()) {
                        LOG.warn("Can not create root folder for workspace VFS {}", event.getWorkspaceId());
                    }
                }
            } catch (VirtualFileSystemException e) {
                LOG.warn("Can not calculate path to root folder for workspace VFS {}", event.getWorkspaceId());
            }
        }
    }

    /** Remove root folder for VFS when workspace was deleted */
    @Singleton
    private static class VFSRootRemover implements EventSubscriber<DeleteWorkspaceEvent> {
        private static final Logger LOG = LoggerFactory.getLogger(VFSRootRemover.class);

        private final VirtualFileSystemRegistry fileSystemRegistry;

        @Inject
        VFSRootRemover(VirtualFileSystemRegistry fileSystemRegistry) {
            this.fileSystemRegistry = fileSystemRegistry;
        }

        @Override
        public void onEvent(DeleteWorkspaceEvent event) {
            try {
                MountPoint mountPoint = fileSystemRegistry.getProvider(event.getWorkspaceId()).getMountPoint(false);
                File rootFolder = ((VirtualFileImpl)mountPoint.getRoot()).getIoFile();
                fileSystemRegistry.unregisterProvider(event.getWorkspaceId());
                if (!rootFolder.delete()) {
                    LOG.warn("Can not delete Virtual File System linked to workspace {}", event.getWorkspaceId());
                }
            } catch (VirtualFileSystemException e) {
                LOG.warn("Can not get mount point of for workspace {}", event.getWorkspaceId());
            }
        }
    }

    @Inject
    public ConsistentProvider(EventService eventService, VirtualFileSystemRegistry vfsRegistry,
                              LocalFSMountStrategy mountStrategy) {
        this.eventService = eventService;
        subscribers = new LinkedList<EventSubscriber>(Arrays.asList(
                new VFSRootCreator(mountStrategy),
                new VFSRootRemover(vfsRegistry)));
    }

    @PostConstruct
    private void subscribe() {
        for (EventSubscriber eventSubscriber : subscribers) {
            eventService.subscribe(eventSubscriber);
        }
    }

    @PreDestroy
    private void unsubscribe(VFSRootCreator creator, VFSRootRemover remover) {
        for (EventSubscriber eventSubscriber : subscribers) {
            eventService.unsubscribe(eventSubscriber);
        }
    }
}