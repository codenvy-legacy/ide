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
import java.util.Iterator;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.editor.api.Editor;
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
import org.exoplatform.ideall.client.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ideall.client.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ideall.client.editor.event.EditorChangeActiveFileEvent;
import org.exoplatform.ideall.client.editor.event.EditorChangeActiveFileHandler;
import org.exoplatform.ideall.client.editor.event.EditorCloseFileEvent;
import org.exoplatform.ideall.client.editor.event.EditorCloseFileHandler;
import org.exoplatform.ideall.client.editor.event.EditorFileContentChangedEvent;
import org.exoplatform.ideall.client.editor.event.EditorOpenFileEvent;
import org.exoplatform.ideall.client.editor.event.EditorOpenFileHandler;
import org.exoplatform.ideall.client.editor.event.EditorUpdateFileStateEvent;
import org.exoplatform.ideall.client.editor.event.EditorUpdateFileStateHandler;
import org.exoplatform.ideall.client.event.edit.FormatFileEvent;
import org.exoplatform.ideall.client.event.edit.FormatFileHandler;
import org.exoplatform.ideall.client.event.edit.RedoEditingEvent;
import org.exoplatform.ideall.client.event.edit.RedoEditingHandler;
import org.exoplatform.ideall.client.event.edit.ShowLineNumbersEvent;
import org.exoplatform.ideall.client.event.edit.ShowLineNumbersHandler;
import org.exoplatform.ideall.client.event.edit.UndoEditingEvent;
import org.exoplatform.ideall.client.event.edit.UndoEditingHandler;
import org.exoplatform.ideall.client.event.file.FileSavedEvent;
import org.exoplatform.ideall.client.event.file.FileSavedHandler;
import org.exoplatform.ideall.client.event.file.SaveFileAsEvent;
import org.exoplatform.ideall.client.event.file.SaveFileEvent;
import org.exoplatform.ideall.client.model.ApplicationContext;
import org.exoplatform.ideall.client.model.vfs.api.File;
import org.exoplatform.ideall.client.model.vfs.api.event.MoveCompleteEvent;
import org.exoplatform.ideall.client.model.vfs.api.event.MoveCompleteHandler;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Timer;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class EditorPresenter implements EditorContentChangedHandler, EditorInitializedHandler, EditorActivityHandler,
   EditorSaveContentHandler, EditorActiveFileChangedHandler, EditorCloseFileHandler, UndoEditingHandler,
   RedoEditingHandler, FormatFileHandler, RegisterEventHandlersHandler, MoveCompleteHandler,
   InitializeApplicationHandler, ShowLineNumbersHandler, EditorChangeActiveFileHandler, EditorOpenFileHandler,
   FileSavedHandler, EditorUpdateFileStateHandler
{

   public interface Display
   {

      void openTab(File file, boolean lineNumbers, Editor editor);

      void relocateFile(File oldFile, File newFile);

      void closeTab(String href);

      void selectTab(String href);

      void setTabContent(String path, String text);

      String getTabContent(String href);

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

      handlers.addHandler(EditorOpenFileEvent.TYPE, this);
      
      handlers.addHandler(EditorUpdateFileStateEvent.TYPE, this);
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
         ignoreContentChangedList.add(file.getHref());
         try
         {
            Editor editor = EditorUtil.getEditor(file.getContentType(), context);
            context.getOpenedEditors().put(file.getHref(), editor.getDescription());
            display.openTab(file, context.isShowLineNumbers(), editor);
         }
         catch (EditorNotFoundException e)
         {
            e.printStackTrace();
         }
      }

      handlers.addHandler(EditorContentChangedEvent.TYPE, this);
      handlers.addHandler(EditorInitializedEvent.TYPE, this);
      handlers.addHandler(EditorActivityEvent.TYPE, this);
      handlers.addHandler(EditorSaveContentEvent.TYPE, this);

      handlers.addHandler(MoveCompleteEvent.TYPE, this);

      handlers.addHandler(EditorActiveFileChangedEvent.TYPE, this);

      handlers.addHandler(EditorCloseFileEvent.TYPE, this);

      handlers.addHandler(UndoEditingEvent.TYPE, this);
      handlers.addHandler(RedoEditingEvent.TYPE, this);

      handlers.addHandler(FileSavedEvent.TYPE, this);

      handlers.addHandler(FormatFileEvent.TYPE, this);

      handlers.addHandler(ShowLineNumbersEvent.TYPE, this);

      handlers.addHandler(EditorChangeActiveFileEvent.TYPE, this);

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
    * Destroy 
    */
   public void destroy()
   {
      handlers.removeHandlers();
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
         display.setTabContent(file.getHref(), file.getContent());
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
      file.setContent(display.getTabContent(file.getHref()));
      display.updateTabTitle(path);
      eventBus.fireEvent(new EditorFileContentChangedEvent(file, display.hasUndoChanges(path), display
         .hasRedoChanges(path)));
   }

   /**
    * @see org.exoplatform.gwtframework.editor.event.EditorActivityHandler#onEditorActivity(org.exoplatform.gwtframework.editor.event.EditorActivityEvent)
    */
   public void onEditorActivity(EditorActivityEvent event)
   {
      //eventBus.fireEvent(new EditorSetFocusEvent());
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
      display.setEditorFocus(curentFile.getHref());
   }

   public void onEditorSaveContent(EditorSaveContentEvent event)
   {
      File file = context.getActiveFile();
      file.setContent(display.getTabContent(file.getHref()));
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
      display.closeTab(file.getHref());

      file.setContent(null);
      file.setContentChanged(false);

      context.getOpenedFiles().remove(file.getHref());
      CookieManager.storeOpenedFiles(context);
   }

   public void onEditorCloseFile(EditorCloseFileEvent event)
   {

      if (event.isForceClosing())
      {
         closeFile(event.getFile());
         return;
      }
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
                  file.setContent(display.getTabContent(file.getHref()));
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
      display.undoEditing(context.getActiveFile().getHref());
   }

   public void onRedoEditing(RedoEditingEvent event)
   {
      display.redoEditing(context.getActiveFile().getHref());
   }

   private void updateTabTitle(String href)
   {
      display.updateTabTitle(href);
   }

   public void onMoveComplete(MoveCompleteEvent event)
   {
//      String dest = event.getItem().getHref(); //getDestination();
//      ArrayList<String> keys = new ArrayList<String>();
//      for (String key : context.getOpenedFiles().keySet())
//      {
//         keys.add(key);
//      }
//
//      for (String key : keys)
//      {
//         if (key.startsWith(event.getItem().getHref()))
//         {
//            File file = context.getOpenedFiles().get(key);
//            String sourcePath = file.getHref();
//            String destinationPath = file.getHref();
//            destinationPath = destinationPath.substring(event.getItem().getHref().length());
//            destinationPath = dest + destinationPath;
//            file.setHref(destinationPath);
//            display.updateTabTitle(file.getHref());
//
//            context.getOpenedFiles().remove(event.getSource());
//            context.getOpenedFiles().put(destinationPath, file);
//            
//         }
//      }
//
//      CookieManager.storeOpenedFiles(context);
   }

   public void onFormatFile(FormatFileEvent event)
   {
      display.formatFile(context.getActiveFile().getHref());
   }

   private void updateLineNumbers(boolean lineNumbers)
   {
      Iterator<String> iterator = context.getOpenedFiles().keySet().iterator();
      while (iterator.hasNext())
      {
         String path = iterator.next();
         display.setLineNumbers(path, lineNumbers);
      }
      display.setEditorFocus(context.getActiveFile().getHref());
   }

   public void onShowLineNumbers(ShowLineNumbersEvent event)
   {
      updateLineNumbers(event.isShowLineNumber());
   }

   public void onEditorChangeActiveFile(EditorChangeActiveFileEvent event)
   {
      if (timer1 != null)
      {
         timer1.cancel();
      }

      if (timer2 != null)
      {
         timer2.cancel();
      }

      context.setActiveFile(event.getFile());
      display.selectTab(event.getFile().getHref());

      CookieManager.storeOpenedFiles(context);
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

         String href = context.getActiveFile().getHref();
         eventBus.fireEvent(new EditorActiveFileChangedEvent(context.getActiveFile(), display.hasUndoChanges(href),
            display.hasRedoChanges(href)));
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
      display.selectTab(context.getActiveFile().getHref());
      timer1 = new UpdateActiveFileTimer();
      timer1.schedule(1000);
      timer2 = new UpdateActiveFileTimer();
      timer2.schedule(500);
   }

   public void onEditorOpenFile(EditorOpenFileEvent event)
   {
      File file = event.getFile();

      if (context.getOpenedFiles().get(file.getHref()) != null
         && event.getEditor().getDescription().equals(context.getOpenedEditors().get(file.getHref())))
      {
         File openedFile = context.getOpenedFiles().get(file.getHref());
         context.setActiveFile(openedFile);
         display.selectTab(openedFile.getHref());
         CookieManager.storeOpenedFiles(context);
         return;
      }

      ignoreContentChangedList.add(file.getHref());

      display.openTab(file, context.isShowLineNumbers(), event.getEditor());

      context.getOpenedFiles().put(file.getHref(), file);
      context.getOpenedEditors().put(file.getHref(), event.getEditor().getDescription());

      context.setActiveFile(file);
      display.selectTab(file.getHref());

      CookieManager.storeOpenedFiles(context);
   }

   public void onFileSaved(FileSavedEvent event)
   {
      if (closeFileAfterSaving)
      {
         closeFile(event.getFile());
         
         closeFileAfterSaving = false;
      }
      else
      {
         File currentOpenedFile = context.getActiveFile();
         File savedFile = event.getFile();
         if (event.getSourceHref() != null)
         {
            context.getOpenedFiles().remove(event.getSourceHref());
            context.setActiveFile(savedFile);
            context.getOpenedFiles().put(savedFile.getHref(), savedFile);
            display.relocateFile(currentOpenedFile, savedFile);
         }

         updateTabTitle(savedFile.getHref());
         CookieManager.storeOpenedFiles(context);
      }


   }

   public void onEditorUdateFileState(EditorUpdateFileStateEvent event)
   {
      //TODO Auto-generated method stub
      display.updateTabTitle(event.getFile().getHref());
   }

}
