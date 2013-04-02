/**
 * Copyright (C) 2009 eXo Platform SAS.
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
 *
 */

package org.exoplatform.gwtframework.ui.client.component;

import com.google.gwt.user.client.ui.RadioButton;

import org.exoplatform.gwtframework.ui.client.GwtResources;

/**
 * {@link RadioItem} differs from GWT {@link RadioButton} only with css style.
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class RadioItem extends RadioButton {
    /**
     * @param name
     *         name of the form's element
     */
    public RadioItem(String name) {
        super(name);
        initElement();
    }

    /**
     * @param name
     *         name of the form's element
     * @param title
     *         title shown near radiobutton
     */
    public RadioItem(String name, String title) {
        super(name, title);
        initElement();
    }

    /** Set the CSS style to the element. */
    private void initElement() {
        GwtResources.INSTANCE.css().ensureInjected();
        getElement().setClassName(GwtResources.INSTANCE.css().radioButton());
    }

    /** @see com.google.gwt.user.client.ui.CheckBox#setEnabled(boolean) */
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (enabled) {
            getElement().removeClassName(GwtResources.INSTANCE.css().radioButtonDisabled());
        } else {
            getElement().addClassName(GwtResources.INSTANCE.css().radioButtonDisabled());
        }
    }
}
