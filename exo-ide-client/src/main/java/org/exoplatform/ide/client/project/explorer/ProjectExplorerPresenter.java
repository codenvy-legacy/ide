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
package org.exoplatform.ide.client.project.explorer;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorChangeActiveFileEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedHandler;
import org.exoplatform.ide.client.framework.event.IDELoadCompleteEvent;
import org.exoplatform.ide.client.framework.event.IDELoadCompleteHandler;
import org.exoplatform.ide.client.framework.event.OpenFileEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.navigation.event.AddItemTreeIconEvent;
import org.exoplatform.ide.client.framework.navigation.event.AddItemTreeIconHandler;
import org.exoplatform.ide.client.framework.navigation.event.GoToItemEvent;
import org.exoplatform.ide.client.framework.navigation.event.GoToItemHandler;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.RemoveItemTreeIconEvent;
import org.exoplatform.ide.client.framework.navigation.event.RemoveItemTreeIconHandler;
import org.exoplatform.ide.client.framework.navigation.event.SelectItemEvent;
import org.exoplatform.ide.client.framework.navigation.event.SelectItemHandler;
import org.exoplatform.ide.client.framework.navigation.event.ShowHideHiddenFilesEvent;
import org.exoplatform.ide.client.framework.navigation.event.ShowHideHiddenFilesHandler;
import org.exoplatform.ide.client.framework.project.OpenProjectEvent;
import org.exoplatform.ide.client.framework.project.ProjectClosedEvent;
import org.exoplatform.ide.client.framework.project.ProjectClosedHandler;
import org.exoplatform.ide.client.framework.project.ProjectExplorerDisplay;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedHandler;
import org.exoplatform.ide.client.framework.project.api.FolderOpenedEvent;
import org.exoplatform.ide.client.framework.project.api.TreeRefreshedEvent;
import org.exoplatform.ide.client.framework.project.api.TreeRefreshedHandler;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings.Store;
import org.exoplatform.ide.client.framework.settings.ApplicationSettingsReceivedEvent;
import org.exoplatform.ide.client.framework.settings.ApplicationSettingsReceivedHandler;
import org.exoplatform.ide.client.framework.settings.ApplicationSettingsSavedEvent;
import org.exoplatform.ide.client.framework.settings.SaveApplicationSettingsEvent.SaveType;
import org.exoplatform.ide.client.framework.ui.api.event.ViewActivatedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewActivatedHandler;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.model.SettingsService;
import org.exoplatform.ide.client.operation.cutcopy.CopyItemsEvent;
import org.exoplatform.ide.client.operation.cutcopy.CutItemsEvent;
import org.exoplatform.ide.client.operation.cutcopy.PasteItemsEvent;
import org.exoplatform.ide.client.operation.deleteitem.DeleteItemEvent;
import org.exoplatform.ide.extension.samples.client.getstarted.GetStartedEvent;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.event.ItemDeletedEvent;
import org.exoplatform.ide.vfs.client.event.ItemDeletedHandler;
import org.exoplatform.ide.vfs.client.marshal.ChildrenUnmarshaller;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.ItemType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ProjectExplorerPresenter implements SelectItemHandler,
                                                 ApplicationSettingsReceivedHandler,
                                                 ViewClosedHandler, AddItemTreeIconHandler, RemoveItemTreeIconHandler,
                                                 ShowProjectExplorerHandler,
                                                 ViewActivatedHandler, VfsChangedHandler,
                                                 GoToItemHandler, EditorActiveFileChangedHandler, IDELoadCompleteHandler,
                                                 EditorFileOpenedHandler, EditorFileClosedHandler, ShowHideHiddenFilesHandler,
                                                 ItemDeletedHandler,
                                                 ProjectOpenedHandler, ProjectClosedHandler, TreeRefreshedHandler {

    private static final String DEFAULT_TITLE = "Project Explorer";

    /** Comparator for ordering projects by name. */
    private static final Comparator<ProjectModel> PROJECT_COMPARATOR = new ProjectComparator();

    private ProjectExplorerDisplay display;

    private List<ProjectModel> projects = new ArrayList<ProjectModel>();

    private ProjectModel openedProject;

    private FileModel editorActiveFile;

    private ApplicationSettings applicationSettings;

    /** Enabled or disabled Linking with Editor. */
    private boolean linkingWithEditor = false;

    /** Opened files. */
    private Map<String, FileModel> openedFiles = new HashMap<String, FileModel>();

    public ProjectExplorerPresenter() {
        IDE.getInstance().addControl(new OpenProjectControl());

        IDE.addHandler(ApplicationSettingsReceivedEvent.TYPE, this);
        IDE.addHandler(IDELoadCompleteEvent.TYPE, this);
        IDE.addHandler(ShowProjectExplorerEvent.TYPE, this);
        IDE.addHandler(ProjectOpenedEvent.TYPE, this);
        IDE.addHandler(ProjectClosedEvent.TYPE, this);

        IDE.addHandler(VfsChangedEvent.TYPE, this);
        IDE.addHandler(ViewClosedEvent.TYPE, this);
        IDE.addHandler(SelectItemEvent.TYPE, this);

        IDE.addHandler(TreeRefreshedEvent.TYPE, this);
        IDE.addHandler(AddItemTreeIconEvent.TYPE, this);
        IDE.addHandler(RemoveItemTreeIconEvent.TYPE, this);
        IDE.addHandler(ViewActivatedEvent.TYPE, this);
        IDE.addHandler(GoToItemEvent.TYPE, this);

        IDE.addHandler(EditorActiveFileChangedEvent.TYPE, this);

        IDE.addHandler(EditorFileOpenedEvent.TYPE, this);
        IDE.addHandler(EditorFileClosedEvent.TYPE, this);
        IDE.addHandler(ShowHideHiddenFilesEvent.TYPE, this);
        IDE.addHandler(ItemDeletedEvent.TYPE, this);
    }

    /** @see org.exoplatform.ide.client.framework.settings.ApplicationSettingsReceivedHandler#onApplicationSettingsReceived(org
     * .exoplatform.ide.client.framework.settings.ApplicationSettingsReceivedEvent) */
    public void onApplicationSettingsReceived(ApplicationSettingsReceivedEvent event) {
        applicationSettings = event.getApplicationSettings();

        if (applicationSettings.getValueAsMap("lock-tokens") == null) {
            applicationSettings.setValue("lock-tokens", new LinkedHashMap<String, String>(), Store.COOKIES);
        }

        if (applicationSettings.getValueAsBoolean("project-explorer-linked-with-editor") == null) {
            applicationSettings.setValue("project-explorer-linked-with-editor", Boolean.FALSE, Store.COOKIES);
        }
        linkingWithEditor = applicationSettings.getValueAsBoolean("project-explorer-linked-with-editor");

        ensureProjectExplorerDisplayCreated();

        display.setLinkWithEditorButtonSelected(linkingWithEditor);

        if (openedProject == null) {
            display.setLinkWithEditorButtonEnabled(false);
        }
    }

    @Override
    public void onVfsChanged(VfsChangedEvent event) {
        if (display == null) {
            return;
        }

        display.setProject(null);
        display.asView().setTitle(DEFAULT_TITLE);
    }


    /** @see org.exoplatform.ide.client.framework.event.IDELoadCompleteHandler#onIDELoadComplete(org.exoplatform.ide.client.framework
     * .event.IDELoadCompleteEvent) */
    @Override
    public void onIDELoadComplete(IDELoadCompleteEvent event) {
        if (openedProject == null) {
            refreshProjectsList(event);
        }
    }

    /**
     *
     */
    private void ensureProjectExplorerDisplayCreated() {
        if (display != null) {
            return;
        }

        display = GWT.create(ProjectExplorerDisplay.class);
        IDE.getInstance().openView(display.asView());
        bindDisplay();
    }

    /** @see org.exoplatform.ide.client.project.explorer.ShowProjectExplorerHandler#onShowProjectExplorer(org.exoplatform.ide.client
     * .project.explorer.ShowProjectExplorerEvent) */
    @Override
    public void onShowProjectExplorer(ShowProjectExplorerEvent event) {
        if (display != null) {
            IDE.getInstance().closeView(display.asView().getId());
            return;
        }

        ensureProjectExplorerDisplayCreated();

        if (openedProject == null) {
            display.setProject(null);
            refreshProjectsList(event);
            return;
        }

        if (ProjectUpdater.isNeedUpdateProject(openedProject) && !IDE.isRoUser()) {
            ProjectUpdater.updateProject(openedProject, new ProjectUpdater.ProjectUpdatedHandler() {
                @Override
                public void onProjectUpdated() {
                    showProjectTree();
                }
            });
        } else {
            showProjectTree();
        }
    }

    /** @see org.exoplatform.ide.client.framework.project.ProjectOpenedHandler#onProjectOpened(org.exoplatform.ide.client.framework
     * .project.ProjectOpenedEvent) */
    @Override
    public void onProjectOpened(final ProjectOpenedEvent projectOpenedEvent) {
        openedProject = projectOpenedEvent.getProject();
        ensureProjectExplorerDisplayCreated();

        if (ProjectUpdater.isNeedUpdateProject(openedProject) && !IDE.isRoUser()) {
            ProjectUpdater.updateProject(projectOpenedEvent.getProject(), new ProjectUpdater.ProjectUpdatedHandler() {
                @Override
                public void onProjectUpdated() {
                    showProjectTree();
                }
            });
        } else {
            showProjectTree();
        }
    }

    /** @see org.exoplatform.ide.client.framework.project.ProjectClosedHandler#onProjectClosed(org.exoplatform.ide.client.framework
     * .project.ProjectClosedEvent) */
    @Override
    public void onProjectClosed(ProjectClosedEvent event) {
        openedProject = null;

        if (display == null) {
            return;
        }

        display.setProject(null);

        display.asView().setTitle(DEFAULT_TITLE);
        display.setLinkWithEditorButtonEnabled(false);
        refreshProjectsList(event);

        IDE.fireEvent(new ItemsSelectedEvent(new ArrayList<Item>(), display.asView()));
    }

    private void showProjectTree() {
        display.asView().setTitle(openedProject.getName());
        display.setProject(openedProject);

        display.setLinkWithEditorButtonEnabled(true);
        display.setLinkWithEditorButtonSelected(linkingWithEditor);
    }

    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (event.getView() instanceof ProjectExplorerDisplay) {
            display = null;
        }
    }

    public void bindDisplay() {
        display.getProjectTree().addOpenHandler(new OpenHandler<Item>() {
            public void onOpen(final OpenEvent<Item> event) {
                Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                    @Override
                    public void execute() {
                        FolderModel folder = (FolderModel)event.getTarget();
                        List<Item> visibleItems = display.getVisibleItems();
                        IDE.fireEvent(new FolderOpenedEvent(folder, visibleItems));
                    }
                });
            }
        });

        display.getProjectTree().addCloseHandler(new CloseHandler<Item>() {
            @Override
            public void onClose(final CloseEvent<Item> event) {
                Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                    @Override
                    public void execute() {
                        FolderModel folder = (FolderModel)event.getTarget();
                        List<Item> visibleItems = display.getVisibleItems();
                        IDE.fireEvent(new FolderOpenedEvent(folder, visibleItems));
                    }
                });
            }
        });

        display.getProjectTree().addSelectionHandler(new SelectionHandler<Item>() {
            public void onSelection(SelectionEvent<Item> event) {
                treeItemSelected();
            }
        });

        display.getProjectTree().addDoubleClickHandler(new DoubleClickHandler() {
            public void onDoubleClick(DoubleClickEvent event) {
                if (display.getSelectedItem() != null && display.getSelectedItem() instanceof FileModel) {
                    IDE.fireEvent(new OpenFileEvent((FileModel)display.getSelectedItem()));
                }
            }
        });

        display.getProjectTree().addKeyPressHandler(new KeyPressHandler() {
            public void onKeyPress(KeyPressEvent event) {
                onKeyPressed(event.getNativeEvent().getKeyCode(), event.isControlKeyDown());
            }
        });

        display.getLinkWithEditorButton().addClickHandler(linkWithEditorButtonClickHandler);
        display.setLinkWithEditorButtonSelected(linkingWithEditor);

        display.getProjectsListGrid().addSelectionHandler(new SelectionHandler<ProjectModel>() {
            @Override
            public void onSelection(SelectionEvent<ProjectModel> event) {
                IDE.fireEvent(new ProjectSelectedEvent(event.getSelectedItem()));
            }
        });

        display.getProjectsListGrid().addDoubleClickHandler(new DoubleClickHandler() {
            public void onDoubleClick(DoubleClickEvent event) {
                List<ProjectModel> selectedProjects = display.getSelectedProjects();
                if (selectedProjects.size() == 1) {
                    IDE.fireEvent(new OpenProjectEvent(selectedProjects.get(0)));
                }
            }
        });
    }

    protected void treeItemSelected() {
        if (!display.asView().isViewVisible()) {
            return;
        }

        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                if (display == null) {
                    return;
                }

                Item selectedItem = display.getSelectedItem();
                IDE.fireEvent(new ItemsSelectedEvent(selectedItem, display.asView()));

                Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                    @Override
                    public void execute() {
                        changeActiveFileOnSelection();
                    }
                });
            }
        });
    }

    /** @see org.exoplatform.ide.client.framework.navigation.event.SelectItemHandler#onSelectItem(org.exoplatform.ide.client.framework
     * .navigation.event.SelectItemEvent) */
    public void onSelectItem(final SelectItemEvent event) {
        if (display == null) {
            return;
        }

        //if (display.asView().isViewVisible()) {
        display.selectItem(event.getItem());

        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                IDE.fireEvent(new ItemsSelectedEvent(event.getItem(), display.asView()));
            }
        });
        //}
    }

    // keyboard keys doesn't work within the TreeGrid in the Internet Explorer 8.0, Safari 5.0.2 and Google Chrome 7.0.5
    // seems because of SmartGWT issues
    protected void onKeyPressed(int keyCode, boolean isControlKeyDown) {
        if (isControlKeyDown) {
            // "Ctrl+C" hotkey handling
            if (String.valueOf(keyCode).toUpperCase().equals("C")) {
                IDE.fireEvent(new CopyItemsEvent());
            }

            // "Ctrl+X" hotkey handling
            else if (String.valueOf(keyCode).toUpperCase().equals("X")) {
                IDE.fireEvent(new CutItemsEvent());
            }

            // "Ctrl+V" hotkey handling
            else if (String.valueOf(keyCode).toUpperCase().equals("V")) {
                IDE.fireEvent(new PasteItemsEvent());
            }
        }

        // "Delete" hotkey handling
        else if (keyCode == KeyCodes.KEY_DELETE) {
            IDE.fireEvent(new DeleteItemEvent());
        }

        // "Enter" hotkey handling - impossible to handle Enter key pressing event within the TreeGrid and ListGrid in the
        // SmartGWT 2.2 because of bug when Enter keypress is not caugth.
        // http://code.google.com/p/smartgwt/issues/detail?id=430
        // else if (charCode == KeyCodes.KEY_ENTER)
        // {
        // onBrowserDoubleClicked();
        // }
    }

    /**
     * @see org.exoplatform.ide.client.framework.navigation.event.RemoveItemTreeIconHandler#onRemoveItemTreeIcon(org.exoplatform.ide
     *      .client.framework.navigation.event.RemoveItemTreeIconEvent)
     */
    @Override
    public void onRemoveItemTreeIcon(RemoveItemTreeIconEvent event) {
        display.removeItemIcons(event.getIconsToRemove());
    }

    /**
     * @see org.exoplatform.ide.client.framework.navigation.event.AddItemTreeIconHandler#onAddItemTreeIcon(org.exoplatform.ide.client
     *      .framework.navigation.event.AddItemTreeIconEvent)
     */
    @Override
    public void onAddItemTreeIcon(AddItemTreeIconEvent event) {
        display.addItemsIcons(event.getTreeItemIcons());
    }

    @Override
    public void onViewActivated(ViewActivatedEvent event) {
        if (event.getView() instanceof ProjectExplorerDisplay) {
            treeItemSelected();

//            if (linkingWithEditor && editorActiveFile != null) {
//                Item selectedItem = display.getSelectedItem();
//                if (selectedItem == null || !selectedItem.getId().equals(editorActiveFile.getId())) {
//                    display.selectItem(editorActiveFile);                    
//                }
//            } else {
//                treeItemSelected();
//            }
        }
    }

    @Override
    public void onGoToFolder(GoToItemEvent event) {
        if (display != null && openedProject != null) {
            display.selectItem(event.getFileToOpen());
        }
    }

    /** Link with Editor button click handler. */
    private ClickHandler linkWithEditorButtonClickHandler = new ClickHandler() {
        @Override
        public void onClick(ClickEvent event) {
            linkingWithEditor = !linkingWithEditor;
            display.setLinkWithEditorButtonSelected(linkingWithEditor);

            applicationSettings.setValue("project-explorer-linked-with-editor", new Boolean(linkingWithEditor), Store.COOKIES);
            SettingsService.getInstance().saveSettingsToCookies(applicationSettings);
            IDE.fireEvent(new ApplicationSettingsSavedEvent(applicationSettings, SaveType.COOKIES));

            if (linkingWithEditor && editorActiveFile != null) {
                display.selectItem(editorActiveFile);
                IDE.fireEvent(new TreeRefreshedEvent(openedProject));
            }
        }
    };

    @Override
    public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event) {
        editorActiveFile = event.getFile();

        if (display == null) {
            return;
        }

        if (!linkingWithEditor) {
            return;
        }

        if (editorActiveFile == null) {
            return;
        }

        Item selectedItem = display.getSelectedItem();
        if (selectedItem != null && selectedItem.getId().equals(editorActiveFile.getId())) {
            return;
        }

        display.selectItem(editorActiveFile);
    }

    @Override
    public void onEditorFileClosed(EditorFileClosedEvent event) {
        openedFiles = event.getOpenedFiles();
    }

    @Override
    public void onEditorFileOpened(EditorFileOpenedEvent event) {
        openedFiles = event.getOpenedFiles();
    }

    /** Changes the active file in Editor just after item selected in the Tree. */
    private void changeActiveFileOnSelection() {
        Item selectedItem = display.getSelectedItem();

        if (display == null || openedProject == null || !linkingWithEditor || selectedItem == null) {
            return;
        }

        if (!openedFiles.containsKey(selectedItem.getId())) {
            return;
        }

        if (editorActiveFile.getId().equals(selectedItem.getId())) {
            return;
        }

        IDE.fireEvent(new EditorChangeActiveFileEvent((FileModel)selectedItem));
    }

    /**
     * @see org.exoplatform.ide.client.navigation.handler.ShowHideHiddenFilesHandler#onShowHideHiddenFiles(org.exoplatform.ide.client
     *      .navigation.event.ShowHideHiddenFilesEvent)
     */
    @Override
    public void onShowHideHiddenFiles(ShowHideHiddenFilesEvent event) {
        if (display == null) {
            return;
        }

        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                display.refreshTree();
            }
        });
    }

    /** @see org.exoplatform.ide.vfs.client.event.ItemDeletedHandler#onItemDeleted(org.exoplatform.ide.vfs.client.event.ItemDeletedEvent) */
    @Override
    public void onItemDeleted(ItemDeletedEvent event) {
        if (event.getItem() instanceof ProjectModel) {
            if (openedProject == null && display != null) {
                refreshProjectsList(event);
            }
        }
    }

    @Override
    public void onTreeRefreshed(final TreeRefreshedEvent event) {
        if (display != null) {
            display.refreshTree();

            if (event.getItemToSelect() != null) {
                display.selectItem(event.getItemToSelect());
            }

            Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                @Override
                public void execute() {
                    List<Item> visibleItems = display.getVisibleItems();
                    IDE.fireEvent(new FolderOpenedEvent(event.getFolder(), visibleItems));
                }
            });
        }
    }

    private void refreshProjectsList(final GwtEvent event) {
        try {
            VirtualFileSystem.getInstance().getChildren(VirtualFileSystem.getInstance().getInfo().getRoot(),
                                                        ItemType.PROJECT, new AsyncRequestCallback<List<Item>>(
                    new ChildrenUnmarshaller(new ArrayList<Item>())) {
                @Override
                protected void onSuccess(List<Item> result) {
                    projects.clear();
                    for (Item item : result) {
                        if (item instanceof ProjectModel) {
                            projects.add((ProjectModel)item);
                        }
                    }

                    if (event instanceof IDELoadCompleteEvent && projects.size() == 0) {
                        IDE.fireEvent(new GetStartedEvent());
                    }

                    Collections.sort(projects, PROJECT_COMPARATOR);
                    display.getProjectsListGrid().setValue(projects);
                }

                @Override
                protected void onFailure(Throwable exception) {
                    IDE.fireEvent(new ExceptionThrownEvent(exception, "Error loading. Searching of projects failed."));
                }
            });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e, "Searching of projects failed."));
        }
    }

}
