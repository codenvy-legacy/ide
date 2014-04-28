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
package com.codenvy.ide.ui.dialogs;

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
