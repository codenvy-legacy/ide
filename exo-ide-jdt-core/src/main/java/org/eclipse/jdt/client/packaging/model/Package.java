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
package org.eclipse.jdt.client.packaging.model;

import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.FolderModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 */
public class Package extends FolderModel {

    private String packageName;

    private List<FileModel> files = new ArrayList<FileModel>();

    public Package(FolderModel folder, String packageName) {
        super(folder);
        this.packageName = packageName;

        getChildren().getItems().addAll(folder.getChildren().getItems());
        setParent(folder.getParent());
        setProject(folder.getProject());
    }

    public String getPackageName() {
        return packageName;
    }

    public List<FileModel> getFiles() {
        return files;
    }

}
