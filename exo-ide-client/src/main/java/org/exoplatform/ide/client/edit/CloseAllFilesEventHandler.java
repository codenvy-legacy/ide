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

package org.exoplatform.ide.client.edit;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;

import org.exoplatform.gwtframework.ui.client.dialog.BooleanValueReceivedHandler;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.application.event.ApplicationClosingEvent;
import org.exoplatform.ide.client.framework.application.event.ApplicationClosingHandler;
import org.exoplatform.ide.client.framework.editor.event.*;
import org.exoplatform.ide.client.framework.event.*;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.util.Utils;
import org.exoplatform.ide.vfs.client.model.FileModel;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class CloseAllFilesEventHandler implements CloseAllFilesHandler, EditorFileOpenedHandler,
                                                  EditorFileClosedHandler, FileSavedHandler, ApplicationClosingHandler {

    private static final String ASK_DIALOG_TITLE = org.exoplatform.ide.client.IDE.PREFERENCES_CONSTANT
                                                                                 .workspaceCloseAllFilesDialogTitle();

    private static final String ASK_DIALOG_TEXT = org.exoplatform.ide.client.IDE.PREFERENCES_CONSTANT
                                                                                .workspaceCloseAllFilesDialogText();

    /** The message to display to user if he has unsaved files, that may be lost after refresh or close page. */
    private static final String UNSAVED_FILES_MAY_BE_LOST = org.exoplatform.ide.client.IDE.PREFERENCES_CONSTANT
                                                                                          .unsavedFilesMayBeLost();

    private Map<String, FileModel> openedFiles = new HashMap<String, FileModel>();

    private FileModel fileToClose;

    public CloseAllFilesEventHandler() {
        IDE.addHandler(CloseAllFilesEvent.TYPE, this);
        IDE.addHandler(EditorFileOpenedEvent.TYPE, this);
        IDE.addHandler(EditorFileClosedEvent.TYPE, this);
        IDE.addHandler(FileSavedEvent.TYPE, this);
        IDE.addHandler(ApplicationClosingEvent.TYPE, this);
    }

    @Override
    public void onCloseAllFiles(CloseAllFilesEvent event) {
        if (openedFiles.size() == 0) {
            Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                @Override
                public void execute() {
                    IDE.fireEvent(new AllFilesClosedEvent());
                }
            });

            return;
        }

        Dialogs.getInstance().ask(ASK_DIALOG_TITLE, ASK_DIALOG_TEXT, new BooleanValueReceivedHandler() {
            public void booleanValueReceived(Boolean value) {
                if (value == null) {
                    return;
                }
                if (value) {
                    closeNextFile();
                }
            }
        });
    }

    /** Closing opened files. */
    private void closeNextFile() {
        fileToClose = null;

        if (openedFiles.size() == 0) {
            IDE.fireEvent(new AllFilesClosedEvent());
            return;
        }

        String href = openedFiles.keySet().iterator().next();
        final FileModel file = openedFiles.get(href);

        if (file.isContentChanged()) {
            final String fileName = Utils.unescape(file.getName());
            final String message =
                    org.exoplatform.ide.client.IDE.IDE_LOCALIZATION_MESSAGES.selectWorkspaceAskSaveFileBeforeClosing(fileName);
            final String title =
                    org.exoplatform.ide.client.IDE.PREFERENCES_CONSTANT.selectWorkspaceAskSaveFileBeforeClosingDialogTitle();

            Dialogs.getInstance().ask(title, message, new BooleanValueReceivedHandler() {
                public void booleanValueReceived(Boolean value) {
                    if (value == null) {
                        return;
                    }

                    if (value) {
                        if (!file.isPersisted()) {
                            fileToClose = file;
                            IDE.fireEvent(new SaveFileAsEvent(file, SaveFileAsEvent.SaveDialogType.YES_CANCEL, null, null));
                        } else {
                            fileToClose = file;
                            IDE.fireEvent(new SaveFileEvent(file));
                        }
                    } else {
                        IDE.fireEvent(new EditorCloseFileEvent(file, true));
                        closeNextFile();
                    }
                }

            });
            return;
        } else {
            IDE.fireEvent(new EditorCloseFileEvent(file, true));
            closeNextFile();
        }
    }

    @Override
    public void onEditorFileOpened(EditorFileOpenedEvent event) {
        openedFiles = event.getOpenedFiles();
    }

    @Override
    public void onEditorFileClosed(EditorFileClosedEvent event) {
        openedFiles = event.getOpenedFiles();
    }

    @Override
    public void onFileSaved(FileSavedEvent event) {
        if (fileToClose != null) {
            EditorCloseFileEvent e = new EditorCloseFileEvent(fileToClose, true);
            fileToClose = null;
            IDE.fireEvent(e);
            closeNextFile();
        }
    }

    /**
     * @see org.exoplatform.ide.client.framework.application.event.ApplicationClosingHandler#onApplicationClosing(
     *org.exoplatform.ide.client.framework.application.event.ApplicationClosingEvent)
     */
    @Override
    public void onApplicationClosing(ApplicationClosingEvent event) {
        for (FileModel file : openedFiles.values()) {
            if (file.isContentChanged()) {
                event.setMessage(UNSAVED_FILES_MAY_BE_LOST);
                break;
            }
        }
    }

}
