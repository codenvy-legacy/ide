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
package org.exoplatform.ide.client.editor.custom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.commons.dialogs.BooleanValueReceivedHandler;
import org.exoplatform.gwtframework.commons.dialogs.Dialogs;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.editor.api.Editor;
import org.exoplatform.gwtframework.editor.api.EditorFactory;
import org.exoplatform.gwtframework.editor.api.EditorNotFoundException;
import org.exoplatform.ide.client.framework.event.OpenFileEvent;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings.Store;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsSavedEvent;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsSavedHandler;
import org.exoplatform.ide.client.framework.settings.event.SaveApplicationSettingsEvent;
import org.exoplatform.ide.client.framework.settings.event.SaveApplicationSettingsEvent.SaveType;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.client.framework.vfs.event.FileContentReceivedEvent;
import org.exoplatform.ide.client.framework.vfs.event.FileContentReceivedHandler;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasValue;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
*/
public class OpenFileWithPresenter implements FileContentReceivedHandler, ApplicationSettingsSavedHandler
{

   public interface Display
   {

      void closeForm();

      EditorsListGrid getEditorsListGrid();

      HasValue<Boolean> getIsDefaultCheckItem();

      HasClickHandlers getOkButton();

      void enableOpenButton();

      HasClickHandlers getCancelButton();

   }

   private HandlerManager eventBus;

   private Display display;

   private Handlers handlers;

   private Editor selectedEditor;

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
      handlers = new Handlers(eventBus);
   }

   public void destroy()
   {
      handlers.removeHandlers();
   }

   public void bindDisplay(Display d)
   {
      handlers.addHandler(FileContentReceivedEvent.TYPE, this);
      handlers.addHandler(ApplicationSettingsSavedEvent.TYPE, this);

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
            if (event.getSelectedItem() == selectedEditor)
            {
               return;
            }

            selectedEditor = event.getSelectedItem().getEditor();
            display.enableOpenButton();
         }

      });

      fillEditorListGrid();
   }

   private void fillEditorListGrid()
   {
      String mimeType = selectedFile.getContentType();

      try
      {
         List<Editor> editorsItems = EditorFactory.getEditors(mimeType);

         List<EditorInfo> editorInfoItems = new ArrayList<EditorInfo>();

         Editor defaultEditor = null;
         
         Map<String, String> defaultEditors = applicationSettings.getValueAsMap("default-editors");
         if (defaultEditors == null) 
         {
            defaultEditors = new HashMap<String, String>();
         }

         if (defaultEditors.get(mimeType) != null)
         {
            String defaultEdotorDecription = defaultEditors.get(mimeType);
            for (Editor e : editorsItems)
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

         for (Editor e : editorsItems)
         {
            if (e.getDescription().equals(defaultEditor.getDescription()))
            {
               editorInfoItems.add(new EditorInfo(e, true));
            }
            else
            {
               editorInfoItems.add(new EditorInfo(e, false));
            }
         }

         display.getEditorsListGrid().setValue(editorInfoItems);
      }
      catch (EditorNotFoundException e)
      {
         String message = "Can't find editor for type <b>" + mimeType + "</b>";
         eventBus.fireEvent(new ExceptionThrownEvent(new Exception(message)));
      }
   }

   private void openFile()
   {
      if (display.getIsDefaultCheckItem().getValue() == null || display.getIsDefaultCheckItem().getValue() == false)
      {
         eventBus.fireEvent(new OpenFileEvent(selectedFile, selectedEditor.getDescription()));
         display.closeForm();
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
         eventBus.fireEvent(new SaveApplicationSettingsEvent(applicationSettings, SaveType.REGISTRY));
      }
   }

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

   private void tryOpenFile()
   {
      if (openedFiles.get(selectedFile.getHref()) != null)
      {
         showAskReopenDialog();
         return;
      }

      openFile();
   }

   public void onFileContentReceived(FileContentReceivedEvent event)
   {
      display.closeForm();
   }

   public void onApplicationSettingsSaved(ApplicationSettingsSavedEvent event)
   {
      display.closeForm();
      eventBus.fireEvent(new OpenFileEvent(selectedFile));
   }

}
