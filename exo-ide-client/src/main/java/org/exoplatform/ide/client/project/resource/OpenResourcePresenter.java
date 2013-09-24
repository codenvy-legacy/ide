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

package org.exoplatform.ide.client.project.resource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Timer;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.gwtframework.ui.client.api.TextFieldItem;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.event.OpenFileEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.project.ProjectClosedEvent;
import org.exoplatform.ide.client.framework.project.ProjectClosedHandler;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedHandler;
import org.exoplatform.ide.client.framework.project.api.FolderTreeUnmarshaller;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.hotkeys.HotKeyHelper;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.model.*;
import org.exoplatform.ide.vfs.shared.Folder;
import org.exoplatform.ide.vfs.shared.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */
public class OpenResourcePresenter implements OpenResourceHandler, ViewClosedHandler, ProjectOpenedHandler,
                                              ProjectClosedHandler {

    /** Display */
    public interface Display extends IsView {

        /**
         * Get file name field
         *
         * @return file name field
         */
        TextFieldItem getFileNameField();

        /**
         * Get files list grid
         *
         * @return files list grid
         */
        ListGridItem<FileModel> getFilesListGrid();

        /**
         * Get files list grid with ability to handle key pressing
         *
         * @return files list grid
         */
        HasAllKeyHandlers listGrid();

        /** Set focus in list grid */
        void focusListGrid();

        /**
         * Set name of item's parent folder
         *
         * @param folderName
         *         name of item's parent folder
         */
        void setItemFolderName(String folderName);

        /**
         * Get list of selected files
         *
         * @return list of selected files
         */
        List<FileModel> getSelectedItems();

        /**
         * Get Open button
         *
         * @return Open button
         */
        HasClickHandlers getOpenButton();

        /**
         * Get Cancel button
         *
         * @return Cancel button
         */
        HasClickHandlers getCancelButton();

    }

    /** Callback to process selected item in resources list. */
    public interface ResourceSelectedCallback {
        void onSelectedResource(Item resource);
    }

    /** {@link Display} instance */
    private Display display;

    private ProjectModel project;

    private List<FileModel> allFiles = new ArrayList<FileModel>();

    private List<FileModel> filteredFiles;

    private FileModel selectedFile;

    private ResourceSelectedCallback callback;

    public OpenResourcePresenter() {
        IDE.getInstance().addControl(new OpenResourceControl());

        IDE.addHandler(OpenResourceEvent.TYPE, this);
        IDE.addHandler(ViewClosedEvent.TYPE, this);
        IDE.addHandler(ProjectOpenedEvent.TYPE, this);
        IDE.addHandler(ProjectClosedEvent.TYPE, this);
    }

    @Override
    public void onOpenResource(OpenResourceEvent event) {
        if (project == null || display != null) {
            return;
        }

        this.callback = event.getCallback();

        allFiles.clear();

        try {
            FolderTreeUnmarshaller unmarshaller = new FolderTreeUnmarshaller(project);
            VirtualFileSystem.getInstance().getTree(project.getId(), new AsyncRequestCallback<Folder>(unmarshaller) {
                @Override
                protected void onSuccess(Folder result) {
                    findAllFiles(result);
                    display = GWT.create(Display.class);
                    bindDisplay();
                    IDE.getInstance().openView(display.asView());
                }

                @Override
                protected void onFailure(Throwable exception) {
                    Dialogs.getInstance().showError("Failed to read project tree.");
                }
            });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /**
     * Search all files in folder tree.
     *
     * @param item
     *         folder item to start searching files.
     */
    private void findAllFiles(Item item) {
        if (item instanceof FileModel) {
            allFiles.add((FileModel)item);
        } else if (item instanceof FolderModel) {
            FolderModel folder = (FolderModel)item;

            for (Item child : folder.getChildren().getItems()) {
                findAllFiles(child);
            }
        }
    }

    private void bindDisplay() {
        display.setItemFolderName(null);

        display.getFileNameField().addKeyUpHandler(fileNameFieldKeyHandler);

        display.getFilesListGrid().addSelectionHandler(new SelectionHandler<FileModel>() {
            @Override
            public void onSelection(SelectionEvent<FileModel> event) {
                selectedFile = event.getSelectedItem();
                display.setItemFolderName(selectedFile == null ? null : selectedFile.getPath());
            }
        });

        display.getFilesListGrid().addDoubleClickHandler(new DoubleClickHandler() {
            @Override
            public void onDoubleClick(DoubleClickEvent event) {
                actionSelectedFile();
            }
        });

        display.listGrid().addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                if (HotKeyHelper.KeyCode.ENTER == event.getNativeKeyCode()) {
                    actionSelectedFile();
                }
            }
        });

        display.getOpenButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                actionSelectedFile();
            }
        });

        display.getCancelButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                IDE.getInstance().closeView(display.asView().getId());
            }
        });

        selectedFile = null;
        updateTimer.schedule(100);
    }

    private KeyUpHandler fileNameFieldKeyHandler = new KeyUpHandler() {
        @Override
        public void onKeyUp(KeyUpEvent event) {
            if (event.getNativeKeyCode() == HotKeyHelper.KeyCode.DOWN) {
                if (filteredFiles != null && filteredFiles.size() > 0) {
                    display.getFilesListGrid().selectItem(filteredFiles.get(0));
                    display.focusListGrid();
                }
            }

            updateTimer.cancel();
            updateTimer.schedule(100);
        }
    };

    private Timer updateTimer = new Timer() {
        @Override
        public void run() {
            filteredFiles = new ArrayList<FileModel>();

            if (display.getFileNameField().getValue().trim().isEmpty()) {
                for (Item item : allFiles) {
                    if (item instanceof FileModel) {
                        FileModel file = (FileModel)item;
                        filteredFiles.add(file);
                    }
                }
            } else {
                String fileNamePrefix = display.getFileNameField().getValue().trim().toUpperCase();
                for (Item item : allFiles) {
                    if (item instanceof FileModel && item.getName().toUpperCase().startsWith(fileNamePrefix)) {
                        FileModel file = (FileModel)item;
                        filteredFiles.add(file);
                    }
                }
            }

            display.getFilesListGrid().setValue(filteredFiles);
            if (selectedFile != null && filteredFiles.contains(selectedFile)) {
                display.getFilesListGrid().selectItem(selectedFile);
            } else {
                selectedFile = null;
                display.setItemFolderName(null);
            }
        }
    };

    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (event.getView() instanceof Display) {
            display = null;
        }
    }

    @Override
    public void onProjectClosed(ProjectClosedEvent event) {
        project = null;
    }

    @Override
    public void onProjectOpened(ProjectOpenedEvent event) {
        project = event.getProject();
    }

    private void actionSelectedFile() {
        if (callback != null) {
            Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                @Override
                public void execute() {
                    callback.onSelectedResource(selectedFile);
                    closeView();
                }
            });
            return;
        }

        IDE.fireEvent(new OpenFileEvent(selectedFile));
        closeView();
    }

    private void closeView() {
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                if (display != null) {
                    selectedFile = null;
                    IDE.getInstance().closeView(display.asView().getId());
                }
            }
        });
    }
}
