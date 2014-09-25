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
import com.codenvy.ide.api.filetypes.FileType;
import com.codenvy.ide.api.projecttree.generic.FileNode;
import com.google.gwt.resources.client.ImageResource;

import org.vectomatic.dom.svg.ui.SVGResource;

/** @author Vitaly Parfonov */
final class EditorInputImpl implements EditorInput {
    private FileNode file;
    private FileType fileType;

    EditorInputImpl(FileType fileType, FileNode file) {
        this.fileType = fileType;
        this.file = file;
    }

    @Override
    public String getContentDescription() {
        return fileType.getContentDescription();
    }

    @Override
    public String getToolTipText() {
        return null;
    }

    @Override
    public String getName() {
        return file.getDisplayName();
    }

    @Override
    public ImageResource getImageResource() {
        return fileType.getImage();
    }

    @Override
    public SVGResource getSVGResource() {
        return fileType.getSVGImage();
    }

    @Override
    public FileNode getFile() {
        return file;
    }

    @Override
    public void setFile(FileNode file) {
        this.file = file;
    }
}
