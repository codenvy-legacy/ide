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
