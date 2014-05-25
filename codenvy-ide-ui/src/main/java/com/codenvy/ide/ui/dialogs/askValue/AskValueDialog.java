/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 * [2012] - [$today.year] Codenvy, S.A. 
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
package com.codenvy.ide.ui.dialogs.askValue;

import com.codenvy.ide.ui.Locale;
import com.codenvy.ide.ui.window.Window;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * Window for asking user to enter any value.
 *
 * @author Vitaly Parfonov
 * @author Artem Zatsarynnyy
 */
public class AskValueDialog extends Window {

    @UiField
    Label message;

    @UiField
    TextBox value;

    interface AskUiBinder extends UiBinder<Widget, AskValueDialog> {
    }

    private static AskUiBinder uiBinder = GWT.create(AskUiBinder.class);

    private Locale locale = GWT.create(Locale.class);

    /**
     * Create new dialog.
     *
     * @param title
     *         the title for popup window
     * @param message
     *         the message for input field
     * @param callback
     *         the callback that call after user interact
     */
    public AskValueDialog(String title, String message, final AskValueCallback callback) {
        Widget widget = uiBinder.createAndBindUi(this);
        setTitle(title);
        this.message.setText(message);
        setWidget(widget);
        Button ok = createButton(locale.ok(), "askValue-dialog-ok", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                callback.onOk(value.getValue());
                onClose();
            }
        });
        Button cancel = createButton(locale.cancel(), "askValue-dialog-cancel", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                callback.onCancel();
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
