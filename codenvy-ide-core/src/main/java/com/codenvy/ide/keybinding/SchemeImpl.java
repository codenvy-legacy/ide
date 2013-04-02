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

import com.codenvy.ide.api.ui.keybinding.Scheme;
import com.codenvy.ide.api.ui.menu.ExtendedCommand;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.json.JsonIntegerMap;
import com.codenvy.ide.util.input.CharCodeWithModifiers;
import com.codenvy.ide.util.input.SignalEvent;


/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class SchemeImpl implements Scheme {

    private String id;

    private String description;

    private JsonIntegerMap<JsonArray<ExtendedCommand>> handlers;


    public SchemeImpl(String id, String description) {
        this.id = id;
        this.description = description;
        handlers = JsonCollections.createIntegerMap();
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
    public void addKeyBinding(CharCodeWithModifiers keyBinging, ExtendedCommand command) {
        int digest = keyBinging.getKeyDigest();
        if (!handlers.hasKey(digest)) {
            handlers.put(digest, JsonCollections.<ExtendedCommand>createArray());
        }
        handlers.get(digest).add(command);
    }

    public boolean handleKeyEvent(SignalEvent event) {
        int digest = CharCodeWithModifiers.computeKeyDigest(event);
        if (handlers.hasKey(digest)) {
            executeCommand(handlers.get(digest));
            return true;
        } else {
            return false;
        }
    }

    private void executeCommand(JsonArray<ExtendedCommand> commands) {
        for (ExtendedCommand command : commands.asIterable()) {
            // check command context
            if (command.inContext() != null && !command.inContext().getValue()) {
                continue;
            }

            if (command.canExecute() != null && !command.canExecute().getValue()) {
                continue;
            }
            // TODO handle if we have more than one enabled command
            command.execute();
        }
    }
}
