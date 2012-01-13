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

package org.exoplatform.ide.client.project.properties;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.copy.AsyncRequestCallback;
import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.gwtframework.ui.client.dialog.BooleanValueReceivedHandler;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.project.ProjectClosedEvent;
import org.exoplatform.ide.client.framework.project.ProjectClosedHandler;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedHandler;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.marshal.ItemUnmarshaller;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.ItemWrapper;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Property;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ProjectPropertiesPresenter implements ShowProjectPropertiesHandler, ProjectOpenedHandler,
   ProjectClosedHandler, ViewClosedHandler
{

   public interface Display extends IsView
   {

      ListGridItem<Property> getPropertiesListGrid();

      //HasClickHandlers getAddButton();

      HasClickHandlers getEditButton();

      void setEditButtonEnabled(boolean enabled);

      HasClickHandlers getDeleteButton();

      void setDeleteButtonEnabled(boolean enabled);

      HasClickHandlers getOkButton();

      void setOkButtonEnabled(boolean enabled);

      HasClickHandlers getCancelButton();

   }

   private Display display;

   private ProjectModel currentProject;

   private Property selectedProperty;

   private EditPropertyPresenter editPropertyPresenter = new EditPropertyPresenter();

   public ProjectPropertiesPresenter()
   {
      IDE.getInstance().addControl(new ShowProjectPropertiesControl());

      IDE.addHandler(ShowProjectPropertiesEvent.TYPE, this);
      IDE.addHandler(ProjectOpenedEvent.TYPE, this);
      IDE.addHandler(ProjectClosedEvent.TYPE, this);
      IDE.addHandler(ViewClosedEvent.TYPE, this);
   }

   @Override
   public void onShowProjectProperties(ShowProjectPropertiesEvent event)
   {
      if (display != null || currentProject == null)
      {
         return;
      }

      loadProperties();
   }

   private void loadProperties()
   {
      try
      {
         String projectId = currentProject.getId();

         VirtualFileSystem.getInstance().getItemById(projectId,
            new AsyncRequestCallback<ItemWrapper>(new ItemUnmarshaller(new ItemWrapper(new FileModel())))
            {
               @Override
               protected void onSuccess(ItemWrapper result)
               {
                  if (!(result.getItem() instanceof ProjectModel))
                  {
                     Dialogs.getInstance().showError("Item " + result.getItem().getPath() + " is not a project.");
                     return;
                  }

                  currentProject.getProperties().clear();
                  currentProject.getProperties().addAll(result.getItem().getProperties());

                  createDisplay();
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  IDE.fireEvent(new ExceptionThrownEvent(exception));
               }
            });

      }
      catch (Exception e)
      {
         IDE.fireEvent(new ExceptionThrownEvent(e));
      }
   }

   private void createDisplay()
   {
      display = GWT.create(Display.class);
      IDE.getInstance().openView(display.asView());
      bindDisplay();
      refreshProperties();
   }

   @SuppressWarnings("unchecked")
   private void bindDisplay()
   {
      display.setEditButtonEnabled(false);
      display.setDeleteButtonEnabled(false);
      display.setOkButtonEnabled(false);

      display.getOkButton().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            saveAndClose();
         }
      });

      display.getCancelButton().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            IDE.getInstance().closeView(display.asView().getId());
         }
      });

      display.getPropertiesListGrid().addDoubleClickHandler(new DoubleClickHandler()
      {
         @Override
         public void onDoubleClick(DoubleClickEvent event)
         {
            editSelectedProperty();
         }
      });

      display.getPropertiesListGrid().addSelectionHandler(new SelectionHandler()
      {
         @Override
         public void onSelection(SelectionEvent event)
         {
            onPropertySelected((Property)event.getSelectedItem());
         }
      });

      //      display.getAddButton().addClickHandler(new ClickHandler()
      //      {
      //         @Override
      //         public void onClick(ClickEvent event)
      //         {
      //            createProperty();
      //         }
      //      });

      display.getEditButton().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            editSelectedProperty();
         }
      });

      display.getDeleteButton().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            deleteSelectedProperty();
         }
      });
   }

   private void refreshProperties()
   {
      List<Property> propertyList = new ArrayList<Property>();
      for (Property property : currentProject.getProperties())
      {
         if (property.getValue() == null || property.getValue().isEmpty())
         {
            continue;
         }

         propertyList.add(property);
      }

      display.getPropertiesListGrid().setValue(propertyList);
      display.getPropertiesListGrid().selectItem(selectedProperty);
   }

   private void editSelectedProperty()
   {
      editPropertyPresenter.editProperty(selectedProperty, currentProject.getProperties(), propertyEditCompleteHandler);
   }

   private EditCompleteHandler propertyEditCompleteHandler = new EditCompleteHandler()
   {
      @Override
      public void onEditComplete()
      {
         display.setOkButtonEnabled(true);
         refreshProperties();
      }
   };

   private void deleteSelectedProperty()
   {
      String name = PropertyUtil.getHumanReadableName(selectedProperty.getName());
      Dialogs.getInstance().ask("IDE", "Delete property <b>" + name + "</b>?", new BooleanValueReceivedHandler()
      {
         @SuppressWarnings("unchecked")
         @Override
         public void booleanValueReceived(Boolean value)
         {
            if (value != null && value.booleanValue())
            {
               selectedProperty.setValue(null);
               selectedProperty = null;

               display.setEditButtonEnabled(false);
               display.setDeleteButtonEnabled(false);
               display.setOkButtonEnabled(true);
               refreshProperties();
            }
         }
      });
   }

   private void onPropertySelected(Property property)
   {
      selectedProperty = property;
      display.setEditButtonEnabled(true);
      display.setDeleteButtonEnabled(true);
   }

   @Override
   public void onProjectOpened(ProjectOpenedEvent event)
   {
      currentProject = event.getProject();
   }

   @Override
   public void onProjectClosed(ProjectClosedEvent event)
   {
      currentProject = null;
   }

   @Override
   public void onViewClosed(ViewClosedEvent event)
   {
      if (event.getView() instanceof Display)
      {
         display = null;
         selectedProperty = null;
      }
   }

   private void saveAndClose()
   {
      try
      {
         VirtualFileSystem.getInstance().updateItem(currentProject, null, new AsyncRequestCallback<Object>()
         {
            @Override
            protected void onSuccess(Object result)
            {
               IDE.getInstance().closeView(display.asView().getId());
            }

            @Override
            protected void onFailure(Throwable exception)
            {
               IDE.fireEvent(new ExceptionThrownEvent(exception));
            }
         });

      }
      catch (Exception e)
      {
         IDE.fireEvent(new ExceptionThrownEvent(e));
      }

   }

}
