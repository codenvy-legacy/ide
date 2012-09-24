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
package org.exoplatform.ide.resources;

import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.SimpleEventBus;

import org.exoplatform.ide.json.JsonCollections;
import org.exoplatform.ide.json.JsonStringMap;
import org.exoplatform.ide.loader.EmptyLoader;
import org.exoplatform.ide.resources.VirtualFileSystemInfo.ACLCapability;
import org.exoplatform.ide.resources.VirtualFileSystemInfo.QueryCapability;
import org.exoplatform.ide.resources.model.Folder;
import org.exoplatform.ide.resources.model.GenericModelProvider;
import org.exoplatform.ide.resources.model.Link;
import org.exoplatform.ide.resources.model.Project;
import org.exoplatform.ide.resources.model.Property;

/**
 * 
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a>
 * @version $Id: GwtTestVirtualFileSystem.java 34360 2009-07-22 23:58:59Z nzamosenchuk $
 */
public class GwtTestVirtualFileSystem extends GWTTestCase
{
   private static final int DELAY_TEST = 5000;

   private VirtualFileSystemInfo info;

   ResourceProviderComponent resourceProvider;

   @Override
   protected void gwtSetUp() throws Exception
   {
      super.gwtSetUp();
      JsonStringMap<Link> links = JsonCollections.<Link> createStringMap();
      Link link = new Link("/rest/vfs/mock/file", Link.REL_CREATE_FILE, "*/*");
      links.put(Link.REL_CREATE_FILE, link);

      info =
         new VirtualFileSystemInfo("test", true, true, "ANONIM", "ANY", JsonCollections.<String> createArray(),
            ACLCapability.MANAGE, QueryCapability.BOTHCOMBINED, links, new Folder());

      resourceProvider = new ResourceProviderComponent(new SimpleEventBus(), new GenericModelProvider(), new EmptyLoader());
      resourceProvider.vfsInfo = info;
   }

   @SuppressWarnings("rawtypes")
   public void testCreateProject()
   {
      final String parentId = String.valueOf(Random.nextDouble());
      //      JsonStringMap<Link> links = JsonCollections.<Link>createStringMap();
      //      Link link = new Link("/rest/vfs/mock/project/" + parentId, Link.REL_CREATE_PROJECT, "*/*");
      //      links.put(Link.REL_CREATE_PROJECT, link);
      //      
      //      
      //      
      //      
      //      FolderModel parent = new FolderModel("folder", new FolderModel(info.getRoot()), links);
      //      ProjectModel newProject = new ProjectModel("proj", parent, "test-proj", Collections.EMPTY_LIST);
      //      VirtualFileSystem.getInstance().createProject(parent,
      //         new AsyncRequestCallback<ProjectModel>(new ProjectUnmarshaller(newProject))
      //         {
      //            @Override
      //            protected void onSuccess(ProjectModel result)
      //            {
      //               assertNotNull(result);
      //               assertEquals("proj", result.getName());
      //               finishTest();
      //            }
      //
      //            @Override
      //            protected void onFailure(Throwable exception)
      //            {
      //               fail();
      //            }
      //         });

      info.getRoot().getLinks()
         .put(Link.REL_CREATE_PROJECT, new Link("/rest/vfs/mock/project/" + parentId, Link.REL_CREATE_PROJECT, "*/*"));

      resourceProvider.createProject("Project", JsonCollections.<Property> createArray(), new AsyncCallback<Project>()
      {

         @Override
         public void onSuccess(Project result)
         {
            assertNotNull(result);
            assertEquals("Project", result.getName());
            finishTest();
         }

         @Override
         public void onFailure(Throwable caught)
         {
            fail();
         }
      });

      delayTestFinish(DELAY_TEST);
   }

   @Override
   public String getModuleName()
   {
      return "org.exoplatform.ide.resources.Resources";
   }

}
