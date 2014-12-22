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

package com.codenvy.ide.api.projecttree;

import com.codenvy.ide.api.projecttree.generic.ProjectNode;
import com.google.gwt.user.client.rpc.AsyncCallback;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * File that may be opened in editor.
 * This in not necessary {@link com.codenvy.api.project.shared.dto.ItemReference}, it's may be from some external providers.
 * @author Evgen Vidolob
 */
public interface VirtualFile {

    /** get this file path */
    @Nonnull
    String getPath();

    /** get this file name*/
    @Nonnull
    String getName();

    String getDisplayName();

    /** get media type*/
    @Nullable
    String getMediaType();

    /** if user doesn't have wright rights, or file comes from external sources thad doesn't support modifying file content*/
    boolean isReadOnly();

    @Nonnull
    ProjectNode getProject();

    /**
     * Some file type can't represent their content as string.
     * So virtual file provide url where it content.
     * For example if this virtual file represent image,
     * image viewer may use this URL as src for {@link com.google.gwt.user.client.ui.Image}
     * @return url
     */
    String getContentUrl();

    /** * Get content of the file which this node represents. */
    void getContent(AsyncCallback<String> callback);

    /**
     * Update content of the file.
     * Note: this method is optional, some implementations may not support updating their content
     * @param content
     *         new content of the file
     * @param callback
     *         callback to return retrieved content
     */
     void updateContent(String content, AsyncCallback<Void> callback);

}
