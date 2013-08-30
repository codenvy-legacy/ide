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
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Timer;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.gwtframework.ui.client.api.TextFieldItem;
import org.exoplatform.ide.client.framework.application.IDELoader;
import org.exoplatform.ide.client.framework.event.FileOpenedEvent;
import org.exoplatform.ide.client.framework.event.FileOpenedHandler;
import org.exoplatform.ide.client.framework.event.OpenFileEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.project.ProjectClosedEvent;
import org.exoplatform.ide.client.framework.project.ProjectClosedHandler;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedHandler;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.hotkeys.HotKeyHelper;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.marshal.ChildrenUnmarshaller;
import org.exoplatform.ide.vfs.client.marshal.ItemUnmarshaller;
import org.exoplatform.ide.vfs.client.model.*;
import org.exoplatform.ide.vfs.shared.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class OpenResourcePresenter implements OpenResourceHandler, ViewClosedHandler, ProjectOpenedHandler,
                                              ProjectClosedHandler, FileOpenedHandler {

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

    /** Search Failed message */
    private static final String SEARCH_ERROR_MESSAGE = org.exoplatform.ide.client.IDE.ERRORS_CONSTANT
                                                                                     .searchFileSearchError();

    private static final String OPENING_FILE_MESSAGE = "Opening file...";

    /** {@link Display} instance */
    private Display display;

    /**
     *
     */
    private ProjectModel project;

    private List<FileModel> allFiles = new ArrayList<FileModel>();

    private List<FileModel> filteredFiles;

    private FileModel fileToOpen;

    private FileModel selectedFile;

    public OpenResourcePresenter() {
        IDE.getInstance().addControl(new OpenResourceControl());

        IDE.addHandler(OpenResourceEvent.TYPE, this);
        IDE.addHandler(ViewClosedEvent.TYPE, this);
        IDE.addHandler(ProjectOpenedEvent.TYPE, this);
        IDE.addHandler(ProjectClosedEvent.TYPE, this);
        IDE.addHandler(FileOpenedEvent.TYPE, this);
    }

    @Override
    public void onOpenResource(OpenResourceEvent event) {
        if (project == null || display != null) {
            return;
        }

        allFiles.clear();
        addItem(project);

        display = GWT.create(Display.class);
        bindDisplay();
        IDE.getInstance().openView(display.asView());
    }

    private void addItem(Item item) {
        if (item instanceof FileModel) {
            allFiles.add((FileModel)item);
        } else if (item instanceof FolderModel) {
            FolderModel folder = (FolderModel)item;

            for (Item child : folder.getChildren().getItems()) {
                addItem(child);
            }
        }
    }

//    @Override
//    public void onOpenResource(OpenResourceEvent event) {
//        if (project == null || display != null) {
//            return;
//        }
//
//        HashMap<String, String> query = new HashMap<String, String>();
//        String path = project.getPath();
//        if (!"".equals(path) && !path.startsWith("/")) {
//            path = "/" + path;
//        }
//        query.put("path", path);
//
//        try {
//            VirtualFileSystem.getInstance().search(query, -1, 0,
//                                                   new AsyncRequestCallback<List<Item>>(new ChildrenUnmarshaller(new ArrayList<Item>())) {
//                                                       @Override
//                                                       protected void onSuccess(List<Item> result) {
//                                                           IDELoader.hide();
//
//                                                           allFiles.clear();
//                                                           for (Item item : result) {
//                                                               if (item instanceof FileModel) {
//                                                                   allFiles.add((FileModel)item);
//                                                               }
//                                                           }
//
//                                                           display = GWT.create(Display.class);
//                                                           bindDisplay();
//                                                           IDE.getInstance().openView(display.asView());
//                                                       }
//
//                                                       @Override
//                                                       protected void onFailure(Throwable exception) {
//                                                           IDE.fireEvent(new ExceptionThrownEvent(exception, SEARCH_ERROR_MESSAGE));
//                                                       }
//                                                   });
//        } catch (RequestException e) {
//            IDE.fireEvent(new ExceptionThrownEvent(e, SEARCH_ERROR_MESSAGE));
//        }
//    }

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
                openSelectedFile();
            }
        });

        display.listGrid().addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                if (HotKeyHelper.KeyCode.ENTER == event.getNativeKeyCode()) {
                    openSelectedFile();
                }
            }
        });

        display.getOpenButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                openSelectedFile();
            }
        });

        display.getCancelButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                IDE.getInstance().closeView(display.asView().getId());
            }
        });

        fileToOpen = null;
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

    private List<String> itemPathList = new ArrayList<String>();

    private List<Item> itemList = new ArrayList<Item>();

    private void openSelectedFile() {
        List<FileModel> selectedItems = display.getSelectedItems();
        if (selectedItems.size() == 0) {
            return;
        }

        FileModel file = selectedItems.get(0);

        itemPathList.clear();
        itemList.clear();

        String[] pathParts = file.getPath().split("/");
        String path = "";
        for (int i = 1; i < pathParts.length; i++) {
            path += "/" + pathParts[i];
            itemPathList.add(path);
        }

        // Load list of items.
        IDELoader.show(OPENING_FILE_MESSAGE);
        loadListOfPathItems();
    }

    private void loadListOfPathItems() {
        if (itemPathList.size() == 0) {
            IDELoader.hide();
            pathItemsLoadComplete();
            return;
        }

        String itemPath = itemPathList.remove(0);
        try {
            VirtualFileSystem.getInstance().getItemByPath(itemPath,
                                                          new AsyncRequestCallback<ItemWrapper>(
                                                                  new ItemUnmarshaller(new ItemWrapper(new FileModel()))) {
                                                              @Override
                                                              protected void onSuccess(ItemWrapper result) {
                                                                  itemList.add(result.getItem());
                                                                  loadListOfPathItems();
                                                              }

                                                              @Override
                                                              protected void onFailure(Throwable exception) {
                                                                  IDELoader.hide();
                                                                  //String message = IDE.IDE_LOCALIZATION_MESSAGES
                                                                  // .openFileByPathErrorMessage(fileName);
                                                                  //Dialogs.getInstance().showError(message);
                                                                  IDE.fireEvent(new ExceptionThrownEvent(exception));
                                                              }
                                                          });
        } catch (RequestException e) {
            IDELoader.hide();
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    private void pathItemsLoadComplete() {
        if (!(itemList.get(0) instanceof ProjectModel) ||
            !(itemList.get(itemList.size() - 1) instanceof FileModel)) {
            return;
        }

        for (int i = itemList.size() - 1; i > 0; i--) {
            Item item = itemList.get(i);
            Item prevItem = itemList.get(i - 1);
            if (item instanceof ItemContext) {
                ItemContext itemContext = (ItemContext)item;
                itemContext.setProject(project);

                if (prevItem instanceof FolderModel) {
                    itemContext.setParent((FolderModel)prevItem);
                } else if (prevItem instanceof ProjectModel) {
                    itemContext.setParent(new FolderModel((ProjectModel)prevItem));
                }
            }
        }

        fileToOpen = (FileModel)itemList.get(itemList.size() - 1);
        IDE.fireEvent(new OpenFileEvent(fileToOpen));
    }

    @Override
    public void onFileOpened(FileOpenedEvent event) {
        if (display != null && fileToOpen != null && event.getFile().getId().equals(fileToOpen.getId())) {
            fileToOpen = null;
            IDE.getInstance().closeView(display.asView().getId());
        }
    }


}
