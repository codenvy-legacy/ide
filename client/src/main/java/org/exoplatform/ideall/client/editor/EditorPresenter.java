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
import java.util.List;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownHandler;
import org.exoplatform.gwtframework.editor.api.Editor;
import org.exoplatform.gwtframework.editor.api.EditorFactory;
import org.exoplatform.gwtframework.editor.api.EditorNotFoundException;
import org.exoplatform.gwtframework.editor.event.EditorActivityEvent;
import org.exoplatform.gwtframework.editor.event.EditorActivityHandler;
import org.exoplatform.gwtframework.editor.event.EditorContentChangedEvent;
import org.exoplatform.gwtframework.editor.event.EditorContentChangedHandler;
import org.exoplatform.gwtframework.editor.event.EditorInitializedEvent;
import org.exoplatform.gwtframework.editor.event.EditorInitializedHandler;
import org.exoplatform.gwtframework.editor.event.EditorSaveContentEvent;
import org.exoplatform.gwtframework.editor.event.EditorSaveContentHandler;
import org.exoplatform.gwtframework.ui.client.dialogs.Dialogs;
import org.exoplatform.gwtframework.ui.client.dialogs.callback.BooleanValueReceivedCallback;
import org.exoplatform.ideall.client.Utils;
import org.exoplatform.ideall.client.application.event.InitializeApplicationEvent;
import org.exoplatform.ideall.client.application.event.InitializeApplicationHandler;
import org.exoplatform.ideall.client.application.event.RegisterEventHandlersEvent;
import org.exoplatform.ideall.client.application.event.RegisterEventHandlersHandler;
import org.exoplatform.ideall.client.cookie.CookieManager;
import org.exoplatform.ideall.client.editor.event.ChangeActiveFileEvent;
import org.exoplatform.ideall.client.editor.event.ChangeActiveFileHandler;
import org.exoplatform.ideall.client.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ideall.client.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ideall.client.editor.event.EditorCloseFileEvent;
import org.exoplatform.ideall.client.editor.event.EditorCloseFileHandler;
import org.exoplatform.ideall.client.editor.event.EditorSetFocusEvent;
import org.exoplatform.ideall.client.editor.event.FileContentChangedEvent;
import org.exoplatform.ideall.client.event.edit.FormatFileEvent;
import org.exoplatform.ideall.client.event.edit.FormatFileHandler;
import org.exoplatform.ideall.client.event.edit.RedoEditingEvent;
import org.exoplatform.ideall.client.event.edit.RedoEditingHandler;
import org.exoplatform.ideall.client.event.edit.ShowLineNumbersEvent;
import org.exoplatform.ideall.client.event.edit.ShowLineNumbersHandler;
import org.exoplatform.ideall.client.event.edit.UndoEditingEvent;
import org.exoplatform.ideall.client.event.edit.UndoEditingHandler;
import org.exoplatform.ideall.client.event.file.FileCreatedEvent;
import org.exoplatform.ideall.client.event.file.FileCreatedHandler;
import org.exoplatform.ideall.client.event.file.SaveFileAsEvent;
import org.exoplatform.ideall.client.event.file.SaveFileEvent;
import org.exoplatform.ideall.client.model.ApplicationContext;
import org.exoplatform.ideall.client.model.vfs.api.File;
import org.exoplatform.ideall.client.model.vfs.api.event.FileContentReceivedEvent;
import org.exoplatform.ideall.client.model.vfs.api.event.FileContentReceivedHandler;
import org.exoplatform.ideall.client.model.vfs.api.event.FileContentSavedEvent;
import org.exoplatform.ideall.client.model.vfs.api.event.FileContentSavedHandler;
import org.exoplatform.ideall.client.model.vfs.api.event.ItemDeletedEvent;
import org.exoplatform.ideall.client.model.vfs.api.event.ItemDeletedHandler;
import org.exoplatform.ideall.client.model.vfs.api.event.ItemPropertiesSavedEvent;
import org.exoplatform.ideall.client.model.vfs.api.event.ItemPropertiesSavedHandler;
import org.exoplatform.ideall.client.model.vfs.api.event.MoveCompleteEvent;
import org.exoplatform.ideall.client.model.vfs.api.event.MoveCompleteHandler;
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

public class EditorPresenter implements FileCreatedHandler, EditorContentChangedHandler, EditorInitializedHandler,
   EditorActivityHandler, EditorSaveContentHandler, EditorActiveFileChangedHandler, EditorCloseFileHandler,
   UndoEditingHandler, RedoEditingHandler, FileContentSavedHandler, ItemPropertiesSavedHandler,
   FilePropertiesChangedHandler, FileContentReceivedHandler, MoveCompleteHandler, FormatFileHandler,
   ItemDeletedHandler, RegisterEventHandlersHandler, InitializeApplicationHandler, ShowLineNumbersHandler,
   ChangeActiveFileHandler, ExceptionThrownHandler
{

   public interface Display
   {

      void addTab(File file, boolean lineNumbers, Editor editor);

      void relocateFile(File oldFile, File newFile);

      void closeTab(String path);

      void selectTab(String path);

      void setTabContent(String path, String text);

      String getTabContent(String path);

      void updateTabTitle(String path);

      String getPathByEditorId(String editorId);

      void undoEditing(String path);

      void redoEditing(String path);

      boolean hasUndoChanges(String path);

      boolean hasRedoChanges(String path);

      void formatFile(String path);

      void setLineNumbers(String path, boolean lineNumbers);

      void setEditorFocus(String path);

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
         openFile(file);
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
            setFileAsActive();
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
            context.setActiveFile(file);
            setFileAsActive();
         }

      }
   }

   /**
    * Registering event handlers
    */
   private void registerHandlers()
   {
      handlers.addHandler(EditorContentChangedEvent.TYPE, this);
      handlers.addHandler(EditorInitializedEvent.TYPE, this);
      handlers.addHandler(EditorActivityEvent.TYPE, this);
      handlers.addHandler(EditorSaveContentEvent.TYPE, this);

      handlers.addHandler(FileCreatedEvent.TYPE, this);
      handlers.addHandler(ItemPropertiesSavedEvent.TYPE, this);

      handlers.addHandler(EditorActiveFileChangedEvent.TYPE, this);

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

      handlers.addHandler(ChangeActiveFileEvent.TYPE, this);

      handlers.addHandler(ExceptionThrownEvent.TYPE, this);
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
         openFile(file);
         display.selectTab(file.getPath());

         CookieManager.storeOpenedFiles(context);
      }
      catch (Exception exc)
      {
         exc.printStackTrace();
      }

      CookieManager.storeOpenedFiles(context);
   }

   /*
    *  Fired when editor is initialized
    */
   public void onEditorInitialized(EditorInitializedEvent event)
   {
      try
      {
         String editorId = event.getEditorId();
         String path = display.getPathByEditorId(editorId);
         File file = context.getOpenedFiles().get(path);
         display.setTabContent(file.getPath(), file.getContent());
      }
      catch (Exception exc)
      {
         exc.printStackTrace();
      }
   }

   /* 
    * Editor content changed handler
    */
   public void onEditorContentChanged(EditorContentChangedEvent event)
   {
      String editorId = event.getEditorId();
      String path = display.getPathByEditorId(editorId);

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

   /**
    * @see org.exoplatform.gwtframework.editor.event.EditorActivityHandler#onEditorActivity(org.exoplatform.gwtframework.editor.event.EditorActivityEvent)
    */
   public void onEditorActivity(EditorActivityEvent event)
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
      display.setEditorFocus(curentFile.getPath());
   }

   public void onEditorSaveContent(EditorSaveContentEvent event)
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

   private void closeFile(File file)
   {
      display.closeTab(file.getPath());
      file.setContent(null);
      file.setContentChanged(false);

      context.getOpenedFiles().remove(file.getPath());
      CookieManager.storeOpenedFiles(context);
   }

   public void onEditorCloseFile(EditorCloseFileEvent event)
   {
      final File file = event.getFile();
      if (!file.isContentChanged() && !file.isPropertiesChanged())
      {
         closeFile(file);
         return;
      }

      String message = "Do you want to save <b>" + Utils.unescape(file.getName()) + "</b> before closing?<br>&nbsp;";
      Dialogs.getInstance().ask("DevTool", message, new BooleanValueReceivedCallback()
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
               closeFile(file);
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
         closeFile(event.getFile());
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
         openFile(file);
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
         closeFile((File)event.getItem());
      }
      else
      {
         //find out the files are been in the removed folder
         HashMap<String, File> openedFiles = context.getOpenedFiles();

         HashMap<String, File> copy = new HashMap<String, File>();
         for (String key : openedFiles.keySet())
         {
            File file = openedFiles.get(key);
            copy.put(key, file);
         }

         for (File file : copy.values())
         {
            if (Utils.match(file.getPath(), "^" + path + ".*", ""))
            {
               closeFile(file);
            }
         }
      }

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
      display.setEditorFocus(context.getActiveFile().getPath());
   }

   public void onShowLineNumbers(ShowLineNumbersEvent event)
   {
      updateLineNumbers(event.isShowLineNumber());
   }

   public void onChangeActiveFile(ChangeActiveFileEvent event)
   {
      //ignoreContentChangedList.add(file.getPath());
      if (timer1 != null)
      {
         timer1.cancel();
      }

      if (timer2 != null)
      {
         timer2.cancel();
      }

      context.setActiveFile(event.getFile());
      //context.getOpenedFiles().put(file.getPath(), file);
      //display.addTab(file, context.isShowLineNumbers());
      display.selectTab(event.getFile().getPath());

      CookieManager.storeOpenedFiles(context);
      // TODO Auto-generated method stub
   }

   private class UpdateActiveFileTimer extends Timer
   {

      @Override
      public void run()
      {
         if (context.getActiveFile() == null)
         {
            return;
         }

         String path = context.getActiveFile().getPath();
         eventBus.fireEvent(new EditorActiveFileChangedEvent(context.getActiveFile(), display.hasUndoChanges(path),
            display.hasRedoChanges(path)));
      }

   }

   private UpdateActiveFileTimer timer1;

   private UpdateActiveFileTimer timer2;

   private void setFileAsActive()
   {
      if (context.getActiveFile() == null)
      {
         return;
      }
      display.selectTab(context.getActiveFile().getPath());
      timer1 = new UpdateActiveFileTimer();
      timer1.schedule(1000);
      timer2 = new UpdateActiveFileTimer();
      timer2.schedule(500);
   }

   public void onError(ExceptionThrownEvent event)
   {
      context.setSelectedEditorDescriptor(null);
   }

   protected void openFile(File file)
   {
      Editor editor;
      String mimeType = file.getContentType();
      try
      {
         String defaultEditorDescription;
         if (context.getSelectedEditorDescription() != null)
         {
            defaultEditorDescription = context.getSelectedEditorDescription();
            context.setSelectedEditorDescriptor(null);
         }
         else
         {
            defaultEditorDescription = context.getDefaultEditors().get(mimeType);
         }

         editor = getEditor(mimeType, defaultEditorDescription);
      }
      catch (EditorNotFoundException exc)
      {
         Dialogs.getInstance().showError("Can't find editor for type <b>" + mimeType + "</b>");
         return;
      }

      display.addTab(file, context.isShowLineNumbers(), editor);
   }

   private Editor getEditor(String mimeType, String defaultEditorDescription) throws EditorNotFoundException
   {
      Editor editor = null;

      if (defaultEditorDescription == null)
      {
         editor = EditorFactory.getDefaultEditor(mimeType);
      }
      else
      {
         List<Editor> editors = EditorFactory.getEditors(mimeType);
         for (Editor e : editors)
         {
            if (e.getDescription().equals(defaultEditorDescription))
            {
               editor = e;
               break;
            }
         }
      }

      if (editor == null)
      {
         throw new EditorNotFoundException();
      }

      return editor;
   }

}
