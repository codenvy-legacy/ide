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
package com.codenvy.ide.ui.dialogs.info;

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
 * PopUp Dialog window with title, message and button "Ok"
 *
 * @author Roman Nikitenko
 */
public class Info extends Window {

    interface InfoUiBinder extends UiBinder<Widget, Info> {
    }

    private static InfoUiBinder ourUiBinder = GWT.create(InfoUiBinder.class);
    private        Locale       locale      = GWT.create(Locale.class);

    @UiField
    TextArea message;


    /**
     * Create view.
     *
     * @param message
     *         the message for popup window
     */

    public Info(String message) {
        Widget widget = ourUiBinder.createAndBindUi(this);
        this.setWidget(widget);

        Button btnOk = createButton(locale.ok(), "info-window", new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                onClose();
            }
        });
        getFooter().add(btnOk);
        this.message.setText(message);
    }

    /**
     * Create view.
     *
     * @param handler
     *         the handler that call after user interact
     */

    public Info(final InfoHandler handler) {
        Widget widget = ourUiBinder.createAndBindUi(this);
        this.setWidget(widget);

        Button btnOk = createButton(locale.ok(), "info-window", new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                handler.onOk();
                onClose();
            }
        });
        getFooter().add(btnOk);

    }

    /**
     * Create view.
     *
     * @param title
     *         the title for popup window
     * @param message
     *         the message for popup window
     * @param handler
     *         the handler that call after user interact
     */

    public Info(String title, String message, final InfoHandler handler) {
        this(handler);
        this.setTitle(title);
        this.message.setText(message);
    }

    /**
     * Create view.
     *
     * @param title
     *         the title for popup window
     * @param message
     *         the message for popup window
     */

    public Info(String title, String message) {
        this(message);
        this.setTitle(title);
    }

    public void setMessage(String message) {
        this.message.setText(message);
    }

    @Override
    protected void onClose() {
        hide();
    }
}