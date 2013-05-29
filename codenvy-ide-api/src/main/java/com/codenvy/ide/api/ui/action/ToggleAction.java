/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package com.codenvy.ide.api.ui.action;

import com.codenvy.ide.annotations.Nullable;
import com.google.gwt.resources.client.ImageResource;

/**
 * An action which has a selected state, and which toggles its selected state when performed.
 * Can be used to represent a menu item with a checkbox, or a toolbar button which keeps its pressed state.
 *
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public abstract class ToggleAction extends Action implements Toggleable {
    public ToggleAction(@Nullable final String text) {
        super(text);
    }

    public ToggleAction(@Nullable final String text, @Nullable final String description, @Nullable final ImageResource icon) {
        super(text, description, icon);
    }

    @Override
    public final void actionPerformed(final ActionEvent e) {
        final boolean state = !isSelected(e);
        setSelected(e, state);
        final Boolean selected = state ? Boolean.TRUE : Boolean.FALSE;
        final Presentation presentation = e.getPresentation();
        presentation.putClientProperty(SELECTED_PROPERTY, selected);
    }

    /**
     * Returns the selected (checked, pressed) state of the action.
     *
     * @param e
     *         the action event representing the place and context in which the selected state is queried.
     * @return true if the action is selected, false otherwise
     */
    public abstract boolean isSelected(ActionEvent e);

    /**
     * Sets the selected state of the action to the specified value.
     *
     * @param e
     *         the action event which caused the state change.
     * @param state
     *         the new selected state of the action.
     */
    public abstract void setSelected(ActionEvent e, boolean state);

    @Override
    public void update(final ActionEvent e) {
        final Boolean selected = isSelected(e) ? Boolean.TRUE : Boolean.FALSE;
        final Presentation presentation = e.getPresentation();
        presentation.putClientProperty(SELECTED_PROPERTY, selected);
    }
}
