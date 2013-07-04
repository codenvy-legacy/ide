/*
 * Copyright (C) 2012 eXo Platform SAS.
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
        env.setVariable(EnvironmentContext.WORKSPACE_ID, ID);
        env.setVariable(EnvironmentContext.WORKSPACE_NAME, ID);

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
        ConversationState.setCurrent(new ConversationState(new Identity("ide", new ArrayList<MembershipEntry>(0), Arrays.asList("developer"))));
        

    }

    @After
    public void clean() throws VirtualFileSystemException {
        virtualFileSystemRegistry.unregisterProvider(ID);
    }
}
