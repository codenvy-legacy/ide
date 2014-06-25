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
package com.codenvy.ide.core.editor;

import com.codenvy.ide.api.editor.EditorInput;
import com.codenvy.ide.api.resources.FileType;
import com.codenvy.ide.api.resources.model.File;
import com.google.gwt.resources.client.ImageResource;

/**
* @author Vitaly Parfonov
*/
final class EditorInputImpl implements EditorInput {
    private File            file;
    private FileType fileType;

    /** @param file */
    EditorInputImpl(FileType fileType, File file) {
        this.fileType = fileType;
        this.file = file;
    }

    @Override
    public String getContentDescription() {
        return fileType.getContentDescription();
    }

    @Override
    public String getToolTipText() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getName() {
        return file.getName();
    }

    @Override
    public ImageResource getImageResource() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public File getFile() {
        return file;
    }

    /** {@inheritDoc} */
    @Override
    public void setFile(File file) {
        this.file = file;
    }
}
