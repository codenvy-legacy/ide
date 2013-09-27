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
package org.exoplatform.ide.client.operation.createfile;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.framework.editor.event.*;
import org.exoplatform.ide.client.framework.event.RefreshBrowserEvent;
import org.exoplatform.ide.client.framework.module.FileType;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.project.api.IDEProject;
import org.exoplatform.ide.client.framework.template.FileTemplates;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.marshal.FileUnmarshaller;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.client.model.ItemContext;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.Project;

import java.util.*;

/**
 * @author <a href="mailto:azatsarynnyy@exoplatfrom.com">Artem Zatsarynnyy</a>
 * @version $Id: CreateFilePresenter.java Feb 6, 2013 6:14:31 PM azatsarynnyy $
 */
public class CreateFilePresenter implements CreateNewFileHandler, ItemsSelectedHandler, EditorFileOpenedHandler,
                                            EditorFileClosedHandler, ViewClosedHandler {

    public interface Display extends IsView {

        HasValue<String> getFileNameField();

        HasClickHandlers getCreateButton();

        HasClickHandlers getCancelButton();

        HasKeyPressHandlers getFileNameFiledKeyPressed();

        void setFocusInNameField();

        void selectFileName(int extensionLength);
    }

    private List<Item> selectedItems = new ArrayList<Item>();

    private Map<String, FileModel> openedFiles = new HashMap<String, FileModel>();

    private Display display;

    private String mimeType;

    private static final String UNTITLED_FILE_NAME = IDE.NAVIGATION_CONSTANT.createFileUntitledFileName();

    public CreateFilePresenter() {
        IDE.addHandler(EditorFileOpenedEvent.TYPE, this);
        IDE.addHandler(EditorFileClosedEvent.TYPE, this);
        IDE.addHandler(ItemsSelectedEvent.TYPE, this);
        IDE.addHandler(CreateNewFileEvent.TYPE, this);
        IDE.addHandler(ViewClosedEvent.TYPE, this);
    }

    public void bindDisplay() {
        display.getCancelButton().addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                IDE.getInstance().closeView(display.asView().getId());
            }
        });

        display.getCreateButton().addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                String fileName = display.getFileNameField().getValue();
                if (fileName == null || fileName.trim().isEmpty()) {
                    Dialogs.getInstance().showInfo(IDE.IDE_LOCALIZATION_CONSTANT.createFileFormNameEmpty());
                } else {
                    createAndOpenFile(fileName);
                }
            }
        });

        display.getFileNameFiledKeyPressed().addKeyPressHandler(new KeyPressHandler() {
            public void onKeyPress(KeyPressEvent event) {
                if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
                    String fileName = display.getFileNameField().getValue();
                    if (fileName == null || fileName.trim().isEmpty()) {
                        Dialogs.getInstance().showInfo(IDE.IDE_LOCALIZATION_CONSTANT.createFileFormNameEmpty());
                    } else {
                        createAndOpenFile(fileName);
                    }
                }
            }
        });

    }

    /** @see org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler#onItemsSelected(org.exoplatform.ide.client
     * .framework.navigation.event.ItemsSelectedEvent) */
    @Override
    public void onItemsSelected(ItemsSelectedEvent event) {
        selectedItems = event.getSelectedItems();
    }

    /** @see org.exoplatform.ide.client.operation.createfile.CreateNewFileHandler#onCreateNewFile(org.exoplatform.ide.client.operation
     * .createfile.CreateNewFileEvent) */
    @Override
    public void onCreateNewFile(CreateNewFileEvent event) {
        mimeType = event.getMimeType();

        if (display == null) {
            display = GWT.create(Display.class);
            IDE.getInstance().openView(display.asView());
            bindDisplay();
        }

        FileType fileType = IDE.getInstance().getFileTypeRegistry().getFileType(mimeType);
        if (fileType == null) {
            return;
        }

        final String extension = fileType.getExtension();
        String fileName = UNTITLED_FILE_NAME + "." + extension;

        int index = 1;
        Set<String> openedFilesNames = new HashSet<String>();
        for (FileModel m : openedFiles.values()) {
            openedFilesNames.add(m.getName());
        }
        while (openedFilesNames.contains(fileName)) {
            fileName = UNTITLED_FILE_NAME + " " + index + "." + extension;
            index++;
        }

        display.getFileNameField().setValue(fileName);

        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                display.setFocusInNameField();
                display.selectFileName(extension.length());
            }
        });
    }

    /**
     * Creates a new file with the given name and default content.
     * File's content will be opened in a new editor after creation.
     *
     * @param fileName
     *         a new file name
     */
    private void createAndOpenFile(String fileName) {
        FolderModel parent = new FolderModel();
        ProjectModel project = null;
        if (selectedItems != null && selectedItems.size() != 0) {
            Item item = selectedItems.get(0);

            if (item instanceof FileModel) {
                parent = ((FileModel)item).getParent();
            } else if (item instanceof FolderModel) {
                parent = (FolderModel)item;
            } else if (item instanceof ProjectModel) {
                parent = new FolderModel((Project)item);
            }

            if (item instanceof IDEProject) {
                project = (ProjectModel)item;
            } else if (item instanceof ItemContext) {
                project = ((ItemContext)item).getProject();
            }
        }

        String content = FileTemplates.getTemplateFor(mimeType);
        FileModel newFile = new FileModel(fileName, mimeType, content, parent);
        newFile.setId(fileName);
        newFile.setProject(project);

        try {
            VirtualFileSystem.getInstance().createFile(newFile.getParent(),
                                                       new AsyncRequestCallback<FileModel>(new FileUnmarshaller(newFile)) {
                                                           @Override
                                                           protected void onSuccess(FileModel result) {
                                                               IDE.getInstance().closeView(display.asView().getId());
                                                               IDE.fireEvent(new EditorOpenFileEvent(result));
                                                               IDE.fireEvent(new RefreshBrowserEvent(result.getProject(), result));
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

    /** @see org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedHandler#onEditorFileOpened(org.exoplatform.ide.client
     * .framework.editor.event.EditorFileOpenedEvent) */
    @Override
    public void onEditorFileOpened(EditorFileOpenedEvent event) {
        openedFiles = event.getOpenedFiles();
    }

    /** @see org.exoplatform.ide.client.framework.editor.event.EditorFileClosedHandler#onEditorFileClosed(org.exoplatform.ide.client
     * .framework.editor.event.EditorFileClosedEvent) */
    @Override
    public void onEditorFileClosed(EditorFileClosedEvent event) {
        openedFiles = event.getOpenedFiles();
    }

    /** @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent) */
    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (event.getView() instanceof Display) {
            display = null;
        }
    }

}