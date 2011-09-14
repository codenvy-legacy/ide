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
package org.exoplatform.ide.extension.samples.client.wizard.finish;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.copy.AsyncRequestCallback;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.event.RefreshBrowserEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.View;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.samples.client.ProjectProperties;
import org.exoplatform.ide.extension.samples.client.SamplesExtension;
import org.exoplatform.ide.extension.samples.client.SamplesLocalizationConstant;
import org.exoplatform.ide.extension.samples.client.paas.cloudbees.InitializeApplicationEvent;
import org.exoplatform.ide.extension.samples.client.wizard.deployment.ShowWizardDeploymentStepEvent;
import org.exoplatform.ide.extension.samples.client.wizard.event.ProjectCreationFinishedEvent;
import org.exoplatform.ide.extension.samples.client.wizard.event.ProjectCreationFinishedHandler;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.marshal.ProjectUnmarshaller;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Presenter for Step1 (Source) of Wizard for creation Java Project.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: SourceWizardPresenter.java Sep 7, 2011 3:00:58 PM vereshchaka $
 */
public class WizardFinishStepPresenter implements ShowWizardFinishStepHandler, ViewClosedHandler, 
ProjectCreationFinishedHandler, ItemsSelectedHandler
{
   public interface Display extends IsView
   {
      HasClickHandlers getFinishButton();
      
      HasClickHandlers getBackButton();
      
      HasClickHandlers getCancelButton();
      
      HasValue<String> getNameLabel();
      
      HasValue<String> getTypeLable();
   }
   
   private static final SamplesLocalizationConstant lb = SamplesExtension.LOCALIZATION_CONSTANT;
   
   private HandlerManager eventBus;
   
   private Display display;
   
   /**
    * Project properties.
    * Got from previous step.
    */
   private ProjectProperties projectProperties;
   
   private List<Item> selectedItems = new ArrayList<Item>();
   
   public WizardFinishStepPresenter(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
      
      eventBus.addHandler(ShowWizardFinishStepEvent.TYPE, this);
      eventBus.addHandler(ViewClosedEvent.TYPE, this);
      eventBus.addHandler(ProjectCreationFinishedEvent.TYPE, this);
      eventBus.addHandler(ItemsSelectedEvent.TYPE, this);
   }
   
   private void bindDisplay()
   {
      display.getCancelButton().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            eventBus.fireEvent(new ProjectCreationFinishedEvent(true));
            closeView();
         }
      });
      
      display.getBackButton().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            eventBus.fireEvent(new ShowWizardDeploymentStepEvent());
            closeView();
         }
      });
      
      display.getFinishButton().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            if (projectProperties == null)
            {
               Dialogs.getInstance().showError(lb.wizardFinishErrorProjectPropertiesAreNull());
               return;
            }
            if (!(selectedItems.get(0) instanceof FolderModel))
            {
               Dialogs.getInstance().showError(lb.wizardFinishErrorNoFolderSelected());
               return;
            }
            finishProjectCreation();
         }
      });
      
      if (projectProperties != null)
      {
         display.getNameLabel().setValue(projectProperties.getName());
         display.getTypeLable().setValue(projectProperties.getType());
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent)
    */
   @Override
   public void onViewClosed(ViewClosedEvent event)
   {
      if (event.getView() instanceof Display)
      {
         display = null;
      }
   }

   /**
    * @see org.exoplatform.ide.extension.samples.client.wizard.source.ShowWizardSourceHandler#onShowWizardDefinition(org.exoplatform.ide.extension.samples.client.wizard.source.ShowWizardEvent)
    */
   @Override
   public void onShowFinishWizard(ShowWizardFinishStepEvent event)
   {
      if (event.getProjectProperties() != null)
      {
         projectProperties = event.getProjectProperties();
      }
      openView();
   }
   
   /**
    * @see org.exoplatform.ide.extension.samples.client.wizard.event.ProjectCreationFinishedHandler#onProjectCreationFinished(org.exoplatform.ide.extension.samples.client.wizard.event.ProjectCreationFinishedEvent)
    */
   @Override
   public void onProjectCreationFinished(ProjectCreationFinishedEvent event)
   {
      projectProperties = null;
   }
   
   @Override
   public void onItemsSelected(ItemsSelectedEvent event)
   {
      selectedItems = event.getSelectedItems();
   }
   
   private void openView()
   {
      if (display == null)
      {
         Display d = GWT.create(Display.class);
         IDE.getInstance().openView((View)d);
         display = d;
         bindDisplay();
         return;
      }
      else
      {
         eventBus.fireEvent(new ExceptionThrownEvent("Show Wizard must be null"));
      }
   }
   
   private void closeView()
   {
      IDE.getInstance().closeView(display.asView().getId());
   }
   
   private void finishProjectCreation()
   {
//      eventBus.fireEvent(new InitializeApplicationEvent());
//      closeView();
      
      try
      {
         final FolderModel parent = projectProperties.getParenFolder();
         ProjectModel newProject = new ProjectModel(projectProperties.getName(), parent, "test-proj", 
            Collections.EMPTY_LIST);
         VirtualFileSystem.getInstance().createProject(parent, new AsyncRequestCallback<ProjectModel>(
                  new ProjectUnmarshaller(newProject))
         {
            
            @Override
            protected void onSuccess(ProjectModel result)
            {
               eventBus.fireEvent(new ProjectCreationFinishedEvent(false));
               eventBus.fireEvent(new RefreshBrowserEvent(getFoldersToRefresh(parent), parent));
               eventBus.fireEvent(new InitializeApplicationEvent());
               closeView();
            }
            
            @Override
            protected void onFailure(Throwable exception)
            {
               exception.printStackTrace();
               eventBus.fireEvent(new ExceptionThrownEvent(exception, lb.wizardFinishErrorCantCreateProject()));
            }
         });
      }
      catch (RequestException e)
      {
         e.printStackTrace();
         eventBus.fireEvent(new ExceptionThrownEvent(e,
            "Service is not deployed.<br>Destination path does not exist<br>Folder already has item with same name."));
      }
      
   }
   
   /**
    * Work up to the root folder to create a list of folder to refresh.
    * Need to refresh all folders, that were created during "Select Location"
    * step, but not displayed in main navigation tree.
    * 
    * @param folder - the parent folder of your project
    * @return
    */
   private ArrayList<FolderModel> getFoldersToRefresh(FolderModel folder)
   {
      ArrayList<FolderModel> folders = new ArrayList<FolderModel>();
      folders.add(0, folder);
      FolderModel parent = folder.getParent();
      while (parent != null)
      {
         folders.add(0, parent);
         parent = parent.getParent();
      }
      return folders;
   }
   
}
