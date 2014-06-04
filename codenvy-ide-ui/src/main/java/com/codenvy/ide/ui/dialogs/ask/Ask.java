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
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

/**
 * PopUp Dialog window with title, message and buttons "Ok" and "Cancel"/
 *
 *
 * @author Vitaly Parfonov
 */
public class Ask extends Window {

    @UiField
    TextArea message;

    interface AskUiBinder extends UiBinder<Widget, Ask> {
    }

    private static AskUiBinder uiBinder = GWT.create(AskUiBinder.class);

    private Locale locale = GWT.create(Locale.class);

    /**
     * Initialization constructor
     * @param title the title for popup window
     * @param question the question that user must interact
     * @param handler the handler that call after user interact
     */
    public Ask(String title, String question, final AskHandler handler) {
        Widget widget = uiBinder.createAndBindUi(this);
        setTitle(title);
        this.message.setText(question);
        setWidget(widget);
        Button ok = createButton(locale.ok(), "ask-dialog-ok", new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                handler.onOk();
                onClose();
            }
        });
        Button cancel = createButton(locale.cancel(), "ask-dialog-cancel", new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                handler.onCancel();
                onClose();
            }
        });
        getFooter().add(cancel);
        getFooter().add(ok);

    }

    @Override
    protected void onClose() {
        hide();
    }
}
