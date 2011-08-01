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

import org.exoplatform.gwtframework.ui.client.dialog.BooleanValueReceivedHandler;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.Utils;
import org.exoplatform.ide.client.edit.event.ShowLineNumbersEvent;
import org.exoplatform.ide.client.edit.event.ShowLineNumbersHandler;
import org.exoplatform.ide.client.framework.editor.EditorNotFoundException;
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
import org.exoplatform.ide.client.framework.editor.event.EditorReplaceAndFindTextEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorReplaceAndFindTextHandler;
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
import org.exoplatform.ide.client.framework.ui.api.View;
import org.exoplatform.ide.client.framework.ui.api.event.ClosingViewEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ClosingViewHandler;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.ui.api.event.ViewVisibilityChangedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewVisibilityChangedHandler;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.client.framework.vfs.ItemProperty;
import org.exoplatform.ide.client.framework.vfs.Version;
import org.exoplatform.ide.client.hotkeys.event.RefreshHotKeysEvent;
import org.exoplatform.ide.client.hotkeys.event.RefreshHotKeysHandler;
import org.exoplatform.ide.client.versioning.event.VersionRestoredEvent;
import org.exoplatform.ide.client.versioning.event.VersionRestoredHandler;
import org.exoplatform.ide.editor.api.Editor;
import org.exoplatform.ide.editor.api.EditorParameters;
import org.exoplatform.ide.editor.api.EditorProducer;
import org.exoplatform.ide.editor.api.event.EditorContentChangedEvent;
import org.exoplatform.ide.editor.api.event.EditorContentChangedHandler;
import org.exoplatform.ide.editor.api.event.EditorFocusReceivedEvent;
import org.exoplatform.ide.editor.api.event.EditorFocusReceivedHandler;
import org.exoplatform.ide.editor.api.event.EditorSaveContentEvent;
import org.exoplatform.ide.editor.api.event.EditorSaveContentHandler;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Image;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: EditorController Mar 21, 2011 5:22:10 PM evgen $
 *
 */
public class EditorController implements EditorContentChangedHandler, 
   EditorSaveContentHandler, EditorActiveFileChangedHandler, EditorCloseFileHandler, EditorUndoTypingHandler,
   EditorRedoTypingHandler, EditorFormatTextHandler, ShowLineNumbersHandler, EditorChangeActiveFileHandler,
   EditorOpenFileHandler, FileSavedHandler, EditorReplaceFileHandler, EditorDeleteCurrentLineHandler,
   EditorGoToLineHandler, EditorFindTextHandler, EditorReplaceTextHandler, EditorReplaceAndFindTextHandler,
   EditorSetFocusHandler, RefreshHotKeysHandler, ApplicationSettingsReceivedHandler, SaveFileAsHandler,
   ViewVisibilityChangedHandler, ViewClosedHandler, ClosingViewHandler, EditorFocusReceivedHandler, VersionRestoredHandler
{
   
   private static final String CLOSE_FILE = org.exoplatform.ide.client.IDE.EDITOR_CONSTANT.editorControllerAskCloseFile();

   private HandlerManager eventBus;

   /**
    * Used to remove handlers when they are no longer needed.
    */
   private Map<GwtEvent.Type<?>, HandlerRegistration> handlerRegistrations =
      new HashMap<GwtEvent.Type<?>, HandlerRegistration>();

   private ArrayList<String> ignoreContentChangedList = new ArrayList<String>();

   private boolean closeFileAfterSaving = false;

   private ApplicationSettings applicationSettings;

   private File activeFile;

   private Map<String, File> openedFiles = new LinkedHashMap<String, File>();

   private LinkedHashMap<String, String> openedEditorsDescription = new LinkedHashMap<String, String>();

   private Map<String, String> lockTokens;

   private Map<String, EditorView> editorsViews = new HashMap<String, EditorView>();
      
   private boolean waitForEditorInitialized = false;
   
   private boolean isAfterSaveAs = false;

   public EditorController()
   {
      this.eventBus = IDE.EVENT_BUS;
      handlerRegistrations.put(ApplicationSettingsReceivedEvent.TYPE, eventBus.addHandler(ApplicationSettingsReceivedEvent.TYPE, this));
      //eventBus.addHandler(InitializeApplicationEvent.TYPE, this);

      handlerRegistrations.put(EditorOpenFileEvent.TYPE, eventBus.addHandler(EditorOpenFileEvent.TYPE, this));

      handlerRegistrations.put(EditorReplaceFileEvent.TYPE, eventBus.addHandler(EditorReplaceFileEvent.TYPE, this));

      handlerRegistrations.put(RefreshHotKeysEvent.TYPE, eventBus.addHandler(RefreshHotKeysEvent.TYPE, this));

      handlerRegistrations.put(EditorFindTextEvent.TYPE, eventBus.addHandler(EditorFindTextEvent.TYPE, this));
      handlerRegistrations.put(EditorReplaceTextEvent.TYPE, eventBus.addHandler(EditorReplaceTextEvent.TYPE, this));
      handlerRegistrations.put(EditorReplaceAndFindTextEvent.TYPE, eventBus.addHandler(EditorReplaceAndFindTextEvent.TYPE, this));

      handlerRegistrations.put(EditorContentChangedEvent.TYPE, eventBus.addHandler(EditorContentChangedEvent.TYPE, this));
      handlerRegistrations.put(EditorSaveContentEvent.TYPE, eventBus.addHandler(EditorSaveContentEvent.TYPE, this));
      handlerRegistrations.put(EditorActiveFileChangedEvent.TYPE, eventBus.addHandler(EditorActiveFileChangedEvent.TYPE, this));
      handlerRegistrations.put(EditorCloseFileEvent.TYPE, eventBus.addHandler(EditorCloseFileEvent.TYPE, this));
      handlerRegistrations.put(EditorUndoTypingEvent.TYPE, eventBus.addHandler(EditorUndoTypingEvent.TYPE, this));
      handlerRegistrations.put(EditorRedoTypingEvent.TYPE, eventBus.addHandler(EditorRedoTypingEvent.TYPE, this));
      handlerRegistrations.put(FileSavedEvent.TYPE, eventBus.addHandler(FileSavedEvent.TYPE, this));
      handlerRegistrations.put(EditorFormatTextEvent.TYPE, eventBus.addHandler(EditorFormatTextEvent.TYPE, this));
      handlerRegistrations.put(ShowLineNumbersEvent.TYPE, eventBus.addHandler(ShowLineNumbersEvent.TYPE, this));
      handlerRegistrations.put(EditorChangeActiveFileEvent.TYPE, eventBus.addHandler(EditorChangeActiveFileEvent.TYPE, this));
      handlerRegistrations.put(EditorDeleteCurrentLineEvent.TYPE, eventBus.addHandler(EditorDeleteCurrentLineEvent.TYPE, this));
      handlerRegistrations.put(EditorGoToLineEvent.TYPE, eventBus.addHandler(EditorGoToLineEvent.TYPE, this));
      handlerRegistrations.put(EditorSetFocusEvent.TYPE, eventBus.addHandler(EditorSetFocusEvent.TYPE, this));
      handlerRegistrations.put(SaveFileAsEvent.TYPE, eventBus.addHandler(SaveFileAsEvent.TYPE, this));
      handlerRegistrations.put(ViewVisibilityChangedEvent.TYPE, eventBus.addHandler(ViewVisibilityChangedEvent.TYPE, this));
      handlerRegistrations.put(ClosingViewEvent.TYPE, eventBus.addHandler(ClosingViewEvent.TYPE, this));
      handlerRegistrations.put(EditorFocusReceivedEvent.TYPE, eventBus.addHandler(EditorFocusReceivedEvent.TYPE, this));
      handlerRegistrations.put(VersionRestoredEvent.TYPE, eventBus.addHandler(VersionRestoredEvent.TYPE, this));
      
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
         applicationSettings.setValue("default-editors", new LinkedHashMap<String, String>(), Store.SERVER);
      }
   }

   /**
    * Destroy 
    */
   public void destroy()
   {
      removeHandlers();
   }
   
   /**
    * Remove handlers, that are no longer needed.
    */
   private void removeHandlers()
   {
      //TODO: such method is not very convenient.
      //If gwt mvp framework will be used , it will be good to use
      //ResettableEventBus class
      for (HandlerRegistration h : handlerRegistrations.values())
      {
         h.removeHandler();
      }
      handlerRegistrations.clear();
   }

   /* 
    * Editor content changed handler
    */
   public void onEditorContentChanged(EditorContentChangedEvent event)
   {
      Editor editor = getEditorFromView(activeFile.getHref());
      if (editor == null
           || !event.getEditorId().equals(editor.getEditorId()))
      {
         return;
      }
      String path = activeFile.getHref();

      if (ignoreContentChangedList.contains(path))
      {
         ignoreContentChangedList.remove(path);
         return;
      }

      activeFile.setContentChanged(true);

      activeFile.setContent(editor.getText());
      updateTabTitle(activeFile);
      eventBus.fireEvent(new EditorFileContentChangedEvent(activeFile, editor.hasUndoChanges(), editor.hasRedoChanges()));
   }

   public void onEditorFocusReceived(EditorFocusReceivedEvent event)
   {
      editorsViews.get(activeFile.getHref()).activate();
   }
   
   /* (non-Javadoc)
    * @see org.exoplatform.gadgets.devtool.client.editor.event.EditorActiveFileChangedHandler#onEditorChangedActiveFile(org.exoplatform.gadgets.devtool.client.editor.event.EditorActiveFileChangedEvent)
    */
   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      if (activeFile == event.getFile())
      {
         getEditorFromView(event.getFile().getHref()).setFocus();
         return;
      }

      closeFileAfterSaving = false;
      File curentFile = event.getFile();
      if (curentFile == null)
      {
         activeFile = null;
         return;
      }

      activeFile = curentFile;
      getEditorFromView(curentFile.getHref()).setFocus();
      ((View)editorsViews.get(activeFile.getHref())).activate();
   }

   public void onEditorSaveContent(EditorSaveContentEvent event)
   {
      File file = activeFile;
      file.setContent(getEditorFromView(file.getHref()).getText());
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
      EditorView editorView = editorsViews.get(file.getHref());
      
      String editorDescription = openedEditorsDescription.get(file.getHref());
      
      editorsViews.remove(file.getHref());
      openedFiles.remove(file.getHref());
      openedEditorsDescription.remove(file.getHref());

      IDE.getInstance().closeView(editorView.getId());

      file.setContent(null);
      file.setContentChanged(false);
      
      if (ignoreContentChangedList.contains(file.getHref()))
      {
         ignoreContentChangedList.remove(file.getHref());
      }

      eventBus.fireEvent(new EditorFileClosedEvent(file, editorDescription, openedFiles));
      if (editorsViews.isEmpty()) {
         eventBus.fireEvent(new EditorActiveFileChangedEvent(null, null));
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
         final String fileName = Utils.unescape(file.getName());
         String message = org.exoplatform.ide.client.IDE.IDE_LOCALIZATION_MESSAGES.editorDoYouWantToSaveFileBeforeClosing(fileName);
         Dialogs.getInstance().ask(CLOSE_FILE, message, new BooleanValueReceivedHandler()
         {
            public void booleanValueReceived(Boolean value)
            {
               if (value == null)
               {
                  return;
               }

               if (value)
               {
                  file.setContent(getEditorFromView(file.getHref()).getText());
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

   protected Editor getEditorFromView(String href)
   {
      return editorsViews.get(href).getEditor();
   }

   public void onEditorUndoTyping(EditorUndoTypingEvent event)
   {
      getEditorFromView(activeFile.getHref()).undo();
   }

   public void onEditorRedoTyping(EditorRedoTypingEvent event)
   {
      getEditorFromView(activeFile.getHref()).redo();

   }

   private void updateTabTitle(File file)
   {
      editorsViews.get(file.getHref()).setTitle(file, isReadOnly(file));
   }

   public void onFormatFile(EditorFormatTextEvent event)
   {
      getEditorFromView(activeFile.getHref()).formatSource();
   }

   private void updateLineNumbers(boolean lineNumbers)
   {
      Iterator<String> iterator = openedFiles.keySet().iterator();
      while (iterator.hasNext())
      {
         String path = iterator.next();
         getEditorFromView(path).showLineNumbers(lineNumbers);
      }
      getEditorFromView(activeFile.getHref()).setFocus();
   }

   public void onShowLineNumbers(ShowLineNumbersEvent event)
   {

      updateLineNumbers(event.isShowLineNumber());
   }

   public void onEditorChangeActiveFile(EditorChangeActiveFileEvent event)
   {
      if (activeFile == event.getFile())
         return;
      
      activeFile = event.getFile();
      if (activeFile == null) {
         return;
      }
      
      editorsViews.get(activeFile.getHref()).activate();
   }

   public void onEditorOpenFile(EditorOpenFileEvent event)
   {
      File file = event.getFile();

      if (file == null)
      {
         return;
      }
      if (openedFiles.get(file.getHref()) != null)
      {         
         File openedFile = openedFiles.get(file.getHref());
         EditorView openedFileEditorView = editorsViews.get(openedFile.getHref());

         if (! event.getEditorProducer().getDescription().equals(openedEditorsDescription.get(file.getHref())))
         {
            openedFileEditorView.switchToEditor(getNumberOfEditorToShow(event.getEditorProducer()) - 1);
            openedEditorsDescription.put(file.getHref(), event.getEditorProducer().getDescription());
         }
         
         openedFileEditorView.setViewVisible();
         return;
      }

      ignoreContentChangedList.add(file.getHref());
      openedFiles.put(file.getHref(), file);
      openedEditorsDescription.put(file.getHref(), event.getEditorProducer().getDescription());
      
      List<Editor> supportedEditors = getSupportedEditors(event.getEditorProducer(), file, eventBus);
            
      EditorView editorView = new EditorView(file,
                                             isReadOnly(file),
                                             this.eventBus,
                                             supportedEditors, 
                                             getNumberOfEditorToShow(event.getEditorProducer()) - 1);
     
      if (editorsViews.containsKey(file.getHref()))
      {
         editorsViews.put(file.getHref(), editorView);
      }
      else
      {
         editorsViews.put(file.getHref(), editorView);
         waitForEditorInitialized = true;
         IDE.getInstance().openView(editorView);
      }
      
      activeFile = file;
      
      eventBus.fireEvent(new EditorFileOpenedEvent(file, event.getEditorProducer().getDescription(), openedFiles));
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
         File savedFile = event.getFile();
         openedFiles.remove(event.getSourceHref());
         openedFiles.put(savedFile.getHref(), savedFile);

         EditorView oldFileEditorView = editorsViews.get(savedFile.getHref());
         if (oldFileEditorView == null)
         {
            oldFileEditorView = editorsViews.get(activeFile.getHref());
            editorsViews.remove(activeFile.getHref());
         }
         else
         {
            editorsViews.remove(savedFile.getHref());
         }
         
         oldFileEditorView.setFile(savedFile);
         editorsViews.put(savedFile.getHref(), oldFileEditorView);
         updateTabTitle(savedFile);

         try
         {
            oldFileEditorView.setIcon(new Image(savedFile.getIcon()));
         }
         catch (Exception e) {
            e.printStackTrace();
         }
         
         // call activeFileChanged event after the SaveFileAs operation
         if (! activeFile.getHref().equals(savedFile.getHref()) && isAfterSaveAs)
         {
            eventBus.fireEvent(new EditorActiveFileChangedEvent(savedFile, oldFileEditorView.getEditor()));
         }
      }
      
      isAfterSaveAs = false;
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

      ignoreContentChangedList.remove(oldFile.getHref());     
      ignoreContentChangedList.add(newFile.getHref()); 
      
      EditorView oldFileEditorView = editorsViews.get(oldFile.getHref());
      editorsViews.remove(oldFile.getHref());
      editorsViews.put(newFile.getHref(), oldFileEditorView);

      try
      {
         oldFileEditorView.setIcon(new Image(newFile.getIcon()));
      }
      catch (Exception e) {
         e.printStackTrace();
      }

      oldFileEditorView.setContent(newFile.getContent());
            
      updateTabTitle(newFile);
      oldFileEditorView.setFile(newFile);
      
      eventBus.fireEvent(new EditorActiveFileChangedEvent(newFile, oldFileEditorView.getEditor()));
   }

   /**
    * @see org.exoplatform.ide.client.event.edit.EditorDeleteCurrentLineHandler#onEditorDeleteCurrentLine(org.exoplatform.ide.client.event.edit.EditorDeleteCurrentLineEvent)
    */
   public void onEditorDeleteCurrentLine(EditorDeleteCurrentLineEvent event)
   {
      getEditorFromView(activeFile.getHref()).deleteCurrentLine();
   }

   /**
    * @see org.exoplatform.ide.client.editor.event.EditorGoToLineHandler#onEditorGoToLine(org.exoplatform.ide.client.editor.event.EditorGoToLineEvent)
    */
   public void onEditorGoToLine(EditorGoToLineEvent event)
   {
      EditorView activeEditorView = editorsViews.get(activeFile.getHref());
      activeEditorView.getEditor().goToPosition(event.getLineNumber(), event.getColumnNumber());      
   }

   /**
    * @see org.exoplatform.ide.client.editor.event.EditorFindTextHandler#onEditorFindText(org.exoplatform.ide.client.editor.event.EditorFindTextEvent)
    */
   public void onEditorFindText(EditorFindTextEvent event)
   {
      boolean isFound = getEditorFromView(event.getPath()).findAndSelect(event.getFindText(), event.isCaseSensitive());
      eventBus.fireEvent(new EditorTextFoundEvent(isFound));
   }

   /**
    * @see org.exoplatform.ide.client.editor.event.EditorReplaceTextHandler#onEditorReplaceText(org.exoplatform.ide.client.editor.event.EditorReplaceTextEvent)
    */
   public void onEditorReplaceText(EditorReplaceTextEvent event)
   {
      Editor editor = getEditorFromView(event.getPath());
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
    * @see org.exoplatform.ide.client.framework.editor.event.EditorReplaceAndFindTextHandler#onEditorReplaceAndFindText(org.exoplatform.ide.client.framework.editor.event.EditorReplaceAndFindTextEvent)
    */
   public void onEditorReplaceAndFindText(EditorReplaceAndFindTextEvent event)
   {
      Editor editor = getEditorFromView(event.getPath());
      editor.replaceFoundedText(event.getFindText(), event.getReplaceText(), event.isCaseSensitive());
      boolean isFound = editor.findAndSelect(event.getFindText(), event.isCaseSensitive());
      eventBus.fireEvent(new EditorTextFoundEvent(isFound));
   }

   /**
    * @see org.exoplatform.ide.client.editor.event.EditorSetFocusHandler#onEditorSetFocus(org.exoplatform.ide.client.editor.event.EditorSetFocusEvent)
    */
   public void onEditorSetFocus(EditorSetFocusEvent event)
   {
      getEditorFromView(activeFile.getHref()).setFocus();
   }

   public void onRefreshHotKeys(RefreshHotKeysEvent event)
   {
      Map<String, String> hotKeys = applicationSettings.getValueAsMap("hotkeys");
      List<String> hotKeyList = new ArrayList<String>(hotKeys.keySet());
      Iterator<String> it = openedFiles.keySet().iterator();
      while (it.hasNext())
      {
         String file = it.next();
         getEditorFromView(file).setHotKeyList(hotKeyList);
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
      
      isAfterSaveAs = true;
   }

   /**
    * @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent)
    */
   @Override
   public void onViewClosed(ViewClosedEvent event)
   {
      if (event.getView().getType().equals("editor"))
      {
         eventBus.fireEvent(new EditorCloseFileEvent(((EditorView) event.getView()).getFile()));
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.ui.api.event.ViewVisibilityChangedHandler#onViewVisibilityChanged(org.exoplatform.ide.client.framework.ui.api.event.ViewVisibilityChangedEvent)
    */
   @Override
   public void onViewVisibilityChanged(final ViewVisibilityChangedEvent event)
   {
      if (event.getView().getType().equals("editor") && event.getView().isViewVisible())
      {
         final EditorView editorView = (EditorView) event.getView();
         activeFile = editorView.getFile();
         Timer timer = new Timer()
         {
            @Override
            public void run()
            {
               try 
               {
                  if (editorView.getEditor() == null)
                  {
                     return;   
                  }
                  eventBus.fireEvent(new EditorActiveFileChangedEvent(activeFile, editorView.getEditor()));                     
               } 
               catch (Exception e) 
               {
                  e.printStackTrace();
               }
            }
         };
            
         if (waitForEditorInitialized)
         {
            waitForEditorInitialized = false;
            timer.schedule(1000);
         }
         else
         {
            timer.run();
         }

      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.ui.api.event.ClosingViewHandler#onClosingView(org.exoplatform.ide.client.framework.ui.api.event.ClosingViewEvent)
    */
   @Override
   public void onClosingView(ClosingViewEvent event)
   {
      if (event.getView().getType().equals("editor"))
      {
         event.cancelClosing();
         eventBus.fireEvent(new EditorCloseFileEvent(((EditorView) event.getView()).getFile()));
      }

   }

   /**
    * Read applicationSettings and return true if there are more than one editors for file with mimeType.   
    * @param mimeType
    * @return
    * @throws EditorNotFoundException  
    */
   private List<EditorProducer> getSupportedEditorProducers(String mimeType)
   {
      try
      {
         return EditorFactory.getEditors(mimeType);
      }
      catch (EditorNotFoundException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      return null;
   }


   private List<Editor> getSupportedEditors(EditorProducer editorProducer, File file, HandlerManager eventBus)
   {
      boolean isLineNumbers = true;
      if (applicationSettings.getValueAsBoolean("line-numbers") != null)
      {
         isLineNumbers = applicationSettings.getValueAsBoolean("line-numbers");
      }
      
      // create editors for source/design view
      List<Editor> supportedEditors = new ArrayList<Editor>();
      List<EditorProducer> supportedEditorProducers = getSupportedEditorProducers(file.getContentType());
      
      for (EditorProducer supportedEditorProducer : supportedEditorProducers)
      {
         List<String> hotKeyList = new ArrayList<String>((applicationSettings.getValueAsMap("hotkeys")).keySet());
         
         HashMap<String, Object> params = new HashMap<String, Object>();

         params.put(EditorParameters.IS_READ_ONLY, isReadOnly(file));
         params.put(EditorParameters.IS_SHOW_LINE_NUMER, isLineNumbers);
         params.put(EditorParameters.HOT_KEY_LIST, hotKeyList);

         Editor editor = supportedEditorProducer.createEditor(file.getContent(), eventBus, params);
         
         supportedEditors.add(editor);
         DOM.setStyleAttribute(editor.getElement(), "zIndex", "0");
      }
      
      return supportedEditors;
   }
   
   /**
    * Return number from 1 of editor created by editorProducer to show in view among the supported editor producer for some mime type.  
    * @param editorProducer
    * @return
    */
   private int getNumberOfEditorToShow(EditorProducer editorProducer)
   {
      // create editors for source/design view
      List<EditorProducer> supportedEditorProducers = getSupportedEditorProducers(editorProducer.getMimeType());

      int i = 1;
      for (EditorProducer supportedEditorProducer : supportedEditorProducers)
      {
         if (editorProducer.getDescription().equals(supportedEditorProducer.getDescription()))
         {
            return i;
         }
         i++;
      }
      
      return 1;
   }

   public void onVersionRestored(VersionRestoredEvent event)
   {
      File oldVersionFile = event.getFile();
      Editor editor = getEditorFromView(oldVersionFile.getHref());

      // ignore changing of non-opened or non-active file
      if (editor == null 
               || !oldVersionFile.getHref().equals(activeFile.getHref()))
      {
         return;
      }
      
      // file changing should be finished in time of handling EditorContentChangedEvent 
      ignoreContentChangedList.add(oldVersionFile.getHref());
      editor.setText(oldVersionFile.getContent());
   }
   
}
