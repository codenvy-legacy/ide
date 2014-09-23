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
package com.codenvy.ide.keybinding;

import com.codenvy.ide.api.keybinding.Scheme;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.collections.IntegerMap;
import com.codenvy.ide.collections.StringMap;
import com.codenvy.ide.util.input.CharCodeWithModifiers;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

/**
 * @author Evgen Vidolob
 * @author Artem Zatsarynnyy
 */
public class SchemeImpl implements Scheme {

    private String id;

    private String description;

    private IntegerMap<Array<String>> handlers;

    private StringMap<CharCodeWithModifiers> actionId2CharCode;

    public SchemeImpl(String id, String description) {
        this.id = id;
        this.description = description;
        handlers = Collections.createIntegerMap();
        actionId2CharCode = Collections.createStringMap();
    }

    /** {@inheritDoc} */
    @Override
    public String getSchemeId() {
        return id;
    }

    /** {@inheritDoc} */
    @Override
    public String getDescription() {
        return description;
    }

    /** {@inheritDoc} */
    @Override
    public void addKey(@NotNull CharCodeWithModifiers key, @NotNull String actionId) {
        final int digest = key.getKeyDigest();
        if (!handlers.hasKey(digest)) {
            handlers.put(digest, Collections.<String>createArray());
        }
        handlers.get(digest).add(actionId);
        actionId2CharCode.put(actionId, key);
    }

    /** {@inheritDoc} */
    @Override
    public void removeKey(@NotNull CharCodeWithModifiers key, @NotNull String actionId) {
        final int digest = key.getKeyDigest();

        Array<String> array = handlers.get(digest);
        if (array != null) {
            array.remove(actionId);
            if (array.isEmpty()) {
                handlers.erase(digest);
            }
        }

        actionId2CharCode.remove(actionId);
    }

    /** {@inheritDoc} */
    @NotNull
    @Override
    public Array<String> getActionIds(int digest) {
        if (handlers.hasKey(digest)) {
            return handlers.get(digest);
        }
        return Collections.createArray();
    }

    /** {@inheritDoc} */
    @Nullable
    @Override
    public CharCodeWithModifiers getKeyBinding(@NotNull String actionId) {
        return actionId2CharCode.get(actionId);
    }
}
