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
package org.exoplatform.ide.client.editor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.commons.dialogs.BooleanValueReceivedHandler;
import org.exoplatform.gwtframework.commons.dialogs.Dialogs;
import org.exoplatform.ide.client.Images;
import org.exoplatform.ide.client.Utils;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorChangeActiveFileEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorChangeActiveFileHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorCloseFileEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorCloseFileHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorDeleteCurrentLineEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorDeleteCurrentLineHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileContentChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFindAndReplaceTextEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFindAndReplaceTextHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorFindTextEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFindTextHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorFormatTextEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFormatTextHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorGoToLineEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorGoToLineHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorOpenFileEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorOpenFileHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorRedoTypingEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorRedoTypingHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorReplaceFileEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorReplaceFileHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorReplaceTextEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorReplaceTextHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorSetFocusEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorSetFocusHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorTextFoundEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorUndoTypingEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorUndoTypingHandler;
import org.exoplatform.ide.client.framework.event.FileSavedEvent;
import org.exoplatform.ide.client.framework.event.FileSavedHandler;
import org.exoplatform.ide.client.framework.event.SaveFileAsEvent;
import org.exoplatform.ide.client.framework.event.SaveFileAsHandler;
import org.exoplatform.ide.client.framework.event.SaveFileEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings.Store;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedEvent;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedHandler;
import org.exoplatform.ide.client.framework.ui.event.SelectViewEvent;
import org.exoplatform.ide.client.framework.ui.gwt.ClosingViewEvent;
import org.exoplatform.ide.client.framework.ui.gwt.ClosingViewHandler;
import org.exoplatform.ide.client.framework.ui.gwt.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.gwt.ViewClosedHandler;
import org.exoplatform.ide.client.framework.ui.gwt.ViewVisibilityChangedEvent;
import org.exoplatform.ide.client.framework.ui.gwt.ViewVisibilityChangedHandler;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.client.framework.vfs.ItemProperty;
import org.exoplatform.ide.client.framework.vfs.Version;
import org.exoplatform.ide.client.hotkeys.event.RefreshHotKeysEvent;
import org.exoplatform.ide.client.hotkeys.event.RefreshHotKeysHandler;
import org.exoplatform.ide.client.module.edit.event.ShowLineNumbersEvent;
import org.exoplatform.ide.client.module.edit.event.ShowLineNumbersHandler;
import org.exoplatform.ide.editor.api.Editor;
import org.exoplatform.ide.editor.api.EditorParameters;
import org.exoplatform.ide.editor.api.EditorProducer;
import org.exoplatform.ide.editor.api.event.EditorContentChangedEvent;
import org.exoplatform.ide.editor.api.event.EditorContentChangedHandler;
import org.exoplatform.ide.editor.api.event.EditorCursorActivityEvent;
import org.exoplatform.ide.editor.api.event.EditorCursorActivityHandler;
import org.exoplatform.ide.editor.api.event.EditorSaveContentEvent;
import org.exoplatform.ide.editor.api.event.EditorSaveContentHandler;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Image;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: EditorController Mar 21, 2011 5:22:10 PM evgen $
 *
 */
public class EditorController implements EditorContentChangedHandler, EditorCursorActivityHandler,
   EditorSaveContentHandler, EditorActiveFileChangedHandler, EditorCloseFileHandler, EditorUndoTypingHandler,
   EditorRedoTypingHandler, EditorFormatTextHandler, ShowLineNumbersHandler, EditorChangeActiveFileHandler,
   EditorOpenFileHandler, FileSavedHandler, EditorReplaceFileHandler, EditorDeleteCurrentLineHandler,
   EditorGoToLineHandler, EditorFindTextHandler, EditorReplaceTextHandler, EditorFindAndReplaceTextHandler,
   EditorSetFocusHandler, RefreshHotKeysHandler, ApplicationSettingsReceivedHandler, SaveFileAsHandler,
   ViewVisibilityChangedHandler, ViewClosedHandler, ClosingViewHandler
{

   private HandlerManager eventBus;

   private Handlers handlers;

   private ArrayList<String> ignoreContentChangedList = new ArrayList<String>();

   private boolean closeFileAfterSaving = false;

   private ApplicationSettings applicationSettings;

   private File activeFile;

   private Map<String, File> openedFiles = new LinkedHashMap<String, File>();

   private LinkedHashMap<String, String> openedEditors = new LinkedHashMap<String, String>();

   private Map<String, String> lockTokens;

   private Map<String, EditorView> editorsViews = new HashMap<String, EditorView>();

   private Map<String, Editor> editors = new HashMap<String, Editor>();
   
   private boolean waitForEditorInitialized = false;

   public EditorController()
   {
      this.eventBus = IDE.EVENT_BUS;
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
      handlers.addHandler(EditorCursorActivityEvent.TYPE, this);
      handlers.addHandler(EditorSaveContentEvent.TYPE, this);
      handlers.addHandler(EditorActiveFileChangedEvent.TYPE, this);
      handlers.addHandler(EditorCloseFileEvent.TYPE, this);
      handlers.addHandler(EditorUndoTypingEvent.TYPE, this);
      handlers.addHandler(EditorRedoTypingEvent.TYPE, this);
      handlers.addHandler(FileSavedEvent.TYPE, this);
      handlers.addHandler(EditorFormatTextEvent.TYPE, this);
      handlers.addHandler(ShowLineNumbersEvent.TYPE, this);
      handlers.addHandler(EditorChangeActiveFileEvent.TYPE, this);
      handlers.addHandler(EditorDeleteCurrentLineEvent.TYPE, this);
      handlers.addHandler(EditorGoToLineEvent.TYPE, this);
      handlers.addHandler(EditorSetFocusEvent.TYPE, this);
      handlers.addHandler(SaveFileAsEvent.TYPE, this);
      //      handlers.addHandler(ViewClosedEvent.TYPE, this);
      handlers.addHandler(ClosingViewEvent.TYPE, this);

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
    * Destroy 
    */
   public void destroy()
   {
      handlers.removeHandlers();
   }

   ///*
   // *  Fired when editor is initialized
   // */
   //public void onEditorInitialized(EditorInitializedEvent event)
   //{
   //   try
   //   {
   //      String editorId = event.getEditorId();
   //      String path = display.getPathByEditorId(editorId);
   //      final File file = openedFiles.get(path);
   //      if (file == null)
   //         return;
   //      display.setTabContent(file.getHref(), file.getContent());
   //
   //      new Timer()
   //      {
   //         @Override
   //         public void run()
   //         {
   //            eventBus.fireEvent(new EditorFileOpenedEvent(file, openedFiles));
   //         }
   //
   //      }.schedule(200);
   //      //eventBus.fireEvent(new EditorFileOpenedEvent(file, openedFiles));
   //      //eventBus.fireEvent(new EditorActiveFileChangedEvent(file, display.getEditor(file.getHref())));         
   //   }
   //   catch (Exception exc)
   //   {
   //      exc.printStackTrace();
   //   }
   //}

   /* 
    * Editor content changed handler
    */
   public void onEditorContentChanged(EditorContentChangedEvent event)
   {
      String editorId = event.getEditorId();
      String path = editorsViews.get(editorId).getFileHref();
      if (path == null)
      {
         return;
      }

      if (ignoreContentChangedList.contains(path))
      {
         ignoreContentChangedList.remove(path);
         return;
      }

      File file = openedFiles.get(path);
      file.setContentChanged(true);

      Editor editor = editors.get(file.getHref());

      file.setContent(editor.getText());
      updateTabTitle(file);
      eventBus.fireEvent(new EditorFileContentChangedEvent(file, editor.hasUndoChanges(), editor.hasRedoChanges()));
   }

   public void onEditorCursorActivity(EditorCursorActivityEvent event)
   {
      eventBus.fireEvent(new EditorSetFocusEvent());
   }

   /* (non-Javadoc)
    * @see org.exoplatform.gadgets.devtool.client.editor.event.EditorActiveFileChangedHandler#onEditorChangedActiveFile(org.exoplatform.gadgets.devtool.client.editor.event.EditorActiveFileChangedEvent)
    */
   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      closeFileAfterSaving = false;
      File curentFile = event.getFile();
      if (curentFile == null)
      {
         activeFile = null;
         return;
      }

      activeFile = curentFile;
      Editor editor = editors.get(curentFile.getHref());
      editor.setFocus();
   }

   public void onEditorSaveContent(EditorSaveContentEvent event)
   {
      File file = activeFile;
      file.setContent(editors.get(file.getHref()).getText());
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
      Editor editor = editors.get(file.getHref());
      editors.remove(file.getHref());

      IDE.getInstance().closeView(editorsViews.get(editor.getEditorId()).getId());
      editorsViews.remove(editor.getEditorId());
      //   display.closeTab(file.getHref());

      file.setContent(null);
      file.setContentChanged(false);

      openedFiles.remove(file.getHref());
      if (ignoreContentChangedList.contains(file.getHref()))
      {
         ignoreContentChangedList.remove(file.getHref());
      }

      try
      {
         eventBus.fireEvent(new EditorFileClosedEvent(file, openedFiles));
         if (editors.isEmpty())
            eventBus.fireEvent(new EditorActiveFileChangedEvent(null, null));
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
         closeFileAfterSaving = false;
         closeFile(event.getFile());
         return;
      }
      final File file = event.getFile();

      if (file.getProperty(ItemProperty.LOCKDISCOVERY) != null)
      {
         if (!lockTokens.containsKey(file.getHref()))
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

      if (file.isNewFile())
      {
         closeFileAfterSaving = true;
         eventBus.fireEvent(new SaveFileAsEvent(file, SaveFileAsEvent.SaveDialogType.EXTENDED,
            new EditorCloseFileEvent(file, true), null));
      }
      else
      {
         closeFileAfterSaving = true;
         String message = "Do you want to save <b>" + Utils.unescape(file.getName()) + "</b> before closing?<br>&nbsp;";
         Dialogs.getInstance().ask("Close file", message, new BooleanValueReceivedHandler()
         {
            public void booleanValueReceived(Boolean value)
            {
               if (value == null)
               {
                  return;
               }

               if (value)
               {
                  file.setContent(editors.get(file.getHref()).getText());
                  eventBus.fireEvent(new SaveFileEvent());
               }
               else
               {
                  closeFileAfterSaving = false;
                  closeFile(file);
               }
            }
         });
      }
   }

   public void onEditorUndoTyping(EditorUndoTypingEvent event)
   {
      editors.get(activeFile.getHref()).undo();
   }

   public void onEditorRedoTyping(EditorRedoTypingEvent event)
   {
      editors.get(activeFile.getHref()).redo();

   }

   private void updateTabTitle(File file)
   {
      Editor editor = editors.get(file.getHref());

      editorsViews.get(editor.getEditorId()).setTitle(getFileTitle(file));

   }

   private String getFileTitle(File file)
   {
      boolean fileChanged = file.isContentChanged() || file.isPropertiesChanged();

      String fileName = Utils.unescape(fileChanged ? file.getName() + "&nbsp;*" : file.getName());

      String mainHint = file.getHref();

      String readonlyImage =
         (isReadOnly(file))
            ? "<img id=\"fileReadonly\"  style=\"margin-left:-4px; margin-bottom: -4px;\" border=\"0\" suppress=\"true\" src=\""
               + Images.Editor.READONLY_FILE + "\" />" : "";

      mainHint = (isReadOnly(file)) ? "File opened in read only mode. Use SaveAs command." : mainHint;
      String title = "<span title=\"" + mainHint + "\">" + readonlyImage + "&nbsp;" + fileName + "</span>";

      return title;
   }

   public void onFormatFile(EditorFormatTextEvent event)
   {
      editors.get(activeFile.getHref()).formatSource();
   }

   private void updateLineNumbers(boolean lineNumbers)
   {
      Iterator<String> iterator = openedFiles.keySet().iterator();
      while (iterator.hasNext())
      {
         String path = iterator.next();
         editors.get(path).showLineNumbers(lineNumbers);
      }
      editors.get(activeFile.getHref()).setFocus();
   }

   public void onShowLineNumbers(ShowLineNumbersEvent event)
   {

      updateLineNumbers(event.isShowLineNumber());
   }

   public void onEditorChangeActiveFile(EditorChangeActiveFileEvent event)
   {
      activeFile = event.getFile();
      Editor editor = editors.get(activeFile.getHref());
      //   TextEditor textEditor = display.getEditor(event.getFile().getHref());

      //   display.selectTab(event.getFile().getHref());

      eventBus.fireEvent(new SelectViewEvent(editorsViews.get(editor.getEditorId()).getId()));

      //      context.setActiveFile(event.getFile());
      //      context.setActiveTextEditor(display.getEditor(event.getFile().getHref()));

      //String href = context.getActiveFile().getHref();
      //   eventBus.fireEvent(new EditorActiveFileChangedEvent(activeFile, textEditor));
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
      //   display.selectTab(file.getHref());
      Editor editor = editors.get(file.getHref());
      eventBus.fireEvent(new SelectViewEvent(editorsViews.get(editor.getEditorId()).getId()));

      activeFile = file;

      //   String href = file.getHref();
      eventBus.fireEvent(new EditorActiveFileChangedEvent(file, editor));
   }

   public void onEditorOpenFile(EditorOpenFileEvent event)
   {
      File file = event.getFile();

      if (file == null)
      {
         return;
      }

      if (openedFiles.get(file.getHref()) != null
         && event.getEditorProducer().getDescription().equals(openedEditors.get(file.getHref())))
      {
         File openedFile = openedFiles.get(file.getHref());
         ignoreContentChangedList.add(file.getHref());

         //      display.selectTab(openedFile.getHref());
         Editor editor = editors.get(file.getHref());
         EditorView view = editorsViews.get(editor.getEditorId());
         view.setContent(file);
         view.setVisible(true);
         //      display.setTabContent(file.getHref(), file.getContent());
         return;
      }

      ignoreContentChangedList.add(file.getHref());
      openedFiles.put(file.getHref(), file);
      openedEditors.put(file.getHref(), event.getEditorProducer().getDescription());

      boolean lineNumbers = true;
      if (applicationSettings.getValueAsBoolean("line-numbers") != null)
      {
         lineNumbers = applicationSettings.getValueAsBoolean("line-numbers");
      }

      try
      {
         List<String> hotKeyList = new ArrayList<String>((applicationSettings.getValueAsMap("hotkeys")).keySet());
         HashMap<String, Object> params = new HashMap<String, Object>();

         params.put(EditorParameters.IS_READ_ONLY, isReadOnly(file));
         params.put(EditorParameters.IS_SHOW_LINE_NUMER, lineNumbers);
         params.put(EditorParameters.HOT_KEY_LIST, hotKeyList);
         EditorProducer producer = event.getEditorProducer();
         Editor editor = producer.createEditor(file.getContent(), eventBus, params);
         if (editors.containsKey(file.getHref()))
         {
            Editor oldEditor = editors.get(file.getHref());
            file.setContent(oldEditor.getText());
            EditorView editorView = editorsViews.get(oldEditor.getEditorId());
            editorView.remove(oldEditor);
            editorView.add(editor);
            editors.put(file.getHref(), editor);
            editorsViews.put(editor.getEditorId(), editorView);

         }
         else
         {
            editors.put(file.getHref(), editor);

            EditorView view = new EditorView(editor, file, getFileTitle(file));
            editorsViews.put(editor.getEditorId(), view);
            waitForEditorInitialized = true;
            IDE.getInstance().openView(view);
         }
         //      display.openTab(file, lineNumbers, event.getEditor(), isReadOnly(file));
         //      display.selectTab(file.getHref());
      }
      catch (Throwable e)
      {
         e.printStackTrace();
      }

      eventBus.fireEvent(new EditorFileOpenedEvent(file, openedFiles));
      //         eventBus.fireEvent(new EditorActiveFileChangedEvent(file,editors.get(file.getHref())));
   }

   /**
    * Check is file locked
    * 
    * @param file
    * @return true if file is locked and client not have lock token, else return false
    */
   private boolean isReadOnly(File file)
   {
      if (file instanceof Version || file.isSystem())
      {
         return true;
      }
      else if (file.getProperty(ItemProperty.LOCKDISCOVERY) != null)
      {
         return !(lockTokens.containsKey(file.getHref()));
      }
      else
         return false;
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
//TODO 
            openedFiles.put(savedFile.getHref(), savedFile);
            replaceFile(currentOpenedFile, savedFile);
         }

         updateTabTitle(savedFile);
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
         updateTabTitle(oldFile);
         return;
      }
      if (ignoreContentChangedList.contains(oldFile.getHref()))
      {
         ignoreContentChangedList.remove(oldFile.getHref());
         ignoreContentChangedList.add(newFile.getHref());
      }
      //TODO 
      
      String editorId = editors.get(oldFile.getHref()).getEditorId();
      editors.remove(oldFile.getHref());
      
      EditorView editorView = editorsViews.get(editorId);
      Editor editor = editorView.getEditor();
      
      
      editors.put(newFile.getHref(), editor);
      editorsViews.put(editor.getEditorId(), editorView);
      editorView.setIcon(new Image(newFile.getIcon()));
      
      boolean contentChanged = newFile.isContentChanged();
      editorView.setContent(newFile);
      
      //to avoid change content
      newFile.setContentChanged(contentChanged);
      
      updateTabTitle(newFile);
      //   display.replaceFile(oldFile, newFile);

      //   display.updateTabTitle(newFile.getHref());
      eventBus.fireEvent(new EditorActiveFileChangedEvent(newFile, editorView.getEditor()));
   }

   /**
    * @see org.exoplatform.ide.client.event.edit.EditorDeleteCurrentLineHandler#onEditorDeleteCurrentLine(org.exoplatform.ide.client.event.edit.EditorDeleteCurrentLineEvent)
    */
   public void onEditorDeleteCurrentLine(EditorDeleteCurrentLineEvent event)
   {
      editors.get(activeFile.getHref()).deleteCurrentLine();
   }

   /**
    * @see org.exoplatform.ide.client.editor.event.EditorGoToLineHandler#onEditorGoToLine(org.exoplatform.ide.client.editor.event.EditorGoToLineEvent)
    */
   public void onEditorGoToLine(EditorGoToLineEvent event)
   {
      editors.get(activeFile.getHref()).goToPosition(event.getLineNumber(), event.getColumnNumber());
   }

   /**
    * @see org.exoplatform.ide.client.editor.event.EditorFindTextHandler#onEditorFindText(org.exoplatform.ide.client.editor.event.EditorFindTextEvent)
    */
   public void onEditorFindText(EditorFindTextEvent event)
   {
      boolean isFound = editors.get(event.getPath()).findAndSelect(event.getFindText(), event.isCaseSensitive());
      eventBus.fireEvent(new EditorTextFoundEvent(isFound));
   }

   /**
    * @see org.exoplatform.ide.client.editor.event.EditorReplaceTextHandler#onEditorReplaceText(org.exoplatform.ide.client.editor.event.EditorReplaceTextEvent)
    */
   public void onEditorReplaceText(EditorReplaceTextEvent event)
   {
      Editor editor = editors.get(event.getPath());
      if (event.isReplaceAll())
      {
         while (editor.findAndSelect(event.getFindText(), event.isCaseSensitive()))
         {

            editor.replaceFoundedText(event.getFindText(), event.getReplaceText(), event.isCaseSensitive());
         }
         //      display.replaceAllText(event.getFindText(), event.getReplaceText(), event.isCaseSensitive(), event.getPath());
      }
      else
      {
         editor.replaceFoundedText(event.getFindText(), event.getReplaceText(), event.isCaseSensitive());
         //      display.replaceText(event.getFindText(), event.getReplaceText(), event.isCaseSensitive(), event.getPath());
      }
   }

   /**
    * @see org.exoplatform.ide.client.editor.event.EditorFindReplaceTextHandler#onEditorFindAndReplaceText(org.exoplatform.ide.client.editor.event.EditorFindReplaceTextEvent)
    */
   public void onEditorFindAndReplaceText(EditorFindAndReplaceTextEvent event)
   {
      Editor editor = editors.get(event.getPath());
      boolean isFound = editor.findAndSelect(event.getFindText(), event.isCaseSensitive());
      if (isFound)
         editor.replaceFoundedText(event.getFindText(), event.getReplaceText(), event.isCaseSensitive());

      eventBus.fireEvent(new EditorTextFoundEvent(isFound));
   }

   /**
    * @see org.exoplatform.ide.client.editor.event.EditorSetFocusHandler#onEditorSetFocus(org.exoplatform.ide.client.editor.event.EditorSetFocusEvent)
    */
   public void onEditorSetFocus(EditorSetFocusEvent event)
   {
      editors.get(activeFile.getHref()).setFocus();
   }

   public void onRefreshHotKeys(RefreshHotKeysEvent event)
   {
      Map<String, String> hotKeys = applicationSettings.getValueAsMap("hotkeys");
      List<String> hotKeyList = new ArrayList<String>(hotKeys.keySet());
      Iterator<String> it = openedFiles.keySet().iterator();
      while (it.hasNext())
      {
         String file = it.next();
         editors.get(file).setHotKeyList(hotKeyList);
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.event.SaveFileAsHandler#onSaveFileAs(org.exoplatform.ide.client.framework.event.SaveFileAsEvent)
    */
   public void onSaveFileAs(SaveFileAsEvent event)
   {
      if (event.getDialogType().equals(SaveFileAsEvent.SaveDialogType.YES_CANCEL))
      {
         closeFileAfterSaving = false;
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.ui.gwt.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.gwt.ViewClosedEvent)
    */
   @Override
   public void onViewClosed(ViewClosedEvent event)
   {
      if (event.getView().getType().equals("editor"))
      {
         eventBus.fireEvent(new EditorCloseFileEvent(((EditorView)event.getView()).getFile()));
      }

   }

   /**
    * @see org.exoplatform.ide.client.framework.ui.gwt.ViewVisibilityChangedHandler#onViewVisibilityChanged(org.exoplatform.ide.client.framework.ui.gwt.ViewVisibilityChangedEvent)
    */
   @Override
   public void onViewVisibilityChanged(final ViewVisibilityChangedEvent event)
   {
      if (event.getView().getType().equals("editor"))
      {
         final EditorView editorView = (EditorView)event.getView();
         if (editorView.isVisible())
         {
            activeFile = editorView.getFile();
            Timer timer = new Timer()
            {

               @Override
               public void run()
               {
                  eventBus.fireEvent(new EditorActiveFileChangedEvent(activeFile, editorView.getEditor()));
               }
            };
            if(waitForEditorInitialized)
            {
               waitForEditorInitialized = false;
               timer.schedule(500);
            }
            else
            {
               timer.run();
            }

         }
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.ui.gwt.ClosingViewHandler#onClosingView(org.exoplatform.ide.client.framework.ui.gwt.ClosingViewEvent)
    */
   @Override
   public void onClosingView(ClosingViewEvent event)
   {
      if (event.getView().getType().equals("editor"))
      {
         event.cancelClosing();
         eventBus.fireEvent(new EditorCloseFileEvent(((EditorView)event.getView()).getFile()));
      }

   }

}
