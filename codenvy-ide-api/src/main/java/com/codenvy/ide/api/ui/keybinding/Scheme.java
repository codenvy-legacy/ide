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

import com.codenvy.ide.collections.Array;
import com.codenvy.ide.util.input.CharCodeWithModifiers;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;


/**
 * Scheme is set of the key bindings
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public interface Scheme {

    /**
     * Get id of the scheme.
     *
     * @return the scheme id
     */
    String getSchemeId();

    /**
     * Get scheme description
     *
     * @return the scheme description
     */
    String getDescription();

    /**
     * Add key binding for action.
     *
     * @param key
     *         the hot key which bind.
     * @param actionId
     *         the action id which keys bind.
     */
    void addKey(@NotNull CharCodeWithModifiers key, @NotNull String actionId);

    /**
     * @return all actions that have the specified key. If there are no
     *         such actions then the method returns an empty array.
     */
    @NotNull
    Array<String> getActionIds(int digest);

    /**
     * @return keyboard shortcut for the action with the specified <code>actionId</code>
     *         or an null if the action doesn't have any keyboard shortcut.
     */
    @Nullable
    CharCodeWithModifiers getKeyBinding(@NotNull String actionId);
}
