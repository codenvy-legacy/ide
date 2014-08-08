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
package com.codenvy.ide.api.texteditor;

import com.codenvy.ide.util.ListenerRegistrar;

/**
 * Tracks the focus state of the editor.
 *
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public interface FocusManager {
    /** A listener that is called when the editor receives or loses focus. */
    public interface FocusListener {
        void onFocusChange(boolean hasFocus);
    }

    /**
     * Listener registrar for focus listeners.
     *
     * @return the focus listener registrar
     */
    ListenerRegistrar<FocusListener> getFocusListenerRegistrar();

    /**
     * True if editor has focus
     *
     * @return true if editor has focus.
     */
    boolean hasFocus();

    /** Set focus to editor. */
    void focus();
}
