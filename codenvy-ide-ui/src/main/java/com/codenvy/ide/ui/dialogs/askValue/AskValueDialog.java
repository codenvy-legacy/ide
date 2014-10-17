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
package com.codenvy.ide.ui.dialogs.askValue;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import elemental.events.KeyboardEvent.KeyCode;

import com.codenvy.ide.ui.Locale;
import com.codenvy.ide.ui.window.Window;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import java.util.HashMap;
import java.util.Map;

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

    private Button ok;

    private AskValueCallback callback;

    private boolean isEmptyAllowed =true;

    private HashMap<String, String> replaceMap;


    interface AskUiBinder extends UiBinder<Widget, AskValueDialog> {
    }

    private static AskUiBinder uiBinder = GWT.create(AskUiBinder.class);

    private Locale locale = GWT.create(Locale.class);

    /**
     * Creates and displays new AskValueDialog.
     *
     * @param title
     *         the title for popup window
     * @param message
     *         the message for input field
     * @param callback
     *         the callback that call after user interact
     */
    public AskValueDialog(String title, String message, final AskValueCallback callback) {
        this(title, message, null, callback);
    }

    /**
     * Creates and displays new AskValueDialog.
     *
     * @param title
     *         the title for popup window
     * @param message
     *         the message for input field
     * @param defaultValue
     *         default value for input field
     * @param callback
     *         the callback that call after user interact
     */
    public AskValueDialog(final String title, final String message, final String defaultValue, final AskValueCallback callback) {
        this(title, message, defaultValue, 0, 0, true, callback);
    }

    /**
     * Creates and displays new AskValueDialog.
     *
     * @param title
     *         the title for popup window
     * @param message
     *         the message for input field
     * @param defaultValue
     *         default value for input field
     * @param selectionStartIndex
     *         indicates the start position of selection
     * @param selectionLength
     *         indicates length of selection
     * @param callback
     *         the callback that call after user interact
     */
    public AskValueDialog(final String title, final String message, final String defaultValue,
                          final int selectionStartIndex, final int selectionLength, boolean isEmptyAllowed, final AskValueCallback callback) {
        this(title, message, defaultValue, selectionStartIndex, selectionLength, isEmptyAllowed, null, callback);
    }

    /**
     * Creates and displays new AskValueDialog.
     *
     * @param title
     *         the title for popup window
     * @param message
     *         the message for input field
     * @param defaultValue
     *         default value for input field
     * @param selectionStartIndex
     *         indicates the start position of selection
     * @param selectionLength
     *         indicates length of selection
     * @param callback
     *         the callback that call after user interact
     */
    public AskValueDialog(final String title, final String message, final String defaultValue,
                          final int selectionStartIndex, final int selectionLength,
                          boolean isEmptyAllowed, final HashMap<String, String> replaceMap,
                          final AskValueCallback callback) {
        this.callback = callback;
        this.isEmptyAllowed = isEmptyAllowed;
        this.replaceMap = replaceMap;

        Widget widget = uiBinder.createAndBindUi(this);
        setTitle(title);
        this.message.setText(message);
        setWidget(widget);
        ok = createButton(locale.ok(), "askValue-dialog-ok", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                callback.onOk(value.getValue());
                onClose();
            }
        });
        ok.addStyleName(resources.centerPanelCss().blueButton());
        ok.getElement().getStyle().setMarginRight(12, Style.Unit.PX);

        Button cancel = createButton(locale.cancel(), "askValue-dialog-cancel", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                callback.onCancel();
                onClose();
            }
        });
        getFooter().add(ok);
        getFooter().add(cancel);

        if (defaultValue != null && !defaultValue.isEmpty()) {
            value.setText(defaultValue);
            Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                @Override
                public void execute() {
                    value.setSelectionRange(selectionStartIndex, selectionLength);
                }
            });
        }
        this.ensureDebugId("askValueDialog-window");
        this.value.ensureDebugId("askValueDialog-textBox");

        if (!isEmptyAllowed && defaultValue == null) {
            ok.setEnabled(false);
        }
    }

    @UiHandler("value")
    void onKeyUp(KeyUpEvent event) {
        if (!isEmptyAllowed) {
            if (value.getValue() != null) {
                if (replaceMap != null) {
                    for (Map.Entry<String, String> entry : replaceMap.entrySet()) {
                        if (value.getValue().indexOf(entry.getKey()) >= 0) {
                            value.setValue(value.getValue().replaceAll(entry.getKey(), entry.getValue()));
                        }
                    }
                }

                if (value.getValue().trim().isEmpty()) {
                    ok.setEnabled(false);
                } else {
                    ok.setEnabled(true);
                }
            } else {
                ok.setEnabled(false);
            }
        }

        if (event.getNativeEvent().getKeyCode() == KeyCode.ENTER && callback != null) {
            callback.onOk(value.getValue());
            onClose();
        }
    }


    @Override
    public void show() {
        new Timer() {
            @Override
            public void run() {
                value.setFocus(true);
            }
        }.schedule(300);
        super.show();
    }

    @Override
    protected void onClose() {
        hide();
    }
}
