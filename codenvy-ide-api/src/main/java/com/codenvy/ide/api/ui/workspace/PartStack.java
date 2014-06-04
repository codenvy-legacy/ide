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
package com.codenvy.ide.api.ui.workspace;

import com.codenvy.ide.api.mvp.Presenter;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

/**
 * Part Stack is tabbed layout element, containing Parts.
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a>
 */
public interface PartStack extends Presenter {

    /** {@inheritDoc} */
    @Override
    void go(AcceptsOneWidget container);

    /**
     * Change the focused state of the PartStack to desired value
     *
     * @param focused
     */
    void setFocus(boolean focused);

    /**
     * Add part to the PartStack. To immediately show part, you must call <code>setActivePart()</code>.
     *
     * @param part
     */
    void addPart(PartPresenter part);

    /**
     * Ask if PartStack contains given Part.
     *
     * @param part
     * @return
     */
    boolean containsPart(PartPresenter part);

    /**
     * Number of parts in the PartStack
     *
     * @return
     */
    int getNumberOfParts();

    /**
     * Get active Part. Active is the part that is currently displayed on the screen
     *
     * @return
     */
    PartPresenter getActivePart();

    /**
     * Activate given part (force show it on the screen). If part wasn't previously added
     * to the PartStack or has been removed, that method has no effect.
     *
     * @param part
     */
    void setActivePart(PartPresenter part);

    /**
     * Hide given part (remove from the screen). If part not active part that method has no effect.
     *
     * @param part
     */
    void hidePart(PartPresenter part);

    /**
     * Remove given part from PartStack.
     *
     * @param part
     */
    void removePart(PartPresenter part);

}