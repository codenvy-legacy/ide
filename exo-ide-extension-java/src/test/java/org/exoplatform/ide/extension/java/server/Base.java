/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package org.exoplatform.ide.extension.java.server;

import org.everrest.core.RequestHandler;
import org.everrest.core.ResourceBinder;
import org.everrest.core.tools.DummySecurityContext;
import org.everrest.core.tools.ResourceLauncher;
import org.everrest.test.mock.MockPrincipal;
import org.exoplatform.container.StandaloneContainer;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.core.CredentialsImpl;
import org.exoplatform.services.jcr.dataflow.PersistentDataManager;
import org.exoplatform.services.jcr.ext.app.SessionProviderService;
import org.exoplatform.services.jcr.ext.app.ThreadLocalSessionProviderService;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.jcr.impl.core.RepositoryImpl;
import org.exoplatform.services.jcr.impl.core.SessionImpl;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.Identity;
import org.junit.BeforeClass;

import java.util.HashSet;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.ValueFactory;
import javax.jcr.Workspace;


/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Base Mar 30, 2011 11:35:14 AM evgen $
 *
 */

public class Base
{

   public static final String WS_NAME = "ws";
   
   public static final String REPO_NAME = "db1";

   protected static SessionImpl session;

   protected static RepositoryImpl repository;

   protected static CredentialsImpl credentials;

   protected static Workspace workspace;

   protected static RepositoryService repositoryService;

   protected static Node root;

   protected PersistentDataManager dataManager;

   protected static ValueFactory valueFactory;
   
   protected static SessionProviderService sessionProviderService;

   protected static StandaloneContainer container;

   public int maxBufferSize = 200 * 1024;

   public static ResourceBinder binder;

   public static ResourceLauncher launcher;

   public static int resourceNumber = 0;
   
   protected static DummySecurityContext adminSecurityContext;

   protected final Log log = ExoLogger.getLogger(this.getClass().getSimpleName());
   
   @BeforeClass
   public static void setUp() throws Exception
   {
      String containerConf = Base.class.getResource("/conf/standalone/test-configuration.xml").toString();

      StandaloneContainer.addConfigurationURL(containerConf);

      container = StandaloneContainer.getInstance();

      if (System.getProperty("java.security.auth.login.config") == null)
         System.setProperty("java.security.auth.login.config", Thread.currentThread().getContextClassLoader()
            .getResource("login.conf").toString());

      credentials = new CredentialsImpl("root", "exo".toCharArray());

      repositoryService = (RepositoryService)container.getComponentInstanceOfType(RepositoryService.class);

      repository = (RepositoryImpl)repositoryService.getRepository("db1");

      session = (SessionImpl)repository.login(credentials, WS_NAME);
      workspace = session.getWorkspace();
      root = session.getRootNode();
      valueFactory = session.getValueFactory();

      binder = (ResourceBinder)container.getComponentInstanceOfType(ResourceBinder.class);
      resourceNumber = binder.getSize();
      RequestHandler handler = (RequestHandler)container.getComponentInstanceOfType(RequestHandler.class);
      System.out.println("Base.setUp()" + handler);
      launcher = new ResourceLauncher(handler);
      sessionProviderService =
         (SessionProviderService)container.getComponentInstanceOfType(ThreadLocalSessionProviderService.class);
      ConversationState state = new ConversationState(new Identity("root"));
      SessionProvider sessionProvider = new SessionProvider(state);
      ConversationState.setCurrent(state);
      sessionProvider.setCurrentRepository(repository);
      sessionProviderService.setSessionProvider(null, sessionProvider);
      Set<String> adminRoles = new HashSet<String>();
      adminRoles.add("administrators");
      Set<String> devRoles = new HashSet<String>();
      devRoles.add("developers");
      adminSecurityContext = new DummySecurityContext(new MockPrincipal("root"), adminRoles);
   }
   

}
