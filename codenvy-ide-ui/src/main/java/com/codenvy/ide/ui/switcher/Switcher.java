/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 *  [2012] - [2014] Codenvy, S.A. 
 *  All Rights Reserved.
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
package com.codenvy.ide.ui.switcher;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.SimpleCheckBox;

/**
 * UI element with two states boolean states: "ON" - true, "OFF" - false.
 * User switches the state by click.
 * 
 * @author Ann Shumilova
 */
public class Switcher extends Composite implements HasValue<Boolean> {
    private static final Resources  resources = GWT.create(Resources.class);

    static {
        resources.switcherCSS().ensureInjected();
    }
   
    SimpleCheckBox checkbox;

    public interface Resources extends ClientBundle {
        public interface SwitcherCSS extends CssResource {
            String onoffswitchInner();

            String onoffswitch();

            String onoffswitchSwitch();

            String onoffswitchLabel();

            String onoffswitchCheckbox();

        }

        @Source({"switcher.css", "com/codenvy/ide/api/ui/style.css"})
        SwitcherCSS switcherCSS();
    }


    public Switcher() {
        FlowPanel mainPanel = new FlowPanel();
        mainPanel.setStyleName(resources.switcherCSS().onoffswitch());

        checkbox = new SimpleCheckBox();
        checkbox.getElement().setId("switcher");
        checkbox.setName("onoffswitch");
        checkbox.setStyleName(resources.switcherCSS().onoffswitchCheckbox());
        mainPanel.add(checkbox);

        Element label = DOM.createLabel();
        label.setClassName(resources.switcherCSS().onoffswitchLabel());
        label.setAttribute("for", "switcher");

        Element inner = DOM.createDiv();
        inner.setClassName(resources.switcherCSS().onoffswitchInner());
        label.appendChild(inner);

        Element sw = DOM.createDiv();
        sw.setClassName(resources.switcherCSS().onoffswitchSwitch());
        label.appendChild(sw);
        
        mainPanel.getElement().appendChild(label);

        initWidget(mainPanel);
    }

    /** {@inheritDoc} */
    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Boolean> handler) {
        return checkbox.addValueChangeHandler(handler);
    }


    /** {@inheritDoc} */
    @Override
    public Boolean getValue() {
        return checkbox.getValue();
    }


    /** {@inheritDoc} */
    @Override
    public void setValue(Boolean value) {
        checkbox.setValue(value);
    }


    /** {@inheritDoc} */
    @Override
    public void setValue(Boolean value, boolean fireEvents) {
        checkbox.setValue(value);
        if (fireEvents) {
            ValueChangeEvent.fire(this, value);
        }
    }

}
