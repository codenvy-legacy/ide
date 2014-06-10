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
package com.codenvy.ide.tutorial.wizard.pages.page1;

import com.codenvy.ide.api.mvp.View;
import com.google.inject.ImplementedBy;

/**
 * The view of {@link Page1Presenter}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@ImplementedBy(Page1ViewImpl.class)
public interface Page1View extends View<Page1View.ActionDelegate> {
    /** Required for delegating functions in view. */
    public interface ActionDelegate {
        /** Performs some actions in response to a user's choosing page 2. */
        void onPage2Chosen();

        /** Performs some actions in response to a user's choosing page 3. */
        void onPage3Chosen();

        /** Performs some actions in response to a user's clicking show page 4. */
        void onPage4Clicked();
    }

    /**
     * Returns whether the page 2 is next.
     *
     * @return <code>true</code> if the page 2 is next, and <code>false</code> if it's not
     */
    boolean isPage2Next();

    /**
     * Change state of the visual component of view. This component provides logical about next page.
     *
     * @param page2Next
     *         need to choose page 2 item or item page 3
     */
    void setPage2Next(boolean page2Next);

    /**
     * Returns whether the page 4 need to show.
     *
     * @return <code>true</code> if the page 4 need to show, and <code>false</code> if it's not
     */
    boolean isPage4Show();

    /**
     * Change state of the visual component of view. This component provides logical about showing page 4.
     *
     * @param skip
     *         <code>true</code> if the page 4 isn't shown, and <code>false</code> otherwise
     */
    void setPage4Show(boolean skip);
}