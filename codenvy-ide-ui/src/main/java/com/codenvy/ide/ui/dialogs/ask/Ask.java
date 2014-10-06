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
package com.codenvy.ide.ui.dialogs.ask;

import com.codenvy.ide.ui.Locale;
import com.codenvy.ide.ui.window.Window;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import org.vectomatic.dom.svg.ui.SVGImage;

/**
 * PopUp Dialog window with title, message and buttons "Ok" and "Cancel"/
 *
 * @author Vitaly Parfonov
 */
public class Ask extends Window {

    @UiField
    SimplePanel message;

    @UiField
    SVGImage questionImage;

    interface AskUiBinder extends UiBinder<Widget, Ask> {
    }

    private static AskUiBinder uiBinder = GWT.create(AskUiBinder.class);

    private Locale locale = GWT.create(Locale.class);
    
    private final AskHandler handler;
    
    /**
     * Initialization constructor
     *
     * @param title
     *         the title for popup window
     * @param question
     *         the question that user must interact
     * @param handler
     *         the handler that call after user interact
     */
    public Ask(String title, String question, final AskHandler handler) {
        this.handler = handler;
        setTitle(title);
        Widget widget = uiBinder.createAndBindUi(this);
        setWidget(widget);
        message.addStyleName(resources.centerPanelCss().label());
        message.getElement().setInnerHTML(question);
        questionImage.getElement().setAttribute("class", resources.centerPanelCss().image());

        Button ok = createButton(locale.ok(), "ask-dialog-ok", new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                handler.onOk();
                onClose();
            }
        });
        ok.addStyleName(resources.centerPanelCss().blueButton());
        
        Button cancel = createButton(locale.cancel(), "ask-dialog-cancel", new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                handler.onCancel();
                onClose();
            }
        });
        
        getFooter().add(ok);
        getFooter().add(cancel);
        
    }
    
    /** {@inheritDoc} */
    @Override
    protected void onEnterClicked() {
        handler.onOk();
        onClose();
    }
    
    @Override
    protected void onClose() {
        hide();
    }
    
}
