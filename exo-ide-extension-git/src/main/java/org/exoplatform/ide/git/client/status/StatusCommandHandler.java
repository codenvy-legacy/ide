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
package org.exoplatform.ide.git.client.status;

import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.URL;
import com.google.gwt.resources.client.ImageResource;

import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.AutoBeanUnmarshaller;
import org.exoplatform.gwtframework.ui.client.component.TreeIconPosition;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.navigation.event.AddItemTreeIconEvent;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage;
import org.exoplatform.ide.client.framework.project.ProjectClosedEvent;
import org.exoplatform.ide.client.framework.project.ProjectClosedHandler;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedHandler;
import org.exoplatform.ide.client.framework.project.api.FolderOpenedEvent;
import org.exoplatform.ide.client.framework.project.api.FolderOpenedHandler;
import org.exoplatform.ide.git.client.GitClientBundle;
import org.exoplatform.ide.git.client.GitClientService;
import org.exoplatform.ide.git.client.GitExtension;
import org.exoplatform.ide.git.client.GitPresenter;
import org.exoplatform.ide.git.client.marshaller.StringUnmarshaller;
import org.exoplatform.ide.git.shared.Status;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Handler to process actions with displaying the status of the Git work tree.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Mar 28, 2011 3:58:20 PM anya $
 */
public class StatusCommandHandler extends GitPresenter implements ShowWorkTreeStatusHandler, ProjectOpenedHandler, ProjectClosedHandler,
                                                      FolderOpenedHandler {
    /**
     * Store the status of the working tree (changed, untracked files). Status will be checked once, only when expands project item in
     * Project Explorer.
     */
    private Status       workingTreeStatus;

    private ProjectModel openedProject;

    public StatusCommandHandler() {
        IDE.addHandler(ShowWorkTreeStatusEvent.TYPE, this);
        IDE.addHandler(ProjectOpenedEvent.TYPE, this);
        IDE.addHandler(FolderOpenedEvent.TYPE, this);
        IDE.addHandler(ProjectClosedEvent.TYPE, this);
    }

    /**
     * Get the status for Git work tree and display it on success.
     */
    @SuppressWarnings("unchecked")
    private void getStatusText(ProjectModel project) {
        if (project == null) {
            return;
        }
        try {
            GitClientService.getInstance().statusText(vfs.getId(), project.getId(), false,
                                                      new AsyncRequestCallback(new StringUnmarshaller(new StringBuilder())) {
                                                          @Override
                                                          protected void onSuccess(Object result) {
                                                              String output = result.toString();
                                                              IDE.fireEvent(new OutputEvent(output, OutputMessage.Type.GIT));
                                                          }

                                                          @Override
                                                          protected void onFailure(Throwable exception) {
                                                              String errorMessage =
                                                                                    (exception.getMessage() != null)
                                                                                        ? exception.getMessage()
                                                                                        : GitExtension.MESSAGES.statusFailed();
                                                              IDE.fireEvent(new OutputEvent(errorMessage, OutputMessage.Type.GIT));
                                                          }
                                                      });
        } catch (RequestException e) {
            String errorMessage = (e.getMessage() != null) ? e.getMessage() : GitExtension.MESSAGES.statusFailed();
            IDE.fireEvent(new OutputEvent(errorMessage, OutputMessage.Type.GIT));
        }
    }

    /**
     * @see org.exoplatform.ide.client.framework.navigation.event.FolderRefreshedHandler#onFolderRefreshed(org.exoplatform.ide.client
     *      .framework.navigation.event.FolderRefreshedEvent)
     * @Override public void onFolderRefreshed(FolderRefreshedEvent event) { FolderModel folder = (FolderModel)event.getFolder(); if
     *           (folder.getChildren().getItems().isEmpty() || folder.getId() == null || folder.getId().isEmpty()) { return; }
     *           getStatus(folder, true, new ArrayList<Item>()); }
     */

    /**
     * Get the files in different state of Git cycle and mark them in browser tree.
     * 
     * @param folder folder to be updated
     */
    private void getStatus(final FolderModel folder, boolean forced, final List<Item> additionalItems) {
        if (openedProject.getProperty(GitExtension.GIT_REPOSITORY_PROP) == null)
            return;
        if (!folder.getId().equals(openedProject.getId()) && !forced) {
            addItemsTreeIcons(folder, additionalItems);
            return;
        }

        try {
            GitClientService.getInstance()
                            .status(vfs.getId(),
                                    openedProject.getId(),
                                    new AsyncRequestCallback<Status>(
                                                                     new AutoBeanUnmarshaller<Status>(
                                                                                                      GitExtension.AUTO_BEAN_FACTORY.status())) {
                                        @Override
                                        protected void onSuccess(Status result) {
                                            workingTreeStatus = result;
                                            addItemsTreeIcons(folder, additionalItems);
                                        }

                                        @Override
                                        protected void onFailure(Throwable exception) {
                                        }
                                    });
        } catch (RequestException ignored) {
        }
    }

    /**
     * Update icons for all items in the specified folder.
     * 
     * @param project project
     * @param folder folder to be updated
     */
    private void addItemsTreeIcons(FolderModel folder, List<Item> additionalItems) {
        if (workingTreeStatus == null) {
            return;
        }

        Map<Item, Map<TreeIconPosition, ImageResource>> treeNodesToUpdate =
                                                                            new HashMap<Item, Map<TreeIconPosition, ImageResource>>();

        List<Item> itemsToCheck = new ArrayList<Item>();

        itemsToCheck.add(folder);
        itemsToCheck.addAll(folder.getChildren().getItems());
        itemsToCheck.addAll(additionalItems);

        for (Item item : itemsToCheck) {
            String path = URL.decodePathSegment(item.getPath());
            String pattern = path.replaceFirst(openedProject.getPath(), "");
            pattern = (pattern.startsWith("/")) ? pattern.replaceFirst("/", "") : pattern;
            Map<TreeIconPosition, ImageResource> map = new HashMap<TreeIconPosition, ImageResource>();
            if (pattern.length() == 0 || "/".equals(pattern)) {
                map.put(TreeIconPosition.BOTTOMRIGHT, GitClientBundle.INSTANCE.itemRoot());
            } else if (contains(workingTreeStatus.getAdded(), pattern)) {
                map.put(TreeIconPosition.BOTTOMRIGHT, GitClientBundle.INSTANCE.itemAdded());
            } else if (contains(workingTreeStatus.getChanged(), pattern)) {
                map.put(TreeIconPosition.BOTTOMRIGHT, GitClientBundle.INSTANCE.itemChanged());
            } else if (contains(workingTreeStatus.getConflicting(), pattern)) {
                map.put(TreeIconPosition.BOTTOMRIGHT, GitClientBundle.INSTANCE.itemConflicting());
            } else if (contains(workingTreeStatus.getMissing(), pattern)) {
                map.put(TreeIconPosition.BOTTOMRIGHT, GitClientBundle.INSTANCE.itemMissing());
            } else if (contains(workingTreeStatus.getRemoved(), pattern)) {
                map.put(TreeIconPosition.BOTTOMRIGHT, GitClientBundle.INSTANCE.itemRemoved());
            } else if (contains(workingTreeStatus.getModified(), pattern)) {
                map.put(TreeIconPosition.BOTTOMRIGHT, GitClientBundle.INSTANCE.itemModified());
            } else if (contains(workingTreeStatus.getUntracked(), pattern)) {
                map.put(TreeIconPosition.BOTTOMRIGHT, GitClientBundle.INSTANCE.itemUntracked());
            } else if (contains(workingTreeStatus.getUntrackedFolders(), pattern)) {
                map.put(TreeIconPosition.BOTTOMRIGHT, GitClientBundle.INSTANCE.itemUntracked());
            } else {
                map.put(TreeIconPosition.BOTTOMRIGHT, GitClientBundle.INSTANCE.itemInRepo());
            }
            treeNodesToUpdate.put(item, map);
        }
        IDE.fireEvent(new AddItemTreeIconEvent(treeNodesToUpdate));
    }

    /**
     * Check whether files from Git status contain the match with pointed pattern.
     * 
     * @param files file paths in status
     * @param pattern pattern to compare
     * @return pattern matchers one of the files in the list or not
     */
    private boolean contains(Set<String> files, String pattern) {
        for (String file : files) {
            if (pattern.equals(file)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onProjectOpened(ProjectOpenedEvent event) {
        openedProject = event.getProject();
        getStatus(openedProject, true, new ArrayList<Item>());
    }

    @Override
    public void onFolderOpened(FolderOpenedEvent event) {
        getStatus(event.getFolder(), false, event.getChildren());
    }

    @Override
    public void onProjectClosed(ProjectClosedEvent event) {
        openedProject = null;
    }

    @Override
    public void onShowWorkTreeStatus(ShowWorkTreeStatusEvent event) {
        if (makeSelectionCheck()) {
            getStatusText(getSelectedProject());
        }
    }

}
