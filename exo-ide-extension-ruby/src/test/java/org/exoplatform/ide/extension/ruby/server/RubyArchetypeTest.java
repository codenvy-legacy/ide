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
package org.exoplatform.ide.extension.ruby.server;

import org.everrest.core.impl.RuntimeDelegateImpl;
import org.exoplatform.container.StandaloneContainer;
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
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.InputStream;
import java.net.URL;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.RuntimeDelegate;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class RubyArchetypeTest 
{

   private Map<String, Item> items = new HashMap<String, Item>();

   String[] assertnames = {"/RailsDemo/app/controllers",
      "/RailsDemo/db",
      "/RailsDemo/app/views/layouts/application.html.erb", 
      "/RailsDemo/app/helpers/application_helper.rb"
      };

   @BeforeClass
   public static void setUp() throws Exception
   {
      RuntimeDelegate.setInstance(new RuntimeDelegateImpl());
   }
   
   @Test
   public void createProject() throws Exception
   {
      RubyProjectArchetype archetype =  new RubyProjectArchetype();
      URL url = RubyArchetypeTest.class.getResource("/RailsDemo");
      archetype.exportResources(url, "RailsDemo", "exo.ide.rubyonrails.project", "/", new MockVfs());
      for (int i = 0; i < assertnames.length; i++)
      {
         Assert.assertTrue(items.containsKey(assertnames[i]));
      }
   }
   

   private class MockVfs extends MockVirtualFileSystem
   {
      
     

      public MockVfs()
      {
         items.put("/",
            new Folder("/", "/", Folder.FOLDER_MIME_TYPE, "/", null, System.currentTimeMillis(), null, null));
      }

      @Override
      public File createFile(String parentId, String name, MediaType mediaType, InputStream content)
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
         return file;
      }

      @Override
      public Folder createFolder(String parentId, String name) throws ItemNotFoundException,
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
         return folder;
      }

      @Override
      public Project createProject(String parentId, String name, String type, List<ConvertibleProperty> properties)
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
         return project;
      }
   }
}
