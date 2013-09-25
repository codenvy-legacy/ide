/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 *  [2012] - [2013] Codenvy, S.A. 
 *  All Rights Reserved.
 * 
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
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