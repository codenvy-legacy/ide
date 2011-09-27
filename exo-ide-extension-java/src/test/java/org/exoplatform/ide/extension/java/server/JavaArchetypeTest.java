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

import org.exoplatform.ide.testframework.server.MockVirtualFileSystem;
import org.exoplatform.ide.vfs.server.ConvertibleProperty;
import org.exoplatform.ide.vfs.server.exceptions.InvalidArgumentException;
import org.exoplatform.ide.vfs.server.exceptions.ItemNotFoundException;
import org.exoplatform.ide.vfs.server.exceptions.PermissionDeniedException;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.shared.File;
import org.exoplatform.ide.vfs.shared.Folder;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.Project;
import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class JavaArchetypeTest extends Base
{

   private Map<String, Item> items = new HashMap<String, Item>();

   String[] assertnames = {"/java-spring-project/src/main/webapp",
      "/java-spring-project/src/main/webapp/WEB-INF/spring-servlet.xml",
      "/java-spring-project/src/main/java/test/Product.java", "/java-spring-project",
      "/java-spring-project/src/main/java/test/CartController.java",
      "/java-spring-project/src/main/webapp/WEB-INF/jsp", "/java-spring-project/src/main/webapp/WEB-INF/web.xml",
      "/java-spring-project/pom.xml", "/java-spring-project/src/main/webapp/WEB-INF"};

   private String groupId = UUID.randomUUID().toString();

   private String artifactId = UUID.randomUUID().toString();

   private String versionId = UUID.randomUUID().toString();

   private String modifiedPom;

   @Test
   public void createProject() throws Exception
   {
      JavaProjectArchetype archetype =  new JavaProjectArchetype();
      URL url = JavaArchetypeTest.class.getResource("/java-spring-project");
      archetype.exportResources(url, "java-spring","java-spring-project", groupId, artifactId, versionId, "/", new MockVfs());
      for (int i = 0; i < assertnames.length; i++)
      {
         Assert.assertTrue(items.containsKey(assertnames[i]));
      }

   }
   
   @Test
   public void checkPom() throws Exception
   {
      JavaProjectArchetype archetype =
         new JavaProjectArchetype();
      URL url = JavaArchetypeTest.class.getResource("/java-spring-project");
      archetype.exportResources(url, "java-spring","java-spring-project", groupId, artifactId, versionId, "/", new MockVfs());
     Assert.assertNotNull(modifiedPom);
     Assert.assertTrue(modifiedPom.contains(versionId));
     Assert.assertTrue(modifiedPom.contains(artifactId));
     Assert.assertTrue(modifiedPom.contains(groupId));
   }

   private class MockVfs extends MockVirtualFileSystem
   {

      public MockVfs()
      {
         items.put("/",
            new Folder("/", "/", Folder.FOLDER_MIME_TYPE, "/", null, System.currentTimeMillis(), null, null));
      }

      @Override
      public Response createFile(String parentId, String name, MediaType mediaType, InputStream content)
         throws ItemNotFoundException, InvalidArgumentException, PermissionDeniedException, VirtualFileSystemException
      {
         String path = items.get(parentId).getPath();
         if (!path.endsWith("/"))
            path += "/";
         path += name;
         File file =
            new File(path, name, path, parentId, Calendar.getInstance().getTimeInMillis(), Calendar.getInstance()
               .getTimeInMillis(), "version-id", mediaType.getType(), 100500, false, null, null);
         items.put(path, file);
         if ("pom.xml".equals(name))
         {
            try
            {
               StringBuilder buffer = new StringBuilder(content.available());
               InputStreamReader streamReader = new InputStreamReader(content);
               BufferedReader reader = new BufferedReader(streamReader);
               String line = reader.readLine();
               while (line != null)
               {
                  buffer.append(line + "\n");
                  line = reader.readLine();
               }
               modifiedPom = buffer.toString();
            }
            catch (IOException e)
            {
               e.printStackTrace();
            }
         }
         return Response.ok(file).build();
      }

      @Override
      public Response createFolder(String parentId, String name) throws ItemNotFoundException,
         InvalidArgumentException, PermissionDeniedException, VirtualFileSystemException
      {
         String path = items.get(parentId).getPath();
         if (!path.endsWith("/"))
            path += "/";
         path += name;
         Folder folder =
            new Folder(path, name, Folder.FOLDER_MIME_TYPE, path, parentId, Calendar.getInstance().getTimeInMillis(),
               null, null);
         items.put(path, folder);
         return Response.ok(folder).build();
      }

      @Override
      public Response createProject(String parentId, String name, String type, List<ConvertibleProperty> properties)
         throws ItemNotFoundException, InvalidArgumentException, PermissionDeniedException, VirtualFileSystemException
      {
         String path = items.get(parentId).getPath();
         if (!path.endsWith("/"))
            path += "/";
         path += name;
         Project project =
            new Project(path, name, Project.PROJECT_MIME_TYPE, path, parentId,
               Calendar.getInstance().getTimeInMillis(), null, null, type);
         items.put(path, project);
         return Response.ok(project).build();
      }
   }
}
