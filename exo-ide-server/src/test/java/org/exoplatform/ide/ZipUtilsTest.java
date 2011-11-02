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
package org.exoplatform.ide;

import org.exoplatform.ide.zip.ZipUtils;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.core.CredentialsImpl;
import org.exoplatform.services.jcr.impl.core.RepositoryImpl;
import org.exoplatform.services.jcr.impl.core.SessionImpl;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;

/**
 * @author <a href="mailto:oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: Dec 16, 2010 $
 *
 */
public class ZipUtilsTest extends BaseTest
{
   
   private static Log log = ExoLogger.getLogger(ZipUtilsTest.class);

   private static String WORKSPACE = "dev-monit";

   private SessionImpl session;

   private RepositoryImpl repository;

   private CredentialsImpl credentials;

   private RepositoryService repositoryService;

   @Before
   public void setUp() throws Exception
   {
      super.setUp();
      credentials = new CredentialsImpl("root", "exo".toCharArray());
      repositoryService = (RepositoryService)container.getComponentInstanceOfType(RepositoryService.class);
      repository = (RepositoryImpl)repositoryService.getDefaultRepository();
      session = (SessionImpl)repository.login(credentials, WORKSPACE);
      Assert.assertNotNull(session);
      
   }
   
   @Test
   public void testZippingFolder() throws RepositoryException, IOException
   {
      Node rootNode = session.getRootNode();
      
      rootNode.addNode("test", "nt:folder");
      
      Node base = rootNode.getNode("test");
      String filePath = "file.txt";
      base = base.addNode(filePath, "nt:file");
      base = base.addNode("jcr:content", "nt:resource");
      base.setProperty("jcr:data", "sample data");
      base.setProperty("jcr:lastModified", Calendar.getInstance());
      base.setProperty("jcr:mimeType", "text/plain");
      
      Node node = (Node)session.getItem("/test");
      
      ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
      
      ZipUtils.writeZip(byteStream, node);
      
      byte[] bytes = byteStream.toByteArray();
      
      ByteArrayInputStream byteInputStream = new ByteArrayInputStream(bytes);
      byte[] buf = new byte[1024];
      ZipInputStream zin = new ZipInputStream(byteInputStream);
      
      ZipEntry zipentry;

      zipentry = zin.getNextEntry();
      if (zipentry == null)
         Assert.fail("Zip archive is empty");
      
      List<String> items = new ArrayList<String>();
      
      while (zipentry != null)
      {
         String entryName = zipentry.getName();
         if (zipentry.isDirectory())
         {
            items.add(entryName);
            Assert.assertEquals("test/", entryName);
         }
         else
         {
            items.add(entryName);
            Assert.assertEquals("test/file.txt", entryName);
            
            int bytesRead;
            ByteArrayOutputStream outS = new ByteArrayOutputStream();

            while ((bytesRead = zin.read(buf, 0, 1024)) > -1)
            {
               outS.write(buf, 0, bytesRead);
            }
            String data = outS.toString();
            outS.close();

            Assert.assertEquals("sample data", data);
         }
         zin.closeEntry();
         zipentry = zin.getNextEntry();

      }//while
      zin.close();
      
      Assert.assertEquals(2, items.size());
   }
   
   public void tearDown() throws Exception
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
                  if (!node.getPath().startsWith("/jcr:system") && !node.getPath().startsWith("/exo:registry"))
                  {
                     node.remove();
                  }
               }
               session.save();
            }
         }
         catch (Exception e)
         {
            log.error("tearDown() ERROR " + getClass().getName() + " " + e, e);
         }
         finally
         {
            session.logout();
         }
      }
      
   }

}
