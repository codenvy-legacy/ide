/*
 * Copyright (C) 2003-2010 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ide.client.workspace;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.commons.dialogs.Dialogs;
import org.exoplatform.gwtframework.commons.dialogs.callback.BooleanValueReceivedCallback;
import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.ide.client.Utils;
import org.exoplatform.ide.client.cookie.CookieManager;
import org.exoplatform.ide.client.framework.editor.event.EditorCloseFileEvent;
import org.exoplatform.ide.client.model.discovery.Scheme;
import org.exoplatform.ide.client.model.discovery.marshal.EntryPoint;
import org.exoplatform.ide.client.model.discovery.marshal.EntryPointList;
import org.exoplatform.ide.client.module.navigation.event.SaveFileAsEvent;
import org.exoplatform.ide.client.module.vfs.api.File;
import org.exoplatform.ide.client.module.vfs.api.VirtualFileSystem;
import org.exoplatform.ide.client.module.vfs.api.event.FileContentSavedEvent;
import org.exoplatform.ide.client.module.vfs.api.event.FileContentSavedHandler;
import org.exoplatform.ide.client.workspace.event.SwitchEntryPointEvent;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
*/
public class SelectWorkspacePresenter implements FileContentSavedHandler
{

   public interface Display
   {
      ListGridItem<EntryPoint> getEntryPoints();

      void closeForm();

      HasClickHandlers getOkButton();

      HasClickHandlers getCancelButton();

      void enableOkButton();

      void disableOkButton();

   }

   private HandlerManager eventBus;

   //private ApplicationContext context;

   private Handlers handlers;

   private EntryPointList entryPointList;

   private Display display;

   private EntryPoint selectedEntryPoint;

   private boolean isSameEntryPoint = true;
   
   private String currentEntryPoint;
   
   private Map<String, File> openedFiles;

   public SelectWorkspacePresenter(HandlerManager eventBus, String currentEntryPoint, EntryPointList entryPointList, Map<String, File> openedFiles)
   {
      this.eventBus = eventBus;
      this.entryPointList = entryPointList;
      
      this.currentEntryPoint = currentEntryPoint;
      this.openedFiles = openedFiles;

      handlers = new Handlers(eventBus);
   }

   public void bindDisplay(Display d)
   {
      display = d;
      handlers.addHandler(FileContentSavedEvent.TYPE, SelectWorkspacePresenter.this);

      display.disableOkButton();

      display.getCancelButton().addClickHandler(new ClickHandler()
      {

         public void onClick(ClickEvent arg0)
         {
            display.closeForm();
         }

      });

      display.getOkButton().addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent event)
         {
            changeEntryPoint();
         }
      });

      display.getEntryPoints().addDoubleClickHandler(new DoubleClickHandler()
      {

         public void onDoubleClick(DoubleClickEvent event)
         {
            if (!isSameEntryPoint)
               changeEntryPoint();
         }
      });

      List<EntryPoint> entryPoints = new ArrayList<EntryPoint>();
      for (int i = 0; i < entryPointList.getEntryPoints().length(); i++)
      {
         EntryPoint entryPoint = entryPointList.getEntryPoints().get(i);
         if (entryPoint.getScheme().equals(Scheme.WEBDAV))
         {
            entryPoints.add(entryPoint);
         }
      }

      display.getEntryPoints().setValue(entryPoints);
      display.getEntryPoints().addSelectionHandler(new SelectionHandler<EntryPoint>()
      {

         public void onSelection(SelectionEvent<EntryPoint> event)
         {
            onEntryPointSelected(event.getSelectedItem());
         }

      });
   }

   protected void onEntryPointSelected(EntryPoint selectedItem)
   {
      if (selectedItem == null)
      {
         display.disableOkButton();
         return;
      }

      if (selectedItem.getHref().equals(currentEntryPoint))
      {
         display.disableOkButton();
         isSameEntryPoint = true;
         return;
      }
      isSameEntryPoint = false;
      selectedEntryPoint = selectedItem;
      display.enableOkButton();
   }

   public void destroy()
   {
      handlers.removeHandlers();
   }

   private void changeEntryPoint()
   {
      if (openedFiles.size() != 0)
      {
         Dialogs.getInstance().ask("IDEall", "All opened files will be closed.<br>Do you want to continue?",
            new BooleanValueReceivedCallback()
            {

               public void execute(Boolean value)
               {
                  if (value == null)
                  {
                     return;
                  }
                  if (value)
                  {
                     closeNextFile();
                  }
                  else
                  {
                     display.closeForm();
                  }
               }

            });
         return;
      }
      else
      {
         swichEntryPoint();
      }
   }

   private void swichEntryPoint()
   {
      display.closeForm();
  
      System.out.println("storing entry point: " + selectedEntryPoint.getHref());
      
      CookieManager.getInstance().storeEntryPoint(selectedEntryPoint.getHref());
      eventBus.fireEvent(new SwitchEntryPointEvent(selectedEntryPoint.getHref()));
   }

   private void closeNextFile()
   {
      if (openedFiles.size() == 0)
      {
         swichEntryPoint();
         return;
      }

      String href = openedFiles.keySet().iterator().next();
      final File file = openedFiles.get(href);

      if (file.isContentChanged())
      {
         String message = "Do you want to save <b>" + Utils.unescape(file.getName()) + "</b> before closing?<br>&nbsp;";
         Dialogs.getInstance().ask("IDEall", message, new BooleanValueReceivedCallback()
         {
            public void execute(Boolean value)
            {
               if (value == null)
               {
                  return;
               }

               if (value)
               {
                  if (file.isNewFile())
                  {
                     eventBus.fireEvent(new SaveFileAsEvent(file, true));
                  }
                  else
                  {
                     VirtualFileSystem.getInstance().saveContent(file);
                  }
               }
               else
               {
                  eventBus.fireEvent(new EditorCloseFileEvent(file, true));
                  closeNextFile();
               }
            }

         });
         return;
      }
      else
      {
         eventBus.fireEvent(new EditorCloseFileEvent(file, true));
         closeNextFile();
      }
   }

   public void onFileContentSaved(FileContentSavedEvent event)
   {
      eventBus.fireEvent(new EditorCloseFileEvent(event.getFile(), true));
      closeNextFile();
   }

}
