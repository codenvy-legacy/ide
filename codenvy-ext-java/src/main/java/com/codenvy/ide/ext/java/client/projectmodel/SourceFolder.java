/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
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
