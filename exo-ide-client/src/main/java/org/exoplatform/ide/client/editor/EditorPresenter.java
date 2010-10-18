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
package org.exoplatform.ide.client.editor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.commons.dialogs.Dialogs;
import org.exoplatform.gwtframework.commons.dialogs.callback.BooleanValueReceivedCallback;
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
import org.exoplatform.ide.client.Utils;
import org.exoplatform.ide.client.editor.event.EditorReplaceFileEvent;
import org.exoplatform.ide.client.editor.event.EditorReplaceFileHandler;
import org.exoplatform.ide.client.event.edit.EditorDeleteCurrentLineEvent;
import org.exoplatform.ide.client.event.edit.EditorDeleteCurrentLineHandler;
import org.exoplatform.ide.client.framework.application.event.InitializeApplicationEvent;
import org.exoplatform.ide.client.framework.application.event.InitializeApplicationHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorChangeActiveFileEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorChangeActiveFileHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorCloseFileEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorCloseFileHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileContentChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFindAndReplaceTextEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFindAndReplaceTextHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorFindTextEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFindTextHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorGoToLineEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorGoToLineHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorOpenFileEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorOpenFileHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorReplaceTextEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorReplaceTextHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorSetFocusEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorSetFocusHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorTextFoundEvent;
import org.exoplatform.ide.client.framework.event.FileSavedEvent;
import org.exoplatform.ide.client.framework.event.FileSavedHandler;
import org.exoplatform.ide.client.framework.event.SaveFileAsEvent;
import org.exoplatform.ide.client.framework.event.SaveFileEvent;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings.Store;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedEvent;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedHandler;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.client.framework.vfs.ItemProperty;
import org.exoplatform.ide.client.framework.vfs.Version;
import org.exoplatform.ide.client.hotkeys.event.RefreshHotKeysEvent;
import org.exoplatform.ide.client.hotkeys.event.RefreshHotKeysHandler;
import org.exoplatform.ide.client.model.ApplicationContext;
import org.exoplatform.ide.client.module.edit.event.FormatFileEvent;
import org.exoplatform.ide.client.module.edit.event.FormatFileHandler;
import org.exoplatform.ide.client.module.edit.event.RedoTypingEvent;
import org.exoplatform.ide.client.module.edit.event.RedoTypingHandler;
import org.exoplatform.ide.client.module.edit.event.ShowLineNumbersEvent;
import org.exoplatform.ide.client.module.edit.event.ShowLineNumbersHandler;
import org.exoplatform.ide.client.module.edit.event.UndoTypingEvent;
import org.exoplatform.ide.client.module.edit.event.UndoTypingHandler;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Timer;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class EditorPresenter implements EditorContentChangedHandler, EditorInitializedHandler, EditorActivityHandler,
   EditorSaveContentHandler, EditorActiveFileChangedHandler, EditorCloseFileHandler, UndoTypingHandler,
   RedoTypingHandler, FormatFileHandler, InitializeApplicationHandler, ShowLineNumbersHandler,
   EditorChangeActiveFileHandler, EditorOpenFileHandler, FileSavedHandler, EditorReplaceFileHandler,
   EditorDeleteCurrentLineHandler, EditorGoToLineHandler, EditorFindTextHandler, EditorReplaceTextHandler,
   EditorFindAndReplaceTextHandler, EditorSetFocusHandler, RefreshHotKeysHandler, ApplicationSettingsReceivedHandler
{

   public interface Display
   {

      void openTab(File file, boolean lineNumbers, Editor editor, boolean readOnly);

      void replaceFile(File oldFile, File newFile);

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

      void goToLine(String path, int lineNumber, int columnNumber);

      boolean findText(String findText, boolean caseSensitive, String path);

      boolean findReplaceText(String findText, String replace, boolean caseSensitive, String path);

      void replaceText(String findText, String replace, boolean caseSensitive, String path);

      void replaceAllText(String findText, String replace, boolean caseSensitive, String path);

      TextEditor getEditor(String path);

   }

   private HandlerManager eventBus;

   private Display display;

   private Handlers handlers;

   private ArrayList<String> ignoreContentChangedList = new ArrayList<String>();

   private boolean closeFileAfterSaving = false;

   private ApplicationSettings applicationSettings;

   private File activeFile;

   private Map<String, File> openedFiles = new LinkedHashMap<String, File>();

   private LinkedHashMap<String, String> openedEditors = new LinkedHashMap<String, String>();

   private Map<String, String> lockTokens;

   public EditorPresenter(HandlerManager eventBus, ApplicationContext context)
   {
      this.eventBus = eventBus;
      handlers = new Handlers(eventBus);
      handlers.addHandler(ApplicationSettingsReceivedEvent.TYPE, this);
      //handlers.addHandler(InitializeApplicationEvent.TYPE, this);

      handlers.addHandler(EditorOpenFileEvent.TYPE, this);

      handlers.addHandler(EditorReplaceFileEvent.TYPE, this);

      handlers.addHandler(RefreshHotKeysEvent.TYPE, this);

      handlers.addHandler(EditorFindTextEvent.TYPE, this);
      handlers.addHandler(EditorReplaceTextEvent.TYPE, this);
      handlers.addHandler(EditorFindAndReplaceTextEvent.TYPE, this);

      handlers.addHandler(EditorContentChangedEvent.TYPE, this);
      handlers.addHandler(EditorInitializedEvent.TYPE, this);
      handlers.addHandler(EditorActivityEvent.TYPE, this);
      handlers.addHandler(EditorSaveContentEvent.TYPE, this);
      handlers.addHandler(EditorActiveFileChangedEvent.TYPE, this);
      handlers.addHandler(EditorCloseFileEvent.TYPE, this);
      handlers.addHandler(UndoTypingEvent.TYPE, this);
      handlers.addHandler(RedoTypingEvent.TYPE, this);
      handlers.addHandler(FileSavedEvent.TYPE, this);
      handlers.addHandler(FormatFileEvent.TYPE, this);
      handlers.addHandler(ShowLineNumbersEvent.TYPE, this);
      handlers.addHandler(EditorChangeActiveFileEvent.TYPE, this);
      handlers.addHandler(EditorDeleteCurrentLineEvent.TYPE, this);
      handlers.addHandler(EditorGoToLineEvent.TYPE, this);
      handlers.addHandler(EditorSetFocusEvent.TYPE, this);
   }

   public void bindDisplay(Display d)
   {
      display = d;
   }

   public void onApplicationSettingsReceived(ApplicationSettingsReceivedEvent event)
   {
      applicationSettings = event.getApplicationSettings();

      if (applicationSettings.getValueAsMap("lock-tokens") == null)
      {
         applicationSettings.setValue("lock-tokens", new LinkedHashMap<String, String>(), Store.COOKIES);
      }
      lockTokens = applicationSettings.getValueAsMap("lock-tokens");

      if (applicationSettings.getValueAsMap("default-editors") == null)
      {
         applicationSettings.setValue("default-editors", new LinkedHashMap<String, String>(), Store.REGISTRY);
      }
   }

   /**
    * Initializing application handler
    * 
    */
   public void onInitializeApplication(InitializeApplicationEvent event)
   {
      this.openedFiles = event.getOpenedFiles();

      final File fileToSetAsActive = event.getActiveFile() == null ? null : openedFiles.get(event.getActiveFile());
      if (event.getActiveFile() != null)
      {
         activeFile = openedFiles.get(event.getActiveFile());
      }

      Map<String, String> defaultEditors = applicationSettings.getValueAsMap("default-editors");
      if (defaultEditors == null)
      {
         defaultEditors = new LinkedHashMap<String, String>();
      }

      for (File file : openedFiles.values())
      {
         ignoreContentChangedList.add(file.getHref());
         try
         {
            String editorDescription = defaultEditors.get(file.getContentType());
            Editor editor = EditorUtil.getEditor(file.getContentType(), editorDescription);
            openedEditors.put(file.getHref(), editor.getDescription());

            boolean lineNumbers = true;
            if (applicationSettings.getValueAsBoolean("line-numbers") != null)
            {
               lineNumbers = applicationSettings.getValueAsBoolean("line-numbers");
            }

            display.openTab(file, lineNumbers, editor, isReadOnly(file));
            eventBus.fireEvent(new EditorFileOpenedEvent(file, openedFiles));
         }
         catch (EditorNotFoundException e)
         {
            e.printStackTrace();
         }
      }

      new Timer()
      {
         @Override
         public void run()
         {
            if (fileToSetAsActive != null)
            {
               try
               {
                  selectFile(fileToSetAsActive);
               }
               catch (Exception exc)
               {
                  exc.printStackTrace();
               }
            }
            else
            {
               if (openedFiles.size() > 0)
               {
                  String fileName = (String)openedFiles.keySet().toArray()[0];
                  File file = openedFiles.get(fileName);

                  TextEditor textEditor = display.getEditor(file.getHref());
                  eventBus.fireEvent(new EditorActiveFileChangedEvent(activeFile, textEditor));
                  selectFile(activeFile);
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
         final File file = openedFiles.get(path);
         if (file == null) return;
         display.setTabContent(file.getHref(), file.getContent());

         new Timer()
         {
            @Override
            public void run()
            {
               eventBus.fireEvent(new EditorFileOpenedEvent(file, openedFiles));
            }

         }.schedule(200);
         //eventBus.fireEvent(new EditorFileOpenedEvent(file, openedFiles));
         //eventBus.fireEvent(new EditorActiveFileChangedEvent(file, display.getEditor(file.getHref())));         
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

      File file = openedFiles.get(path);
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
         activeFile = null;
         //eventBus.fireEvent(new EditorActiveFileChangedEvent(null, null));
         //         context.setActiveFile(null);
         //         context.setActiveTextEditor(null);
         return;
      }

      activeFile = curentFile;
      //      TextEditor textEditor = display.getEditor(curentFile.getHref());
      //eventBus.fireEvent(new EditorActiveFileChangedEvent());
      //event.

      //      context.setActiveFile(curentFile);
      //      context.setActiveTextEditor(display.getEditor(curentFile.getHref()));
      //      CookieManager.getInstance().storeOpenedFiles(context);

      //TODO

      display.setEditorFocus(curentFile.getHref());
   }

   public void onEditorSaveContent(EditorSaveContentEvent event)
   {
      File file = activeFile;
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

      openedFiles.remove(file.getHref());

      try
      {
         eventBus.fireEvent(new EditorFileClosedEvent(file, openedFiles));
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   public void onEditorCloseFile(EditorCloseFileEvent event)
   {
      if (event.isForceClosing())
      {
         closeFile(event.getFile());
         return;
      }
      final File file = event.getFile();
      
      if(file.getProperty(ItemProperty.JCR_LOCKOWNER)!= null)
      {
         if(!lockTokens.containsKey(file.getHref()))
         {
            closeFile(file);
            return;
         }
      }
      
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

            if (value)
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

   public void onUndoTypig(UndoTypingEvent event)
   {
      display.undoEditing(activeFile.getHref());
   }

   public void onRedoTyping(RedoTypingEvent event)
   {
      display.redoEditing(activeFile.getHref());
   }

   private void updateTabTitle(String href)
   {
      display.updateTabTitle(href);
   }

   public void onFormatFile(FormatFileEvent event)
   {
      display.formatFile(activeFile.getHref());
   }

   private void updateLineNumbers(boolean lineNumbers)
   {
      Iterator<String> iterator = openedFiles.keySet().iterator();
      while (iterator.hasNext())
      {
         String path = iterator.next();
         display.setLineNumbers(path, lineNumbers);
      }
      display.setEditorFocus(activeFile.getHref());
   }

   public void onShowLineNumbers(ShowLineNumbersEvent event)
   {
      updateLineNumbers(event.isShowLineNumber());
   }

   public void onEditorChangeActiveFile(EditorChangeActiveFileEvent event)
   {
      activeFile = event.getFile();
      TextEditor textEditor = display.getEditor(event.getFile().getHref());

      display.selectTab(event.getFile().getHref());

      //      context.setActiveFile(event.getFile());
      //      context.setActiveTextEditor(display.getEditor(event.getFile().getHref()));

      //String href = context.getActiveFile().getHref();
      eventBus.fireEvent(new EditorActiveFileChangedEvent(activeFile, textEditor));
      //CookieManager.getInstance().storeOpenedFiles(context);
   }

   /**
    * Select file
    */
   private void selectFile(File file)
   {
      if (file == null)
      {
         return;
      }
      display.selectTab(file.getHref());

      activeFile = file;

      String href = file.getHref();
      eventBus.fireEvent(new EditorActiveFileChangedEvent(file, display.getEditor(href)));
   }

   public void onEditorOpenFile(EditorOpenFileEvent event)
   {
      File file = event.getFile();

      if (file == null)
      {
         return;
      }

      if (openedFiles.get(file.getHref()) != null
         && event.getEditor().getDescription().equals(openedEditors.get(file.getHref())))
      {
         File openedFile = openedFiles.get(file.getHref());
         ignoreContentChangedList.add(file.getHref());
         display.selectTab(openedFile.getHref());
         display.setTabContent(file.getHref(), file.getContent());
         return;
      }

      ignoreContentChangedList.add(file.getHref());
      openedFiles.put(file.getHref(), file);
      openedEditors.put(file.getHref(), event.getEditor().getDescription());

      boolean lineNumbers = true;
      if (applicationSettings.getValueAsBoolean("line-numbers") != null)
      {
         lineNumbers = applicationSettings.getValueAsBoolean("line-numbers");
      }

      try
      {
         display.openTab(file, lineNumbers, event.getEditor(), isReadOnly(file));
         display.selectTab(file.getHref());
      }
      catch (Throwable e)
      {
         e.printStackTrace();
      }

      //      eventBus.fireEvent(new EditorFileOpenedEvent(file, openedFiles));
      //      eventBus.fireEvent(new EditorActiveFileChangedEvent(file, display.getEditor(file.getHref())));
   }

   /**
    * Check is file locked
    * 
    * @param file
    * @return true if file is locked and client not have lock token, else return false
    */
   private boolean isReadOnly(File file)
   {
      if (file instanceof Version)
      {
         return true;
      }

      if (file.getProperty(ItemProperty.JCR_LOCKOWNER) != null)
      {
         return !(lockTokens.containsKey(file.getHref()));
      }
      else
         return false;
      //      return !(file.getProperty(ItemProperty.JCR_LOCKOWNER) != null && !lockTokens.containsKey(file.getHref()));
   }

   public void onFileSaved(FileSavedEvent event)
   {
      if (closeFileAfterSaving)
      {
         File file = event.getFile();
         if (event.getSourceHref() != null)
         {
            file.setHref(event.getSourceHref());
         }
         closeFile(file);
         closeFileAfterSaving = false;
      }
      else
      {
         File currentOpenedFile = activeFile;
         File savedFile = event.getFile();
         if (event.getSourceHref() != null)
         {
            openedFiles.remove(event.getSourceHref());
            activeFile = savedFile;

            openedFiles.put(savedFile.getHref(), savedFile);
            replaceFile(currentOpenedFile, savedFile);
         }

         updateTabTitle(savedFile.getHref());
      }
   }

   public void onEditorReplaceFile(EditorReplaceFileEvent event)
   {
      replaceFile(event.getFile(), event.getNewFile());
   }

   private void replaceFile(File oldFile, File newFile)
   {
      if (newFile == null)
      {
         display.updateTabTitle(oldFile.getHref());
         return;
      }
      ignoreContentChangedList.add(newFile.getHref());
      display.replaceFile(oldFile, newFile);

      display.updateTabTitle(newFile.getHref());
   }

   /**
    * @see org.exoplatform.ide.client.event.edit.EditorDeleteCurrentLineHandler#onEditorDeleteCurrentLine(org.exoplatform.ide.client.event.edit.EditorDeleteCurrentLineEvent)
    */
   public void onEditorDeleteCurrentLine(EditorDeleteCurrentLineEvent event)
   {
      display.deleteCurrentLune(activeFile.getHref());
   }

   /**
    * @see org.exoplatform.ide.client.editor.event.EditorGoToLineHandler#onEditorGoToLine(org.exoplatform.ide.client.editor.event.EditorGoToLineEvent)
    */
   public void onEditorGoToLine(EditorGoToLineEvent event)
   {
      display.goToLine(activeFile.getHref(), event.getLineNumber(), event.getColumnNumber());
   }

   /**
    * @see org.exoplatform.ide.client.editor.event.EditorFindTextHandler#onEditorFindText(org.exoplatform.ide.client.editor.event.EditorFindTextEvent)
    */
   public void onEditorFindText(EditorFindTextEvent event)
   {
      boolean isFound = display.findText(event.getFindText(), event.isCaseSensitive(), event.getPath());
      eventBus.fireEvent(new EditorTextFoundEvent(isFound));
   }

   /**
    * @see org.exoplatform.ide.client.editor.event.EditorReplaceTextHandler#onEditorReplaceText(org.exoplatform.ide.client.editor.event.EditorReplaceTextEvent)
    */
   public void onEditorReplaceText(EditorReplaceTextEvent event)
   {
      if (event.isReplaceAll())
      {
         display.replaceAllText(event.getFindText(), event.getReplaceText(), event.isCaseSensitive(), event.getPath());
      }
      else
      {
         display.replaceText(event.getFindText(), event.getReplaceText(), event.isCaseSensitive(), event.getPath());
      }
   }

   /**
    * @see org.exoplatform.ide.client.editor.event.EditorFindReplaceTextHandler#onEditorFindAndReplaceText(org.exoplatform.ide.client.editor.event.EditorFindReplaceTextEvent)
    */
   public void onEditorFindAndReplaceText(EditorFindAndReplaceTextEvent event)
   {
      boolean isFound =
         display.findReplaceText(event.getFindText(), event.getReplaceText(), event.isCaseSensitive(), event.getPath());
      eventBus.fireEvent(new EditorTextFoundEvent(isFound));
   }

   /**
    * @see org.exoplatform.ide.client.editor.event.EditorSetFocusHandler#onEditorSetFocus(org.exoplatform.ide.client.editor.event.EditorSetFocusEvent)
    */
   public void onEditorSetFocus(EditorSetFocusEvent event)
   {
      display.setEditorFocus(activeFile.getHref());
   }

   public void onRefreshHotKeys(RefreshHotKeysEvent event)
   {
      Map<String, String> hotKeys = applicationSettings.getValueAsMap("hotkeys");
      List<String> hotKeyList = new ArrayList<String>(hotKeys.keySet());
      Iterator<String> it = openedFiles.keySet().iterator();
      while (it.hasNext())
      {
         String file = it.next();
         display.getEditor(file).setHotKeyList(hotKeyList);
      }
   }

}
