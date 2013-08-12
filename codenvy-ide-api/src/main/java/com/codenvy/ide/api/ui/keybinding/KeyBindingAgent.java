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
package com.codenvy.ide.api.ui.keybinding;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.annotations.Nullable;
import com.codenvy.ide.api.extension.SDK;
import com.codenvy.ide.util.input.CharCodeWithModifiers;

/**
 * Public interface of the key binding management.
 * The key binding defines the key sequence that should be used to invoke the command.
 * A key binding may reference a scheme which is used to group key bindings into different
 * named schemes that the user may activate.
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
@SDK(title = "ide.api.ui.keyBinding")
public interface KeyBindingAgent {


    /**
     * Global scheme, bindings added in this scheme always
     *
     * @return
     */
    Scheme getGlobal();

    /**
     * Get build in Eclipse key binding scheme.
     *
     * @return the Eclipse scheme.
     */
    Scheme getEclipse();

    /**
     * Currently active scheme.
     *
     * @return the scheme
     */
    Scheme getActive();

    /**
     * @return keyboard shortcut for the action with the specified <code>actionId</code>
     *         or an null if the action doesn't have any keyboard shortcut.
     */
    @Nullable
    CharCodeWithModifiers getKeyBinding(@NotNull String actionId);


}
