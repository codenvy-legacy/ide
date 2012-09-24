/*
 * Copyright (C) 2003-2012 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ide.client;

import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.inject.Inject;

import org.exoplatform.ide.api.resources.ResourceProvider;
import org.exoplatform.ide.client.projectExplorer.ProjectExplorerPresenter;
import org.exoplatform.ide.client.workspace.WorkspacePeresenter;
import org.exoplatform.ide.core.ComponentException;
import org.exoplatform.ide.core.ComponentRegistry;
import org.exoplatform.ide.json.JsonCollections;
import org.exoplatform.ide.resources.model.File;
import org.exoplatform.ide.resources.model.Folder;
import org.exoplatform.ide.resources.model.Project;
import org.exoplatform.ide.resources.model.Property;

import java.util.Date;

/**
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 *          exo@exoplatform.com
 * Sep 13, 2012  
 */
public class BootstrapController
{

   WorkspacePeresenter workspacePeresenter;

   ProjectExplorerPresenter projectExpolrerPresenter;

   private final ResourceProvider resourceManager;

   @Inject
   public BootstrapController(ComponentRegistry componentRegistry, final WorkspacePeresenter workspacePeresenter,
      ResourceProvider resourceManager, ProjectExplorerPresenter projectExpolorerPresenter)
   {
      this.workspacePeresenter = workspacePeresenter;
      this.resourceManager = resourceManager;
      this.projectExpolrerPresenter = projectExpolorerPresenter;

      // initialize components
      componentRegistry.start(new Callback<Void, ComponentException>()
      {
         @Override
         public void onSuccess(Void result)
         {
            // Start UI
            workspacePeresenter.go(RootLayoutPanel.get());

            createDemoContent();
         }

         @Override
         public void onFailure(ComponentException caught)
         {
            GWT.log("FAILED to start service:" + caught.getComponent());
         }
      });
   }

   /**
    * 
    */
   @SuppressWarnings("rawtypes")
   protected void createDemoContent()
   {
      // DUMMY CREATE DEMO CONTENT
      resourceManager.createProject("Test Project " + (new Date().getTime()), JsonCollections.<Property> createArray(),
         new AsyncCallback<Project>()
         {

            @Override
            public void onSuccess(final Project project)
            {
               project.createFolder(project, "Test Folder", new AsyncCallback<Folder>()
               {

                  @Override
                  public void onSuccess(Folder result)
                  {
                     project.createFile(result, "Test file on FS", "This is file content of the file from VFS",
                        "text/text-pain", new AsyncCallback<File>()
                        {

                           @Override
                           public void onSuccess(File result)
                           {
                              projectExpolrerPresenter.setContent(project.getParent());
                           }

                           @Override
                           public void onFailure(Throwable caught)
                           {
                              GWT.log("Error creating demo folder" + caught);
                           }
                        });

                  }

                  @Override
                  public void onFailure(Throwable caught)
                  {
                     GWT.log("Error creating demo folder" + caught);
                  }
               });

            }

            @Override
            public void onFailure(Throwable caught)
            {
               GWT.log("Error creating demo content" + caught);
            }
         });
   }
}
