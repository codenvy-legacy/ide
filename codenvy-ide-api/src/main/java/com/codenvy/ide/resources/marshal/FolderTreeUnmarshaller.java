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
package com.codenvy.ide.resources.marshal;

import com.codenvy.api.project.shared.dto.ItemReference;
import com.codenvy.api.project.shared.dto.TreeElement;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.resources.model.File;
import com.codenvy.ide.resources.model.Folder;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.resources.model.Resource;
import com.codenvy.ide.util.loging.Log;

import java.util.List;

/**
 * Recursively traverses the JSON Response to build tree Folder model.
 *
 * @author Nikolay Zamosenchuk
 */
public class FolderTreeUnmarshaller {

    private final Folder  parentFolder;
    private final Project parentProject;

    public FolderTreeUnmarshaller(Folder parentFolder, Project parentProject) {
        this.parentFolder = parentFolder;
        this.parentFolder.setChildren(Collections.<Resource>createArray());
        this.parentProject = parentProject;
    }

    public void unmarshal(TreeElement treeElement) {
        getChildren(treeElement.getChildren(), parentFolder, parentProject);
    }

    private void getChildren(List<TreeElement> children, Folder parentFolder, Project parentProject) {
        for (TreeElement treeElement : children) {
            ItemReference child = treeElement.getNode();
            // Get item
            final String type = child.getType();

            if (Project.TYPE.equalsIgnoreCase(type)) {
                Log.error(this.getClass(), "Unsupported operation. Unmarshalling a child projects is not supported");
            } else if (Folder.TYPE.equalsIgnoreCase(type)) {
                Folder folder;
                // find if Folder already exists. This is a refresh usecase.
                Resource existingFolder = parentFolder.findChildByName(child.getName());
                // Make sure found resource is Folder
                if (existingFolder != null && Folder.TYPE.equalsIgnoreCase(existingFolder.getResourceType())) {
                    // use existing folder instance as is
                    folder = (Folder)existingFolder;
                } else {
                    folder = new Folder(child);
                    parentFolder.addChild(folder);
                    folder.setProject(parentProject);
                }
                // recursively get project
                getChildren(treeElement.getChildren(), folder, parentProject);
            } else if (File.TYPE.equalsIgnoreCase(type)) {
                File file = new File(child);
                parentFolder.addChild(file);
                file.setProject(parentProject);
            } else {
                Log.error(this.getClass(), "Unsupported resource type: " + type);
            }
        }
    }

    public Folder getPayload() {
        return parentFolder;
    }

}
