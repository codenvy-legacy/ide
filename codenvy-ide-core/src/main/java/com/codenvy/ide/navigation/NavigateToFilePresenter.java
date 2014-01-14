/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 *  [2012] - [2013] Codenvy, S.A. 
 *  All Rights Reserved.
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
package com.codenvy.ide.navigation;

import com.codenvy.ide.api.resources.FileEvent;
import com.codenvy.ide.api.resources.FileEvent.FileOperation;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.collections.StringMap;
import com.codenvy.ide.resources.model.File;
import com.codenvy.ide.resources.model.Folder;
import com.codenvy.ide.resources.model.Resource;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Presenter for file navigation (find file by name and open it).
 * 
 * @author Ann Shumilova
 */
@Singleton
public class NavigateToFilePresenter implements NavigateToFileView.ActionDelegate {

    private NavigateToFileView view;
    private ResourceProvider   resourceProvider;
    private EventBus           eventBus;
    private StringMap<File>    projectFiles;

    @Inject
    public NavigateToFilePresenter(NavigateToFileView view, ResourceProvider resourceProvider, EventBus eventBus) {
        this.resourceProvider = resourceProvider;
        this.view = view;
        this.eventBus = eventBus;
        view.setDelegate(this);
    }

    /**
     * Show dialog with view for navigation.
     */
    public void showDialog() {
        Array<String> files = Collections.createArray();
        projectFiles = Collections.createStringMap();
        getFiles(resourceProvider.getActiveProject().getChildren(), files);
        view.setFiles(files);
        view.showDialog();
        view.clearInput();
        view.focusInput();
    }

    /**
     * Recursively get project's files.
     * 
     * @param children children where to search files
     * @param files found files
     */
    private void getFiles(Array<Resource> children, Array<String> files) {
        for (Resource child : children.asIterable()) {
            if (child.isFile()) {
                String displayName = getDisplayName((File)child);
                projectFiles.put(displayName, (File)child);
                files.add(displayName);
            } else if (child instanceof Folder) {
                getFiles(((Folder)child).getChildren(), files);
            }
        }
    }

    /**
     * Returns the formed display name of the file.
     * 
     * @param file file to display
     * @return {@link String}
     */
    private String getDisplayName(File file)
    {
        String displayName = file.getName();
        String path = file.getParent() != null ? "   (" + file.getParent().getPath().replaceFirst("/", "") + ")" : "";
        return displayName + path;
    }

    /** {@inheritDoc} */
    @Override
    public void onFileSelected() {
        File fileToOpen = projectFiles.get(view.getFile());
        view.close();
        eventBus.fireEvent(new FileEvent(fileToOpen, FileOperation.OPEN));
    }

}
