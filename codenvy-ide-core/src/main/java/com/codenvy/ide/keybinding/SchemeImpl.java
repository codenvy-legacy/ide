/*
 * Copyright (C) 2012 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.codenvy.ide.keybinding;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.annotations.Nullable;
import com.codenvy.ide.api.ui.keybinding.Scheme;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.json.JsonIntegerMap;
import com.codenvy.ide.json.JsonStringMap;
import com.codenvy.ide.util.input.CharCodeWithModifiers;


/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class SchemeImpl implements Scheme {

    private String id;

    private String description;

    private JsonIntegerMap<JsonArray<String>> handlers;

    private JsonStringMap<CharCodeWithModifiers> actionId2CharCode;


    public SchemeImpl(String id, String description) {
        this.id = id;
        this.description = description;
        handlers = JsonCollections.createIntegerMap();
        actionId2CharCode = JsonCollections.createStringMap();
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
            handlers.put(digest, JsonCollections.<String>createArray());
        }
        handlers.get(digest).add(actionId);
        actionId2CharCode.put(actionId, key);
    }

    /** {@inheritDoc} */
    @NotNull
    @Override
    public JsonArray<String> getActionIds(int digest) {
        if (handlers.hasKey(digest)) {
            return handlers.get(digest);
        }
        return JsonCollections.createArray();
    }

    /** {@inheritDoc} */
    @Nullable
    @Override
    public CharCodeWithModifiers getKeyBinding(@NotNull String actionId) {
        return actionId2CharCode.get(actionId);
    }
}
