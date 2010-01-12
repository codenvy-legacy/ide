/*
 * Copyright (C) 2003-2007 eXo Platform SAS.
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
package org.exoplatform.ideall.client.workspace;

import org.exoplatform.ideall.client.Handlers;
import org.exoplatform.ideall.client.Images;
import org.exoplatform.ideall.client.application.event.InitializeApplicationEvent;
import org.exoplatform.ideall.client.model.ApplicationContext;
import org.exoplatform.ideall.client.model.jcrservice.RepositoryService;
import org.exoplatform.ideall.client.model.jcrservice.bean.Repository;
import org.exoplatform.ideall.client.model.jcrservice.bean.RepositoryServiceConfiguration;
import org.exoplatform.ideall.client.model.jcrservice.bean.Workspace;
import org.exoplatform.ideall.client.model.jcrservice.event.RepositoryConfigurationReceivedEvent;
import org.exoplatform.ideall.client.model.jcrservice.event.RepositoryConfigurationReceivedHandler;
import org.exoplatform.ideall.client.workspace.event.SwitchWorkspaceEvent;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasValue;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class SelectWorkspacePresenter implements RepositoryConfigurationReceivedHandler
{

   public interface Display
   {

      HasValue<JCRConfigurationItem> getJCRItemsTreeGrid();

      HasSelectionHandlers<JCRConfigurationItem> getJCRItemsTreeGridSelectable();

      void enableJCRItemsTreeGrid();

      void disableJCRItemsTreeGrid();

      void closeForm();

      HasClickHandlers getOkButton();

      HasClickHandlers getCancelButton();

      void enableOkButton();

      void disableOkButton();

   }

   private HandlerManager eventBus;

   private Display display;

   private Handlers handlers;

   private ApplicationContext context;

   private JCRConfigurationItem selectedItem;

   public SelectWorkspacePresenter(HandlerManager eventBus, ApplicationContext context)
   {
      this.eventBus = eventBus;
      this.context = context;
      handlers = new Handlers(eventBus);
   }

   public void destroy()
   {
      handlers.removeHandlers();
   }

   public void bindDisplay(Display d)
   {
      display = d;
      handlers.addHandler(RepositoryConfigurationReceivedEvent.TYPE, this);

      display.getOkButton().addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent event)
         {
            save();
         }
      });

      display.getCancelButton().addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent event)
         {
            display.closeForm();
         }
      });

      display.getJCRItemsTreeGridSelectable().addSelectionHandler(new SelectionHandler<JCRConfigurationItem>()
      {
         public void onSelection(SelectionEvent<JCRConfigurationItem> event)
         {
            onItemSelected(event.getSelectedItem());
         }
      });

      if (context.getRepositoryServiceConfiguration() == null)
      {
         display.disableJCRItemsTreeGrid();
         RepositoryService.getInstance().getRepositoryServiceConfiguration();
      }
      else
      {
         display.enableJCRItemsTreeGrid();
         updateConfigurationTree();
      }

      display.disableOkButton();
   }

   protected void onItemSelected(JCRConfigurationItem selectedItem)
   {
      if (this.selectedItem == selectedItem)
      {
         return;
      }

      this.selectedItem = selectedItem;

      if (selectedItem.getEntry() instanceof Workspace)
      {
         display.enableOkButton();
      }
      else
      {
         display.disableOkButton();
      }
   }

   protected void save()
   {
      if (selectedItem == null)
      {
         return;
      }

      for (Repository repository : context.getRepositoryServiceConfiguration().getRepositories())
      {
         for (Workspace workspace : repository.getWorkspaces())
         {
            if (workspace == (Workspace)selectedItem.getEntry())
            {
               display.closeForm();

               if (context.isInitialized())
               {
                  eventBus.fireEvent(new SwitchWorkspaceEvent(repository.getName(), workspace.getName()));
               }
               else
               {
                  context.getPreloadFiles().clear();
                  context.setSelectedItem(null);
                  
                  context.setRepository(repository.getName());
                  context.setWorkspace(workspace.getName());
                  context.setInitialized(true);
                  eventBus.fireEvent(new InitializeApplicationEvent());
               }

               return;
            }
         }
      }
   }

   public void onRepositoryConfigurationReceived(RepositoryConfigurationReceivedEvent event)
   {
      context.setRepositoryServiceConfiguration(event.getConfiguration());
      display.enableJCRItemsTreeGrid();
      updateConfigurationTree();
   }

   protected void updateConfigurationTree()
   {
      JCRConfigurationItem configurationTree = getTreeGridValueFor(context.getRepositoryServiceConfiguration());
      display.getJCRItemsTreeGrid().setValue(configurationTree);
   }

   private JCRConfigurationItem getTreeGridValueFor(RepositoryServiceConfiguration repoConfig)
   {
      JCRConfigurationItem configurationItem =
         new JCRConfigurationItem("Repository Service", Images.RepositoryService.SERVICE, repoConfig);

      for (Repository repository : repoConfig.getRepositories())
      {
         String repoName = repository.getName();

         if (repoConfig.getDefaultRepositoryName().equals(repoName))
         {
            repoName += " [Default]";
         }

         JCRConfigurationItem repoItem =
            new JCRConfigurationItem(repoName, Images.RepositoryService.REPOSITORY, repository);

         for (Workspace workspace : repository.getWorkspaces())
         {
            String workspaceName = workspace.getName();
            if (repository.getSystemWorkspaceName().equals(workspaceName))
            {
               workspaceName += " [System]";
            }

            JCRConfigurationItem workspaceItem =
               new JCRConfigurationItem(workspaceName, Images.RepositoryService.WORKSPACE, workspace);
            repoItem.getChildren().add(workspaceItem);
         }

         configurationItem.getChildren().add(repoItem);
      }

      return configurationItem;
   }

}
