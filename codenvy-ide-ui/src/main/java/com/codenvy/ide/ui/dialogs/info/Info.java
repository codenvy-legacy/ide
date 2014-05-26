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
     * @param handler
     *         the handler that call after user interact
     */

    public Info(final InfoHandler handler, String message) {
        this(handler);
        this.message.setText(message);
    }

    public void setMessage(String message) {
        this.message.setText(message);
    }

    @Override
    protected void onClose() {
        hide();
    }
}