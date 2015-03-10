/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide.jseditor.client.popup;

import static elemental.css.CSSStyleDeclaration.Unit.PX;

import org.eclipse.che.ide.util.dom.Elements;

import elemental.dom.Document;
import elemental.dom.Element;
import elemental.dom.Node;
import elemental.events.Event;
import elemental.events.EventListener;
import elemental.events.EventTarget;
import elemental.events.MouseEvent;
import elemental.html.ClientRect;
import elemental.html.Window;

/**
 * Popup widow that hides itself on outside mouse down actions..
 */
public abstract class PopupWidget<T> {

    private static final int MAX_HEIGHT = 500;

    private static final int MAX_WIDTH = 400;

    private static final int MIN_HEIGHT = 200;

    private static final int MIN_WIDTH = 200;

    /** The main element for the popup. */
    private final Element popupElement;

    /** The list (ul) element for the popup. */
    private final Element listElement;

    private final EventListener popupListener;

    /**
     * The keyboard listener in the popup.
     */
    private final EventListener keyboardListener;

    private final PopupResources popupResources;

    /**
     * The previously focused element.
     */
    private Element previousFocus;

    public PopupWidget(final PopupResources popupResources) {
        this.popupElement = Elements.createDivElement(popupResources.popupStyle().window());
        this.listElement = Elements.createUListElement();
        this.popupElement.appendChild(this.listElement);
        this.popupResources = popupResources;

        this.popupListener = new EventListener() {
            @Override
            public void handleEvent(final Event evt) {
                if (evt instanceof MouseEvent) {
                    final MouseEvent mouseEvent = (MouseEvent) evt;
                    final EventTarget target = mouseEvent.getTarget();
                    if (target instanceof Element) {
                        final Element elementTarget = (Element) target;
                        if (!PopupWidget.this.popupElement.contains(elementTarget)) {
                            hide();
                            evt.preventDefault();
                        }
                    }
                }
                // else won't happen
            }
        };
        this.keyboardListener = new PopupKeyDownListener(this, this.listElement);
    }

    /** Returns the content to display when no items were added. */
    public abstract Element getEmptyDisplay();

    /** Create an element for the given item data. */
    public abstract Element createItem(final T itemModel);

    /** Remove all items. */
    public void clear() {
        Node lastChild = this.listElement.getLastChild(); 
        while (lastChild != null) {
            this.listElement.removeChild(lastChild);
            lastChild = this.listElement.getLastChild();
        }
    }

    /**
     * Show the widget at the given document position.
     * @param Xcoord the horizontal pixel position in the document
     * @param Ycoord the vertical pixel position in the document
     */
    public void show(final float Xcoord, final float Ycoord) {
        if (this.listElement.getChildElementCount() == 0) {
            Element emptyElement = getEmptyDisplay();
            if (emptyElement != null) {
                emptyElement.setTabIndex(1);
                this.listElement.appendChild(emptyElement);
            } else {
                return;
            }
        }

        final Document document = Elements.getDocument();
        document.getBody().appendChild(this.popupElement);
        document.addEventListener(Event.MOUSEDOWN, this.popupListener, false);

        this.popupElement.getStyle().setTop(Ycoord, PX);
        this.popupElement.getStyle().setLeft(Xcoord, PX);
        this.popupElement.getStyle().setProperty("max-height", Integer.toString(MAX_HEIGHT) + "px");
        this.popupElement.getStyle().setProperty("max-width", Integer.toString(MAX_WIDTH) + "px");

        // does it fit inside the doc body?
        // This does exactly the same thing for height/top and width/left

        final Window window = Elements.getWindow();
        final int winX = window.getInnerWidth();
        final int winY = window.getInnerHeight();
        ClientRect widgetRect = this.popupElement.getBoundingClientRect();
        if (widgetRect.getBottom() > winY) {
            // it doesn't fit
            final float overflow = widgetRect.getBottom() - winY;
            if (widgetRect.getHeight() - overflow > MIN_HEIGHT) {
                // the widget can be shrunk to fit
                this.popupElement.getStyle().setHeight(widgetRect.getHeight() - overflow, PX);
            } else {
                // we need to shrink AND move the widget up
                this.popupElement.getStyle().setHeight(MIN_HEIGHT, PX);
                final int newTop = Math.max(winY - MIN_HEIGHT, MIN_HEIGHT);
                this.popupElement.getStyle().setTop(newTop, PX);
            }
        }
        // bounding rect has changed
        widgetRect = this.popupElement.getBoundingClientRect();
        if (widgetRect.getRight() > winX) {
            // it doesn't fit
            final float overflow = widgetRect.getRight() - winX;
            if (widgetRect.getWidth() - overflow > MIN_WIDTH) {
                // the widget can be shrunk to fit
                this.popupElement.getStyle().setWidth(widgetRect.getWidth() - overflow, PX);
            } else {
                // we need to shrink AND move the widget up
                this.popupElement.getStyle().setWidth(MIN_WIDTH, PX);
                final int newLeft = Math.max(winX - MIN_WIDTH, MIN_WIDTH);
                this.popupElement.getStyle().setLeft(newLeft - MIN_WIDTH, PX);
            }
        }

        if (needsFocus()) {
            // save previous focus and set focus in popup
            this.previousFocus = Elements.getDocument().getActiveElement();
            final Element toFocus = this.listElement.getFirstElementChild();
            toFocus.focus();
        }

        // add key event listener on popup
        this.listElement.addEventListener(Event.KEYDOWN, this.keyboardListener, false);
    }

    /** Returns the style to add to all items. */
    protected String getItemStyle() {
        return this.popupResources.popupStyle().item();
    }

    /**
     * Add an item in the popup view.
     * @param itemModel the data for the item
     */
    public void addItem(final T itemModel) {
        if (itemModel == null) {
            return;
        }
        final Element itemElement = createItem(itemModel);
        if (itemElement != null) {
            // makes the element focusable
            itemElement.setTabIndex(1);
            this.listElement.appendChild(itemElement);
        }
    }

    /** Hide the popup. */
    public void hide() {
        // restore previous focus state
        if (this.previousFocus != null) {
            this.previousFocus.focus();
            this.previousFocus = null;
        }

        // remove the keyboard listener
        this.listElement.removeEventListener(Event.KEYDOWN, this.keyboardListener, false);

        // remove the element from dom
        final Document document = Elements.getDocument();
        final Node parent = this.popupElement.getParentNode();
        if (parent  != null) {
            parent.removeChild(this.popupElement);
        }

        // remove the mouse listener
        document.removeEventListener(Event.MOUSEDOWN, this.popupListener);
    }

    /**
     * Action taken when an item is validated.
     * @param itemElement the validated item
     */
    public void validateItem(final Element itemElement) {
        // by default, only hide the popup
        hide();
    }

    /**
     * Returns the widget viewed as an element.
     * @return the element
     */
    public Element asElement() {
        return this.popupElement;
    }

    /**
     * Tells if the popup widget wants focus.<br/>
     * Override the method to match needed value.
     * @return true iff the widget needs the focus
     */
    public boolean needsFocus() {
        return false;
    }
}
