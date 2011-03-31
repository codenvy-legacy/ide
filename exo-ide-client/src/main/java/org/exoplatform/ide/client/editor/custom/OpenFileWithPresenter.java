/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package org.exoplatform.ide.client.editor.custom;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.gwtframework.commons.dialogs.BooleanValueReceivedHandler;
import org.exoplatform.gwtframework.commons.dialogs.Dialogs;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.client.editor.EditorFactory;
import org.exoplatform.ide.client.framework.editor.EditorNotFoundException;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedHandler;
import org.exoplatform.ide.client.framework.event.OpenFileEvent;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings.Store;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.client.model.settings.SettingsService;
import org.exoplatform.ide.editor.api.EditorProducer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Presenter for open file with editor form.
 * 
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
*/
public class OpenFileWithPresenter implements EditorFileOpenedHandler
{

   public interface Display
   {

      void closeForm();

      EditorsListGrid getEditorsListGrid();

      HasValue<Boolean> getIsDefaultCheckItem();

      HasClickHandlers getOkButton();

      void enableOpenButton();

      HasClickHandlers getCancelButton();

      void setSelectedItem(EditorInfo item);      
   }

   private HandlerManager eventBus;

   private Display display;

   private EditorProducer selectedEditor;

   private File selectedFile;

   private Map<String, File> openedFiles;

   private ApplicationSettings applicationSettings;

   public OpenFileWithPresenter(HandlerManager eventBus, File selectedFile, Map<String, File> openedFiles,
      ApplicationSettings applicationSettings)
   {
      this.eventBus = eventBus;
      this.selectedFile = selectedFile;
      this.openedFiles = openedFiles;
      this.applicationSettings = applicationSettings;
      this.eventBus.addHandler(EditorFileOpenedEvent.TYPE, this);
   }

   public void destroy()
   {
   }
   
   public void bindDisplay(Display d)
   {

      display = d;
      display.getCancelButton().addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent arg0)
         {
            display.closeForm();
         }
      });

      display.getEditorsListGrid().addDoubleClickHandler(new DoubleClickHandler()
      {

         public void onDoubleClick(DoubleClickEvent arg0)
         {
            tryOpenFile();
         }

      });
      
      display.getOkButton().addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent arg0)
         {
            tryOpenFile();
         }
      });

      display.getEditorsListGrid().addSelectionHandler(new SelectionHandler<EditorInfo>()
      {

         public void onSelection(SelectionEvent<EditorInfo> event)
         {
            if (event.getSelectedItem().getEditor() == selectedEditor)
            {
               return;
            }

            selectedEditor = event.getSelectedItem().getEditor();
            display.enableOpenButton();
         }

      });

      fillEditorListGrid();
   }

   /**
    * Find all editors for file's mime-type and show
    * them in list grid.
    */
   private void fillEditorListGrid()
   {
      String mimeType = selectedFile.getContentType();

      try
      {
         List<EditorProducer> editorsItems = EditorFactory.getEditors(mimeType);

         List<EditorInfo> editorInfoItems = new ArrayList<EditorInfo>();

         EditorProducer defaultEditor = null;
         
         Map<String, String> defaultEditors = applicationSettings.getValueAsMap("default-editors");
         if (defaultEditors == null) 
         {
            defaultEditors = new HashMap<String, String>();
         }

         if (defaultEditors.get(mimeType) != null)
         {
            String defaultEdotorDecription = defaultEditors.get(mimeType);
            for (EditorProducer e : editorsItems)
            {
               if (e.getDescription().equals(defaultEdotorDecription))
               {
                  defaultEditor = e;
               }
            }
         }
         else
         {
            defaultEditor = EditorFactory.getDefaultEditor(mimeType);
         }

         EditorInfo defaultEditorItem = null;
         
         for (EditorProducer e : editorsItems)
         {
            if (e.getDescription().equals(defaultEditor.getDescription()))
            {
               defaultEditorItem = new EditorInfo(e, true);
               editorInfoItems.add(defaultEditorItem); 
            }
            else
            {
               editorInfoItems.add(new EditorInfo(e, false));
            }
         }

         display.getEditorsListGrid().setValue(editorInfoItems);
       
         if (defaultEditorItem != null)
         {
            display.setSelectedItem(defaultEditorItem);            
         }

      }
      catch (EditorNotFoundException e)
      {
         String message = "Can't find editor for type <b>" + mimeType + "</b>";
         eventBus.fireEvent(new ExceptionThrownEvent(new Exception(message)));
      }
   }

   /**
    * Open file in selected editor.
    * Fires {@link OpenFileEvent} and close form if all success.
    */
   private void openFile()
   {
      if (display.getIsDefaultCheckItem().getValue() == null || display.getIsDefaultCheckItem().getValue() == false)
      {
         eventBus.fireEvent(new OpenFileEvent(selectedFile, selectedEditor.getDescription()));
      }
      else
      {
         String mimeType = selectedFile.getContentType();
         
         Map<String, String> defaultEditors = applicationSettings.getValueAsMap("default-editors");
         if (defaultEditors == null) 
         {
            defaultEditors = new HashMap<String, String>();
            applicationSettings.setValue("default-editors", defaultEditors, Store.REGISTRY);
         }
         
         defaultEditors.put(mimeType, selectedEditor.getDescription());
         
         SettingsService.getInstance().saveSettingsToRegistry(applicationSettings, 
            new AsyncRequestCallback<ApplicationSettings>()
            {

               @Override
               protected void onSuccess(ApplicationSettings result)
               {
                  eventBus.fireEvent(new OpenFileEvent(selectedFile));
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  eventBus.fireEvent(new ExceptionThrownEvent("Can't save information about default editor"));
               }
            });
      }
   }

   /**
    * If file is opened, than show reopen dialog.
    */
   private void showAskReopenDialog()
   {
      Dialogs.getInstance().ask("Info", "Do you want to reopen <b>" + selectedFile.getName() + "</b> in selected editor?",
         new BooleanValueReceivedHandler()
         {
            public void booleanValueReceived(Boolean value)
            {
               if (value == null)
               {
                  return;
               }

               if (value == true)
               {
                  openFile();
               }
               else
               {
                  display.closeForm();
               }
            }
         });
   }

   /**
    * Check, is file opened or closed.
    * If opened, than show ask dialog,
    * otherwise open file.
    */
   private void tryOpenFile()
   {
      if (openedFiles.get(selectedFile.getHref()) != null)
      {
         showAskReopenDialog();
         return;
      }

      openFile();
   }

   /**
    * @see org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedHandler#onEditorFileOpened(org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedEvent)
    */
   @Override
   public void onEditorFileOpened(EditorFileOpenedEvent event)
   {
      display.closeForm();
   }

}
