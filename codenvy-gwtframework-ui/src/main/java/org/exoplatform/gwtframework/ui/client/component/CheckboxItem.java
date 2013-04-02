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

import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.GwtResources;

/**
 * Checkbox item.
 * The title near checkbox can be displayed at the left or right side of it.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Mar 1, 2011 5:40:47 PM anya $
 */
public class CheckboxItem extends CheckBox implements HasValue<Boolean> {
    /** Default constructor. */
    public CheckboxItem() {
        GwtResources.INSTANCE.css().ensureInjected();
        setStyleName(GwtResources.INSTANCE.css().checkBox());
    }

    /**
     * Initialize widget setting the element.
     *
     * @param widget
     */
    protected void initWidget(Widget widget) {
        setElement(widget.getElement());
    }

    /**
     * @param name
     *         name of the form element.
     */
    public CheckboxItem(String name) {
        this();
        setName(name);
    }

    /**
     * @param name
     *         name of the form element
     * @param title
     *         title shown near checkbox
     */
    public CheckboxItem(String name, String title) {
        this();
        setName(name);
        setText(title);
    }

    /** @see com.google.gwt.user.client.ui.CheckBox#setEnabled(boolean) */
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (enabled) {
            removeStyleName(GwtResources.INSTANCE.css().checkBoxDisabled());
        } else {
            addStyleName(GwtResources.INSTANCE.css().checkBoxDisabled());
        }
    }

    /**
     * Shows the title on the left side of the checkbox.
     *
     * @param labelAsTitle
     */
    public void setLabelAsTitle(boolean labelAsTitle) {
        NodeList<com.google.gwt.dom.client.Element> labels = getElement().getElementsByTagName("label");
        if (labels == null || labels.getLength() <= 0)
            return;

        if (labelAsTitle) {
            labels.getItem(0).addClassName(GwtResources.INSTANCE.css().checkBoxTitleLeft());
        } else {
            labels.getItem(0).removeClassName(GwtResources.INSTANCE.css().checkBoxTitleLeft());
        }
    }

    /** Get title */
    public String getTitle() {
        return getText();
    }

    public void setTitle(String title) {
        setText(title);
    }

    /**
     * Sets new ID attribute to widget's element.
     *
     * @param id
     */
    public void setId(String id) {
        getElement().setId(id);
    }

    public void setCheckBoxId(String id) {
        getElement().setId(id);
    }

}
