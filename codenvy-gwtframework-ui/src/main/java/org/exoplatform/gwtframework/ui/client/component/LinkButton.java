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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HasText;

import java.util.ArrayList;
import java.util.List;

/**
 * Styled HTML link(<code><a></a></code>), which looks like button.
 *
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Jul 3, 2012 2:01:32 PM anya $
 */
public class LinkButton extends Anchor implements HasText, MouseDownHandler, MouseOverHandler, MouseUpHandler,
                                                  MouseOutHandler, HasClickHandlers, ClickHandler {

    /** Button's id. */
    private String id;

    /** Button's title text. */
    private String text;

    /** Button's enabled state. */
    private boolean enabled = true;

    /** Click handlers. */
    private List<ClickHandler> clickHandlers = new ArrayList<ClickHandler>();

    private interface Style extends CssResource {
        String linkButtonHidden();

        String linkButtonPanel();

        String linkButtonOver();

        String linkButtonDown();

        String linkButtonDisabled();
    }

    private interface Resources extends ClientBundle {
        @Source("link-button/link-button-22.css")
        Style css();

        @Source("link-button/link-button-22.png")
        ImageResource link();
    }

    private static final Resources RESOURCES = GWT.create(Resources.class);

    static {
        RESOURCES.css().ensureInjected();
    }

    public LinkButton() {
        this(null);
    }

    public LinkButton(String text) {
        super();
        setText(text);

        getElement().setAttribute("button-enabled", enabled + "");
        setStylePrimaryName(RESOURCES.css().linkButtonPanel());

        addDomHandler(this, MouseOverEvent.getType());
        addDomHandler(this, MouseOutEvent.getType());
        addDomHandler(this, MouseDownEvent.getType());
        addDomHandler(this, MouseUpEvent.getType());
        addDomHandler(this, ClickEvent.getType());
    }

    /** @see com.google.gwt.event.dom.client.MouseOutHandler#onMouseOut(com.google.gwt.event.dom.client.MouseOutEvent) */
    @Override
    public void onMouseOut(MouseOutEvent event) {
        getElement().removeClassName(RESOURCES.css().linkButtonOver());
    }

    /** @see com.google.gwt.event.dom.client.MouseUpHandler#onMouseUp(com.google.gwt.event.dom.client.MouseUpEvent) */
    @Override
    public void onMouseUp(MouseUpEvent event) {
        getElement().removeClassName(RESOURCES.css().linkButtonDown());
        getElement().addClassName(RESOURCES.css().linkButtonOver());
    }

    /** @see com.google.gwt.event.dom.client.MouseOverHandler#onMouseOver(com.google.gwt.event.dom.client.MouseOverEvent) */
    @Override
    public void onMouseOver(MouseOverEvent event) {
        getElement().removeClassName(RESOURCES.css().linkButtonDown());
        getElement().addClassName(RESOURCES.css().linkButtonOver());
    }

    /** @see com.google.gwt.event.dom.client.MouseDownHandler#onMouseDown(com.google.gwt.event.dom.client.MouseDownEvent) */
    @Override
    public void onMouseDown(MouseDownEvent event) {
        getElement().removeClassName(RESOURCES.css().linkButtonOver());
        getElement().addClassName(RESOURCES.css().linkButtonDown());
    }

    /** @return the id */
    public String getId() {
        return id;
    }

    /**
     * @param id
     *         the id to set
     */
    public void setId(String id) {
        this.id = id;
        getElement().setId(id);
    }

    /** @return the text */
    public String getText() {
        return text;
    }

    /**
     * @param text
     *         the text to set
     */
    public void setText(String text) {
        this.text = text;
        super.setText(text);
    }

    /** @return the enabled */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * @param enabled
     *         the enabled to set
     */
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        this.enabled = enabled;

        getElement().removeClassName(RESOURCES.css().linkButtonDown());
        getElement().removeClassName(RESOURCES.css().linkButtonOver());

        if (enabled) {
            getElement().removeClassName(RESOURCES.css().linkButtonDisabled());
        } else {
            getElement().addClassName(RESOURCES.css().linkButtonDisabled());
        }
        getElement().setAttribute("button-enabled", enabled + "");
    }

    /** @see com.google.gwt.event.dom.client.ClickHandler#onClick(com.google.gwt.event.dom.client.ClickEvent) */
    @Override
    public void onClick(ClickEvent event) {
        if (!enabled) {
            event.preventDefault();
            event.stopPropagation();
            return;
        }

        List<ClickHandler> oneCycleClickHandlers = new ArrayList<ClickHandler>(clickHandlers);
        for (ClickHandler handler : oneCycleClickHandlers) {
            handler.onClick(event);
        }
    }

    /** @see com.google.gwt.event.dom.client.HasClickHandlers#addClickHandler(com.google.gwt.event.dom.client.ClickHandler) */
    public HandlerRegistration addClickHandler(ClickHandler handler) {
        clickHandlers.add(handler);
        return new ClickHandlerRegistration(handler);
    }

    private class ClickHandlerRegistration implements HandlerRegistration {

        private ClickHandler clickHandler;

        public ClickHandlerRegistration(ClickHandler clickHandler) {
            this.clickHandler = clickHandler;
        }

        @Override
        public void removeHandler() {
            clickHandlers.remove(clickHandler);
        }
    }

}
