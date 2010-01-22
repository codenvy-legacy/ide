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
package org.exoplatform.ideall.client.editor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.exoplatform.gwt.commons.editor.codemirror.event.CodeMirrorActivityEvent;
import org.exoplatform.gwt.commons.editor.codemirror.event.CodeMirrorActivityHandler;
import org.exoplatform.gwt.commons.editor.codemirror.event.CodeMirrorContentChangedEvent;
import org.exoplatform.gwt.commons.editor.codemirror.event.CodeMirrorContentChangedHandler;
import org.exoplatform.gwt.commons.editor.codemirror.event.CodeMirrorInitializedEvent;
import org.exoplatform.gwt.commons.editor.codemirror.event.CodeMirrorInitializedHandler;
import org.exoplatform.gwt.commons.editor.codemirror.event.CodeMirrorSaveContentEvent;
import org.exoplatform.gwt.commons.editor.codemirror.event.CodeMirrorSaveContentHandler;
import org.exoplatform.gwt.commons.smartgwt.dialogs.BooleanReceivedCallback;
import org.exoplatform.gwt.commons.smartgwt.dialogs.Dialogs;
import org.exoplatform.ideall.client.Handlers;
import org.exoplatform.ideall.client.Utils;
import org.exoplatform.ideall.client.application.event.InitializeApplicationEvent;
import org.exoplatform.ideall.client.application.event.InitializeApplicationHandler;
import org.exoplatform.ideall.client.application.event.RegisterEventHandlersEvent;
import org.exoplatform.ideall.client.application.event.RegisterEventHandlersHandler;
import org.exoplatform.ideall.client.cookie.CookieManager;
import org.exoplatform.ideall.client.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ideall.client.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ideall.client.editor.event.EditorCloseFileEvent;
import org.exoplatform.ideall.client.editor.event.EditorCloseFileHandler;
import org.exoplatform.ideall.client.editor.event.EditorSetFocusEvent;
import org.exoplatform.ideall.client.editor.event.FileContentChangedEvent;
import org.exoplatform.ideall.client.event.edit.FormatFileEvent;
import org.exoplatform.ideall.client.event.edit.FormatFileHandler;
import org.exoplatform.ideall.client.event.edit.HideLineNumbersEvent;
import org.exoplatform.ideall.client.event.edit.HideLineNumbersHandler;
import org.exoplatform.ideall.client.event.edit.RedoEditingEvent;
import org.exoplatform.ideall.client.event.edit.RedoEditingHandler;
import org.exoplatform.ideall.client.event.edit.ShowLineNumbersEvent;
import org.exoplatform.ideall.client.event.edit.ShowLineNumbersHandler;
import org.exoplatform.ideall.client.event.edit.UndoEditingEvent;
import org.exoplatform.ideall.client.event.edit.UndoEditingHandler;
import org.exoplatform.ideall.client.event.file.FileCreatedEvent;
import org.exoplatform.ideall.client.event.file.FileCreatedHandler;
import org.exoplatform.ideall.client.event.file.ItemSelectedEvent;
import org.exoplatform.ideall.client.event.file.SaveFileAsEvent;
import org.exoplatform.ideall.client.event.file.SaveFileEvent;
import org.exoplatform.ideall.client.model.ApplicationContext;
import org.exoplatform.ideall.client.model.File;
import org.exoplatform.ideall.client.model.Folder;
import org.exoplatform.ideall.client.model.data.DataService;
import org.exoplatform.ideall.client.model.data.event.FileContentReceivedEvent;
import org.exoplatform.ideall.client.model.data.event.FileContentReceivedHandler;
import org.exoplatform.ideall.client.model.data.event.FileContentSavedEvent;
import org.exoplatform.ideall.client.model.data.event.FileContentSavedHandler;
import org.exoplatform.ideall.client.model.data.event.ItemDeletedEvent;
import org.exoplatform.ideall.client.model.data.event.ItemDeletedHandler;
import org.exoplatform.ideall.client.model.data.event.ItemPropertiesSavedEvent;
import org.exoplatform.ideall.client.model.data.event.ItemPropertiesSavedHandler;
import org.exoplatform.ideall.client.model.data.event.MoveCompleteEvent;
import org.exoplatform.ideall.client.model.data.event.MoveCompleteHandler;
import org.exoplatform.ideall.client.operation.properties.event.FilePropertiesChangedEvent;
import org.exoplatform.ideall.client.operation.properties.event.FilePropertiesChangedHandler;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Timer;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class EditorPresenter implements FileCreatedHandler, CodeMirrorContentChangedHandler,
   CodeMirrorInitializedHandler, CodeMirrorActivityHandler, CodeMirrorSaveContentHandler,
   EditorActiveFileChangedHandler, EditorCloseFileHandler, UndoEditingHandler, RedoEditingHandler,
   FileContentSavedHandler, ItemPropertiesSavedHandler, FilePropertiesChangedHandler, FileContentReceivedHandler,
   MoveCompleteHandler, FormatFileHandler, ItemDeletedHandler, RegisterEventHandlersHandler,
   InitializeApplicationHandler, ShowLineNumbersHandler, HideLineNumbersHandler
{

   public interface Display
   {

      void addTab(File file, boolean lineNumbers);

      void relocateFile(File oldFile, File newFile);

      void closeTab(String path);

      void selectTab(String path);

      void setTabContent(String path, String text);

      String getTabContent(String path);

      void updateTabTitle(String path);

      String getPathByEditorId(String codeMirrorId);

      void undoEditing(String path);

      void redoEditing(String path);

      boolean hasUndoChanges(String path);

      boolean hasRedoChanges(String path);

      void formatFile(String path);

      void setLineNumbers(String path, boolean lineNumbers);

      void setCodemirrorFocus(String path);

   }

   private HandlerManager eventBus;

   private Display display;

   private Handlers handlers;

   private ApplicationContext context;

   private ArrayList<String> ignoreContentChangedList = new ArrayList<String>();

   private boolean closeFileAfterSaving = false;

   public EditorPresenter(HandlerManager eventBus, ApplicationContext context)
   {
      this.eventBus = eventBus;
      this.context = context;
      handlers = new Handlers(eventBus);
      handlers.addHandler(RegisterEventHandlersEvent.TYPE, this);
      handlers.addHandler(InitializeApplicationEvent.TYPE, this);
   }

   public void bindDisplay(Display d)
   {
      display = d;
   }

   /**
    * Registration event handlers
    * 
    */
   public void onRegisterEventHandlers(RegisterEventHandlersEvent event)
   {
      for (File file : context.getOpenedFiles().values())
      {
         ignoreContentChangedList.add(file.getPath());
         System.out.println("adding tab > " + file.getPath());
         display.addTab(file, context.isShowLineNumbers());
      }

      registerHandlers();
   }

   /**
    * Initializing application handler
    * 
    */
   public void onInitializeApplication(InitializeApplicationEvent event)
   {
      if (context.getActiveFile() != null)
      {
         try
         {
            setFileAsActive(context.getActiveFile());
         }
         catch (Exception exc)
         {
            exc.printStackTrace();
         }
      }
      else
      {
         if (context.getOpenedFiles().size() > 0)
         {
            String fileName = (String)context.getOpenedFiles().keySet().toArray()[0];
            File file = context.getOpenedFiles().get(fileName);
            setFileAsActive(file);
         }

      }
   }

   private class UpdateActiveFileTimer extends Timer
   {

      private File file;

      public UpdateActiveFileTimer(File file)
      {
         this.file = file;
      }

      @Override
      public void run()
      {
         String path = file.getPath();
         eventBus.fireEvent(new EditorActiveFileChangedEvent(file, display.hasUndoChanges(path), display
            .hasRedoChanges(path)));
      }

   }

   private void setFileAsActive(File file)
   {
      display.selectTab(file.getPath());
      new UpdateActiveFileTimer(file).schedule(1000);
      new UpdateActiveFileTimer(file).schedule(500);
   }

   /**
    * Registering event handlers
    */
   private void registerHandlers()
   {
      handlers.addHandler(CodeMirrorContentChangedEvent.TYPE, this);
      handlers.addHandler(CodeMirrorInitializedEvent.TYPE, this);
      handlers.addHandler(CodeMirrorActivityEvent.TYPE, this);

      handlers.addHandler(FileCreatedEvent.TYPE, this);
      handlers.addHandler(ItemPropertiesSavedEvent.TYPE, this);

      handlers.addHandler(EditorActiveFileChangedEvent.TYPE, this);

      handlers.addHandler(CodeMirrorSaveContentEvent.TYPE, this);
      handlers.addHandler(EditorCloseFileEvent.TYPE, this);

      handlers.addHandler(UndoEditingEvent.TYPE, this);
      handlers.addHandler(RedoEditingEvent.TYPE, this);

      handlers.addHandler(FileContentSavedEvent.TYPE, this);
      handlers.addHandler(FilePropertiesChangedEvent.TYPE, this);
      handlers.addHandler(FileContentReceivedEvent.TYPE, this);
      handlers.addHandler(MoveCompleteEvent.TYPE, this);
      handlers.addHandler(FormatFileEvent.TYPE, this);
      handlers.addHandler(ItemDeletedEvent.TYPE, this);

      handlers.addHandler(ShowLineNumbersEvent.TYPE, this);
      handlers.addHandler(HideLineNumbersEvent.TYPE, this);
   }

   /**
    * Destroy 
    */
   public void destroy()
   {
      handlers.removeHandlers();
   }

   /* 
    * Fired when file created my menu or from template
    * (non-Javadoc)
    * @see org.exoplatform.gadgets.devtool.client.event.FileCreatedHandler#onFileCreated(org.exoplatform.gadgets.devtool.client.event.FileCreatedEvent)
    */
   public void onFileCreated(FileCreatedEvent event)
   {
      try
      {
         File file = event.getFile();

         if (context.getOpenedFiles().get(file.getPath()) != null)
         {
            String extension = file.getPath().substring(file.getPath().lastIndexOf("."));
            String pathOnly = file.getPath().substring(0, file.getPath().lastIndexOf("."));

            // changing file index
            int fileIndex = 1;
            while (true)
            {
               String path = pathOnly + " " + fileIndex + extension;

               if (context.getOpenedFiles().get(path) == null)
               {
                  file.setPath(path);
                  break;
               }

               fileIndex++;
            }
         }

         ignoreContentChangedList.add(file.getPath());
         context.setActiveFile(file);
         context.getOpenedFiles().put(file.getPath(), file);
         display.addTab(file, context.isShowLineNumbers());
         display.selectTab(file.getPath());

         CookieManager.storeOpenedFiles(context);
      }
      catch (Exception exc)
      {
         exc.printStackTrace();
      }

      CookieManager.storeOpenedFiles(context);      
   }

   /* Fired when codemirror is initialized
    * (non-Javadoc)
    * @see org.exoplatform.gwt.commons.editor.codemirror.event.CodeMirrorInitializedHandler#onCodeMirrorInitialized(org.exoplatform.gwt.commons.editor.codemirror.event.CodeMirrorInitializedEvent)
    */
   public void onCodeMirrorInitialized(CodeMirrorInitializedEvent event)
   {
      try
      {
         String codeMirrorId = event.getCodemirrorId();
         String path = display.getPathByEditorId(codeMirrorId);
         File file = context.getOpenedFiles().get(path);
         display.setTabContent(file.getPath(), file.getContent());
      }
      catch (Exception exc)
      {
         exc.printStackTrace();
      }
   }

   /* Editor content changed handler
    * (non-Javadoc)
    * @see org.exoplatform.gwt.commons.editor.codemirror.event.CodeMirrorContentChangedHandler#onCodemirrorContentChanged(org.exoplatform.gwt.commons.editor.codemirror.event.CodeMirrorContentChangedEvent)
    */
   public void onCodemirrorContentChanged(CodeMirrorContentChangedEvent event)
   {
      String codeMirrorId = event.getCodeMirrorId();
      String path = display.getPathByEditorId(codeMirrorId);

      if (ignoreContentChangedList.contains(path))
      {
         ignoreContentChangedList.remove(path);
         return;
      }

      File file = context.getOpenedFiles().get(path);
      file.setContentChanged(true);
      file.setContent(display.getTabContent(file.getPath()));
      display.updateTabTitle(path);
      eventBus.fireEvent(new FileContentChangedEvent(file, display.hasUndoChanges(path), display.hasRedoChanges(path)));
   }

   /* 
    * (non-Javadoc)
    * @see org.exoplatform.gwt.commons.editor.codemirror.event.CodeMirrorActivityHandler#onCodeMirrorActivity(org.exoplatform.gwt.commons.editor.codemirror.event.CodeMirrorActivityEvent)
    */
   public void onCodeMirrorActivity(CodeMirrorActivityEvent event)
   {
      eventBus.fireEvent(new EditorSetFocusEvent());
   }

   /* (non-Javadoc)
    * @see org.exoplatform.gadgets.devtool.client.editor.event.EditorActiveFileChangedHandler#onEditorChangedActiveFile(org.exoplatform.gadgets.devtool.client.editor.event.EditorActiveFileChangedEvent)
    */
   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      File curentFile = event.getFile();
      if (curentFile == null)
      {
         context.setActiveFile(null);
         return;
      }

      context.setActiveFile(curentFile);
      CookieManager.storeOpenedFiles(context);      
      display.setCodemirrorFocus(curentFile.getPath());
   }

   public void onCodeMirrorSaveContent(CodeMirrorSaveContentEvent event)
   {
      File file = context.getActiveFile();
      file.setContent(display.getTabContent(file.getPath()));
      if (file.isNewFile())
      {
         eventBus.fireEvent(new SaveFileAsEvent());
      }
      else
      {
         eventBus.fireEvent(new SaveFileEvent());
      }
   }

   public void onEditorCloseFile(EditorCloseFileEvent event)
   {
      final File file = event.getFile();
      if (!file.isContentChanged() && !file.isPropertiesChanged())
      {
         context.getOpenedFiles().remove(file.getPath());
         CookieManager.storeOpenedFiles(context);         
         return;
      }

      String message = "Do you want to save <b>" + Utils.unescape(file.getName()) + "</b> before closing?<br>&nbsp;";
      Dialogs.ask("DevTool", message, new BooleanReceivedCallback()
      {
         public void execute(Boolean value)
         {
            if (value == null)
            {
               return;
            }

            if (value == true)
            {
               closeFileAfterSaving = true;

               if (file.isNewFile())
               {
                  eventBus.fireEvent(new SaveFileAsEvent(file));
               }
               else
               {
                  file.setContent(display.getTabContent(file.getPath()));
                  eventBus.fireEvent(new SaveFileEvent());
               }
            }
            else
            {
               display.closeTab(file.getPath());
               context.getOpenedFiles().remove(file.getPath());
               CookieManager.storeOpenedFiles(context);               
            }
         }
      });
   }

   public void onUndoEditing(UndoEditingEvent event)
   {
      display.undoEditing(context.getActiveFile().getPath());
   }

   public void onRedoEditing(RedoEditingEvent event)
   {
      display.redoEditing(context.getActiveFile().getPath());
   }

   private void updateTabTitle(String path)
   {
      display.updateTabTitle(path);
   }

   public void onFileContentSaved(FileContentSavedEvent event)
   {
      if (closeFileAfterSaving)
      {
         closeFileAfterSaving = false;
         display.closeTab(event.getFile().getPath());
         context.getOpenedFiles().remove(event.getFile().getPath());
      }
      else
      {
         File currentOpenedFile = context.getActiveFile();
         File savedFile = event.getFile();

         if (event.isSaveAs() || event.isNewFile())
         {
            savedFile.setPath(event.getPath());
            context.getOpenedFiles().remove(context.getActiveFile().getPath());
            context.setActiveFile(savedFile);
            context.getOpenedFiles().put(savedFile.getPath(), savedFile);
            display.relocateFile(currentOpenedFile, savedFile);
         }

         System.out.println("saved file content changed: " + savedFile.isContentChanged());
         System.out.println("saved file properties changed: " + savedFile.isPropertiesChanged());

         updateTabTitle(savedFile.getPath());
      }

      CookieManager.storeOpenedFiles(context);
   }

   /*
    * File properties saved handler
    * 
    */
   public void onItemPropertiesSaved(ItemPropertiesSavedEvent event)
   {
      if (!(event.getItem() instanceof File))
      {
         return;
      }

      updateTabTitle(event.getItem().getPath());
   }

   public void onFilePropertiesChanged(FilePropertiesChangedEvent event)
   {
      updateTabTitle(event.getFile().getPath());
   }

   public void onFileContentReceived(FileContentReceivedEvent event)
   {
      File file = event.getFile();
      if (context.getOpenedFiles().get(file.getPath()) != null)
      {
         display.selectTab(file.getPath());
      }
      else
      {
         ignoreContentChangedList.add(file.getPath());
         context.getOpenedFiles().put(file.getPath(), file);
         display.addTab(file, context.isShowLineNumbers());
         display.selectTab(file.getPath());
      }

      context.setActiveFile(file);
      CookieManager.storeOpenedFiles(context);
   }

   public void onMoveComplete(MoveCompleteEvent event)
   {
      String dest = event.getDestination();
      ArrayList<String> keys = new ArrayList<String>();
      for (String key : context.getOpenedFiles().keySet())
      {
         keys.add(key);
      }

      for (String key : keys)
      {
         if (key.startsWith(event.getItem().getPath()))
         {
            File file = context.getOpenedFiles().get(key);
            String sourcePath = file.getPath();
            String destinationPath = file.getPath();
            destinationPath = destinationPath.substring(event.getItem().getPath().length());
            destinationPath = dest + destinationPath;
            file.setPath(destinationPath);
            display.updateTabTitle(file.getPath());

            context.getOpenedFiles().remove(sourcePath);
            context.getOpenedFiles().put(destinationPath, file);
         }
      }

      CookieManager.storeOpenedFiles(context);
   }

   public void onFormatFile(FormatFileEvent event)
   {
      display.formatFile(context.getActiveFile().getPath());
   }

   public void onItemDeleted(ItemDeletedEvent event)
   {
      // fix of bug (WBT-225)
      String path = event.getItem().getPath();

      if (event.getItem() instanceof File)
      {
         display.closeTab(path); // trying to close tab with the same path
         context.getOpenedFiles().remove(path);
      }
      else
      {
         //find out the files are been in the removed folder
         HashMap<String, File> openedFiles = context.getOpenedFiles();

         for (File openedFile : openedFiles.values())
         {
            if (Utils.match(openedFile.getPath(), "^" + path + ".*", ""))
            {
               display.closeTab(openedFile.getPath());
               context.getOpenedFiles().remove(path);
            }
         }
      }

      // set selectedItem on parent folder (fix of bug (WBT-231))
      String selectedItemPath = context.getSelectedItem().getPath();

      selectedItemPath = selectedItemPath.substring(0, selectedItemPath.lastIndexOf("/"));

      Folder folder = new Folder(selectedItemPath);
      DataService.getInstance().getFolderContent(folder.getPath());

      context.setSelectedItem(folder);

      eventBus.fireEvent(new ItemSelectedEvent(folder));

      CookieManager.storeOpenedFiles(context);
   }

   private void updateLineNumbers(boolean lineNumbers)
   {
      Iterator<String> iterator = context.getOpenedFiles().keySet().iterator();
      while (iterator.hasNext())
      {
         String path = iterator.next();
         display.setLineNumbers(path, lineNumbers);
      }
      display.setCodemirrorFocus(context.getActiveFile().getPath());
   }

   public void onShowLineNumbers(ShowLineNumbersEvent event)
   {
      updateLineNumbers(true);
   }

   public void onHideLineNumbers(HideLineNumbersEvent event)
   {
      updateLineNumbers(false);
   }

}
