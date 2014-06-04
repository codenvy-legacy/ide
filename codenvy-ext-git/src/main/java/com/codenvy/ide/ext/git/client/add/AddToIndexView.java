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
package com.codenvy.ide.ext.git.client.add;

import com.codenvy.ide.api.mvp.View;

import javax.validation.constraints.NotNull;

/**
 * The view of {@link AddToIndexPresenter}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public interface AddToIndexView extends View<AddToIndexView.ActionDelegate> {
    /** Needs for delegate some function into CloneRepository view. */
    public interface ActionDelegate {
        /** Performs any actions appropriate in response to the user having pressed the Add button. */
        void onAddClicked();

        /** Performs any actions appropriate in response to the user having pressed the Cancel button. */
        void onCancelClicked();
    }

    /**
     * Set content into message field.
     *
     * @param message
     *         content of message
     */
    void setMessage(@NotNull String message);

    /** @return <code>true</code> if new file must be added to index, and <code>false</code> otherwise */
    boolean isUpdated();

    /**
     * Set state of add new file.
     *
     * @param isUpdated
     *         <code>true</code> to add new file to index, <code>false</code> don't do it
     */
    void setUpdated(boolean isUpdated);

    /** Close dialog. */
    void close();

    /** Show dialog. */
    void showDialog();
}