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
package org.exoplatform.ide.extension.java.server;

import com.codenvy.commons.env.EnvironmentContext;

import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.VirtualFileSystemRegistry;
import org.exoplatform.ide.vfs.server.exceptions.*;
import org.exoplatform.ide.vfs.server.observation.EventListenerList;
import org.exoplatform.ide.vfs.shared.Folder;
import org.exoplatform.ide.vfs.shared.PropertyFilter;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import java.io.IOException;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version ${Id}: Nov 29, 2011 2:11:05 PM evgen $
 */
public abstract class JavaDocBase extends Base {
    /**
     *
     */
    protected static final String VFS_ID = "ws";

    protected static VirtualFileSystem vfs;

    protected static Folder project;

    protected static VirtualFileSystemRegistry vfsRegistry;

    protected static JavaCodeAssistant javaCa;

    @BeforeClass
    public static void init() throws VirtualFileSystemException, IOException, InterruptedException {
        EnvironmentContext env = EnvironmentContext.getCurrent();
        env.setVariable(EnvironmentContext.WORKSPACE_ID, VFS_ID);
        env.setVariable(EnvironmentContext.WORKSPACE_NAME, VFS_ID);
        vfsRegistry = (VirtualFileSystemRegistry)container.getComponentInstanceOfType(VirtualFileSystemRegistry.class);
        EventListenerList eventListenerList = (EventListenerList)container.getComponentInstanceOfType(EventListenerList.class);
        vfs = vfsRegistry.getProvider(VFS_ID).newInstance(null, eventListenerList);
        try {
            project =
                    (Folder)vfs.getItemByPath(JavaDocBuilderVfsTest.class.getSimpleName(), null, false, PropertyFilter.NONE_FILTER);
            vfs.delete(project.getId(), null);
            project = vfs.createFolder(vfs.getInfo().getRoot().getId(), JavaDocBuilderVfsTest.class.getSimpleName());
        } catch (ItemNotFoundException e) {
            project = vfs.createFolder(vfs.getInfo().getRoot().getId(), JavaDocBuilderVfsTest.class.getSimpleName());
        }
        vfs.importZip(project.getId(),
                      Thread.currentThread().getContextClassLoader().getResourceAsStream("exo-ide-client.zip"), true);
        javaCa = new JavaCodeAssistant(null, vfsRegistry);
    }

    @AfterClass
    public static void cleanUp() throws ItemNotFoundException, ConstraintException, LockException,
                                        PermissionDeniedException, VirtualFileSystemException {
        vfs.delete(project.getId(), null);
    }
}
