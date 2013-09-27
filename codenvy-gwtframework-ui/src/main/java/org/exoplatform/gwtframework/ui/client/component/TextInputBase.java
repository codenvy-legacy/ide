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

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.TextBoxBase;

import org.exoplatform.gwtframework.ui.client.GwtResources;

/**
 * Represents the common component, which can allow display and edit text.
 * It changes CSS style on receiving or losing focus, and in enabled/disabled state.
 * Also the usage of this component fixes the problem of firing {@link ValueChangedEvent},
 * when text is pasted to the input(context menu or hot keys)
 * or when text is typed to the input and it still is focused.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Feb 23, 2011 10:02:55 AM anya $
 * @deprecated use {@link TextInput}
 */
public class TextInputBase extends TextBoxBase {
    /** The enabled state of the component. */
    private boolean isEnabled = true;

    /**
     * Such elements can be passed:
     * <ol>
     * <li><b>text input</b> - <code>new TextBox().getElement()</code></li>
     * <li><b>password input</b> - <code>new PasswordTextBox().getElement()</code></li>
     * <li><b>textarea</b>  - <code>new TextArea().getElement()</code></li>
     * </ol>
     *
     * @param element
     *         the HTML element as base
     */
    public TextInputBase(Element element) {
        super(element);
        GwtResources.INSTANCE.css().ensureInjected();
        getElement().setClassName(GwtResources.INSTANCE.css().textBox());
        sinkEvents(Event.FOCUSEVENTS | Event.ONCLICK | Event.MOUSEEVENTS | Event.KEYEVENTS | Event.ONPASTE);
    }

    /** @see com.google.gwt.user.client.ui.ValueBoxBase#onBrowserEvent(com.google.gwt.user.client.Event) */
    @Override
    public void onBrowserEvent(Event event) {
        if (!isEnabled())
            return;

        int type = DOM.eventGetType(event);
        switch (type) {
            case Event.ONFOCUS:
                onFocus();
                break;
            case Event.ONBLUR:
                onBlur();
                break;
            case Event.ONPASTE:
                valueChange();
                break;
            case Event.ONKEYUP:
                valueChange();
                break;
        }
        super.onBrowserEvent(event);
    }

    /** @see com.google.gwt.user.client.ui.FocusWidget#setEnabled(boolean) */
    @Override
    public void setEnabled(boolean enabled) {
        setEnabled(enabled, true);
    }

    /**
     * Set the enabled state of the component.
     *
     * @param enabled
     *         enabled state
     * @param isShowDisabled
     *         show the disabled state
     */
    protected void setEnabled(boolean enabled, boolean isShowDisabled) {
        if (isEnabled() == enabled)
            return;
        this.isEnabled = enabled;
        DOM.setElementPropertyBoolean(getElement(), "disabled", !enabled);
        if (isEnabled || !isShowDisabled) {
            getElement().removeClassName(GwtResources.INSTANCE.css().textBoxDisabled());
        } else {
            getElement().addClassName(GwtResources.INSTANCE.css().textBoxDisabled());
        }
    }

    /** @return the isEnabled */
    public boolean isEnabled() {
        return isEnabled;
    }

    /** Change style when items gets browser's focus. */
    public void onFocus() {
        getElement().addClassName(GwtResources.INSTANCE.css().textBoxFocused());
    }

    /** Change style when items loses browser's focus. */
    public void onBlur() {
        getElement().removeClassName(GwtResources.INSTANCE.css().textBoxFocused());
    }

    /** Fire {@link ValueChangedEvent} event with changed value. */
    public void valueChange() {
        ValueChangeEvent.fire(this, getText());
    }
}
