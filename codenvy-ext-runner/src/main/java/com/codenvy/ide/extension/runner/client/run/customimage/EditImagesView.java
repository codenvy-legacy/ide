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
package com.codenvy.ide.extension.runner.client.run.customimage;

import com.codenvy.api.project.shared.dto.ItemReference;
import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.collections.Array;

/**
 * The view of {@link EditImagesPresenter}.
 *
 * @author Artem Zatsarynnyy
 */
public interface EditImagesView extends View<EditImagesView.ActionDelegate> {
    /**
     * Set state of the 'Edit' button.
     *
     * @param isEnabled
     *         <code>true</code> to enable the button, <code>false</code> to disable it
     */
    void setEditButtonEnabled(boolean isEnabled);

    /**
     * Set existing images.
     *
     * @param images
     *         images array
     */
    void setImages(Array<ItemReference> images);

    /** Close the dialog. */
    void close();

    /** Show the dialog. */
    void showDialog();

    /** Needs for delegate some function into ChangePerspective view. */
    public interface ActionDelegate {
        /** Called when 'Edit' button clicked. */
        void onEditClicked();

        /** Called when 'Close' button clicked. */
        void onCloseClicked();

        /** Called when image selected. */
        void onImageSelected(ItemReference projectName);
    }
}