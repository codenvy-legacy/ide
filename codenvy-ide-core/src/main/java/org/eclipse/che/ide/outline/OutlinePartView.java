/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide.outline;

import org.eclipse.che.ide.api.mvp.View;
import org.eclipse.che.ide.api.parts.base.BaseActionDelegate;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

/**
 * @author Evgen Vidolob
 * @version $Id:
 */
public interface OutlinePartView extends View<OutlinePartView.ActionDelegate> {

    /**
     * Returns container for placing an outline view.
     *
     * @return container for placing an outline view
     */
    AcceptsOneWidget getContainer();

    /**
     * Enables the outline.
     */
    void enableOutline();

    /**
     * Disables outline and displays a message.
     *
     * @param cause message to display
     */
    void disableOutline(String cause);

    /**
     * Sets new title for outline part.
     *
     * @param title new title
     */
    void setTitle(String title);

    /**
     * Clears outline part.
     */
    void clear();

    /**
     * A delegate to bind UI with logic.
     */
    public interface ActionDelegate extends BaseActionDelegate {
    }

}
