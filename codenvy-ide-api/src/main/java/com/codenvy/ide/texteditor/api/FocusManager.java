/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
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
package com.codenvy.ide.texteditor.api;

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
