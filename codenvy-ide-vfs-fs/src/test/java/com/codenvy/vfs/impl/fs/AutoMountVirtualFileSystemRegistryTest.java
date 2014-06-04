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
