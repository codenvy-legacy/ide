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
import org.exoplatform.ide.client.framework.util.ImageUtil;
import org.exoplatform.ide.client.framework.util.Utils;
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
import org.exoplatform.ide.vfs.client.model.FileModel;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Image;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: EditorController Mar 21, 2011 5:22:10 PM evgen $
 *
 */
public class EditorController implements EditorContentChangedHandler, EditorSaveContentHandler,
   EditorActiveFileChangedHandler, EditorCloseFileHandler, EditorUndoTypingHandler, EditorRedoTypingHandler,
   EditorFormatTextHandler, ShowLineNumbersHandler, EditorChangeActiveFileHandler, EditorOpenFileHandler,
   FileSavedHandler, EditorReplaceFileHandler, EditorDeleteCurrentLineHandler, EditorGoToLineHandler,
   EditorFindTextHandler, EditorReplaceTextHandler, EditorReplaceAndFindTextHandler, EditorSetFocusHandler,
   RefreshHotKeysHandler, ApplicationSettingsReceivedHandler, SaveFileAsHandler, ViewVisibilityChangedHandler,
   ViewClosedHandler, ClosingViewHandler, EditorFocusReceivedHandler, VersionRestoredHandler
{

   private static final String CLOSE_FILE = org.exoplatform.ide.client.IDE.EDITOR_CONSTANT
      .editorControllerAskCloseFile();

   private ArrayList<String> ignoreContentChangedList = new ArrayList<String>();

   private boolean closeFileAfterSaving = false;

   private ApplicationSettings applicationSettings;

   private FileModel activeFile;

   private Map<String, FileModel> openedFiles = new LinkedHashMap<String, FileModel>();

   private LinkedHashMap<String, String> openedEditorsDescription = new LinkedHashMap<String, String>();

   private Map<String, String> lockTokens;

   private Map<String, EditorView> editorsViews = new HashMap<String, EditorView>();

   private boolean waitForEditorInitialized = false;

   private boolean isAfterSaveAs = false;

   public EditorController()
   {
      IDE.addHandler(ApplicationSettingsReceivedEvent.TYPE, this);
      IDE.addHandler(EditorOpenFileEvent.TYPE, this);
      IDE.addHandler(EditorReplaceFileEvent.TYPE, this);
      IDE.addHandler(RefreshHotKeysEvent.TYPE, this);

      IDE.addHandler(EditorFindTextEvent.TYPE, this);
      IDE.addHandler(EditorReplaceTextEvent.TYPE, this);
      IDE.addHandler(EditorReplaceAndFindTextEvent.TYPE, this);

      IDE.addHandler(EditorContentChangedEvent.TYPE, this);
      IDE.addHandler(EditorSaveContentEvent.TYPE, this);
      IDE.addHandler(EditorActiveFileChangedEvent.TYPE, this);
      IDE.addHandler(EditorCloseFileEvent.TYPE, this);
      IDE.addHandler(EditorUndoTypingEvent.TYPE, this);
      IDE.addHandler(EditorRedoTypingEvent.TYPE, this);
      IDE.addHandler(FileSavedEvent.TYPE, this);
      IDE.addHandler(EditorFormatTextEvent.TYPE, this);
      IDE.addHandler(ShowLineNumbersEvent.TYPE, this);
      IDE.addHandler(EditorChangeActiveFileEvent.TYPE, this);
      IDE.addHandler(EditorDeleteCurrentLineEvent.TYPE, this);
      IDE.addHandler(EditorGoToLineEvent.TYPE, this);
      IDE.addHandler(EditorSetFocusEvent.TYPE, this);
      IDE.addHandler(SaveFileAsEvent.TYPE, this);
      IDE.addHandler(ViewVisibilityChangedEvent.TYPE, this);
      IDE.addHandler(ClosingViewEvent.TYPE, this);
      IDE.addHandler(EditorFocusReceivedEvent.TYPE, this);
      IDE.addHandler(VersionRestoredEvent.TYPE, this);
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

   /* 
    * Editor content changed handler
    */
   public void onEditorContentChanged(EditorContentChangedEvent event)
   {
      Editor editor = getEditorFromView(activeFile.getId());
      if (editor == null || !event.getEditorId().equals(editor.getEditorId()))
      {
         return;
      }
      String path = activeFile.getId();

      if (ignoreContentChangedList.contains(path))
      {
         ignoreContentChangedList.remove(path);
         return;
      }

      activeFile.setContentChanged(true);

      activeFile.setContent(editor.getText());
      updateTabTitle(activeFile);
      IDE.fireEvent(new EditorFileContentChangedEvent(activeFile, editor.hasUndoChanges(), editor.hasRedoChanges()));
   }

   public void onEditorFocusReceived(EditorFocusReceivedEvent event)
   {
      editorsViews.get(activeFile.getId()).activate();
   }

   /* (non-Javadoc)
    * @see org.exoplatform.gadgets.devtool.client.editor.event.EditorActiveFileChangedHandler#onEditorChangedActiveFile(org.exoplatform.gadgets.devtool.client.editor.event.EditorActiveFileChangedEvent)
    */
   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      if (event.getFile() == null)
      {
         return;
      }
      if (activeFile == event.getFile())
      {
         getEditorFromView(event.getFile().getId()).setFocus();
         return;
      }

      closeFileAfterSaving = false;
      FileModel curentFile = event.getFile();
      if (curentFile == null)
      {
         activeFile = null;
         return;
      }

      activeFile = curentFile;
      getEditorFromView(curentFile.getId()).setFocus();
      ((View)editorsViews.get(activeFile.getId())).activate();
   }

   public void onEditorSaveContent(EditorSaveContentEvent event)
   {
      FileModel file = activeFile;
      file.setContent(getEditorFromView(file.getId()).getText());
      if (!file.isPersisted())
      {
         IDE.fireEvent(new SaveFileAsEvent());
      }
      else
      {
         IDE.fireEvent(new SaveFileEvent());
      }
   }

   private void closeFile(FileModel file)
   {
      EditorView editorView = editorsViews.get(file.getId());

      String editorDescription = openedEditorsDescription.get(file.getId());

      editorsViews.remove(file.getId());
      openedFiles.remove(file.getId());
      openedEditorsDescription.remove(file.getId());

      IDE.getInstance().closeView(editorView.getId());

      file.setContent(null);
      file.setContentChanged(false);

      if (ignoreContentChangedList.contains(file.getId()))
      {
         ignoreContentChangedList.remove(file.getId());
      }

      IDE.fireEvent(new EditorFileClosedEvent(file, editorDescription, openedFiles));
      if (editorsViews.isEmpty())
      {
         IDE.fireEvent(new EditorActiveFileChangedEvent(null, null));
      }
   }

   public void onEditorCloseFile(EditorCloseFileEvent event)
   {
      if (event.isIgnoreChanges())
      {
         closeFileAfterSaving = false;
         closeFile(event.getFile());
         return;
      }
      final FileModel file = event.getFile();

      if (file.isLocked())
      {
         if (!lockTokens.containsKey(file.getId()))
         {
            closeFile(file);
            return;
         }
      }

      //TODO 
      if (!file.isContentChanged() /*&& !file.isPropertiesChanged()*/)
      {
         closeFile(file);
         return;
      }

      if (!file.isPersisted())
      {
         closeFileAfterSaving = true;
         IDE.fireEvent(new SaveFileAsEvent(file, SaveFileAsEvent.SaveDialogType.EXTENDED,
            new EditorCloseFileEvent(file, true), null));
      }
      else
      {
         closeFileAfterSaving = true;
         final String fileName = Utils.unescape(file.getName());
         String message =
            org.exoplatform.ide.client.IDE.IDE_LOCALIZATION_MESSAGES.editorDoYouWantToSaveFileBeforeClosing(fileName);
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
                  file.setContent(getEditorFromView(file.getId()).getText());
                  IDE.fireEvent(new SaveFileEvent());
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
      getEditorFromView(activeFile.getId()).undo();
   }

   public void onEditorRedoTyping(EditorRedoTypingEvent event)
   {
      getEditorFromView(activeFile.getId()).redo();
   }

   private void updateTabTitle(FileModel file)
   {
      editorsViews.get(file.getId()).setTitle(file, isReadOnly(file));
   }

   public void onFormatFile(EditorFormatTextEvent event)
   {
      getEditorFromView(activeFile.getId()).formatSource();
   }

   private void updateLineNumbers(boolean lineNumbers)
   {
      Iterator<String> iterator = openedFiles.keySet().iterator();
      while (iterator.hasNext())
      {
         String path = iterator.next();
         getEditorFromView(path).showLineNumbers(lineNumbers);
      }
      getEditorFromView(activeFile.getId()).setFocus();
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
      if (activeFile == null)
      {
         return;
      }

      editorsViews.get(activeFile.getId()).activate();
   }

   public void onEditorOpenFile(EditorOpenFileEvent event)
   {
      FileModel file = event.getFile();

      if (file == null)
      {
         return;
      }
      if (openedFiles.get(file.getId()) != null)
      {
         FileModel openedFile = openedFiles.get(file.getId());
         EditorView openedFileEditorView = editorsViews.get(openedFile.getId());

         if (!event.getEditorProducer().getDescription().equals(openedEditorsDescription.get(file.getId())))
         {
            openedFileEditorView.switchToEditor(getNumberOfEditorToShow(event.getEditorProducer()) - 1);
            openedEditorsDescription.put(file.getId(), event.getEditorProducer().getDescription());
         }

         openedFileEditorView.setViewVisible();
         return;
      }

      ignoreContentChangedList.add(file.getId());
      openedFiles.put(file.getId(), file);
      openedEditorsDescription.put(file.getId(), event.getEditorProducer().getDescription());

      List<Editor> supportedEditors = getSupportedEditors(event.getEditorProducer(), file);

      EditorView editorView =
         new EditorView(file, isReadOnly(file), IDE.eventBus(), supportedEditors,
            getNumberOfEditorToShow(event.getEditorProducer()) - 1);

      if (editorsViews.containsKey(file.getId()))
      {
         editorsViews.put(file.getId(), editorView);
      }
      else
      {
         editorsViews.put(file.getId(), editorView);
         waitForEditorInitialized = true;
         IDE.getInstance().openView(editorView);
      }

      activeFile = file;

      IDE.fireEvent(new EditorFileOpenedEvent(file, event.getEditorProducer().getDescription(), openedFiles));
   }

   /**
    * Check is file locked
    * 
    * @param file
    * @return true if file is locked and client not have lock token, else return false
    */
   private boolean isReadOnly(FileModel file)
   {
      if (file.isVersion())
      {
         return true;
      }
      else if (file.isLocked())
      {
         return !(lockTokens.containsKey(file.getId()));
      }
      else
         return false;
   }

   public void onFileSaved(FileSavedEvent event)
   {
      if (closeFileAfterSaving)
      {
         FileModel file = event.getFile();
         if (event.getSourceHref() != null)
         {
            file.setPath(event.getSourceHref());
         }
         if(event.getSourceHref() == null)
         {
           closeFile(file);
         }
         else
         {
           //closing file Saved "As" ...
           FileModel oldFile=new FileModel(event.getFile());
           oldFile.setId(event.getSourceHref());
           closeFile(oldFile);
         }
         closeFileAfterSaving = false;
      }
      else
      {
         FileModel savedFile = event.getFile();
         openedFiles.remove(event.getSourceHref());
         openedFiles.put(savedFile.getId(), savedFile);

         EditorView oldFileEditorView = editorsViews.get(savedFile.getId());
         if (oldFileEditorView == null)
         {
            oldFileEditorView = editorsViews.get(activeFile.getId());
            editorsViews.remove(activeFile.getId());
         }
         else
         {
            editorsViews.remove(savedFile.getId());
         }

         oldFileEditorView.setFile(savedFile);
         editorsViews.put(savedFile.getId(), oldFileEditorView);
         updateTabTitle(savedFile);

         try
         {
            oldFileEditorView.setIcon(new Image(ImageUtil.getIcon(savedFile.getMimeType())));
         }
         catch (Exception e)
         {
            e.printStackTrace();
         }
         // call activeFileChanged event after the SaveFileAs operation
         if (!savedFile.getId().equals(activeFile.getId()) && isAfterSaveAs)
         {
            IDE.fireEvent(new EditorActiveFileChangedEvent(savedFile, oldFileEditorView.getEditor()));
         }
      }

      isAfterSaveAs = false;
   }

   public void onEditorReplaceFile(EditorReplaceFileEvent event)
   {
      replaceFile(event.getFile(), event.getNewFile());
   }

   private void replaceFile(FileModel oldFile, FileModel newFile)
   {
      if (newFile == null)
      {
         updateTabTitle(oldFile);
         return;
      }

      ignoreContentChangedList.remove(oldFile.getId());
      ignoreContentChangedList.add(newFile.getId());

      EditorView oldFileEditorView = editorsViews.get(oldFile.getId());
      editorsViews.remove(oldFile.getId());
      editorsViews.put(newFile.getId(), oldFileEditorView);

      try
      {
         oldFileEditorView.setIcon(new Image(ImageUtil.getIcon(newFile)));
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
      IDE.fireEvent(new EditorActiveFileChangedEvent(newFile, oldFileEditorView.getEditor()));
      oldFileEditorView.setContent(newFile.getContent());

      updateTabTitle(newFile);
      oldFileEditorView.setFile(newFile);
      
   }

   /**
    * @see org.exoplatform.ide.client.event.edit.EditorDeleteCurrentLineHandler#onEditorDeleteCurrentLine(org.exoplatform.ide.client.event.edit.EditorDeleteCurrentLineEvent)
    */
   public void onEditorDeleteCurrentLine(EditorDeleteCurrentLineEvent event)
   {
      getEditorFromView(activeFile.getId()).deleteCurrentLine();
   }

   /**
    * @see org.exoplatform.ide.client.editor.event.EditorGoToLineHandler#onEditorGoToLine(org.exoplatform.ide.client.editor.event.EditorGoToLineEvent)
    */
   public void onEditorGoToLine(EditorGoToLineEvent event)
   {
      EditorView activeEditorView = editorsViews.get(activeFile.getId());
      activeEditorView.getEditor().goToPosition(event.getLineNumber(), event.getColumnNumber());
   }

   /**
    * @see org.exoplatform.ide.client.editor.event.EditorFindTextHandler#onEditorFindText(org.exoplatform.ide.client.editor.event.EditorFindTextEvent)
    */
   public void onEditorFindText(EditorFindTextEvent event)
   {
      boolean isFound =
         getEditorFromView(event.getFileId()).findAndSelect(event.getFindText(), event.isCaseSensitive());
      IDE.fireEvent(new EditorTextFoundEvent(isFound));
   }

   /**
    * @see org.exoplatform.ide.client.editor.event.EditorReplaceTextHandler#onEditorReplaceText(org.exoplatform.ide.client.editor.event.EditorReplaceTextEvent)
    */
   public void onEditorReplaceText(EditorReplaceTextEvent event)
   {
      Editor editor = getEditorFromView(event.getFileId());
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
      Editor editor = getEditorFromView(event.getFileId());
      editor.replaceFoundedText(event.getFindText(), event.getReplaceText(), event.isCaseSensitive());
      boolean isFound = editor.findAndSelect(event.getFindText(), event.isCaseSensitive());
      IDE.fireEvent(new EditorTextFoundEvent(isFound));
   }

   /**
    * @see org.exoplatform.ide.client.editor.event.EditorSetFocusHandler#onEditorSetFocus(org.exoplatform.ide.client.editor.event.EditorSetFocusEvent)
    */
   public void onEditorSetFocus(EditorSetFocusEvent event)
   {
      getEditorFromView(activeFile.getId()).setFocus();
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
         if (!(event.getView() instanceof EditorView))
         {
            return;
         }
         IDE.fireEvent(new EditorCloseFileEvent(((EditorView)event.getView()).getFile()));
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
         if (!(event.getView() instanceof EditorView))
         {
            IDE.fireEvent(new EditorActiveFileChangedEvent(null, null));
            return;
         }
         final EditorView editorView = (EditorView)event.getView();
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
                  IDE.fireEvent(new EditorActiveFileChangedEvent(activeFile, editorView.getEditor()));
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
         if (!(event.getView() instanceof EditorView))
         {
            return;
         }
         event.cancelClosing();
         IDE.fireEvent(new EditorCloseFileEvent(((EditorView)event.getView()).getFile()));
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
         e.printStackTrace();
      }
      return null;
   }

   private List<Editor> getSupportedEditors(EditorProducer editorProducer, FileModel file)
   {
      boolean isLineNumbers = true;
      if (applicationSettings.getValueAsBoolean("line-numbers") != null)
      {
         isLineNumbers = applicationSettings.getValueAsBoolean("line-numbers");
      }

      // create editors for source/design view
      List<Editor> supportedEditors = new ArrayList<Editor>();
      List<EditorProducer> supportedEditorProducers = getSupportedEditorProducers(file.getMimeType());

      for (EditorProducer supportedEditorProducer : supportedEditorProducers)
      {
         List<String> hotKeyList = new ArrayList<String>((applicationSettings.getValueAsMap("hotkeys")).keySet());

         HashMap<String, Object> params = new HashMap<String, Object>();

         params.put(EditorParameters.IS_READ_ONLY, isReadOnly(file));
         params.put(EditorParameters.IS_SHOW_LINE_NUMER, isLineNumbers);
         params.put(EditorParameters.HOT_KEY_LIST, hotKeyList);

         Editor editor = supportedEditorProducer.createEditor(file.getContent(), IDE.eventBus(), params);

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
      FileModel oldVersionFile = event.getFile();
      Editor editor = getEditorFromView(oldVersionFile.getId());

      // ignore changing of non-opened or non-active file
      if (editor == null || !oldVersionFile.getId().equals(activeFile.getId()))
      {
         return;
      }

      // file changing should be finished in time of handling EditorContentChangedEvent 
      ignoreContentChangedList.add(oldVersionFile.getId());
      editor.setText(oldVersionFile.getContent());
   }

}
