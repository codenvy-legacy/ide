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

import org.exoplatform.services.rest.impl.ContainerResponse;
import org.exoplatform.services.rest.tools.ByteArrayContainerResponseWriter;

import java.io.ByteArrayInputStream;
import java.util.Calendar;

import javax.jcr.Node;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class VersionsTest extends JcrFileSystemTest
{
   private Node versionsTestNode;

   private String document;

   /**
    * @see org.exoplatform.ide.vfs.impl.jcr.JcrFileSystemTest#setUp()
    */
   @Override
   protected void setUp() throws Exception
   {
      super.setUp();
      String name = getClass().getName();
      versionsTestNode = testRoot.addNode(name, "nt:unstructured");
      versionsTestNode.addMixin("exo:privilegeable");

      Node documentNode = versionsTestNode.addNode("VersionsTest_DOCUMENT", "nt:file");
      Node contentNode = documentNode.addNode("jcr:content", "nt:resource");
      contentNode.setProperty("jcr:mimeType", "text/plain");
      contentNode.setProperty("jcr:lastModified", Calendar.getInstance());
      contentNode.setProperty("jcr:data", new ByteArrayInputStream("__TEST__".getBytes()));
      documentNode.addMixin("mix:versionable");
      session.save();

      documentNode.checkin();
      documentNode.checkout();
      contentNode.setProperty("jcr:data", new ByteArrayInputStream("__TEST__001".getBytes()));
      session.save();

      documentNode.checkin();
      documentNode.checkout();
      contentNode.setProperty("jcr:data", new ByteArrayInputStream("__TEST__002".getBytes()));
      session.save();

      document = documentNode.getPath();
   }
   
   public void testGetVersions() throws Exception
   {
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String path = new StringBuilder() //
         .append("/vfs/db1/ws/versions") //
         .append(document) //
         .toString();
      ContainerResponse response = launcher.service("GET", path, "", null, null, writer, null);
      assertEquals(200, response.getStatus());
      // TODO
      log.info(">>>>>> "+new String(writer.getBody()));
   }

   public void testGetVersionById() throws Exception
   {
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      String path = new StringBuilder() //
         .append("/vfs/db1/ws/version") //
         .append(document) //
         .append("/2") //
         .toString();
      ContainerResponse response = launcher.service("GET", path, "", null, null, writer, null);
      assertEquals(200, response.getStatus());
      // TODO
      log.info(">>>>>> "+new String(writer.getBody()));
   }
}
