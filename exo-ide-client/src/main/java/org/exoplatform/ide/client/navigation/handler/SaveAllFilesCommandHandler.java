/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package org.exoplatform.ide.client.navigation.handler;

import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedHandler;
import org.exoplatform.ide.client.framework.event.AllFilesSavedEvent;
import org.exoplatform.ide.client.framework.event.FileSavedEvent;
import org.exoplatform.ide.client.framework.event.SaveAllFilesEvent;
import org.exoplatform.ide.client.framework.event.SaveAllFilesHandler;
import org.exoplatform.ide.client.navigation.control.SaveAllFilesControl;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.model.FileModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class SaveAllFilesCommandHandler implements SaveAllFilesHandler, EditorFileOpenedHandler,
                                                   EditorFileClosedHandler {

    private Map<String, FileModel> openedFiles = new HashMap<String, FileModel>();

    public SaveAllFilesCommandHandler() {
        IDE.getInstance().addControl(new SaveAllFilesControl());

        IDE.addHandler(SaveAllFilesEvent.TYPE, this);
        IDE.addHandler(EditorFileOpenedEvent.TYPE, this);
        IDE.addHandler(EditorFileClosedEvent.TYPE, this);
    }

    private List<FileModel> savedFiles = new ArrayList<FileModel>();

    public void onSaveAllFiles(SaveAllFilesEvent event) {
        savedFiles.clear();
        saveNextFile();
    }

    private void saveNextFile() {
        final FileModel fileToSave = getUnsavedFile();
        if (fileToSave == null) {
            IDE.fireEvent(new AllFilesSavedEvent());
            return;
        }

        try {
            VirtualFileSystem.getInstance().updateContent(fileToSave, new AsyncRequestCallback<FileModel>() {
                @Override
                protected void onSuccess(FileModel result) {
                    savedFiles.add(fileToSave);
                    fileToSave.setContentChanged(false);
                    IDE.fireEvent(new FileSavedEvent(fileToSave, null));
                    saveNextFile();
                }

                @Override
                protected void onFailure(Throwable exception) {
                    IDE.fireEvent(new ExceptionThrownEvent(exception, "Service is not deployed.<br>Resource not found."));
                }
            });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e, "Service is not deployed.<br>Resource not found."));
        }

    }

    private FileModel getUnsavedFile() {
        for (FileModel file : openedFiles.values()) {
            if (file.isContentChanged() && file.isPersisted()) {
                return file;
            }
        }

        return null;
    }

    public void onEditorFileOpened(EditorFileOpenedEvent event) {
        openedFiles = event.getOpenedFiles();
    }

    public void onEditorFileClosed(EditorFileClosedEvent event) {
        openedFiles = event.getOpenedFiles();
    }

}
