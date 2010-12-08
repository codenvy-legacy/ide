/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package org.exoplatform.ide.vfs.impl.jcr;

import junit.framework.TestCase;

import org.exoplatform.container.StandaloneContainer;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.core.CredentialsImpl;
import org.exoplatform.services.jcr.core.ManageableRepository;
import org.exoplatform.services.jcr.ext.app.ThreadLocalSessionProviderService;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.RequestHandler;
import org.exoplatform.services.rest.impl.ApplicationContextImpl;
import org.exoplatform.services.rest.impl.ProviderBinder;
import org.exoplatform.services.rest.tools.ResourceLauncher;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.Identity;

import javax.jcr.Node;
import javax.jcr.Session;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public abstract class JcrFileSystemTest extends TestCase
{
   protected Log log = ExoLogger.getExoLogger(getClass());

   protected String REPOSITORY_NAME = "db1";

   protected String WORKSPACE_NAME = "ws";

   protected String TEST_ROOT_NAME = "TESTROOT";

   protected Session session;

   protected Node testRoot;

   protected ResourceLauncher launcher;

   private ThreadLocalSessionProviderService sessionFactory;

   /**
    * @see junit.framework.TestCase#setUp()
    */
   @Override
   protected void setUp() throws Exception
   {
      super.setUp();

      String conf = getClass().getResource("/conf/standalone/test-configuration.xml").toString();
      StandaloneContainer.setConfigurationURL(conf);
      StandaloneContainer container = StandaloneContainer.getInstance();

      String loginConf = getClass().getResource("/login.conf").toString();
      if (System.getProperty("java.security.auth.login.config") == null)
         System.setProperty("java.security.auth.login.config", loginConf);

      // JCR
      RepositoryService repositoryService =
         (RepositoryService)container.getComponentInstanceOfType(RepositoryService.class);
      ManageableRepository repository = repositoryService.getRepository(REPOSITORY_NAME);
      session = (Session)repository.login(new CredentialsImpl("root", "exo".toCharArray()), WORKSPACE_NAME);
      testRoot = session.getRootNode().addNode(TEST_ROOT_NAME, "nt:unstructured");
      session.save();

      // REST
      RequestHandler requestHandler = (RequestHandler)container.getComponentInstanceOfType(RequestHandler.class);
      ApplicationContextImpl.setCurrent(new ApplicationContextImpl(null, null, ProviderBinder.getInstance()));
      launcher = new ResourceLauncher(requestHandler);

      // RUNTIME VARIABLES
      ConversationState user = new ConversationState(new Identity("john"));
      ConversationState.setCurrent(user);
      sessionFactory =
         (ThreadLocalSessionProviderService)container
            .getComponentInstanceOfType(ThreadLocalSessionProviderService.class);
      sessionFactory.setSessionProvider(null, new SessionProvider(user));
   }

   /**
    * @see junit.framework.TestCase#tearDown()
    */
   protected void tearDown() throws Exception
   {
      try
      {
         if (session != null)
         {
            if (testRoot != null)
            {
               testRoot.remove();
               session.save();
            }
            session.logout();
         }
         if (sessionFactory != null)
         {
            sessionFactory.getSessionProvider(null).close();
         }
      }
      catch (Exception e)
      {
         log.error(e.getMessage(), e);
      }
      super.tearDown();
   }
}
