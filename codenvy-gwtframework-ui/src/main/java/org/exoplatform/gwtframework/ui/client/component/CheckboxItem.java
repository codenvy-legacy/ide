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
