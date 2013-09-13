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

package org.exoplatform.ide.client.edit.switching;

import org.exoplatform.ide.client.framework.editor.event.*;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.vfs.client.model.FileModel;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class SwitchingEditorCommandHandler implements GoNextEditorHandler, GoPreviousEditorHandler,
                                                      EditorFileOpenedHandler, EditorFileClosedHandler, EditorActiveFileChangedHandler {

    private Map<String, FileModel> openedFiles = new HashMap<String, FileModel>();

    private FileModel activeFile;

    public SwitchingEditorCommandHandler() {
        IDE.getInstance().addControl(new GoNextEditorControl());
        IDE.getInstance().addControl(new GoPreviousEditorControl());

        IDE.addHandler(GoNextEditorEvent.TYPE, this);
        IDE.addHandler(GoPreviousEditorEvent.TYPE, this);
        IDE.addHandler(EditorFileOpenedEvent.TYPE, this);
        IDE.addHandler(EditorFileClosedEvent.TYPE, this);
        IDE.addHandler(EditorActiveFileChangedEvent.TYPE, this);
    }

    @Override
    public void onGoPreviousEditor(GoPreviousEditorEvent event) {
        if (activeFile == null && openedFiles.size() < 2) {
            return;
        }

        String[] keys = openedFiles.keySet().toArray(new String[openedFiles.size()]);
        for (int i = 0; i < keys.length; i++) {
            if (activeFile.getId().equals(keys[i]) && i > 0) {
                String prevFileId = keys[i - 1];
                FileModel file = openedFiles.get(prevFileId);
                IDE.fireEvent(new EditorChangeActiveFileEvent(file));
                return;
            }
        }

    }

    @Override
    public void onGoNextEditor(GoNextEditorEvent event) {
        if (activeFile == null || openedFiles.size() < 2) {
            return;
        }

        String[] keys = openedFiles.keySet().toArray(new String[openedFiles.size()]);
        for (int i = 0; i < keys.length; i++) {
            if (activeFile.getId().equals(keys[i]) && i < keys.length - 1) {
                String nextFileId = keys[i + 1];
                FileModel file = openedFiles.get(nextFileId);
                IDE.fireEvent(new EditorChangeActiveFileEvent(file));
                return;
            }
        }
    }

    @Override
    public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event) {
        activeFile = event.getFile();
    }

    @Override
    public void onEditorFileClosed(EditorFileClosedEvent event) {
        openedFiles = event.getOpenedFiles();
    }

    @Override
    public void onEditorFileOpened(EditorFileOpenedEvent event) {
        openedFiles = event.getOpenedFiles();
    }

}
