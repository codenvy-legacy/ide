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
package com.codenvy.ide.api.ui.action;

import com.google.gwt.resources.client.ImageResource;

import javax.annotation.Nullable;

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
