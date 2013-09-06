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
package org.exoplatform.ide.client.project;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.ide.client.framework.application.IDELoader;
import org.exoplatform.ide.client.framework.event.AllFilesClosedEvent;
import org.exoplatform.ide.client.framework.event.AllFilesClosedHandler;
import org.exoplatform.ide.client.framework.event.CloseAllFilesEvent;
import org.exoplatform.ide.client.framework.event.FileSavedEvent;
import org.exoplatform.ide.client.framework.event.FileSavedHandler;
import org.exoplatform.ide.client.framework.event.RefreshBrowserEvent;
import org.exoplatform.ide.client.framework.event.RefreshBrowserHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.navigation.event.FolderRefreshedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.navigation.event.SelectItemEvent;
import org.exoplatform.ide.client.framework.project.CloseProjectEvent;
import org.exoplatform.ide.client.framework.project.CloseProjectHandler;
import org.exoplatform.ide.client.framework.project.OpenProjectEvent;
import org.exoplatform.ide.client.framework.project.OpenProjectHandler;
import org.exoplatform.ide.client.framework.project.ProjectClosedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.project.api.IDEProject;
import org.exoplatform.ide.client.framework.project.api.IDEProject.FolderChangedHandler;
import org.exoplatform.ide.client.framework.project.api.ProjectBuilder;
import org.exoplatform.ide.client.framework.project.api.TreeRefreshedEvent;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.shared.Folder;
import org.exoplatform.ide.vfs.shared.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 */
public class ProjectProcessor implements OpenProjectHandler, CloseProjectHandler, AllFilesClosedHandler,
                             RefreshBrowserHandler, ItemsSelectedHandler, FolderChangedHandler, FileSavedHandler {

    private IDEProject openedProject;

    private List<Item> selectedItems;

    public ProjectProcessor() {
        IDE.addHandler(OpenProjectEvent.TYPE, this);
        IDE.addHandler(CloseProjectEvent.TYPE, this);
        IDE.addHandler(RefreshBrowserEvent.TYPE, this);
        IDE.addHandler(ItemsSelectedEvent.TYPE, this);
        IDE.addHandler(FileSavedEvent.TYPE, this);
    }

    @Override
    public void onOpenProject(OpenProjectEvent event) {
        if (openedProject != null) {
            return;
        }

        openedProject = ProjectBuilder.createProject(event.getProject());
        openedProject.setFolderChangedHandler(ProjectProcessor.this);

        IDELoader.show("Loading project...");
        openedProject.refresh(openedProject, new AsyncCallback<Folder>() {
            @Override
            public void onFailure(Throwable caught) {
                caught.printStackTrace();

                IDELoader.hide();
                IDE.fireEvent(new ExceptionThrownEvent(caught));
            }

            @Override
            public void onSuccess(Folder result) {
                IDELoader.hide();
                // openedProject.dump();
                IDE.fireEvent(new ProjectOpenedEvent(openedProject));

                new Timer() {
                    @Override
                    public void run() {
                        IDE.fireEvent(new SelectItemEvent(openedProject));
                    }
                }.schedule(200);
            }
        });

    }

    @Override
    public void onItemsSelected(ItemsSelectedEvent event) {
        selectedItems = event.getSelectedItems();
    }

    private List<FolderModel> foldersToBeRefreshed = new ArrayList<FolderModel>();

    private List<FolderModel> refreshedFolders     = new ArrayList<FolderModel>();

    private Item              itemToBeSelectedAfterRefreshing;

    @Override
    public void onRefreshBrowser(RefreshBrowserEvent event) {
        if (openedProject == null) {
            return;
        }

        foldersToBeRefreshed.clear();
        for (Folder f : event.getFolders()) {
            foldersToBeRefreshed.add((FolderModel)f);
        }

        if (foldersToBeRefreshed.isEmpty() && !selectedItems.isEmpty()) {
            for (Item i : selectedItems) {
                if (i instanceof FolderModel) {
                    foldersToBeRefreshed.add((FolderModel)i);
                } else if (i instanceof FileModel) {
                    foldersToBeRefreshed.add(((FileModel)i).getParent());
                }
            }
        }

        itemToBeSelectedAfterRefreshing = event.getItemToSelect();
        if (itemToBeSelectedAfterRefreshing == null && selectedItems.size() == 1) {
            itemToBeSelectedAfterRefreshing = selectedItems.get(0);
        }

        refreshedFolders.clear();
        refreshFolders();
    }

    private Item getItemToSelect(Item item) {
        while (true) {
            try {
                openedProject.getResource(item.getPath());
                return item;
            } catch (Exception e) {
                // e.printStackTrace();
                if (item instanceof FolderModel) {
                    item = ((FolderModel)item).getParent();
                } else if (item instanceof FileModel) {
                    item = ((FileModel)item).getParent();
                } else {
                    return null;
                }
            }
        }
    }

    private void refreshFolders() {
        if (foldersToBeRefreshed.size() == 0) {
            while (!refreshedFolders.isEmpty()) {
                if (itemToBeSelectedAfterRefreshing != null) {
                    itemToBeSelectedAfterRefreshing = getItemToSelect(itemToBeSelectedAfterRefreshing);
                }

                final FolderModel folder = refreshedFolders.remove(0);
                IDE.fireEvent(new TreeRefreshedEvent(folder, itemToBeSelectedAfterRefreshing));
                Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                    @Override
                    public void execute() {
                        IDE.fireEvent(new FolderRefreshedEvent(folder));
                    }
                });
            }

            return;
        }

        FolderModel folder = foldersToBeRefreshed.remove(0);

        IDELoader.show("Refreshing...");
        openedProject.refresh(folder, new AsyncCallback<Folder>() {
            @Override
            public void onSuccess(Folder result) {
                IDELoader.hide();

                refreshedFolders.add((FolderModel)result);
                Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                    @Override
                    public void execute() {
                        refreshFolders();
                    }
                });
            }

            @Override
            public void onFailure(Throwable caught) {
                IDELoader.hide();
                caught.printStackTrace();
                // IDE.fireEvent(new ExceptionThrownEvent(caught));
            }
        });
    }

    HandlerRegistration allFilesClosedHandler;

    @Override
    public void onCloseProject(CloseProjectEvent event) {
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                if (allFilesClosedHandler == null)
                {
                    allFilesClosedHandler = IDE.addHandler(AllFilesClosedEvent.TYPE, ProjectProcessor.this);
                }

                Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                    @Override
                    public void execute() {
                        IDE.fireEvent(new CloseAllFilesEvent());
                    }
                });
            }
        });
    }

    @Override
    public void onAllFilesClosed(AllFilesClosedEvent event) {
        if (allFilesClosedHandler != null)
        {
            allFilesClosedHandler.removeHandler();
            allFilesClosedHandler = null;
        }

        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                if (openedProject == null) {
                    return;
                }

                final IDEProject closedProject = openedProject;
                closedProject.setFolderChangedHandler(null);
                openedProject = null;
                IDE.fireEvent(new ProjectClosedEvent(closedProject));
            }
        });
    }

    @Override
    public void onFolderChanged(final FolderModel folder) {
        if (folder != null) {
            IDE.fireEvent(new TreeRefreshedEvent(folder, itemToBeSelectedAfterRefreshing));
        } else {
            IDE.fireEvent(new TreeRefreshedEvent(openedProject, itemToBeSelectedAfterRefreshing));
        }
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                if (folder != null) {
                    IDE.fireEvent(new FolderRefreshedEvent(folder));
                } else {
                    IDE.fireEvent(new FolderRefreshedEvent(openedProject));
                }
            }
        });
    }

    @Override
    public void onFileSaved(final FileSavedEvent event) {
        if (openedProject != null) {
            Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                @Override
                public void execute() {
                    openedProject.resourceChanged(event.getFile());
                }
            });
        }
    }

}
