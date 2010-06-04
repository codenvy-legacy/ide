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
import org.exoplatform.gwtframework.editor.api.TextEditor;
import org.exoplatform.gwtframework.editor.event.EditorActivityEvent;
import org.exoplatform.gwtframework.editor.event.EditorActivityHandler;
import org.exoplatform.gwtframework.editor.event.EditorContentChangedEvent;
import org.exoplatform.gwtframework.editor.event.EditorContentChangedHandler;
import org.exoplatform.gwtframework.editor.event.EditorInitializedEvent;
import org.exoplatform.gwtframework.editor.event.EditorInitializedHandler;
import org.exoplatform.gwtframework.editor.event.EditorSaveContentEvent;
import org.exoplatform.gwtframework.editor.event.EditorSaveContentHandler;
import org.exoplatform.gwtframework.commons.dialogs.Dialogs;
import org.exoplatform.gwtframework.commons.dialogs.callback.BooleanValueReceivedCallback;
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
import org.exoplatform.ideall.client.editor.event.EditorGoToLineEvent;
import org.exoplatform.ideall.client.editor.event.EditorGoToLineHandler;
import org.exoplatform.ideall.client.editor.event.EditorOpenFileEvent;
import org.exoplatform.ideall.client.editor.event.EditorOpenFileHandler;
import org.exoplatform.ideall.client.editor.event.EditorSetFocusOnActiveFileEvent;
import org.exoplatform.ideall.client.editor.event.EditorSetFocusOnActiveFileHandler;
import org.exoplatform.ideall.client.editor.event.EditorUpdateFileStateEvent;
import org.exoplatform.ideall.client.editor.event.EditorUpdateFileStateHandler;
import org.exoplatform.ideall.client.event.edit.DeleteCurrentLineEvent;
import org.exoplatform.ideall.client.event.edit.DeleteCurrentLineHandler;
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
   RedoEditingHandler, FormatFileHandler, RegisterEventHandlersHandler, InitializeApplicationHandler,
   ShowLineNumbersHandler, EditorChangeActiveFileHandler, EditorOpenFileHandler, FileSavedHandler,
   EditorUpdateFileStateHandler, DeleteCurrentLineHandler, EditorGoToLineHandler, EditorSetFocusOnActiveFileHandler
{

   public interface Display
   {

      void openTab(File file, boolean lineNumbers, Editor editor, boolean fireEvent);

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
      
      void deleteCurrentLune(String path);
      
      void goToLine(String path, int lineNumber);
      
      TextEditor getEditor(String path);

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
            display.openTab(file, context.isShowLineNumbers(), editor, false);
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

      handlers.addHandler(EditorActiveFileChangedEvent.TYPE, this);

      handlers.addHandler(EditorCloseFileEvent.TYPE, this);

      handlers.addHandler(UndoEditingEvent.TYPE, this);
      handlers.addHandler(RedoEditingEvent.TYPE, this);

      handlers.addHandler(FileSavedEvent.TYPE, this);

      handlers.addHandler(FormatFileEvent.TYPE, this);

      handlers.addHandler(ShowLineNumbersEvent.TYPE, this);

      handlers.addHandler(EditorChangeActiveFileEvent.TYPE, this);
      
      handlers.addHandler(DeleteCurrentLineEvent.TYPE, this);
      
      handlers.addHandler(EditorGoToLineEvent.TYPE, this);
      
      handlers.addHandler(EditorSetFocusOnActiveFileEvent.TYPE, this);

   }

   /**
    * Initializing application handler
    * 
    */
   public void onInitializeApplication(InitializeApplicationEvent event)
   {
      new Timer()
      {
         @Override
         public void run()
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
      }.schedule(1000);

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
      CookieManager.getInstance().storeOpenedFiles(context);
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
      CookieManager.getInstance().storeOpenedFiles(context);
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
      Dialogs.getInstance().ask("Close file", message, new BooleanValueReceivedCallback()
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
      context.setActiveFile(event.getFile());
      display.selectTab(event.getFile().getHref());

      String href = context.getActiveFile().getHref();
//      eventBus.fireEvent(new EditorActiveFileChangedEvent(context.getActiveFile(), display.hasUndoChanges(href),
//         display.hasRedoChanges(href)));

      eventBus.fireEvent(new EditorActiveFileChangedEvent(context.getActiveFile(), display.getEditor(href)));
      CookieManager.getInstance().storeOpenedFiles(context);
   }

   private void setFileAsActive()
   {
      if (context.getActiveFile() == null)
      {
         return;
      }
      display.selectTab(context.getActiveFile().getHref());
      if (context.getActiveFile() == null)
      {
         return;
      }

      String href = context.getActiveFile().getHref();
//      eventBus.fireEvent(new EditorActiveFileChangedEvent(context.getActiveFile(), display.hasUndoChanges(href),
//         display.hasRedoChanges(href)));
      eventBus.fireEvent(new EditorActiveFileChangedEvent(context.getActiveFile(), display.getEditor(href)));
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
         CookieManager.getInstance().storeOpenedFiles(context);
         return;
      }

      ignoreContentChangedList.add(file.getHref());

      context.getOpenedFiles().put(file.getHref(), file);
      context.getOpenedEditors().put(file.getHref(), event.getEditor().getDescription());
      context.setActiveFile(file);

      display.openTab(file, context.isShowLineNumbers(), event.getEditor(), true);
      
      display.selectTab(file.getHref());

      CookieManager.getInstance().storeOpenedFiles(context);
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
         CookieManager.getInstance().storeOpenedFiles(context);
      }

   }

   public void onEditorUdateFileState(EditorUpdateFileStateEvent event)
   {
      display.updateTabTitle(event.getFile().getHref());
      CookieManager.getInstance().storeOpenedFiles(context);
   }

   /**
    * @see org.exoplatform.ideall.client.event.edit.DeleteCurrentLineHandler#onDeleteCurrentLine(org.exoplatform.ideall.client.event.edit.DeleteCurrentLineEvent)
    */
   public void onDeleteCurrentLine(DeleteCurrentLineEvent event)
   {
        display.deleteCurrentLune(context.getActiveFile().getHref());
   }

   /**
    * @see org.exoplatform.ideall.client.editor.event.EditorGoToLineHandler#onEditorGoToLine(org.exoplatform.ideall.client.editor.event.EditorGoToLineEvent)
    */
   public void onEditorGoToLine(EditorGoToLineEvent event)
   {
      display.goToLine(context.getActiveFile().getHref(), event.getLineNumber());
   }

   /**
    * @see org.exoplatform.ideall.client.editor.event.EditorSetFocusOnActiveFileHandler#onEditorSetFocuOnActiveFile(org.exoplatform.ideall.client.editor.event.EditorSetFocusOnActiveFileEvent)
    */
   public void onEditorSetFocuOnActiveFile(EditorSetFocusOnActiveFileEvent event)
   {
      display.setEditorFocus(context.getActiveFile().getHref());
   }

}
