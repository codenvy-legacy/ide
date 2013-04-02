/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package org.exoplatform.ide.client.project.explorer;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.*;
import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.editor.event.*;
import org.exoplatform.ide.client.framework.event.IDELoadCompleteEvent;
import org.exoplatform.ide.client.framework.event.IDELoadCompleteHandler;
import org.exoplatform.ide.client.framework.event.OpenFileEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.navigation.event.*;
import org.exoplatform.ide.client.framework.project.*;
import org.exoplatform.ide.client.framework.project.api.FolderOpenedEvent;
import org.exoplatform.ide.client.framework.project.api.TreeRefreshedEvent;
import org.exoplatform.ide.client.framework.project.api.TreeRefreshedHandler;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings.Store;
import org.exoplatform.ide.client.framework.settings.ApplicationSettingsReceivedEvent;
import org.exoplatform.ide.client.framework.settings.ApplicationSettingsReceivedHandler;
import org.exoplatform.ide.client.framework.settings.ApplicationSettingsSavedEvent;
import org.exoplatform.ide.client.framework.settings.SaveApplicationSettingsEvent.SaveType;
import org.exoplatform.ide.client.framework.ui.api.event.*;
import org.exoplatform.ide.client.model.SettingsService;
import org.exoplatform.ide.client.operation.cutcopy.CopyItemsEvent;
import org.exoplatform.ide.client.operation.cutcopy.CutItemsEvent;
import org.exoplatform.ide.client.operation.cutcopy.PasteItemsEvent;
import org.exoplatform.ide.client.operation.deleteitem.DeleteItemEvent;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.event.*;
import org.exoplatform.ide.vfs.client.marshal.ChildrenUnmarshaller;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.*;

import java.util.*;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ProjectExplorerPresenter implements SelectItemHandler,
                                                 ViewVisibilityChangedHandler, ItemUnlockedHandler, ItemLockedHandler,
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

    private HashMap<String, ProjectModel> map = new HashMap<String, ProjectModel>();

    private List<Item> selectedItems = new ArrayList<Item>();

    private ProjectModel openedProject;

    private FileModel editorActiveFile;

    private ApplicationSettings applicationSettings;

    private boolean ideLoadComplete = false;

    public ProjectExplorerPresenter() {
        IDE.getInstance().addControl(new OpenProjectControl());

        IDE.addHandler(ShowProjectExplorerEvent.TYPE, this);

        IDE.addHandler(ViewClosedEvent.TYPE, this);
        IDE.addHandler(ViewVisibilityChangedEvent.TYPE, this);
        IDE.addHandler(ApplicationSettingsReceivedEvent.TYPE, this);
        IDE.addHandler(ItemLockedEvent.TYPE, this);
        IDE.addHandler(ItemUnlockedEvent.TYPE, this);
        IDE.addHandler(AddItemTreeIconEvent.TYPE, this);
        IDE.addHandler(RemoveItemTreeIconEvent.TYPE, this);
        IDE.addHandler(ViewActivatedEvent.TYPE, this);
        IDE.addHandler(VfsChangedEvent.TYPE, this);

        IDE.addHandler(SelectItemEvent.TYPE, this);
        IDE.addHandler(GoToItemEvent.TYPE, this);
        IDE.addHandler(EditorActiveFileChangedEvent.TYPE, this);
        IDE.addHandler(IDELoadCompleteEvent.TYPE, this);
        IDE.addHandler(EditorFileOpenedEvent.TYPE, this);
        IDE.addHandler(EditorFileClosedEvent.TYPE, this);
        IDE.addHandler(ShowHideHiddenFilesEvent.TYPE, this);
        IDE.addHandler(ItemDeletedEvent.TYPE, this);

        IDE.addHandler(ProjectOpenedEvent.TYPE, this);
        IDE.addHandler(ProjectClosedEvent.TYPE, this);
        IDE.addHandler(TreeRefreshedEvent.TYPE, this);
    }

    private void ensureProjectExplorerDisplayCreated() {
        if (display != null) {
            return;
        }

        display = GWT.create(ProjectExplorerDisplay.class);
        IDE.getInstance().openView(display.asView());
        bindDisplay();
    }

    public void bindDisplay() {
        display.getBrowserTree().addOpenHandler(new OpenHandler<Item>() {
            public void onOpen(final OpenEvent<Item> event) {
                Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                    @Override
                    public void execute() {
                        FolderModel folder = (FolderModel)event.getTarget();
                        IDE.fireEvent(new FolderOpenedEvent(folder));
                    }
                });
            }
        });

        display.getBrowserTree().addCloseHandler(new CloseHandler<Item>() {
            public void onClose(CloseEvent<Item> event) {
                onCloseFolder((Folder)event.getTarget());
            }
        });

        display.getBrowserTree().addSelectionHandler(new SelectionHandler<Item>() {
            public void onSelection(SelectionEvent<Item> event) {
                treeItemSelected();
            }
        });

        display.getBrowserTree().addDoubleClickHandler(new DoubleClickHandler() {
            public void onDoubleClick(DoubleClickEvent event) {
                onBrowserDoubleClicked();
            }
        });

        display.getBrowserTree().addKeyPressHandler(new KeyPressHandler() {
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

    /**
     * Perform actions when folder is closed in browser tree.
     *
     * @param folder
     *         closed folder
     */
    public void onCloseFolder(Folder folder) {
        for (Item item : display.getSelectedItems()) {
            if (item.getPath().startsWith(folder.getPath()) && !item.getId().equals(folder.getId())) {
                display.deselectItem(item.getId());
            }
        }
    }

    /** Handling item selected event from browser */
    protected void treeItemSelected() {
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                if (display == null) {
                    return;
                }

                selectedItems = display.getSelectedItems();
                IDE.fireEvent(new ItemsSelectedEvent(selectedItems, display.asView()));
                changeActiveFileOnSelection();
            }
        });
    }

    /** Handling of mouse double clicking */
    protected void onBrowserDoubleClicked() {
        if (selectedItems.size() != 1) {
            return;
        }

        Item item = selectedItems.get(0);

        if (item instanceof File) {
            IDE.fireEvent(new OpenFileEvent((FileModel)item));
        }
    }

    /** Select chosen item in browser. */
    public void onSelectItem(final SelectItemEvent event) {
        if (display == null) {
            return;
        }

        display.selectItem(event.getItem());

        if (display.asView().isViewVisible()) {
//         display.selectItem(event.getItem());

            Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                @Override
                public void execute() {
                    IDE.fireEvent(new ItemsSelectedEvent(event.getItem(), display.asView()));
                }
            });
        }
    }

    public void onItemUnlocked(ItemUnlockedEvent event) {
        Item item = event.getItem();
//      onRefreshBrowser(new RefreshBrowserEvent());
        if (item instanceof FileModel) {
            FileModel file = (FileModel)item;
            file.setLocked(false);
            file.setLock(null);
            display.updateItemState(file);
        }
    }

    public void onItemLocked(ItemLockedEvent event) {
        Item item = event.getItem();
        if (item instanceof FileModel) {
            FileModel file = (FileModel)item;
            file.setLocked(true);
            file.setLock(new Lock("", event.getLockToken().getLockToken(), 0));
            display.updateItemState(file);
        }
    }

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

        display.setLockTokens(applicationSettings.getValueAsMap("lock-tokens"));
        display.setLinkWithEditorButtonSelected(linkingWithEditor);

        if (openedProject == null) {
            display.setLinkWithEditorButtonEnabled(false);
        }
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

    /** @see org.exoplatform.ide.client.framework.ui.api.event.ViewVisibilityChangedHandler#onViewVisibilityChanged(org.exoplatform.ide
     * .client.framework.ui.api.event.ViewVisibilityChangedEvent) */
    @Override
    public void onViewVisibilityChanged(ViewVisibilityChangedEvent event) {
        if (event.getView() instanceof ProjectExplorerDisplay && event.getView().isViewVisible()) {
            treeItemSelected();
        }
    }

    /** @see org.exoplatform.ide.client.framework.navigation.event.RemoveItemTreeIconHandler#onRemoveItemTreeIcon(org.exoplatform.ide
     * .client.framework.navigation.event.RemoveItemTreeIconEvent) */
    @Override
    public void onRemoveItemTreeIcon(RemoveItemTreeIconEvent event) {
        display.removeItemIcons(event.getIconsToRemove());
    }

    /** @see org.exoplatform.ide.client.framework.navigation.event.AddItemTreeIconHandler#onAddItemTreeIcon(org.exoplatform.ide.client
     * .framework.navigation.event.AddItemTreeIconEvent) */
    @Override
    public void onAddItemTreeIcon(AddItemTreeIconEvent event) {
        display.addItemsIcons(event.getTreeItemIcons());
    }

    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (event.getView() instanceof ProjectExplorerDisplay) {
            display = null;
        }
    }

//   @Override
//   public void onItemsSelected(ItemsSelectedEvent event)
//   {
//      if (event.getSelectedItems().size() == 1)
//      {
//         Item item = event.getSelectedItems().get(0);
//         if (item.getItemType().equals(ItemType.PROJECT))
//         {
//            currentProject = (ProjectModel)item;
//         }
//         else
//         {
//            currentProject = map.get(item.getId());
//         }
//         if (currentProject != null)
//         {
//            IDE.fireEvent(new ActiveProjectChangedEvent(currentProject));
//         }
//         else
//         {
//            IDE.fireEvent(new ActiveProjectChangedEvent(openedProject));
//         }
//      }
//   }

    private String lastNavigatorId = null;

    protected ProjectModel projectTree;

    protected ItemNode tree;

    @Override
    public void onViewActivated(ViewActivatedEvent event) {
        if ("ideWorkspaceView".equals(event.getView().getId()) && !event.getView().getId().equals(lastNavigatorId)) {
            lastNavigatorId = "ideWorkspaceView";
        } else if ("ideTinyProjectExplorerView".equals(event.getView().getId())
                   && !event.getView().getId().equals(lastNavigatorId)) {
            lastNavigatorId = "ideTinyProjectExplorerView";
        }

        if (event.getView() instanceof ProjectExplorerDisplay) {
            treeItemSelected();
        }
    }


    @Override
    public void onVfsChanged(VfsChangedEvent event) {
        if (display == null) {
            return;
        }

        display.setProjectExplorerTreeVisible(false);
        if (ideLoadComplete) {
            refreshProjectsList();
        }

        display.asView().setTitle(DEFAULT_TITLE);
    }


    @Override
    public void onGoToFolder(GoToItemEvent event) {
        goToFolder();
    }

    private void goToFolder() {
        if (editorActiveFile == null) {
            return;
        }

        if (display.selectItem(editorActiveFile)) {
            return;
        }

        display.navigateToItem(editorActiveFile);
    }

   /*
   * Linking With Editor functionality
   */

    /** Enabled or disabled Linking with Editor. */
    private boolean linkingWithEditor = false;

    /** Opened files. */
    private Map<String, FileModel> openedFiles = new HashMap<String, FileModel>();

    /** Link with Editor button click handler. */
    private ClickHandler linkWithEditorButtonClickHandler = new ClickHandler() {
        @Override
        public void onClick(ClickEvent event) {
            linkingWithEditor = !linkingWithEditor;
            display.setLinkWithEditorButtonSelected(linkingWithEditor);

            applicationSettings.setValue("project-explorer-linked-with-editor", new Boolean(linkingWithEditor),
                                         Store.COOKIES);

            SettingsService.getInstance().saveSettingsToCookies(applicationSettings);
         /*
          * fire event for show-hide line numbers command be able to update state.
          */
            IDE.fireEvent(new ApplicationSettingsSavedEvent(applicationSettings, SaveType.COOKIES));

            if (linkingWithEditor) {
                goToFolder();
            }
        }
    };

    /** @see org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform
     * .ide.client.framework.editor.event.EditorActiveFileChangedEvent) */
    @Override
    public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event) {
        editorActiveFile = event.getFile();

        if (ideLoadComplete && linkingWithEditor) {
            if (selectedItems.size() == 1 && editorActiveFile != null
                && editorActiveFile.getId().equals(selectedItems.get(0).getId())) {
                return;
            }

            goToFolder();
        }
    }

    /** Changes the active file in Editor just after item selected in the Tree. */
    private void changeActiveFileOnSelection() {
        if (!ideLoadComplete || !linkingWithEditor || selectedItems.size() != 1) {
            return;
        }

        Item selectedItem = selectedItems.get(0);
        FileModel file = openedFiles.get(selectedItem.getId());
        if (file != null && editorActiveFile != null) {
            if (!file.getId().equals(editorActiveFile.getId())) {
                IDE.fireEvent(new EditorChangeActiveFileEvent(file));
                display.asView().activate();

            }
        }
    }

    /** @see org.exoplatform.ide.client.framework.event.IDELoadCompleteHandler#onIDELoadComplete(org.exoplatform.ide.client.framework
     * .event.IDELoadCompleteEvent) */
    @Override
    public void onIDELoadComplete(IDELoadCompleteEvent event) {
        ideLoadComplete = true;

        if (linkingWithEditor) {
            goToFolder();
        }

        if (openedProject == null) {
            refreshProjectsList();
        }
    }

    /** @see org.exoplatform.ide.client.framework.editor.event.EditorFileClosedHandler#onEditorFileClosed(org.exoplatform.ide.client
     * .framework.editor.event.EditorFileClosedEvent) */
    @Override
    public void onEditorFileClosed(EditorFileClosedEvent event) {
        openedFiles = event.getOpenedFiles();
    }

    /** @see org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedHandler#onEditorFileOpened(org.exoplatform.ide.client
     * .framework.editor.event.EditorFileOpenedEvent) */
    @Override
    public void onEditorFileOpened(EditorFileOpenedEvent event) {
        openedFiles = event.getOpenedFiles();
    }

    /** @see org.exoplatform.ide.client.navigation.handler.ShowHideHiddenFilesHandler#onShowHideHiddenFiles(org.exoplatform.ide.client
     * .navigation.event.ShowHideHiddenFilesEvent) */
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

    /** Refreshes the list of existing projects in project explorer and changes visibility of projects list. */
    private void refreshProjectsList() {
        try {
            VirtualFileSystem.getInstance().getChildren(VirtualFileSystem.getInstance().getInfo().getRoot(),
                                                        ItemType.PROJECT, new AsyncRequestCallback<List<Item>>(
                    new ChildrenUnmarshaller(new ArrayList<Item>())) {
                @Override
                protected void onSuccess(List<Item> result) {
                    if (openedProject != null) {
                        display.setProjectsListGridVisible(false);
                        IDE.fireEvent(new ProjectSelectedEvent(null));
                        display.setProjectNotOpenedPanelVisible(false);
                        return;
                    }

                    List<ProjectModel> projects = new ArrayList<ProjectModel>();
                    for (Item item : result) {
                        if (item instanceof ProjectModel) {
                            projects.add((ProjectModel)item);
                        }
                    }

                    Collections.sort(projects, PROJECT_COMPARATOR);
                    display.getProjectsListGrid().setValue(projects);

                    if (projects.size() == 0) {
                        display.setProjectsListGridVisible(false);
                        IDE.fireEvent(new ProjectSelectedEvent(null));
                        display.setProjectNotOpenedPanelVisible(true);
                    } else {
                        display.setProjectsListGridVisible(true);
                        display.setProjectNotOpenedPanelVisible(false);
                    }
                }

                @Override
                protected void onFailure(Throwable exception) {
                    IDE.fireEvent(new ExceptionThrownEvent(exception, "Searching of projects failed."));
                }
            });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e, "Searching of projects failed."));
        }
    }

    /** @see org.exoplatform.ide.vfs.client.event.ItemDeletedHandler#onItemDeleted(org.exoplatform.ide.vfs.client.event.ItemDeletedEvent) */
    @Override
    public void onItemDeleted(ItemDeletedEvent event) {
        if (event.getItem() instanceof ProjectModel) {
            if (openedProject == null) {
                refreshProjectsList();
            }
        }
    }


    /** @see org.exoplatform.ide.client.framework.project.ProjectOpenedHandler#onProjectOpened(org.exoplatform.ide.client.framework
     * .project.ProjectOpenedEvent) */
    @Override
    public void onProjectOpened(final ProjectOpenedEvent projectOpenedEvent) {
        openedProject = projectOpenedEvent.getProject();
        ensureProjectExplorerDisplayCreated();

        if (ProjectUpdater.isNeedUpdateProject(openedProject)) {
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
            display.setProjectExplorerTreeVisible(false);
            refreshProjectsList();
            return;
        }

        if (ProjectUpdater.isNeedUpdateProject(openedProject)) {
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

    private void showProjectTree() {
        display.asView().setTitle(openedProject.getName());

        display.setProjectExplorerTreeVisible(true);
        display.setProject(openedProject);

        selectedItems = new ArrayList<Item>();

        display.setLinkWithEditorButtonEnabled(true);
        display.setLinkWithEditorButtonSelected(linkingWithEditor);
    }

    @Override
    public void onTreeRefreshed(final TreeRefreshedEvent event) {
        if (display != null) {
            display.refreshTree();

            if (event.getItemToSelect() != null) {
                display.navigateToItem(event.getItemToSelect());
                //display.selectItem(event.getItemToSelect());
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
        display.setProjectExplorerTreeVisible(false);
        refreshProjectsList();
        display.setLinkWithEditorButtonEnabled(false);

        selectedItems.clear();
        IDE.fireEvent(new ItemsSelectedEvent(selectedItems, display.asView()));
    }

}
