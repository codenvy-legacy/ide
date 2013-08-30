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
package org.exoplatform.ide.editor.api.codeassitant.ui;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;

import org.exoplatform.gwtframework.commons.util.BrowserResolver;
import org.exoplatform.gwtframework.commons.util.BrowserResolver.Browser;
import org.exoplatform.ide.editor.api.codeassitant.Token;
import org.exoplatform.ide.editor.codeassistant.CodeAssistantClientBundle;

import java.util.List;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Nov 19, 2010 4:12:46 PM evgen $
 */
public class AssistImportDeclarationForm extends Composite implements ResizeHandler, Event.NativePreviewHandler {

    private static final String ID = "ideAssistImportDeclarationForm";

    private AbsolutePanel lockLayer;

    private AbsolutePanel absolutePanel;

    private LockLayer blockMouseEventsPanel;

    private AssistantImportDeclarationPanel flowPanel;

    private CodeAssitantScrollPanel scrollPanel;

    private MousHandler mousHandler;

    private ImportDeclarationSelectedHandler handler;

    private TokenWidget selectedWidget;

    private HandlerRegistration keyboardManagerRegistration;

    private TokenWidgetFactory factory;

    private HandlerRegistration resizeHandler;

    public AssistImportDeclarationForm(int left, int top, List<Token> items, TokenWidgetFactory factory,
                                       ImportDeclarationSelectedHandler handler) {
        absolutePanel = new AbsolutePanel();

        this.handler = handler;
        this.factory = factory;
        initWidget(absolutePanel);

        lockLayer = new AbsolutePanel();
        RootPanel.get().add(lockLayer, 0, 0);

        lockLayer.setWidth("" + Window.getClientWidth() + "px");
        lockLayer.setHeight("" + Window.getClientHeight() + "px");
        DOM.setElementAttribute(lockLayer.getElement(), "id", "menu-lock-layer-id");
        DOM.setStyleAttribute(lockLayer.getElement(), "zIndex", "" + (Integer.MAX_VALUE));

        blockMouseEventsPanel = new LockLayer();
        blockMouseEventsPanel.setStyleName("exo-lockLayer");
        blockMouseEventsPanel.setWidth("" + Window.getClientWidth() + "px");
        blockMouseEventsPanel.setHeight("" + Window.getClientHeight() + "px");
        lockLayer.add(blockMouseEventsPanel, 0, 0);

        flowPanel = new AssistantImportDeclarationPanel();
        flowPanel.getElement().setId(ID);

        scrollPanel = new CodeAssitantScrollPanel();

        keyboardManagerRegistration = Event.addNativePreviewHandler(this);

        resizeHandler = Window.addResizeHandler(this);

        FocusPanel focusPanel = new FocusPanel();

        focusPanel.add(scrollPanel);

        scrollPanel.add(flowPanel);
        scrollPanel.setHeight("100%");

        mousHandler = new MousHandler();
        flowPanel.setWidth("100%");

        scrollPanel.setStylePrimaryName(CodeAssistantClientBundle.INSTANCE.css().panelStyle());

        focusPanel.setHeight("100px");
        focusPanel.setWidth("350px");

        lockLayer.add(focusPanel, left, top);
        flowPanel.add(new Label("No Proposals."));

        fillTokens(items);
        focusPanel.setFocus(true);

    }

    /** @param items */
    private void fillTokens(List<Token> items) {
        if (items.isEmpty()) {
            return;
        }

        flowPanel.clear();
        for (Token token : items) {
            TokenWidget widget = factory.buildTokenWidget(token);
            widget.addClickHandler(mousHandler);
            widget.addDoubleClickHandler(mousHandler);
            flowPanel.addWidget(widget);
        }

        selectTokenWidget(flowPanel.getEntries().get(0));
    }

    private void selectTokenWidget(TokenWidget widget) {
        if (widget.equals(selectedWidget)) {
            return;
        }

        if (selectedWidget != null) {
            selectedWidget.setDefaultStyle();
        }

        selectedWidget = widget;
        selectedWidget.setSelectedStyle();
    }

    private void cancelAssistant() {
        lockLayer.removeFromParent();
        handler.onImportCanceled();
    }

    private void tokenSelected() {
        lockLayer.removeFromParent();
        handler.onImportDeclarationSelected(selectedWidget.getToken());
    }

    @Override
    protected void onDetach() {
        super.onDetach();

        if (keyboardManagerRegistration != null) {
            keyboardManagerRegistration.removeHandler();
            keyboardManagerRegistration = null;
        }
        resizeHandler.removeHandler();
    }

    /**
     *
     */
    public void keyPressDown() {
        if (selectedWidget == null)
            return;
        int i = flowPanel.getEntries().indexOf(selectedWidget);
        if (flowPanel.getEntries().size() - 1 > i) {
            selectWidget(i + 1);
        }
    }

    /**
     *
     */
    public void keyPressUP() {
        if (selectedWidget == null)
            return;

        int i = flowPanel.getEntries().indexOf(selectedWidget);
        if (0 < i) {
            selectWidget(i - 1);
        }
    }

    /** @param i */
    private void selectWidget(int i) {
        scrollPanel.ensureVisible(flowPanel.getEntries().get(i));
        // DOM.scrollIntoView(widgets.get(i).getElement());
        // ensureVisibleImpl(scrollPanel.getElement(), widgets.get(i).getElement());
        selectTokenWidget(flowPanel.getEntries().get(i));

    }

    private class CodeAssitantScrollPanel extends ScrollPanel implements HasMouseOutHandlers, HasKeyDownHandlers {

        /** @see com.google.gwt.event.dom.client.HasMouseOutHandlers#addMouseOutHandler(com.google.gwt.event.dom.client.MouseOutHandler) */
        public HandlerRegistration addMouseOutHandler(MouseOutHandler handler) {
            return addDomHandler(handler, MouseOutEvent.getType());
        }

        /** @see com.google.gwt.event.dom.client.HasKeyDownHandlers#addKeyDownHandler(com.google.gwt.event.dom.client.KeyDownHandler) */
        public HandlerRegistration addKeyDownHandler(KeyDownHandler handler) {
            return addDomHandler(handler, KeyDownEvent.getType());
        }

    }

    /** Lock Layer uses for locking of screen. Uses for hiding popups. */
    private class LockLayer extends AbsolutePanel {

        public LockLayer() {
            sinkEvents(Event.ONMOUSEDOWN);
        }

        @Override
        public void onBrowserEvent(Event event) {
            switch (DOM.eventGetType(event)) {
                case Event.ONMOUSEDOWN:
                    cancelAssistant();
                    break;

            }
        }

    }

    protected class MousHandler implements ClickHandler, DoubleClickHandler {

        /** @see com.google.gwt.event.dom.client.ClickHandler#onClick(com.google.gwt.event.dom.client.ClickEvent) */
        public void onClick(ClickEvent event) {
            TokenWidget t = (TokenWidget)event.getSource();
            selectTokenWidget(t);
        }

        /** @see com.google.gwt.event.dom.client.DoubleClickHandler#onDoubleClick(com.google.gwt.event.dom.client.DoubleClickEvent) */
        public void onDoubleClick(DoubleClickEvent event) {
            tokenSelected();
        }

    }

    /**
     * @see com.google.gwt.user.client.Event.NativePreviewHandler#onPreviewNativeEvent(com.google.gwt.user.client.Event
     *      .NativePreviewEvent)
     */
    public void onPreviewNativeEvent(NativePreviewEvent event) {
        NativeEvent nativeEvent = event.getNativeEvent();

        int type = event.getTypeInt();
        int typeEvent = Event.ONKEYDOWN;
        if (BrowserResolver.CURRENT_BROWSER.equals(Browser.FIREFOX)) {
            typeEvent = Event.ONKEYPRESS;
        }

        if (type == typeEvent) {
            switch (nativeEvent.getKeyCode()) {
                case KeyCodes.KEY_DOWN:
                    keyPressDown();
                    break;

                case KeyCodes.KEY_UP:
                    keyPressUP();
                    break;

                case KeyCodes.KEY_ENTER:
                    tokenSelected();
                    break;

                case KeyCodes.KEY_ESCAPE:
                    cancelAssistant();
                    break;

            }
        }
    }

    /** @see com.google.gwt.event.logical.shared.ResizeHandler#onResize(com.google.gwt.event.logical.shared.ResizeEvent) */
    @Override
    public void onResize(ResizeEvent event) {
        cancelAssistant();
    }

}
