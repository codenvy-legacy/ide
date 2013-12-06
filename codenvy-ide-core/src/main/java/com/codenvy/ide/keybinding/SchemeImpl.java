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
package com.codenvy.ide.keybinding;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.annotations.Nullable;
import com.codenvy.ide.api.ui.keybinding.Scheme;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.collections.IntegerMap;
import com.codenvy.ide.collections.StringMap;
import com.codenvy.ide.util.input.CharCodeWithModifiers;


/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
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
        int digest = key.getKeyDigest();
        if (!handlers.hasKey(digest)) {
            handlers.put(digest, Collections.<String>createArray());
        }
        handlers.get(digest).add(actionId);
        actionId2CharCode.put(actionId, key);
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
