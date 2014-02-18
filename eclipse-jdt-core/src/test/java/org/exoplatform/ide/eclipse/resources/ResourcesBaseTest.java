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
package org.exoplatform.ide.eclipse.resources;

import com.codenvy.commons.env.EnvironmentContext;
import com.codenvy.eclipse.core.resources.ResourcesPlugin;
import com.codenvy.eclipse.resources.WorkspaceResource;

import org.everrest.core.RequestHandler;
import org.everrest.core.ResourceBinder;
import org.everrest.core.impl.ApplicationContextImpl;
import org.everrest.core.impl.ApplicationProviderBinder;
import org.everrest.core.impl.ApplicationPublisher;
import org.everrest.core.impl.EverrestConfiguration;
import org.everrest.core.impl.ProviderBinder;
import org.everrest.core.impl.RequestDispatcher;
import org.everrest.core.impl.RequestHandlerImpl;
import org.everrest.core.impl.ResourceBinderImpl;
import org.everrest.core.tools.DependencySupplierImpl;
import org.everrest.core.tools.ResourceLauncher;
import org.exoplatform.ide.vfs.server.URLHandlerFactorySetup;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.VirtualFileSystemApplication;
import org.exoplatform.ide.vfs.server.VirtualFileSystemRegistry;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.server.impl.memory.MemoryFileSystemProvider;
import org.exoplatform.ide.vfs.server.impl.memory.context.MemoryFileSystemContext;
import org.exoplatform.ide.vfs.server.observation.EventListenerList;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.Identity;
import org.exoplatform.services.security.MembershipEntry;
import org.junit.After;
import org.junit.Before;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public abstract class ResourcesBaseTest {

    protected static final String ID = "memory";

    protected static EventListenerList eventListenerList;

    protected static VirtualFileSystemRegistry virtualFileSystemRegistry = new VirtualFileSystemRegistry();

    static {
        URLHandlerFactorySetup.setup(virtualFileSystemRegistry, eventListenerList);
    }


    protected static MemoryFileSystemContext memoryContext;

    protected static VirtualFileSystem vfs;

    protected static WorkspaceResource ws;

    @Before
    public void setUp() throws Exception {

        System.setProperty("org.exoplatform.mimetypes", "conf/mimetypes.properties");

        eventListenerList = new EventListenerList();
        memoryContext = new MemoryFileSystemContext();
        EnvironmentContext env = EnvironmentContext.getCurrent();
        env.setWorkspaceId(ID);
        env.setWorkspaceName(ID);

        virtualFileSystemRegistry.registerProvider(ID, new MemoryFileSystemProvider(ID, memoryContext));
        vfs = virtualFileSystemRegistry.getProvider(ID).newInstance(null, eventListenerList);
        if (ws == null) {
            ws = new WorkspaceResource(vfs);
            ResourcesPlugin.addWorkspace(ws);
        } else {
            ws.setVfs(vfs);
        }
        ConversationState.setCurrent(new ConversationState(new Identity("test")));
        DependencySupplierImpl dependencies = new DependencySupplierImpl();
        dependencies.addComponent(VirtualFileSystemRegistry.class, virtualFileSystemRegistry);
        dependencies.addComponent(EventListenerList.class, eventListenerList);
        ResourceBinder resources = new ResourceBinderImpl();
        ProviderBinder providers = new ApplicationProviderBinder();
        RequestHandler requestHandler = new RequestHandlerImpl(new RequestDispatcher(resources), providers, dependencies,
                                                               new EverrestConfiguration());
        ApplicationContextImpl.setCurrent(new ApplicationContextImpl(null, null, ProviderBinder.getInstance()));
        ResourceLauncher launcher = new ResourceLauncher(requestHandler);

        ApplicationPublisher deployer = new ApplicationPublisher(resources, providers);
        deployer.publish(new VirtualFileSystemApplication());

        // RUNTIME VARIABLES
        ConversationState.setCurrent(new ConversationState(new Identity("ide", new ArrayList<MembershipEntry>(0), Arrays.asList("workspace/developer"))));
        

    }

    @After
    public void clean() throws VirtualFileSystemException {
        virtualFileSystemRegistry.unregisterProvider(ID);
    }
}
