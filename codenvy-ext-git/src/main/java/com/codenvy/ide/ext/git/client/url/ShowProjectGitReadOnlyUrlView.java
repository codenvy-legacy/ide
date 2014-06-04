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
package com.codenvy.ide.ext.git.client.url;

import com.codenvy.ide.api.mvp.View;

import javax.validation.constraints.NotNull;

/**
 * The view of {@link ShowProjectGitReadOnlyUrlPresenter}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public interface ShowProjectGitReadOnlyUrlView extends View<ShowProjectGitReadOnlyUrlView.ActionDelegate> {
    /** Needs for delegate some function into Git url view. */
    public interface ActionDelegate {
        /** Performs any actions appropriate in response to the user having pressed the Close button. */
        void onCloseClicked();
    }

    /**
     * Set project name into field on the view.
     *
     * @param url
     *         text what will be shown on view
     */
    void setUrl(@NotNull String url);

    /** Close dialog. */
    void close();

    /** Show dialog. */
    void showDialog();
}