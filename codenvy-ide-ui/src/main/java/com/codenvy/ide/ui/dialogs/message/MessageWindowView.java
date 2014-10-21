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
package com.codenvy.ide.ui.dialogs.message;

import com.google.gwt.user.client.ui.IsWidget;

/**
 * The view interface for the message window component.
 * 
 * @author "Mickaël Leduque"
 */
public interface MessageWindowView {

    /** Sets the action delegate. */
    void setDelegate(ActionDelegate delegate);

    /** Displays the dialog window. */
    void showDialog();

    /** Closes the dialog window. */
    void closeDialog();

    /** Fill the window with its content. */
    void setContent(IsWidget content);

    /** Sets the window title. */
    void setTitle(String title);

    /** The interface for the action delegate. */
    public interface ActionDelegate {

        /** Defines what's done when the user clicks cancel. */
        void accepted();
    }
}
