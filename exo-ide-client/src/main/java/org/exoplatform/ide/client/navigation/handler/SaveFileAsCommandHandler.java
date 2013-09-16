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

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.dialogs.ValueCallback;
import org.exoplatform.ide.client.dialogs.ValueDiscardCallback;
import org.exoplatform.ide.client.framework.control.Docking;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.event.FileSavedEvent;
import org.exoplatform.ide.client.framework.event.RefreshBrowserEvent;
import org.exoplatform.ide.client.framework.event.SaveFileAsEvent;
import org.exoplatform.ide.client.framework.event.SaveFileAsHandler;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings.Store;
import org.exoplatform.ide.client.framework.settings.ApplicationSettingsReceivedEvent;
import org.exoplatform.ide.client.framework.settings.ApplicationSettingsReceivedHandler;
import org.exoplatform.ide.client.navigation.control.SaveFileAsControl;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.marshal.FileUnmarshaller;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.client.model.ItemContext;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Folder;
import org.exoplatform.ide.vfs.shared.Item;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class SaveFileAsCommandHandler implements SaveFileAsHandler, ItemsSelectedHandler,
                                                 EditorActiveFileChangedHandler, ApplicationSettingsReceivedHandler {

    private String sourceId;

    private List<Item> selectedItems = new ArrayList<Item>();

    private FileModel activeFile;

    private static final String PREFIX = IDE.NAVIGATION_CONSTANT.saveFileAsNewFileNamePrefix();

    private static final String SAVE_AS_DIALOG_TITLE = IDE.NAVIGATION_CONSTANT.saveFileAsDialogTitle();

    private static final String SAVE_AS_DIALOG_ENTER_NEW_NAME = IDE.NAVIGATION_CONSTANT.saveFileAsDialogEnterNewName();

    private static final String SAVE_AS_DIALOG_DO_YOU_WANT_TO_SAVE = IDE.NAVIGATION_CONSTANT
                                                                        .saveFileAsDialogDoYouWantToSave();

    /** Event to be fired after pressing No button in ask dialog. */
    private GwtEvent<?> eventFiredOnNoButtonPressed;

    /** Event to be fired after pressing Cancel button in ask dialog. */
    private GwtEvent<?> eventFiredOnCancelButtonPressed;

    /** File to be saved. */
    private FileModel fileToSave;

    public SaveFileAsCommandHandler() {
        IDE.getInstance().addControl(new SaveFileAsControl(), Docking.TOOLBAR);

        IDE.addHandler(SaveFileAsEvent.TYPE, this);
        IDE.addHandler(ItemsSelectedEvent.TYPE, this);
        IDE.addHandler(EditorActiveFileChangedEvent.TYPE, this);
        IDE.addHandler(ApplicationSettingsReceivedEvent.TYPE, this);
    }

    /** Add handlers Open Save As Dialog */
    public void onSaveFileAs(SaveFileAsEvent event) {
        if (selectedItems == null || selectedItems.size() == 0) {
            Dialogs.getInstance().showInfo(IDE.ERRORS_CONSTANT.saveFileAsTargetNotSelected());
            return;
        }

        FileModel file = event.getFile() != null ? event.getFile() : activeFile;

        eventFiredOnCancelButtonPressed = event.getEventFiredOnCancel();

        eventFiredOnNoButtonPressed = event.getEventFiredOnNo();

        fileToSave = file;

        sourceId = file.getId();

        askForNewFileName(event.getDialogType());
    }

    /** Open Save As Dialog */
    private void askForNewFileName(SaveFileAsEvent.SaveDialogType type) {
        final String newFileName = !fileToSave.isPersisted() ? fileToSave.getName() : PREFIX + " " + fileToSave.getName();
        // sourceHref = fileToSave.getId();

        if (type.equals(SaveFileAsEvent.SaveDialogType.YES_CANCEL)) {
            org.exoplatform.ide.client.dialogs.AskForValueDialog.getInstance().ask(SAVE_AS_DIALOG_TITLE,
                                                                                   SAVE_AS_DIALOG_ENTER_NEW_NAME, newFileName, 400, true,
                                                                                   fileNameEnteredCallback);
        } else {
            org.exoplatform.ide.client.dialogs.AskForValueDialog.getInstance().ask(SAVE_AS_DIALOG_TITLE,
                                                                                   SAVE_AS_DIALOG_DO_YOU_WANT_TO_SAVE, newFileName, 400,
                                                                                   true, fileNameEnteredCallback, noButtonSelectedCallback);
        }
    }

    private ValueCallback fileNameEnteredCallback = new ValueCallback() {
        @Override
        public void execute(String value) {
            if (value == null) {
                if (eventFiredOnCancelButtonPressed != null) {
                    IDE.fireEvent(eventFiredOnCancelButtonPressed);
                }

                return;
            }

            saveFileAs(fileToSave, value);
        }
    };

    ValueDiscardCallback noButtonSelectedCallback = new ValueDiscardCallback() {
        @Override
        public void discard() {
            if (eventFiredOnNoButtonPressed != null) {
                IDE.fireEvent(eventFiredOnNoButtonPressed);
            }
        }
    };

    private void saveFileAs(FileModel file, String name) {
        final Folder folderToSave =
                (selectedItems.get(0) instanceof FileModel) ? ((FileModel)selectedItems.get(0)).getParent()
                                                            : (Folder)selectedItems.get(0);
        FileModel newFile = new FileModel(name, file.getMimeType(), file.getContent(), new FolderModel(folderToSave));
        final ProjectModel project = ((ItemContext)selectedItems.get(0)).getProject();
        if (file.isPersisted()) {
            newFile.getProperties().addAll(file.getProperties());
        }

        try {
            VirtualFileSystem.getInstance().createFile(folderToSave,
                                                       new AsyncRequestCallback<FileModel>(new FileUnmarshaller(newFile)) {
                                                           @Override
                                                           protected void onSuccess(FileModel result) {
                                                               result.setProject(project);
                                                               IDE.fireEvent(new FileSavedEvent(result, sourceId));
                                                               IDE.fireEvent(new RefreshBrowserEvent(folderToSave));
                                                           }

                                                           @Override
                                                           protected void onFailure(Throwable exception) {
                                                               IDE.fireEvent(new ExceptionThrownEvent(exception));
                                                           }
                                                       });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    public void onItemsSelected(ItemsSelectedEvent event) {
        selectedItems = event.getSelectedItems();
    }

    public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event) {
        activeFile = event.getFile();
    }

    public void onApplicationSettingsReceived(ApplicationSettingsReceivedEvent event) {
        if (event.getApplicationSettings().getValueAsMap("lock-tokens") == null) {
            event.getApplicationSettings().setValue("lock-tokens", new LinkedHashMap<String, String>(), Store.COOKIES);
        }
    }

}
