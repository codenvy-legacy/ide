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
package com.codenvy.ide.ext.java.client.projectmodel;

import com.codenvy.api.project.shared.dto.ItemReference;
import com.codenvy.ide.api.resources.model.Folder;

/** @author Nikolay Zamosenchuk */
public class SourceFolder extends Folder {
    public static final String TYPE = "java.sourcefolder";
    private String sourceFolderName;

    protected SourceFolder() {
        super(TYPE, FOLDER_MIME_TYPE);
    }

    /**
     * Init Java Source Folder from {@link ItemReference}.
     *
     * @param itemReference
     */
    protected SourceFolder(ItemReference itemReference, String name) {
        this();
        init(itemReference);
        sourceFolderName = name;
    }

    /** {@inheritDoc} */
    @Override
    public String getName() {
        return sourceFolderName;
    }

    /** {@inheritDoc} */
    @Override
    public String getPath() {
        return parent.getPath() + "/" + getName();
    }

    public void init(ItemReference object, String projectPath) {
        init(object);
        sourceFolderName = object.getName();
    }
}
