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
package org.eclipse.jdt.client.packaging;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Timer;

import org.eclipse.jdt.client.packaging.model.Dependencies;
import org.eclipse.jdt.client.packaging.model.Dependency;
import org.eclipse.jdt.client.packaging.model.JavaProject;
import org.exoplatform.ide.client.framework.control.Docking;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorChangeActiveFileEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedHandler;
import org.exoplatform.ide.client.framework.event.OpenFileEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.navigation.event.AddItemTreeIconEvent;
import org.exoplatform.ide.client.framework.navigation.event.AddItemTreeIconHandler;
import org.exoplatform.ide.client.framework.navigation.event.GoToItemEvent;
import org.exoplatform.ide.client.framework.navigation.event.GoToItemHandler;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.navigation.event.RemoveItemTreeIconEvent;
import org.exoplatform.ide.client.framework.navigation.event.RemoveItemTreeIconHandler;
import org.exoplatform.ide.client.framework.navigation.event.SelectItemEvent;
import org.exoplatform.ide.client.framework.navigation.event.SelectItemHandler;
import org.exoplatform.ide.client.framework.navigation.event.ShowHideHiddenFilesEvent;
import org.exoplatform.ide.client.framework.navigation.event.ShowHideHiddenFilesHandler;
import org.exoplatform.ide.client.framework.project.PackageExplorerDisplay;
import org.exoplatform.ide.client.framework.project.ProjectClosedEvent;
import org.exoplatform.ide.client.framework.project.ProjectClosedHandler;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedHandler;
import org.exoplatform.ide.client.framework.project.api.FolderOpenedEvent;
import org.exoplatform.ide.client.framework.project.api.IDEProject;
import org.exoplatform.ide.client.framework.project.api.ProjectBuilder;
import org.exoplatform.ide.client.framework.project.api.ProjectBuilder.Builder;
import org.exoplatform.ide.client.framework.project.api.TreeRefreshedEvent;
import org.exoplatform.ide.client.framework.project.api.TreeRefreshedHandler;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings.Store;
import org.exoplatform.ide.client.framework.settings.ApplicationSettingsReceivedEvent;
import org.exoplatform.ide.client.framework.settings.ApplicationSettingsReceivedHandler;
import org.exoplatform.ide.client.framework.settings.ApplicationSettingsSavedEvent;
import org.exoplatform.ide.client.framework.settings.SaveApplicationSettingsEvent;
import org.exoplatform.ide.client.framework.settings.SaveApplicationSettingsEvent.SaveType;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.ui.api.event.ViewOpenedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewOpenedHandler;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 */
public class PackageExplorerPresenter implements ShowPackageExplorerHandler, 
        ViewOpenedHandler, ViewClosedHandler, ProjectOpenedHandler, ProjectClosedHandler, 
        SelectItemHandler, EditorActiveFileChangedHandler, EditorFileOpenedHandler, 
        EditorFileClosedHandler, ApplicationSettingsReceivedHandler, ItemsSelectedHandler, 
        TreeRefreshedHandler, AddItemTreeIconHandler, RemoveItemTreeIconHandler, ShowHideHiddenFilesHandler, GoToItemHandler {

    private static final String    PACKAGE_EXPLORER_LINK_WITH_EDITOR_CONFIG = "package-explorer-linked-with-editor";

    private PackageExplorerDisplay display;

    private IDEProject             project;

    private boolean                linkWithEditor                           = false;

    private FileModel              editorActiveFile;

    private Map<String, FileModel> openedFiles                              = new HashMap<String, FileModel>();

    public PackageExplorerPresenter() {
        IDE.getInstance().addControl(new ShowPackageExplorerControl(), Docking.TOOLBAR);

        IDE.addHandler(ShowPackageExplorerEvent.TYPE, this);

        IDE.addHandler(ViewOpenedEvent.TYPE, this);
        IDE.addHandler(ViewClosedEvent.TYPE, this);

        IDE.addHandler(ProjectOpenedEvent.TYPE, this);
        IDE.addHandler(ProjectClosedEvent.TYPE, this);

        IDE.addHandler(SelectItemEvent.TYPE, this);

        IDE.addHandler(EditorActiveFileChangedEvent.TYPE, this);
        IDE.addHandler(EditorFileOpenedEvent.TYPE, this);
        IDE.addHandler(EditorFileClosedEvent.TYPE, this);
        IDE.addHandler(ApplicationSettingsReceivedEvent.TYPE, this);

        IDE.addHandler(ItemsSelectedEvent.TYPE, this);
        IDE.addHandler(TreeRefreshedEvent.TYPE, this);
        IDE.addHandler(AddItemTreeIconEvent.TYPE, this);
        IDE.addHandler(RemoveItemTreeIconEvent.TYPE, this);
        IDE.addHandler(ShowHideHiddenFilesEvent.TYPE, this);
        IDE.addHandler(GoToItemEvent.TYPE, this);

        for (String type : JavaProjects.getList()) {
            ProjectBuilder.addBuilder(type, new Builder() {
                @Override
                public IDEProject build(ProjectModel project) {
                    return new JavaProject(project);
                }
            });
        }
    }

    @Override
    public void onShowPackageExplorer(ShowPackageExplorerEvent event) {
        if (display == null) {
            if (project == null) {
                return;
            }

            if (!JavaProjects.contains(project)) {
                return;
            }

            display = GWT.create(PackageExplorerDisplay.class);
            bindDisplay();
            IDE.getInstance().openView(display.asView());
        } else {
            IDE.getInstance().closeView(display.asView().getId());
        }
    }

    private void bindDisplay() {
        display.getBrowserTree().addOpenHandler(new OpenHandler<Item>() {
            @Override
            public void onOpen(final OpenEvent<Item> event) {
                Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                    @Override
                    public void execute() {
                        if (!(event.getTarget() instanceof FolderModel)) {
                            return;
                        }

                        FolderModel folder = (FolderModel)event.getTarget();
                        List<Item> children = display.getVisibleItems();
                        IDE.fireEvent(new FolderOpenedEvent(folder, children));
                    }
                });
            }
        });
        
        display.getBrowserTree().addCloseHandler(new CloseHandler<Item>() {
            @Override
            public void onClose(final CloseEvent<Item> event) {
                Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                    @Override
                    public void execute() {
                        if (!(event.getTarget() instanceof FolderModel)) {
                            return;
                        }

                        FolderModel folder = (FolderModel)event.getTarget();
                        List<Item> children = display.getVisibleItems();
                        IDE.fireEvent(new FolderOpenedEvent(folder, children));
                    }
                });                
            }
        });

        display.getBrowserTree().addDoubleClickHandler(new DoubleClickHandler()
        {
            @Override
            public void onDoubleClick(DoubleClickEvent event)
            {
                Item selectedItem = display.getSelectedItem();
                if (selectedItem instanceof FileModel)
                {
                    IDE.fireEvent(new OpenFileEvent((FileModel)selectedItem));
                }
            }
        });

        display.getBrowserTree().addSelectionHandler(new SelectionHandler<Item>()
        {
            @Override
            public void onSelection(SelectionEvent<Item> event)
            {
                Scheduler.get().scheduleDeferred(new ScheduledCommand()
                {
                    @Override
                    public void execute()
                    {
                        treeItemSelected();
                    }
                });
            }
        });

        display.getLinkWithEditorButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                linkWithEditorButtonClicked();
            }
        });

        display.setLinkWithEditorButtonSelected(linkWithEditor);
    }

    private void treeItemSelected() {
        Item selectedItem = display.getSelectedItem();

        if (selectedItem instanceof Dependencies ||
            selectedItem instanceof Dependency) {

            IDE.fireEvent(new ItemsSelectedEvent((Item)null, display.asView()));
        } else if (selectedItem != null) {
            changeActiveFile(selectedItem);
            IDE.fireEvent(new ItemsSelectedEvent(selectedItem, display.asView()));
        }
    }

    /**
     * Switch Editor to selected file.
     * 
     * @param item
     */
    private void changeActiveFile(final Item item) {
        if (!linkWithEditor || item == null || !(item instanceof FileModel) || !openedFiles.containsKey(item.getId())
            || editorActiveFile.getId().equals(item.getId())) {
            return;
        }

        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                IDE.fireEvent(new EditorChangeActiveFileEvent((FileModel)item));
            }
        });
    }

    @Override
    public void onSelectItem(final SelectItemEvent event) {
        if (display == null) {
            return;
        }

        if (display.asView().isViewVisible()) {
            display.selectItem(event.getItem());

            Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                @Override
                public void execute() {
                    IDE.fireEvent(new ItemsSelectedEvent(event.getItem(), display.asView()));
                }
            });
        }
    }

    /** Handle click on "Link with Editor" button. */
    private void linkWithEditorButtonClicked() {
        linkWithEditor = !linkWithEditor;
        display.setLinkWithEditorButtonSelected(linkWithEditor);

        applicationSettings
                           .setValue(PACKAGE_EXPLORER_LINK_WITH_EDITOR_CONFIG, new Boolean(linkWithEditor), Store.COOKIES);


        IDE.fireEvent(new SaveApplicationSettingsEvent(applicationSettings, SaveType.COOKIES));

        /*
         * Fire event for show-hide line numbers command be able to update state.
         */
        IDE.fireEvent(new ApplicationSettingsSavedEvent(applicationSettings, SaveType.COOKIES));

        if (linkWithEditor && editorActiveFile != null) {
            IDE.fireEvent(new TreeRefreshedEvent(project));
            display.selectItem(editorActiveFile);
        }
    }

    /**
     * @see org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform
     *      .ide.client.framework.editor.event.EditorActiveFileChangedEvent)
     */
    @Override
    public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event) {
        editorActiveFile = event.getFile();
        if (display != null && linkWithEditor && editorActiveFile != null) {
            display.selectItem(event.getFile());
            if (linkWithEditor) {
                IDE.fireEvent(new TreeRefreshedEvent(project));
            }
        }
    }

    @Override
    public void onEditorFileClosed(EditorFileClosedEvent event) {
        openedFiles = event.getOpenedFiles();
    }

    @Override
    public void onEditorFileOpened(EditorFileOpenedEvent event) {
        openedFiles = event.getOpenedFiles();
    }

    private ApplicationSettings applicationSettings;

    public void onApplicationSettingsReceived(ApplicationSettingsReceivedEvent event) {
        applicationSettings = event.getApplicationSettings();

        if (applicationSettings.getValueAsBoolean(PACKAGE_EXPLORER_LINK_WITH_EDITOR_CONFIG) == null) {
            applicationSettings.setValue(PACKAGE_EXPLORER_LINK_WITH_EDITOR_CONFIG, Boolean.FALSE, Store.COOKIES);
        } else {
            linkWithEditor = applicationSettings.getValueAsBoolean(PACKAGE_EXPLORER_LINK_WITH_EDITOR_CONFIG);
        }
    }

    @Override
    public void onProjectOpened(ProjectOpenedEvent event) {
        project = (IDEProject)event.getProject();

        if (display == null) {
            if (JavaProjects.contains(project)) {
                display = GWT.create(PackageExplorerDisplay.class);
                bindDisplay();
                IDE.getInstance().openView(display.asView());
            }

            return;
        }

        if (!JavaProjects.contains(project)) {
            IDE.getInstance().closeView(display.asView().getId());
            return;
        }

        display.setProject(project);
    }

    @Override
    public void onProjectClosed(ProjectClosedEvent event) {
        project = null;

        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                if (display != null) {
                    IDE.getInstance().closeView(display.asView().getId());
                }
            }
        });
    }

    @Override
    public void onViewOpened(ViewOpenedEvent event) {
        if (event.getView() instanceof PackageExplorerDisplay && project != null) {
            display.setProject(project);
        }
    }

    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (event.getView() instanceof PackageExplorerDisplay) {
            display = null;
        }
    }

    @Override
    public void onItemsSelected(ItemsSelectedEvent event) {
        if (event.getSelectedItems().size() == 1 && linkWithEditor) {
            Item selectedItem = event.getSelectedItems().get(0);
            if (editorActiveFile != null && !editorActiveFile.getId().equals(selectedItem.getId()) &&
                (selectedItem instanceof FileModel)) {
                IDE.fireEvent(new EditorChangeActiveFileEvent((FileModel)selectedItem));
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

    /**
     * @see org.exoplatform.ide.client.framework.navigation.event.AddItemTreeIconHandler#onAddItemTreeIcon(org.exoplatform.ide.client
     *      .framework.navigation.event.AddItemTreeIconEvent)
     */
    @Override
    public void onAddItemTreeIcon(AddItemTreeIconEvent event) {
        if (display != null) {
            display.addItemsIcons(event.getTreeItemIcons());
        }
    }

    /**
     * @see org.exoplatform.ide.client.framework.navigation.event.RemoveItemTreeIconHandler#onRemoveItemTreeIcon(org.exoplatform.ide
     *      .client.framework.navigation.event.RemoveItemTreeIconEvent)
     */
    @Override
    public void onRemoveItemTreeIcon(RemoveItemTreeIconEvent event) {
        if (display != null) {
            display.removeItemIcons(event.getIconsToRemove());
        }
    }

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

    @Override
    public void onGoToFolder(GoToItemEvent event) {
       display.selectItem(event.getFileToOpen());
        
    }

}
