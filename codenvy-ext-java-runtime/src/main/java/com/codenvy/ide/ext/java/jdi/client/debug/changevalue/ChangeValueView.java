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
package com.codenvy.ide.ext.java.jdi.client.debug.changevalue;

import com.codenvy.ide.api.mvp.View;

/**
 * The view of {@link ChangeValuePresenter}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public interface ChangeValueView extends View<ChangeValueView.ActionDelegate> {
    /** Needs for delegate some function into ChangeValue view. */
    public interface ActionDelegate {
        /** Performs any actions appropriate in response to the user having pressed the Cancel button. */
        void onCancelClicked();

        /** Performs any actions appropriate in response to the user having pressed the Change button. */
        void onChangeClicked();

        /** Performs any actions appropriate in response to the user having changed value. */
        void onValueChanged();
    }

    /** @return changed value */
    String getValue();

    /**
     * Set new value.
     *
     * @param value
     *         new value
     */
    void setValue(String value);

    /**
     * Change the enable state of the evaluate button.
     *
     * @param isEnable
     *         <code>true</code> to enable the button, <code>false</code> to disable it
     */
    void setEnableChangeButton(boolean isEnable);

    /** Give focus to expression field. */
    void focusInValueField();

    /** Select all text in expression field. */
    void selectAllText();

    /**
     * Set title for value field.
     *
     * @param title
     *         new title for value field
     */
    void setValueTitle(String title);

    /** Close dialog. */
    void close();

    /** Show dialog. */
    void showDialog();
}