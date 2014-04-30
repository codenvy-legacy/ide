/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 *  [2012] - [2013] Codenvy, S.A.
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

import com.codenvy.api.core.notification.EventSubscriber;
import com.codenvy.api.vfs.server.exceptions.VirtualFileSystemException;
import com.codenvy.api.vfs.server.observation.UpdateACLEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Files;

/** @author Sergii Leschenko */
// This class will send signal for cache update in ide2.
// When ide2 doesn't use own implementation of MountPoint then this listener will be redundant
public class _IdeOldCacheUpdater_ implements EventSubscriber<UpdateACLEvent> {
    private static final Logger LOG = LoggerFactory.getLogger(_IdeOldCacheUpdater_.class);
    private LocalFSMountStrategy localFSMountStrategy;

    @Inject
    public _IdeOldCacheUpdater_(LocalFSMountStrategy localFSMountStrategy) {
        this.localFSMountStrategy = localFSMountStrategy;
    }

    @Override
    public void onEvent(UpdateACLEvent event) {
        String pathToVFSRoot;
        try {
            pathToVFSRoot = localFSMountStrategy.getMountPath(event.getWorkspaceId()) + event.getPath();
        } catch (VirtualFileSystemException e) {
            LOG.warn("Can not get path to workspace {} for cache update in ide2", event.getWorkspaceId());
            return;
        }

        java.io.File cacheResetDir = new java.io.File(pathToVFSRoot, FSMountPoint.SERVICE_DIR + java.io.File.separatorChar + "cache");
        if (!(cacheResetDir.exists() || cacheResetDir.mkdirs())) {
            LOG.warn("Unable to create folder {} for cache update in ide2", cacheResetDir.getPath());
        } else {
            java.nio.file.Path resetFilePath = new java.io.File(cacheResetDir, "reset_ide_old").toPath();
            if (!Files.exists(resetFilePath)) {
                try {
                    Files.createFile(resetFilePath);
                } catch (IOException e) {
                    LOG.warn("Unable to create file {} for cache update in ide2", resetFilePath.toAbsolutePath());
                }
            }
        }
    }
}
