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

import com.google.collide.client.CollabEditor;
import com.google.collide.client.CollabEditorExtension;
import com.google.collide.shared.document.Document;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Image;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.gwtframework.ui.client.dialog.BooleanValueReceivedHandler;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.edit.event.ShowLineNumbersEvent;
import org.exoplatform.ide.client.edit.event.ShowLineNumbersHandler;
import org.exoplatform.ide.client.framework.contextmenu.ShowContextMenuEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorChangeActiveFileEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorChangeActiveFileHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorCloseFileEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorCloseFileHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorCollapseFoldEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorCollapseFoldHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorCopyTextEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorCopyTextHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorCutTextEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorCutTextHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorDeleteCurrentLineEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorDeleteCurrentLineHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorDeleteTextEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorDeleteTextHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorExpandFoldEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorExpandFoldHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileContentChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFoldSelectionEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFoldSelectionHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorGoToLineEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorGoToLineHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorOpenFileEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorOpenFileHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorPasteTextEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorPasteTextHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorRedoTypingEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorRedoTypingHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorReplaceFileEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorReplaceFileHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorSelectAllEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorSelectAllHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorSetFocusEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorSetFocusHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorUndoTypingEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorUndoTypingHandler;
import org.exoplatform.ide.client.framework.event.FileSavedEvent;
import org.exoplatform.ide.client.framework.event.FileSavedHandler;
import org.exoplatform.ide.client.framework.event.SaveFileAsEvent;
import org.exoplatform.ide.client.framework.event.SaveFileAsHandler;
import org.exoplatform.ide.client.framework.event.SaveFileEvent;
import org.exoplatform.ide.client.framework.module.EditorNotFoundException;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings.Store;
import org.exoplatform.ide.client.framework.settings.ApplicationSettingsReceivedEvent;
import org.exoplatform.ide.client.framework.settings.ApplicationSettingsReceivedHandler;
import org.exoplatform.ide.client.framework.ui.api.View;
import org.exoplatform.ide.client.framework.ui.api.event.ClosingViewEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ClosingViewHandler;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.ui.api.event.ViewVisibilityChangedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewVisibilityChangedHandler;
import org.exoplatform.ide.client.framework.util.ImageUtil;
import org.exoplatform.ide.client.framework.util.Utils;
import org.exoplatform.ide.editor.client.api.Editor;
import org.exoplatform.ide.editor.client.api.SelectionRange;
import org.exoplatform.ide.editor.client.api.event.EditorContentChangedEvent;
import org.exoplatform.ide.editor.client.api.event.EditorContentChangedHandler;
import org.exoplatform.ide.editor.client.api.event.EditorContextMenuEvent;
import org.exoplatform.ide.editor.client.api.event.EditorContextMenuHandler;
import org.exoplatform.ide.editor.client.api.event.EditorFocusReceivedEvent;
import org.exoplatform.ide.editor.client.api.event.EditorFocusReceivedHandler;
import org.exoplatform.ide.vfs.client.model.FileModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: EditorController Mar 21, 2011 5:22:10 PM evgen $
 */
public class EditorController implements EditorContentChangedHandler, EditorActiveFileChangedHandler,
                                         EditorCloseFileHandler, EditorUndoTypingHandler, EditorRedoTypingHandler, ShowLineNumbersHandler,
                                         EditorChangeActiveFileHandler, EditorOpenFileHandler, FileSavedHandler, EditorReplaceFileHandler,
                                         EditorDeleteCurrentLineHandler, EditorGoToLineHandler, EditorContextMenuHandler,
                                         EditorSetFocusHandler,
                                         ApplicationSettingsReceivedHandler, SaveFileAsHandler, ViewVisibilityChangedHandler,
                                         ViewClosedHandler,
                                         ClosingViewHandler, EditorFocusReceivedHandler, EditorSelectAllHandler, EditorCutTextHandler,
                                         EditorCopyTextHandler,
                                         EditorPasteTextHandler, EditorDeleteTextHandler, EditorCollapseFoldHandler,
                                         EditorExpandFoldHandler,
                                         EditorFoldSelectionHandler {

    private static final String            CLOSE_FILE               = org.exoplatform.ide.client.IDE.EDITOR_CONSTANT
                                                                                                    .editorControllerAskCloseFile();
    private              ArrayList<String> ignoreContentChangedList = new ArrayList<String>();
    private              boolean           closeFileAfterSaving     = false;
    private ApplicationSettings applicationSettings;
    private FileModel           activeFile;
    private Map<String, FileModel> openedFiles = new LinkedHashMap<String, FileModel>();
    private Map<String, String> lockTokens;
    /** Key: View Id Value: EditorView instance */
    private Map<String, EditorView> editorViewList             = new HashMap<String, EditorView>();
    private boolean                 waitForEditorInitialized   = false;
    private boolean                 isAfterSaveAs              = false;
    private SelectionRange          selectionBeforeContextMenu = null;

    public EditorController() {
        IDE.addHandler(ApplicationSettingsReceivedEvent.TYPE, this);
        IDE.addHandler(EditorOpenFileEvent.TYPE, this);
        IDE.addHandler(EditorReplaceFileEvent.TYPE, this);
        IDE.addHandler(EditorContentChangedEvent.TYPE, this);
        IDE.addHandler(EditorActiveFileChangedEvent.TYPE, this);
        IDE.addHandler(EditorCloseFileEvent.TYPE, this);
        IDE.addHandler(EditorUndoTypingEvent.TYPE, this);
        IDE.addHandler(EditorRedoTypingEvent.TYPE, this);
        IDE.addHandler(EditorSelectAllEvent.TYPE, this);

        IDE.addHandler(EditorCutTextEvent.TYPE, this);
        IDE.addHandler(EditorCopyTextEvent.TYPE, this);
        IDE.addHandler(EditorPasteTextEvent.TYPE, this);
        IDE.addHandler(EditorDeleteTextEvent.TYPE, this);

        IDE.addHandler(FileSavedEvent.TYPE, this);
        IDE.addHandler(ShowLineNumbersEvent.TYPE, this);
        IDE.addHandler(EditorChangeActiveFileEvent.TYPE, this);
        IDE.addHandler(EditorDeleteCurrentLineEvent.TYPE, this);
        IDE.addHandler(EditorGoToLineEvent.TYPE, this);
        IDE.addHandler(EditorSetFocusEvent.TYPE, this);
        IDE.addHandler(EditorContextMenuEvent.TYPE, this);

        IDE.addHandler(SaveFileAsEvent.TYPE, this);
        IDE.addHandler(ViewVisibilityChangedEvent.TYPE, this);
        IDE.addHandler(ClosingViewEvent.TYPE, this);
        IDE.addHandler(EditorFocusReceivedEvent.TYPE, this);

        IDE.addHandler(EditorCollapseFoldEvent.TYPE, this);
        IDE.addHandler(EditorExpandFoldEvent.TYPE, this);
        IDE.addHandler(EditorFoldSelectionEvent.TYPE, this);
    }

    public void onApplicationSettingsReceived(ApplicationSettingsReceivedEvent event) {
        applicationSettings = event.getApplicationSettings();

        if (applicationSettings.getValueAsMap("lock-tokens") == null) {
            applicationSettings.setValue("lock-tokens", new LinkedHashMap<String, String>(), Store.COOKIES);
        }
        lockTokens = applicationSettings.getValueAsMap("lock-tokens");

        if (applicationSettings.getValueAsMap("default-editors") == null) {
            applicationSettings.setValue("default-editors", new LinkedHashMap<String, String>(), Store.SERVER);
        }
    }

    /*
     * Editor content changed handler
     */
    public void onEditorContentChanged(EditorContentChangedEvent event) {
        String fileId = null;
        for (Entry<String, EditorView> entry : editorViewList.entrySet()) {
            if (entry.getValue().getEditor().getId().equals(event.getEditor().getId())) {
                fileId = entry.getKey();
            }
        }

        FileModel file = openedFiles.get(fileId);
        //TODO this mey happens in case multiple editors, like HTML, need fix this
        if (file == null) {
            return;
        }
        if (ignoreContentChangedList.contains(file.getId())) {
            ignoreContentChangedList.remove(file.getId());
            return;
        }

        if (!(event.getEditor() instanceof CollabEditor &&
              !MimeType.TEXT_HTML.equals(file.getMimeType()))) {
            file.setContentChanged(true);
            updateTabTitle(file);
        }
        file.setContent(event.getEditor().getText());

        IDE.fireEvent(new EditorFileContentChangedEvent(activeFile,
                                                        event.getEditor().hasUndoChanges(), event.getEditor().hasRedoChanges()));
    }

    public void onEditorFocusReceived(EditorFocusReceivedEvent event) {
        try {
            if (openedFiles.get(activeFile.getId()) != null) {
                editorViewList.get(activeFile.getId()).activate();
            }
        } catch (Exception e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /*
     * (non-Javadoc)
     * @see org.exoplatform.gadgets.devtool.client.editor.event.EditorActiveFileChangedHandler#onEditorChangedActiveFile(org
     * .exoplatform.gadgets.devtool.client.editor.event.EditorActiveFileChangedEvent)
     */
    public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event) {
        if (event.getFile() == null) {
            return;
        }
        if (activeFile == event.getFile()) {
            getEditorFromView(event.getFile().getId()).setFocus();
            return;
        }

        closeFileAfterSaving = false;
        FileModel curentFile = event.getFile();
        if (curentFile == null) {
            activeFile = null;
            return;
        }

        activeFile = curentFile;
        getEditorFromView(curentFile.getId()).setFocus();
        ((View)editorViewList.get(activeFile.getId())).activate();
    }

    private void closeFile(FileModel file) {
        EditorView editorView = editorViewList.get(file.getId());

        editorViewList.remove(file.getId());
        openedFiles.remove(file.getId());
        IDE.getInstance().closeView(editorView.getId());

        file.setContent(null);
        file.setContentChanged(false);

        if (ignoreContentChangedList.contains(file.getId())) {
            ignoreContentChangedList.remove(file.getId());
        }

        if (editorView.getEditor() instanceof CollabEditor && !MimeType.TEXT_HTML.equals(file.getMimeType())) {
            Document document = ((CollabEditor)editorView.getEditor()).getEditor().getDocument();
            CollabEditorExtension.get().getManager().garbageCollectDocument(document);
        }

        IDE.fireEvent(new EditorFileClosedEvent(file, openedFiles));
        if (editorViewList.isEmpty()) {
            IDE.fireEvent(new EditorActiveFileChangedEvent(null, null));
        }
    }

    public void onEditorCloseFile(EditorCloseFileEvent event) {
        if (event.isIgnoreChanges()) {
            closeFileAfterSaving = false;
            closeFile(event.getFile());
            return;
        }
        final FileModel file = event.getFile();

        if (file.isLocked()) {
            if (!lockTokens.containsKey(file.getId())) {
                closeFile(file);
                return;
            }
        }

        // TODO
        if (!file.isContentChanged() /* && !file.isPropertiesChanged() */) {
            closeFile(file);
            return;
        }

        if (!file.isPersisted()) {
            closeFileAfterSaving = true;
            IDE.fireEvent(new SaveFileAsEvent(file, SaveFileAsEvent.SaveDialogType.EXTENDED, new EditorCloseFileEvent(
                    file, true), null));
        } else {
            closeFileAfterSaving = true;
            final String fileName = Utils.unescape(file.getName());
            String message =
                    org.exoplatform.ide.client.IDE.IDE_LOCALIZATION_MESSAGES.editorDoYouWantToSaveFileBeforeClosing(fileName);
            Dialogs.getInstance().ask(CLOSE_FILE, message, new BooleanValueReceivedHandler() {
                public void booleanValueReceived(Boolean value) {
                    if (value == null) {
                        return;
                    }

                    if (value) {
                        file.setContent(getEditorFromView(file.getId()).getText());
                        IDE.fireEvent(new SaveFileEvent());
                    } else {
                        closeFileAfterSaving = false;
                        closeFile(file);
                    }
                }
            });
        }
    }

    protected Editor getEditorFromView(String href) {
        return editorViewList.get(href).getEditor();
    }

    public void onEditorUndoTyping(EditorUndoTypingEvent event) {
        getEditorFromView(activeFile.getId()).undo();
    }

    public void onEditorRedoTyping(EditorRedoTypingEvent event) {
        getEditorFromView(activeFile.getId()).redo();
    }

    private void updateTabTitle(FileModel file) {
        editorViewList.get(file.getId()).setTitle(file, isReadOnly(file));
    }

    private void updateLineNumbers(boolean lineNumbers) {
        Iterator<String> iterator = openedFiles.keySet().iterator();
        while (iterator.hasNext()) {
            String path = iterator.next();
            getEditorFromView(path).showLineNumbers(lineNumbers);
        }
        getEditorFromView(activeFile.getId()).setFocus();
    }

    public void onShowLineNumbers(ShowLineNumbersEvent event) {
        updateLineNumbers(event.isShowLineNumber());
    }

    public void onEditorChangeActiveFile(EditorChangeActiveFileEvent event) {
        if (activeFile == event.getFile()) {
            return;
        }

        activeFile = event.getFile();
        if (activeFile == null) {
            return;
        }

        View view = editorViewList.get(activeFile.getId());
        if (view != null) {
            view.activate();
        }
    }

    /**
     * @see org.exoplatform.ide.client.framework.editor.event.EditorOpenFileHandler#onEditorOpenFile(org.exoplatform.ide.client
     *      .framework.editor.event.EditorOpenFileEvent)
     */
    public void onEditorOpenFile(EditorOpenFileEvent event) {
        FileModel file = event.getFile();
        if (file == null) {
            return;
        }

        if (openedFiles.get(file.getId()) != null) {
            FileModel openedFile = openedFiles.get(file.getId());
            EditorView view = editorViewList.get(openedFile.getId());
            view.setViewVisible();
            if (event.getCursorPosition() != null) {
                view.getEditor().setCursorPosition(event.getCursorPosition().getRow(),
                                                   event.getCursorPosition().getColumn());
            }
            return;
        }

        try {
            Editor[] editors = IDE.getInstance().getFileTypeRegistry().getEditors(file.getMimeType());
            EditorView editorView = new EditorView(file, isReadOnly(file), editors, 0);
            if (!MimeType.APPLICATION_JAVA.equals(file.getMimeType())) {
                ignoreContentChangedList.add(file.getId());
            }
            openedFiles.put(file.getId(), file);
            editorViewList.put(file.getId(), editorView);
            waitForEditorInitialized = true;
            activeFile = file;
            IDE.getInstance().openView(editorView);
            if (event.getCursorPosition() != null) {
                editorView.getEditor().setCursorPosition(event.getCursorPosition().getRow(),
                                                         event.getCursorPosition().getColumn());
            }

            IDE.fireEvent(new EditorFileOpenedEvent(file, editorView.getEditor(), openedFiles));
            
        } catch (EditorNotFoundException e) {
            e.printStackTrace();
            Dialogs.getInstance().showError("Editor for " + file.getMimeType() + " not found!");
        }
    }

    /**
     * Check is file locked
     *
     * @param file
     * @return true if file is locked and client not have lock token, else return false
     */
    private boolean isReadOnly(FileModel file) {

        Set<String> permissions = file.getProject().getPermissions();
        if (permissions != null) {
            return (!(permissions.contains("write") || permissions.contains("all")));
        }

        return false;
    }

    public void onFileSaved(FileSavedEvent event) {
        if (closeFileAfterSaving) {
            FileModel file = event.getFile();
            if (event.getSourceHref() != null) {
                file.setPath(event.getSourceHref());
            }
            if (event.getSourceHref() == null) {
                closeFile(file);
            } else {
                // closing file Saved "As" ...
                FileModel oldFile = new FileModel(event.getFile());
                oldFile.setId(event.getSourceHref());
                closeFile(oldFile);
            }
            closeFileAfterSaving = false;
        } else {
            FileModel savedFile = event.getFile();
            openedFiles.remove(event.getSourceHref());
            openedFiles.put(savedFile.getId(), savedFile);

            EditorView oldFileEditorView = editorViewList.get(savedFile.getId());
            if (oldFileEditorView == null) {
                oldFileEditorView = editorViewList.get(activeFile.getId());
                editorViewList.remove(activeFile.getId());
            } else {
                editorViewList.remove(savedFile.getId());
            }

            oldFileEditorView.setFile(savedFile);
            editorViewList.put(savedFile.getId(), oldFileEditorView);
            updateTabTitle(savedFile);
            oldFileEditorView.setIcon(new Image(ImageUtil.getIcon(savedFile.getMimeType())));
            // call activeFileChanged event after the SaveFileAs operation
            if (!savedFile.getId().equals(activeFile.getId()) && isAfterSaveAs) {
                IDE.fireEvent(new EditorActiveFileChangedEvent(savedFile, oldFileEditorView.getEditor()));
            }
        }

        isAfterSaveAs = false;
    }

    public void onEditorReplaceFile(EditorReplaceFileEvent event) {
        FileModel oldFile = event.getFile();
        FileModel newFile = event.getNewFile();

        if (newFile == null) {
            updateTabTitle(oldFile);
            return;
        }

        ignoreContentChangedList.remove(oldFile.getId());
        ignoreContentChangedList.add(newFile.getId());

        openedFiles.remove(oldFile.getId());
        openedFiles.put(newFile.getId(), newFile);

        EditorView editorView = editorViewList.get(oldFile.getId());
        editorViewList.remove(oldFile.getId());
        editorViewList.put(newFile.getId(), editorView);

        editorView.setFile(newFile);
        editorView.setIcon(new Image(ImageUtil.getIcon(newFile)));
        updateTabTitle(newFile);

        if (activeFile != null && activeFile.getId().equals(oldFile.getId())) {
            IDE.fireEvent(new EditorActiveFileChangedEvent(newFile, editorView.getEditor()));
        }
        //IDE.fireEvent(new EditorActiveFileChangedEvent(newFile, oldFileEditorView.getEditor()));

        if (event.isUpdateContent() && newFile.getContent() != null) {
            //oldFileEditorView.getEditor().setText(newFile.getContent());
            editorView.getEditor().getDocument().set(newFile.getContent());
        }
    }

    /**
     * @see org.exoplatform.ide.client.event.edit.EditorDeleteCurrentLineHandler#onEditorDeleteCurrentLine(org.exoplatform.ide.client
     *      .event.edit.EditorDeleteCurrentLineEvent)
     */
    public void onEditorDeleteCurrentLine(EditorDeleteCurrentLineEvent event) {
        getEditorFromView(activeFile.getId()).deleteCurrentLine();
    }

    /**
     * @see org.exoplatform.ide.client.editor.event.EditorGoToLineHandler#onEditorGoToLine(org.exoplatform.ide.client.editor.event
     *      .EditorGoToLineEvent)
     */
    public void onEditorGoToLine(EditorGoToLineEvent event) {
        EditorView activeEditorView = editorViewList.get(activeFile.getId());
        activeEditorView.getEditor().setCursorPosition(event.getLineNumber(), event.getColumnNumber());
    }

    //   /**
    //    * @see org.exoplatform.ide.client.editor.event.EditorFindTextHandler#onEditorFindText(org.exoplatform.ide.client.editor.event
    // .EditorFindTextEvent)
    //    */
    //   public void onEditorFindText(EditorFindTextEvent event)
    //   {
    //      boolean isFound =
    //         getEditorFromView(event.getFileId()).findAndSelect(event.getFindText(), event.isCaseSensitive());
    //      IDE.fireEvent(new EditorTextFoundEvent(isFound));
    //   }

    //   /**
    //    * @see org.exoplatform.ide.client.editor.event.EditorReplaceTextHandler#onEditorReplaceText(org.exoplatform.ide.client.editor
    // .event.EditorReplaceTextEvent)
    //    */
    //   public void onEditorReplaceText(EditorReplaceTextEvent event)
    //   {
    //      Editor editor = getEditorFromView(event.getFileId());
    //      if (event.isReplaceAll())
    //      {
    //         while (editor.findAndSelect(event.getFindText(), event.isCaseSensitive()))
    //         {
    //            editor.replaceFoundedText(event.getFindText(), event.getReplaceText(), event.isCaseSensitive());
    //         }
    //         // display.replaceAllText(event.getFindText(), event.getReplaceText(), event.isCaseSensitive(),
    //         // event.getPath());
    //      }
    //      else
    //      {
    //         editor.replaceFoundedText(event.getFindText(), event.getReplaceText(), event.isCaseSensitive());
    //         // display.replaceText(event.getFindText(), event.getReplaceText(), event.isCaseSensitive(), event.getPath());
    //      }
    //   }

    //   /**
    //    * @see org.exoplatform.ide.client.framework.editor.event.EditorReplaceAndFindTextHandler#onEditorReplaceAndFindText(org
    // .exoplatform.ide.client.framework.editor.event.EditorReplaceAndFindTextEvent)
    //    */
    //   public void onEditorReplaceAndFindText(EditorReplaceAndFindTextEvent event)
    //   {
    //      Editor editor = getEditorFromView(event.getFileId());
    //      editor.replaceFoundedText(event.getFindText(), event.getReplaceText(), event.isCaseSensitive());
    //      boolean isFound = editor.findAndSelect(event.getFindText(), event.isCaseSensitive());
    //      IDE.fireEvent(new EditorTextFoundEvent(isFound));
    //   }

    /**
     * @see org.exoplatform.ide.client.editor.event.EditorSetFocusHandler#onEditorSetFocus(org.exoplatform.ide.client.editor.event
     *      .EditorSetFocusEvent)
     */
    public void onEditorSetFocus(EditorSetFocusEvent event) {
        getEditorFromView(activeFile.getId()).setFocus();
    }

    // public void onRefreshHotKeys(RefreshHotKeysEvent event)
    // {
    // Map<String, String> hotKeys = applicationSettings.getValueAsMap("hotkeys");
    // List<String> hotKeyList = new ArrayList<String>(hotKeys.keySet());
    // Iterator<String> it = openedFiles.keySet().iterator();
    // while (it.hasNext())
    // {
    // String file = it.next();
    // getEditorFromView(file).setHotKeyList(hotKeyList);
    // }
    // }

    /**
     * @see org.exoplatform.ide.client.framework.event.SaveFileAsHandler#onSaveFileAs(org.exoplatform.ide.client.framework.event
     *      .SaveFileAsEvent)
     */
    public void onSaveFileAs(SaveFileAsEvent event) {
        if (event.getDialogType().equals(SaveFileAsEvent.SaveDialogType.YES_CANCEL)) {
            closeFileAfterSaving = false;
        }

        isAfterSaveAs = true;
    }

    /**
     * @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api
     *      .event.ViewClosedEvent)
     */
    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (event.getView().getType().equals("editor")) {
            if (!(event.getView() instanceof EditorView)) {
                return;
            }
            IDE.fireEvent(new EditorCloseFileEvent(((EditorView)event.getView()).getFile()));
        }
    }

    /**
     * @see org.exoplatform.ide.client.framework.ui.api.event.ViewVisibilityChangedHandler#onViewVisibilityChanged(org.exoplatform.ide
     *      .client.framework.ui.api.event.ViewVisibilityChangedEvent)
     */
    @Override
    public void onViewVisibilityChanged(final ViewVisibilityChangedEvent event) {
        if (event.getView().getType().equals("editor") && event.getView().isViewVisible()) {
            if (!(event.getView() instanceof EditorView)) {
                IDE.fireEvent(new EditorActiveFileChangedEvent(null, null));
                return;
            }

            final EditorView editorView = (EditorView)event.getView();
            if (editorView == null) {
                activeFile = null;
                return;
            }

            activeFile = editorView.getFile();

            Timer timer = new Timer() {
                @Override
                public void run() {
                    try {
                        if (editorView.getEditor() == null) {
                            return;
                        }
                        IDE.fireEvent(new EditorActiveFileChangedEvent(activeFile, editorView.getEditor()));
                    } catch (Exception e) {
                    }
                }
            };

            if (waitForEditorInitialized) {
                waitForEditorInitialized = false;
                timer.schedule(1000);
            } else {
                timer.run();
            }

        }
    }

    /**
     * @see org.exoplatform.ide.client.framework.ui.api.event.ClosingViewHandler#onClosingView(org.exoplatform.ide.client.framework.ui
     *      .api.event.ClosingViewEvent)
     */
    @Override
    public void onClosingView(ClosingViewEvent event) {
        if (event.getView().getType().equals("editor")) {
            if (!(event.getView() instanceof EditorView)) {
                return;
            }
            event.cancelClosing();
            IDE.fireEvent(new EditorCloseFileEvent(((EditorView)event.getView()).getFile()));
        }

    }

    // /**
    // * Read applicationSettings and return true if there are more than one editors for file with mimeType.
    // *
    // * @param mimeType
    // * @return
    // * @throws EditorNotFoundException
    // */
    // private List<Editor> getSupportedEditorsForMimeType(String mimeType)
    // {
    // try
    // {
    // Editor []editors = IDE.getInstance().getEditors(mimeType);
    // List<Editor> editorList = new ArrayList<Editor>();
    // for (Edi) {
    // }
    // editorList.addAll(editors);
    // return new ArrayList<Editor>();
    // return EditorFactory.getEditors(mimeType);
    // }
    // catch (EditorNotFoundException e)
    // {
    // IDE.fireEvent(new ExceptionThrownEvent(e));
    // }
    //
    // return null;
    // }

    // private List<Editor> getSupportedEditors(FileModel file)
    // {
    // boolean isLineNumbers = true;
    // if (applicationSettings.getValueAsBoolean("line-numbers") != null)
    // {
    // isLineNumbers = applicationSettings.getValueAsBoolean("line-numbers");
    // }
    //
    // // create editors for source/design view
    // List<Editor> supportedEditors = new ArrayList<Editor>();
    //
    // List<EditorProducer> supportedEditorProducers = getSupportedEditorProducers(file.getMimeType());
    // for (EditorProducer supportedEditorProducer : supportedEditorProducers)
    // {
    // // HashMap<String, Object> params = new HashMap<String, Object>();
    // // params.put(EditorParameters.IS_READ_ONLY, isReadOnly(file));
    // // params.put(EditorParameters.IS_SHOW_LINE_NUMER, isLineNumbers);
    // Editor editor = supportedEditorProducer.createEditor(file.getContent(), IDE.eventBus(), params);
    // supportedEditors.add(editor);
    // DOM.setStyleAttribute(editor.getElement(), "zIndex", "0");
    // }
    //
    // return supportedEditors;
    // }

    // /**
    // * Return number from 1 of editor created by editorProducer to show in view among the supported editor producer for some mime
    // * type.
    // *
    // * @param editorProducer
    // * @return
    // */
    // private int getNumberOfEditorToShow(Editor editor)
    // {
    // try
    // {
    // // create editors for source/design view
    // //List<Editor> supportedEditors = getSupportedEditorProducers(editor.getMimeType());
    // Editor[] editors = IDE.getInstance().getEditors(editor.getMimeType());
    //
    // int i = 1;
    // for (Editor e : editors)
    // {
    // if (editor.getDescription().equals(e.getDescription()))
    // {
    // return i;
    // }
    // i++;
    // }
    //
    // }
    // catch (EditorNotFoundException e)
    // {
    // IDE.fireEvent(new ExceptionThrownEvent(e));
    // }
    //
    // // for (EditorProducer supportedEditorProducer : supportedEditorProducers)
    // // {
    // // if (editorProducer.getDescription().equals(supportedEditorProducer.getDescription()))
    // // {
    // // return i;
    // // }
    // // i++;
    // // }
    //
    // return 1;
    // }

    /**
     * @see org.exoplatform.ide.editor.client.api.event.EditorContextMenuHandler#onEditorContextMenu(org.exoplatform.ide.editor.client
     *      .api.event.EditorContextMenuEvent)
     */
    @Override
    public void onEditorContextMenu(EditorContextMenuEvent event) {
        // TODO rememeber selected text in editor, when context menu was called:
        selectionBeforeContextMenu = getEditorFromView(activeFile.getId()).getSelectionRange();
        IDE.fireEvent(new ShowContextMenuEvent(event.getX(), event.getY(), getEditorFromView(activeFile.getId())));
    }

    /**
     * @see org.exoplatform.ide.client.framework.editor.event.EditorSelectAllHandler#onEditorSelectAll(org.exoplatform.ide.client
     *      .framework.editor.event.EditorSelectAllEvent)
     */
    @Override
    public void onEditorSelectAll(EditorSelectAllEvent event) {
        getEditorFromView(activeFile.getId()).selectAll();
    }

    /**
     * @see org.exoplatform.ide.client.framework.editor.event.EditorDeleteTextHandler#onEditorDeleteText(org.exoplatform.ide.client
     *      .framework.editor.event.EditorDeleteTextEvent)
     */
    @Override
    public void onEditorDeleteText(EditorDeleteTextEvent event) {
        // TODO fixes the deselection problem, when context menu is called in editor:
        if (selectionBeforeContextMenu != null) {
            getEditorFromView(activeFile.getId()).selectRange(selectionBeforeContextMenu.getStartLine(),
                                                              selectionBeforeContextMenu.getStartSymbol(),
                                                              selectionBeforeContextMenu.getEndLine(),
                                                              selectionBeforeContextMenu.getEndSymbol());
            selectionBeforeContextMenu = null;
        }
        getEditorFromView(activeFile.getId()).delete();
    }

    /**
     * @see org.exoplatform.ide.client.framework.editor.event.EditorPasteTextHandler#onEditorPasteText(org.exoplatform.ide.client
     *      .framework.editor.event.EditorPasteTextEvent)
     */
    @Override
    public void onEditorPasteText(EditorPasteTextEvent event) {
        getEditorFromView(activeFile.getId()).paste();
    }

    /**
     * @see org.exoplatform.ide.client.framework.editor.event.EditorCopyTextHandler#onEditorCopyText(org.exoplatform.ide.client.framework
     *      .editor.event.EditorCopyTextEvent)
     */
    @Override
    public void onEditorCopyText(EditorCopyTextEvent event) {
        getEditorFromView(activeFile.getId()).copy();
    }

    /**
     * @see org.exoplatform.ide.client.framework.editor.event.EditorCutTextHandler#onEditorCutText(org.exoplatform.ide.client.framework
     *      .editor.event.EditorCutTextEvent)
     */
    @Override
    public void onEditorCutText(EditorCutTextEvent event) {
        getEditorFromView(activeFile.getId()).cut();
    }

    /**
     * @see org.exoplatform.ide.client.framework.editor.event.EditorCollapseFoldHandler#onEditorCollapse(org.exoplatform.ide.client
     *      .framework.editor.event.EditorCollapseFoldEvent)
     */
    @Override
    public void onEditorCollapse(EditorCollapseFoldEvent event) {
        if (event.isCollapseAll()) {
            getEditorFromView(activeFile.getId()).collapseAll();
        } else {
            getEditorFromView(activeFile.getId()).collapse();
        }
    }

    /** @see org.exoplatform.ide.client.framework.editor.event.EditorExpandFoldHandler#onEditorExpand(org.exoplatform.ide.client
     * .framework.editor.event.EditorExpandFoldEvent) */
    @Override
    public void onEditorExpand(EditorExpandFoldEvent event) {
        if (event.isExpandAll()) {
            getEditorFromView(activeFile.getId()).expandAll();
        } else {
            getEditorFromView(activeFile.getId()).expand();
        }
    }

    /** @see org.exoplatform.ide.client.framework.editor.event.EditorFoldSelectionHandler#onEditorFoldSelection(org.exoplatform.ide
     * .client.framework.editor.event.EditorFoldSelectionEvent) */
    @Override
    public void onEditorFoldSelection(EditorFoldSelectionEvent event) {
        getEditorFromView(activeFile.getId()).foldSelection();
    }

}
