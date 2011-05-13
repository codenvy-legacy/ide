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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.exoplatform.gwtframework.commons.dialogs.BooleanValueReceivedHandler;
import org.exoplatform.gwtframework.commons.dialogs.Dialogs;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.client.editor.EditorFactory;
import org.exoplatform.ide.client.framework.editor.EditorNotFoundException;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedHandler;
import org.exoplatform.ide.client.framework.event.OpenFileEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings.Store;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedEvent;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedHandler;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.client.framework.vfs.Item;
import org.exoplatform.ide.client.model.settings.SettingsService;
import org.exoplatform.ide.client.navigation.event.OpenFileWithEvent;
import org.exoplatform.ide.client.navigation.event.OpenFileWithHandler;
import org.exoplatform.ide.editor.api.Editor;
import org.exoplatform.ide.editor.api.EditorProducer;

import com.google.gwt.core.client.GWT;
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
 * Presenter for open file with editor form.
 * 
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
*/
public class OpenFileWithPresenter implements EditorFileOpenedHandler, EditorFileClosedHandler, OpenFileWithHandler,
   ItemsSelectedHandler, ViewClosedHandler, ApplicationSettingsReceivedHandler
{

   public interface Display extends IsView
   {

      EditorsListGrid getEditorsListGrid();

      void setSelectedItem(EditorInfo item);

      HasValue<Boolean> getIsDefaultCheckItem();

      HasClickHandlers getOpenButton();

      void setOpenButtonEnabled(boolean enabled);

      HasClickHandlers getCancelButton();

   }

   private HandlerManager eventBus;

   private Display display;

   private EditorProducer selectedEditor;

   private File selectedFile;

   private Map<String, File> openedFiles;
   
   private Map<String, String> openedEditorDescriptions = new HashMap<String, String>();

   private ApplicationSettings applicationSettings;
   
   public OpenFileWithPresenter(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
      eventBus.addHandler(OpenFileWithEvent.TYPE, this);
      eventBus.addHandler(ViewClosedEvent.TYPE, this);
      eventBus.addHandler(ItemsSelectedEvent.TYPE, this);
      eventBus.addHandler(ApplicationSettingsReceivedEvent.TYPE, this);

      eventBus.addHandler(EditorFileOpenedEvent.TYPE, this);
      eventBus.addHandler(EditorFileClosedEvent.TYPE, this);
   }

   public void onOpenFileWith(OpenFileWithEvent event)
   {
      if (display != null)
      {
         return;
      }

      display = GWT.create(Display.class);
      IDE.getInstance().openView(display.asView());
      bindDisplay();
   }

   public void bindDisplay()
   {
      selectedEditor = null;

      display.getCancelButton().addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent arg0)
         {
            IDE.getInstance().closeView(display.asView().getId());
         }
      });

      display.getEditorsListGrid().addDoubleClickHandler(new DoubleClickHandler()
      {

         public void onDoubleClick(DoubleClickEvent arg0)
         {
            tryOpenFile();
         }

      });

      display.getOpenButton().addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent event)
         {
            tryOpenFile();
         }
      });

      display.getEditorsListGrid().addSelectionHandler(new SelectionHandler<EditorInfo>()
      {
         public void onSelection(SelectionEvent<EditorInfo> event)
         {
            onEditorSelected(event.getSelectedItem().getEditor());
         }

      });

      fillEditorListGrid();
   }
   
   private void onEditorSelected(EditorProducer editor) {
      if (openedEditorDescriptions.get(selectedFile.getHref()) != null &&
         editor.getDescription().equals(openedEditorDescriptions.get(selectedFile.getHref()))) {
         display.setOpenButtonEnabled(false);
         return;
      }

      selectedEditor = editor;
      display.setOpenButtonEnabled(true);      
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
      Dialogs.getInstance().ask("IDE",
         "Do you want to reopen <b>" + selectedFile.getName() + "</b> in selected editor?",
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
      if (openedFiles != null && openedFiles.get(selectedFile.getHref()) != null)
      {
         showAskReopenDialog();
         return;
      }

      openFile();
   }

   @Override
   public void onItemsSelected(ItemsSelectedEvent event)
   {
      if (event.getSelectedItems().size() != 1 || !(event.getSelectedItems().get(0) instanceof File))
      {
         selectedFile = null;
         return;
      }

      Item selectedItem = (File)event.getSelectedItems().get(0);
      if (selectedItem instanceof File)
      {
         selectedFile = (File)selectedItem;
      }
      else
      {
         selectedFile = null;
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedHandler#onEditorFileOpened(org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedEvent)
    */
   @Override
   public void onEditorFileOpened(EditorFileOpenedEvent event)
   {
      openedFiles = event.getOpenedFiles();
      openedEditorDescriptions.put(event.getFile().getHref(), event.getEditorDescription());

      if (display != null)
      {
         IDE.getInstance().closeView(display.asView().getId());
      }
   }

   @Override
   public void onEditorFileClosed(EditorFileClosedEvent event)
   {
      openedFiles = event.getOpenedFiles();
      openedEditorDescriptions.remove(event.getEditorDescription());
   }

   @Override
   public void onViewClosed(ViewClosedEvent event)
   {
      if (event.getView() instanceof Display)
      {
         display = null;
      }
   }

   @Override
   public void onApplicationSettingsReceived(ApplicationSettingsReceivedEvent event)
   {
      applicationSettings = event.getApplicationSettings();
   }

}
