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
import com.codenvy.api.vfs.server.VirtualFileSystemProvider;

/**
 * @author andrew00x
 */
public class AutoMountVirtualFileSystemRegistryTest extends LocalFileSystemTest {
    public void testAutoMount() throws Exception {
        // new registry without any registered vfs providers
        AutoMountVirtualFileSystemRegistry registry =
                new AutoMountVirtualFileSystemRegistry(new WorkspaceHashLocalFSMountStrategy(root, root), new EventService(), null);
        final VirtualFileSystemProvider fileSystemProvider = registry.getProvider(MY_WORKSPACE_ID);
        assertEquals(MY_WORKSPACE_ID, fileSystemProvider.getWorkspaceId());
    }
}
