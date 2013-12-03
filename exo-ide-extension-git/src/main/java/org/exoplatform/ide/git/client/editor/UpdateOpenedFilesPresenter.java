package org.exoplatform.ide.git.client.editor;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.editor.event.EditorCloseFileEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedHandler;
import org.exoplatform.ide.client.framework.event.FileSavedEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.editor.client.api.Editor;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.marshal.FileContentUnmarshaller;
import org.exoplatform.ide.vfs.client.model.FileModel;

import java.util.HashMap;
import java.util.Map;

/**
 * Presenter that brings all opened files in editor and tries to check state for every file. And if content of file is changed presenter
 * will update current editor.
 */
public class UpdateOpenedFilesPresenter implements UpdateOpenedFilesHandler, EditorFileOpenedHandler, EditorFileClosedHandler {

    private Map<String, FileModel> openedFiles = new HashMap<String, FileModel>();

    private Map<String, Editor> openedEditors = new HashMap<String, Editor>();


    public UpdateOpenedFilesPresenter() {
        IDE.addHandler(UpdateOpenedFilesEvent.TYPE, this);
        IDE.addHandler(EditorFileOpenedEvent.TYPE, this);
        IDE.addHandler(EditorFileClosedEvent.TYPE, this);
    }

    @Override
    public void onEditorFileClosed(EditorFileClosedEvent event) {
        this.openedFiles = event.getOpenedFiles();
        this.openedEditors.remove(event.getFile().getId());
    }

    @Override
    public void onEditorFileOpened(EditorFileOpenedEvent event) {
        this.openedFiles = event.getOpenedFiles();
        this.openedEditors.put(event.getFile().getId(), event.getEditor());
    }

    @Override
    public void onUpdateOpenedFiles(UpdateOpenedFilesEvent event) {
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                for (final Map.Entry<String, FileModel> openedFile : openedFiles.entrySet()) {
                    updateContent(openedFile.getValue());
                }
            }
        });
    }

    private void updateContent(final FileModel resource) {
        final Editor editor = openedEditors.get(resource.getId());

        try {
            VirtualFileSystem.getInstance().getContent(
                    new AsyncRequestCallback<FileModel>(new FileContentUnmarshaller(new FileModel(resource))) {
                        @Override
                        protected void onSuccess(final FileModel result) {
                            if (resource.getContent() == null || !resource.getContent().equals(result.getContent())) {

                                final int cursorColumn = editor.getCursorColumn();
                                final int cursorRow = editor.getCursorRow();
                                editor.getDocument().set(result.getContent());

                                Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                                    @Override
                                    public void execute() {
                                        editor.setCursorPosition(cursorRow, cursorColumn);
                                        resource.setContentChanged(false);
                                        IDE.fireEvent(new FileSavedEvent(resource, null));
                                    }
                                });
                            }
                        }

                        @Override
                        protected void onFailure(Throwable e) {
                            if (e.getMessage().trim().endsWith("does not exists.")) {
                                IDE.fireEvent(new EditorCloseFileEvent(resource));
                            } else {
                                Dialogs.getInstance().showError(e.getLocalizedMessage());
                            }
                        }
                    });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }
}
