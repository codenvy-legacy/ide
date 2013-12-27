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
