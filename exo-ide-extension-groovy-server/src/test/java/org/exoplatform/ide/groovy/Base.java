/*
 * Copyright (C) 2003-2010 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ide.groovy;

import org.exoplatform.container.StandaloneContainer;
import org.exoplatform.ide.groovy.codeassistant.bean.TypeInfo;
import org.exoplatform.ide.groovy.codeassistant.extractors.TypeInfoExtractor;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.core.CredentialsImpl;
import org.exoplatform.services.jcr.dataflow.PersistentDataManager;
import org.exoplatform.services.jcr.ext.app.SessionProviderService;
import org.exoplatform.services.jcr.ext.app.ThreadLocalSessionProviderService;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.jcr.impl.core.RepositoryImpl;
import org.exoplatform.services.jcr.impl.core.SessionImpl;
import org.exoplatform.services.jcr.impl.dataflow.serialization.ReaderSpoolFileHolder;
import org.exoplatform.services.jcr.impl.util.io.FileCleaner;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.RequestHandler;
import org.exoplatform.services.rest.impl.ResourceBinder;
import org.exoplatform.services.rest.tools.DummySecurityContext;
import org.exoplatform.services.rest.tools.ResourceLauncher;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.Identity;
import org.exoplatform.services.test.mock.MockPrincipal;
import org.exoplatform.ws.frameworks.json.impl.JsonException;
import org.exoplatform.ws.frameworks.json.impl.JsonGeneratorImpl;
import org.junit.After;
import org.junit.Before;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import javax.jcr.AccessDeniedException;
import javax.jcr.InvalidItemStateException;
import javax.jcr.ItemExistsException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.ValueFactory;
import javax.jcr.ValueFormatException;
import javax.jcr.Workspace;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.version.VersionException;

import junit.framework.TestCase;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/

public class Base extends TestCase
{
   public static final String WS_NAME = "ws";

   protected SessionImpl session;

   protected RepositoryImpl repository;

   protected CredentialsImpl credentials;

   protected Workspace workspace;

   protected RepositoryService repositoryService;

   protected Node root;

   protected PersistentDataManager dataManager;

   protected ValueFactory valueFactory;
   
   protected SessionProviderService sessionProviderService;

   protected StandaloneContainer container;

   public int maxBufferSize = 200 * 1024;

   public FileCleaner fileCleaner;

   public ReaderSpoolFileHolder holder;

   public ResourceBinder binder;

   public ResourceLauncher launcher;

   public int resourceNumber = 0;
   
   protected DummySecurityContext adminSecurityContext;

   protected final Log log = ExoLogger.getLogger(this.getClass().getSimpleName());

   @Before
   public void setUp() throws Exception
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

   @After
   protected void tearDown() throws Exception
   {
      
      if (session != null)
      {
         try
         {
            session.refresh(false);
            Node rootNode = session.getRootNode();
            if (rootNode.hasNodes())
            {
               // clean test root
               for (NodeIterator children = rootNode.getNodes(); children.hasNext();)
               {
                  Node node = children.nextNode();
                  if (!node.getPath().startsWith("/jcr:system") && !node.getPath().startsWith("/exo:audit")
                     && !node.getPath().startsWith("/exo:organization") && !node.getPath().startsWith("/dev-doc"))
                  {
                     node.remove();
                  }
               }
               session.save();
            }
         }
         catch (Exception e)
         {
            e.printStackTrace();
            log.error("===== Exception in tearDown() " + e.toString());
         }
         finally
         {
            session.logout();
         }
      }

      super.tearDown();
      // log.info("tearDown() END " + getClass().getName() + "." + getName());
   }

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
   
   protected void putClass(String fqn) throws RepositoryException, IncompatibleClassChangeError, ValueFormatException, JsonException,
   InvalidItemStateException
{
   Node base;
   if (!session.getRootNode().hasNode("classpath"))
   {
      base = session.getRootNode().addNode("classpath", "nt:folder");

   }
   else
   {
      base = session.getRootNode().getNode("classpath");
   }

   try
   {
      String clazz = fqn;
      TypeInfo cd = TypeInfoExtractor.extract(ClassLoader.getSystemClassLoader().loadClass(clazz));
      Node child = base;
      String[] seg = fqn.split("\\.");
      String path = new String();
      for (int i = 0; i < seg.length - 1; i++)
      {
         path = path + seg[i];
         if (!child.hasNode(path))
         {
            child = child.addNode(path, "nt:folder");
         }
         else
         {
            child = child.getNode(path);
         }
         path = path + ".";
      }

      if (!child.hasNode(clazz))
      {
         child = child.addNode(clazz, "nt:file");
         child = child.addNode("jcr:content", "exoide:classDescription");
         JsonGeneratorImpl jsonGenerator = new JsonGeneratorImpl();
         child.setProperty("jcr:data", jsonGenerator.createJsonObject(cd).toString());
         child.setProperty("jcr:lastModified", Calendar.getInstance());
         child.setProperty("jcr:mimeType", "text/plain");
         child.setProperty("exoide:className", clazz.substring(clazz.lastIndexOf(".") + 1));
         child.setProperty("exoide:fqn", clazz);
      }
   }
   catch (ClassNotFoundException e)
   {
      e.printStackTrace();
   }

}
}
