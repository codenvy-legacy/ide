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

package org.exoplatform.ide.client.project.resource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.HasAllKeyHandlers;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Timer;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.gwtframework.ui.client.api.TextFieldItem;
import org.exoplatform.ide.client.framework.event.FileOpenedEvent;
import org.exoplatform.ide.client.framework.event.FileOpenedHandler;
import org.exoplatform.ide.client.framework.event.OpenFileEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.project.ProjectClosedEvent;
import org.exoplatform.ide.client.framework.project.ProjectClosedHandler;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedHandler;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.hotkeys.HotKeyHelper;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.marshal.ChildrenUnmarshaller;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class OpenResourcePresenter implements OpenResourceHandler, ViewClosedHandler, ProjectOpenedHandler,
   ProjectClosedHandler, FileOpenedHandler
{

   /**
    *  Display
    */
   public interface Display extends IsView
   {

      /**
       * Get file name field
       *  
       * @return file name field
       */
      TextFieldItem getFileNameField();

      /**
       * Get files list grid
       * 
       * @return files list grid
       */
      ListGridItem<FileModel> getFilesListGrid();

      /**
       * Get files list grid with ability to handle key pressing
       * 
       * @return files list grid
       */
      HasAllKeyHandlers listGrid();

      /**
       * Set focus in list grid
       */
      void focusListGrid();

      /**
       * Set name of item's parent folder
       * 
       * @param folderName name of item's parent folder
       */
      void setItemFolderName(String folderName);

      /**
       * Get list of selected files
       * 
       * @return list of selected files
       */
      List<FileModel> getSelectedItems();

      /**
       * Get Open button
       * 
       * @return Open button
       */
      HasClickHandlers getOpenButton();

      /**
       * Get Cancel button
       * 
       * @return Cancel button
       */
      HasClickHandlers getCancelButton();

   }

   /**
    * Search Failed message
    */
   private static final String SEARCH_ERROR_MESSAGE = org.exoplatform.ide.client.IDE.ERRORS_CONSTANT.searchFileSearchError();

   /**
    * {@link Display} instance 
    */
   private Display display;

   /**
    * 
    */
   private ProjectModel project;

   private List<Item> projectItems;

   private List<FileModel> filteredFiles;

   private FileModel fileToOpen;
   
   private FileModel selectedFile;

   public OpenResourcePresenter()
   {
      IDE.getInstance().addControl(new OpenResourceControl());

      IDE.addHandler(OpenResourceEvent.TYPE, this);
      IDE.addHandler(ViewClosedEvent.TYPE, this);
      IDE.addHandler(ProjectOpenedEvent.TYPE, this);
      IDE.addHandler(ProjectClosedEvent.TYPE, this);
      IDE.addHandler(FileOpenedEvent.TYPE, this);
   }

   @Override
   public void onOpenResource(OpenResourceEvent event)
   {
      if (project == null || display != null)
      {
         return;
      }

      HashMap<String, String> query = new HashMap<String, String>();
      String path = project.getPath();
      if (!"".equals(path) && !path.startsWith("/"))
      {
         path = "/" + path;
      }
      query.put("path", path);

      try
      {
         VirtualFileSystem.getInstance().search(query, -1, 0,
            new AsyncRequestCallback<List<Item>>(new ChildrenUnmarshaller(new ArrayList<Item>()))
            {
               @Override
               protected void onSuccess(List<Item> result)
               {
                  projectItems = result;
                  display = GWT.create(Display.class);
                  bindDisplay();
                  IDE.getInstance().openView(display.asView());
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  IDE.fireEvent(new ExceptionThrownEvent(exception, SEARCH_ERROR_MESSAGE));
               }
            });
      }
      catch (RequestException e)
      {
         IDE.fireEvent(new ExceptionThrownEvent(e, SEARCH_ERROR_MESSAGE));
      }
   }

   private void bindDisplay()
   {
      display.setItemFolderName(null);

      display.getFileNameField().addKeyUpHandler(fileNameFieldKeyHandler);

      display.getFilesListGrid().addSelectionHandler(new SelectionHandler<FileModel>()
      {
         @Override
         public void onSelection(SelectionEvent<FileModel> event)
         {
            selectedFile = event.getSelectedItem();
            display.setItemFolderName(selectedFile == null ? null : selectedFile.getPath());
         }
      });

      display.getFilesListGrid().addDoubleClickHandler(new DoubleClickHandler()
      {
         @Override
         public void onDoubleClick(DoubleClickEvent event)
         {
            openSelectedFile();
         }
      });

      display.listGrid().addKeyUpHandler(new KeyUpHandler()
      {
         @Override
         public void onKeyUp(KeyUpEvent event)
         {
            if (HotKeyHelper.KeyCode.ENTER == event.getNativeKeyCode())
            {
               openSelectedFile();
            }
         }
      });

      display.getOpenButton().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            openSelectedFile();
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

      fileToOpen = null;
      updateTimer.schedule(100);
   }

   private KeyUpHandler fileNameFieldKeyHandler = new KeyUpHandler()
   {
      @Override
      public void onKeyUp(KeyUpEvent event)
      {
         if (event.getNativeKeyCode() == HotKeyHelper.KeyCode.DOWN)
         {
            if (filteredFiles != null && filteredFiles.size() > 0)
            {
               display.getFilesListGrid().selectItem(filteredFiles.get(0));
               display.focusListGrid();
            }
         }

         updateTimer.cancel();
         updateTimer.schedule(100);
      }
   };

   private Timer updateTimer = new Timer()
   {
      @Override
      public void run()
      {
         filteredFiles = new ArrayList<FileModel>();

         if (display.getFileNameField().getValue().trim().isEmpty())
         {
            for (Item item : projectItems)
            {
               if (item instanceof FileModel)
               {
                  FileModel file = (FileModel)item;
                  filteredFiles.add(file);
               }
            }
         }
         else
         {
            String fileNamePrefix = display.getFileNameField().getValue().trim().toUpperCase();
            for (Item item : projectItems)
            {
               if (item instanceof FileModel && item.getName().toUpperCase().startsWith(fileNamePrefix))
               {
                  FileModel file = (FileModel)item;
                  filteredFiles.add(file);
               }
            }
         }

         display.getFilesListGrid().setValue(filteredFiles);
         if (selectedFile != null && filteredFiles.contains(selectedFile))
         {
            display.getFilesListGrid().selectItem(selectedFile);
         }
         else
         {
            selectedFile = null;
            display.setItemFolderName(null);
         }
      }
   };

   @Override
   public void onViewClosed(ViewClosedEvent event)
   {
      if (event.getView() instanceof Display)
      {
         display = null;
      }
   }

   @Override
   public void onProjectClosed(ProjectClosedEvent event)
   {
      project = null;
   }

   @Override
   public void onProjectOpened(ProjectOpenedEvent event)
   {
      project = event.getProject();
   }

   private void openSelectedFile()
   {
      List<FileModel> selectedItems = display.getSelectedItems();
      if (selectedItems.size() == 0)
      {
         return;
      }

      fileToOpen = selectedItems.get(0);
      IDE.fireEvent(new OpenFileEvent(fileToOpen));
   }

   @Override
   public void onFileOpened(FileOpenedEvent event)
   {
      if (display != null && fileToOpen != null && event.getFile().getId().equals(fileToOpen.getId()))
      {
         fileToOpen = null;
         IDE.getInstance().closeView(display.asView().getId());
      }
   }

}
