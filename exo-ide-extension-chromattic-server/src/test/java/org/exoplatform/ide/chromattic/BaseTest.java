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
package org.exoplatform.ide.chromattic;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.jcr.Node;
import javax.jcr.ValueFactory;
import javax.jcr.Workspace;

import junit.framework.TestCase;

import org.exoplatform.container.StandaloneContainer;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.core.CredentialsImpl;
import org.exoplatform.services.jcr.dataflow.PersistentDataManager;
import org.exoplatform.services.jcr.impl.core.RepositoryImpl;
import org.exoplatform.services.jcr.impl.core.SessionImpl;
import org.exoplatform.services.jcr.impl.dataflow.serialization.ReaderSpoolFileHolder;
import org.exoplatform.services.jcr.impl.util.io.FileCleaner;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.RequestHandler;
import org.exoplatform.services.rest.impl.ResourceBinder;
import org.exoplatform.services.rest.tools.ResourceLauncher;
import org.junit.Before;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/

public abstract class BaseTest extends TestCase
{
   
   protected final Log log = ExoLogger.getLogger(BaseTest.class.getCanonicalName());

   public static final String REPOSITORY = "db1";
   
   public static final String WORKSPACE = "ws";

   protected SessionImpl session;

   protected RepositoryImpl repository;

   protected CredentialsImpl credentials;

   protected Workspace workspace;

   protected RepositoryService repositoryService;

   protected Node root;

   protected PersistentDataManager dataManager;

   protected ValueFactory valueFactory;

   protected StandaloneContainer container;

   public int maxBufferSize = 200 * 1024;

   public FileCleaner fileCleaner;

   public ReaderSpoolFileHolder holder;

   public ResourceBinder binder;

   public ResourceLauncher launcher;

   public int resourceNumber = 0;

   @Before
   public void setUp() throws Exception
   {
      String containerConf = BaseTest.class.getResource("/conf/standalone/test-configuration.xml").toString();

      StandaloneContainer.addConfigurationURL(containerConf);

      container = StandaloneContainer.getInstance();

      if (System.getProperty("java.security.auth.login.config") == null)
         System.setProperty("java.security.auth.login.config", Thread.currentThread().getContextClassLoader()
            .getResource("login.conf").toString());

      credentials = new CredentialsImpl("root", "exo".toCharArray());

      repositoryService = (RepositoryService)container.getComponentInstanceOfType(RepositoryService.class);

      repository = (RepositoryImpl)repositoryService.getRepository(REPOSITORY);

      session = (SessionImpl)repository.login(credentials, WORKSPACE);
      workspace = session.getWorkspace();
      root = session.getRootNode();
      valueFactory = session.getValueFactory();

      binder = (ResourceBinder)container.getComponentInstanceOfType(ResourceBinder.class);
      resourceNumber = binder.getSize();
      RequestHandler handler = (RequestHandler)container.getComponentInstanceOfType(RequestHandler.class);
      launcher = new ResourceLauncher(handler);

   }

//   @After
//   protected void tearDown() throws Exception
//   {
//      if (session != null)
//      {
//         try
//         {
//            session.refresh(false);
//            Node rootNode = session.getRootNode();
//            if (rootNode.hasNodes())
//            {
//               // clean test root
//               for (NodeIterator children = rootNode.getNodes(); children.hasNext();)
//               {
//                  Node node = children.nextNode();
//                  if (!node.getPath().startsWith("/jcr:system") && !node.getPath().startsWith("/exo:audit")
//                     && !node.getPath().startsWith("/exo:organization"))
//                  {
//                     node.remove();
//                  }
//               }
//               session.save();
//            }
//         }
//         catch (Exception e)
//         {
//            e.printStackTrace();
//            log.error("===== Exception in tearDown() " + e.toString());
//         }
//         finally
//         {
//            session.logout();
//         }
//      }
//
//      super.tearDown();
//      // log.info("tearDown() END " + getClass().getName() + "." + getName());
//   }

   public byte[] getResourceAsBytes(String resource) throws IOException
   {
      byte[] data = null;
      InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);
      if (stream != null)
      {
         ByteArrayOutputStream bout = new ByteArrayOutputStream();
         byte[] buf = new byte[1024];
         int r = -1;
         while ((r = stream.read(buf)) != -1)
         {
            bout.write(buf, 0, r);
         }
         data = bout.toByteArray();
      }
      return data;
   }
   
}
