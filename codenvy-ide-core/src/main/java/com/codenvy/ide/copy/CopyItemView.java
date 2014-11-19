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
package com.codenvy.ide.copy;

import com.codenvy.api.project.shared.dto.ItemReference;
import com.codenvy.api.project.shared.dto.ProjectReference;
import com.codenvy.api.vfs.shared.dto.Item;
import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.collections.Array;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.ImplementedBy;

/**
 * View for coping item(s).
 *
 * @author Ann Shumilova
 */
@ImplementedBy(CopyItemViewImpl.class)
public interface CopyItemView extends View<CopyItemView.ActionDelegate> {

    public interface ActionDelegate {
        /**
         * Performs any actions appropriate in response to the user having pressed the Ok button.
         */
        void onOkClicked();

        /**
         * Performs any actions appropriate in response to the user having pressed the Cancel button.
         */
        void onCancelClicked();

        /**
         * Called when suggestions are requested.
         *
         * @param query query string
         * @param callback callback
         */
        void onRequestSuggestions(String query, AsyncCallback<Array<ItemReference>> callback);

        /** Called when value of directory input or new name has changed.*/
        void onValueChanged();
    }

    /**
     * Set copy item(s) title
     *
     * @param title title of the item(s) to be copied
     */
    void setCopyItemTitle(String title);

    /**
     * Set new name block visible.
     *
     * @param isVisible visibility
     */
    void setNewNameVisible(boolean isVisible);

    /**
     * Set the value of the new name input
     *
     * @param name value
     */
    void setNewNameValue(String name);

    /**
     * Set directory value.
     *
     * @param path path
     */
    void setDirectory(String path);

    /**
     * Returns the value of the directory input.
     *
     * @return path to directory
     */
    String getDirectory();

    /**
     * Returns the value of the new name input.
     *
     * @return new name value
     */
    String getNewNameValue();

    /**
     * Returns open in editor value.
     *
     * @return is open in editor
     */
    boolean getOpenInEditor();

    /**
     * Sets copy button enabled state.
     *
     * @param isEnabled is enabled
     */
    void setCopyButtonEnabled(boolean isEnabled);

    /**
     * Sets error message.
     *
     * @param message
     */
    void setErrorMessage(String message);

    /** Open the view and show. */
    void showView();

    /** Close the view. */
    void close();

}