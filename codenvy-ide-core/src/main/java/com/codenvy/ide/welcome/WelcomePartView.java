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
package com.codenvy.ide.welcome;

import com.codenvy.ide.api.mvp.View;
import com.google.gwt.resources.client.ImageResource;

/**
 * The view of {@link WelcomePartPresenter}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public interface WelcomePartView extends View<WelcomePartView.ActionDelegate> {
    /** Needs for delegate some function into WelcomePart view. */
    public interface ActionDelegate {
        /**
         * Performs any actions appropriate in response to the user having pressed some item on welcome page.
         *
         * @param itemIndex
         *         index of item
         */
        void onItemClicked(int itemIndex);
    }

    /**
     * Add item on welcome page.
     *
     * @param title
     * @param caption
     * @param icon
     * @param itemIndex
     */
    void addItem(String title, String caption, ImageResource icon, int itemIndex);
}