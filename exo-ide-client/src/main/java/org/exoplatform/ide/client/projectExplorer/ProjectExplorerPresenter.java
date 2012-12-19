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
package org.exoplatform.ide.client.projectExplorer;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

import org.exoplatform.ide.client.PageResources;
import org.exoplatform.ide.client.event.FileEvent;
import org.exoplatform.ide.client.event.FileEvent.FileOperation;
import org.exoplatform.ide.core.event.ProjectActionEvent;
import org.exoplatform.ide.core.event.ProjectActionHandler;
import org.exoplatform.ide.core.event.ResourceChangedEvent;
import org.exoplatform.ide.core.event.ResourceChangedHandler;
import org.exoplatform.ide.part.AbstractPartPresenter;
import org.exoplatform.ide.resources.model.File;
import org.exoplatform.ide.resources.model.Resource;

/**
 * Tree-like project explorer.
 * TODO : should accept resource model objects.
 * 
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 *          exo@exoplatform.com
 * Jul 27, 2012  
 */
public class ProjectExplorerPresenter extends AbstractPartPresenter implements ProjectExplorerView.ActionDelegate
{
   ProjectExplorerView view;

   EventBus eventBus;

   PageResources resources;

   /**
    * Create presenter.
    * 
    * @param view
    * @param eventBus
    * @param resources
    */
   @Inject
   public ProjectExplorerPresenter(ProjectExplorerView view, EventBus eventBus, PageResources resources)
   {
      this.view = view;
      this.eventBus = eventBus;
      this.resources = resources;
      bind();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void go(HasWidgets container)
   {
      container.clear();
      container.add(view.asWidget());
   }

   /**
    * Sets content.
    * 
    * @param resource
    */
   public void setContent(Resource resource)
   {
      view.setItems(resource);
   }

   /**
    * Adds behavior to view components
    */
   protected void bind()
   {
      view.setDelegate(this);
      eventBus.addHandler(ProjectActionEvent.TYPE, new ProjectActionHandler()
      {
         @Override
         public void onProjectOpened(ProjectActionEvent event)
         {
            setContent(event.getProject().getParent());
         }
         
         @Override
         public void onProjectDescriptionChanged(ProjectActionEvent event)
         {
         }
         
         @Override
         public void onProjectClosed(ProjectActionEvent event)
         {
            setContent(null);
         }
      });
      
      eventBus.addHandler(ResourceChangedEvent.TYPE, new ResourceChangedHandler()
      {
         
         @Override
         public void onResourceRenamed(ResourceChangedEvent event)
         {
            // TODO handle it
         }
         
         @Override
         public void onResourceMoved(ResourceChangedEvent event)
         {
            // TODO handle it
         }
         
         @Override
         public void onResourceDeleted(ResourceChangedEvent event)
         {
            setContent(event.getResource().getProject().getParent());
         }
         
         @Override
         public void onResourceCreated(ResourceChangedEvent event)
         {
            setContent(event.getResource().getProject().getParent());
         }
      });
   }

   /**
    * Opens file.
    * 
    * @param resource
    */
   protected void openFile(Resource resource)
   {
      if (resource.isFile())
      {
         eventBus.fireEvent(new FileEvent((File)resource, FileOperation.OPEN));
      }
   }

    /**
    * {@inheritDoc}
    */
   @Override
   public String getTitle()
   {
      return "Project Explorer";
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ImageResource getTitleImage()
   {
      return resources.projectExplorerIcon();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getTitleToolTip()
   {
      return "This page helps you to do basic operation with your projects. Following features are currently available:"
         + "\n\t- view project's tree" + "\n\t- select and open project's file";
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void onNodeAction(Resource resource)
   {
      openFile(resource);
   }
}
