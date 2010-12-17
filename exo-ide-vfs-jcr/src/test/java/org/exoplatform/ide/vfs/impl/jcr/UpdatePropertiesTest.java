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

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class UpdatePropertiesTest extends JcrFileSystemTest
{
   private Node updatePropertiesTestNode;

   private String document;

   @Override
   protected void setUp() throws Exception
   {
      super.setUp();
      String name = getClass().getName();
      updatePropertiesTestNode = testRoot.addNode(name, "nt:unstructured");
      updatePropertiesTestNode.addMixin("exo:privilegeable");
      updatePropertiesTestNode.addMixin("mix:lockable");

      Node documentNode = updatePropertiesTestNode.addNode("UpdatePropertiesTest_DOCUMENT", "nt:file");
      Node contentNode = documentNode.addNode("jcr:content", "nt:resource");
      contentNode.setProperty("jcr:mimeType", "text/plain");
      contentNode.setProperty("jcr:lastModified", Calendar.getInstance());
      contentNode.setProperty("jcr:data", new ByteArrayInputStream(DEFAULT_CONTENT.getBytes()));
      documentNode.addMixin("exo:unstructuredMixin");
      document = documentNode.getPath();

      session.save();
   }

   public void testUpdatePropertiesDocument() throws Exception
   {
      String properties = "[{\"name\":\"MyProperty\", \"value\":[\"MyValue\"]}]";
      String path = new StringBuilder() //
         .append("/vfs/jcr/db1/ws/properties") //
         .append(document) //
         .toString();
      Map<String, List<String>> h = new HashMap<String, List<String>>(1);
      h.put("Content-Type", Arrays.asList("application/json"));
      ContainerResponse response = launcher.service("POST", path, "", h, properties.getBytes(), null);
      assertEquals(204, response.getStatus());
      Node doc = (Node)session.getItem(document);
      assertEquals("MyValue", doc.getProperty("MyProperty").getString());
   }
}
