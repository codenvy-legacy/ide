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
package org.exoplatform.ide.client.navigation.control;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.control.GroupNames;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.editor.event.*;
import org.exoplatform.ide.client.framework.event.FileSavedEvent;
import org.exoplatform.ide.client.framework.event.FileSavedHandler;
import org.exoplatform.ide.client.framework.event.SaveAllFilesEvent;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */
@RolesAllowed({"developer"})
public class SaveAllFilesControl extends SimpleControl implements IDEControl, EditorFileContentChangedHandler,
                                                                  EditorActiveFileChangedHandler, EditorFileOpenedHandler,
                                                                  EditorFileClosedHandler, VfsChangedHandler,
                                                                  FileSavedHandler {

    public static final String ID = "File/Save All";

    public static final String TITLE = IDE.IDE_LOCALIZATION_CONSTANT.saveAllControl();

    private Map<String, FileModel> openedFiles = new LinkedHashMap<String, FileModel>();

    /** Current workspace's href. */
    private VirtualFileSystemInfo vfsInfo;

    /**
     *
     */
    public SaveAllFilesControl() {
        super(ID);
        setTitle(TITLE);
        setPrompt(TITLE);
        setImages(IDEImageBundle.INSTANCE.saveAll(), IDEImageBundle.INSTANCE.saveAllDisabled());
        setEvent(new SaveAllFilesEvent());
        setGroupName(GroupNames.SAVE);
    }

    /** @see org.exoplatform.ide.client.framework.control.IDEControl#initialize() */
    @Override
    public void initialize() {
        IDE.addHandler(EditorFileContentChangedEvent.TYPE, this);
        IDE.addHandler(FileSavedEvent.TYPE, this);
        IDE.addHandler(EditorActiveFileChangedEvent.TYPE, this);
        IDE.addHandler(EditorFileClosedEvent.TYPE, this);
        IDE.addHandler(EditorFileOpenedEvent.TYPE, this);
        IDE.addHandler(VfsChangedEvent.TYPE, this);
    }

    /** Update control's state. */
    private void updateState() {
        if (vfsInfo == null) {
            setVisible(false);
            return;
        }
        setVisible(true);

        boolean enable = false;
        for (FileModel file : openedFiles.values()) {
            if (file.isPersisted() && file.isContentChanged()) {
                enable = true;
                break;
            }
        }
        setEnabled(enable);
    }

    public void onEditorFileContentChanged(EditorFileContentChangedEvent event) {
        updateState();
    }

    @Override
    public void onFileSaved(FileSavedEvent event) {
        updateState();
    }

    public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event) {
        updateState();
    }

    public void onEditorFileOpened(EditorFileOpenedEvent event) {
        openedFiles = event.getOpenedFiles();
    }

    public void onEditorFileClosed(EditorFileClosedEvent event) {
        openedFiles = event.getOpenedFiles();
    }

    /**
     * @see org.exoplatform.ide.client.framework.application.event.VfsChangedHandler#onVfsChanged(org.exoplatform.ide.client.framework
     *      .application.event.VfsChangedEvent)
     */
    @Override
    public void onVfsChanged(VfsChangedEvent event) {
        vfsInfo = event.getVfsInfo();
        updateState();
    }

}
