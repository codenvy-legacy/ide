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
package com.codenvy.ide.api.parts.base;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import java.util.ArrayList;
import java.util.List;

/**
 * Button which can be added to the tool bar.
 * <p/>
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ToolButton extends Composite implements HasClickHandlers {

    /** UIBinder for this TabButton. */
    private static TabButtonUiBinder uiBinder = GWT.create(TabButtonUiBinder.class);

    /** UIBinder class for this TabButton. */
    interface TabButtonUiBinder extends UiBinder<Widget, ToolButton> {
    }

    /** Style Resource for this TabButton. */
    interface Style extends CssResource {

        String buttonOver();

        String buttonDown();

        String iconPanel();

        String button();

        String controlPanel();

    }

    /** Instance of Style Resource. */
    @UiField
    Style style;

    @UiField
    HTML controlPanel;

    @UiField
    DivElement buttonPanel;

    @UiField
    DivElement iconPanel;

    boolean enabled = true;

    private List<ClickHandler> clickHandlers = new ArrayList<ClickHandler>();

    public ToolButton(Image image) {
        this(null, image);
    }

    public ToolButton(String id, Image image) {
        initWidget(uiBinder.createAndBindUi(this));
        iconPanel.appendChild(image.getElement());

        if (id != null) {
            getElement().setId(id);
        }
    }

    @UiHandler("controlPanel")
    void onMouseOver(MouseOverEvent e) {
        buttonPanel.addClassName(style.buttonOver());
    }

    @UiHandler("controlPanel")
    void onMouseOut(MouseOutEvent e) {
        buttonPanel.removeClassName(style.buttonOver());
        buttonPanel.removeClassName(style.buttonDown());
    }

    @UiHandler("controlPanel")
    void onMouseDown(MouseDownEvent e) {
        buttonPanel.addClassName(style.buttonDown());
    }

    @UiHandler("controlPanel")
    void onMouseUp(MouseUpEvent e) {
        buttonPanel.removeClassName(style.buttonDown());
    }

    @UiHandler("controlPanel")
    void onClick(ClickEvent e) {
        for (ClickHandler clickHandler : clickHandlers) {
            clickHandler.onClick(e);
        }
    }

    @Override
    public HandlerRegistration addClickHandler(ClickHandler handler) {
        clickHandlers.add(handler);
        return new ClickHandlerRegistration(handler);
    }

    public boolean isEnabled() {
        return enabled;
    }

    private class ClickHandlerRegistration implements HandlerRegistration {

        private ClickHandler handler;

        public ClickHandlerRegistration(ClickHandler handler) {
            this.handler = handler;
        }

        @Override
        public void removeHandler() {
            clickHandlers.remove(handler);
        }

    }

}
