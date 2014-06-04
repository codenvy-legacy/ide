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
package com.codenvy.ide.api.ui.keybinding;

import com.codenvy.ide.api.extension.SDK;
import com.codenvy.ide.util.input.CharCodeWithModifiers;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

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
