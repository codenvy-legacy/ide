/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
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
import com.google.gwt.user.client.ui.Widget;

import org.vectomatic.dom.svg.ui.SVGImage;

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

    public ToolButton(SVGImage image) {
        this(null, image);
    }

    public ToolButton(String id, SVGImage image) {
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
