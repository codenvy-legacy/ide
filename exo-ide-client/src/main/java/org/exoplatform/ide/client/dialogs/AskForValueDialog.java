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
package org.exoplatform.ide.client.dialogs;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasText;

import org.exoplatform.gwtframework.ui.client.api.TextFieldItem;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class AskForValueDialog implements ViewClosedHandler {

    public interface Display extends IsView {

        HasText getPromptLabel();

        TextFieldItem getTextField();
        
        HasClickHandlers getYesButton();

        void setYesButtonEnabled(boolean enabled);

        HasClickHandlers getNoButton();

        void setNoButtonEnabled(boolean enabled);

        HasClickHandlers getCancelButton();

    }

    private static AskForValueDialog instance;

    public static AskForValueDialog getInstance() {
        return instance;
    }

    private Display display;

    private ValueCallback callback;

    private ValueDiscardCallback discardCallback;

    public AskForValueDialog() {
        instance = this;
        IDE.addHandler(ViewClosedEvent.TYPE, this);
    }

    public void ask(String title, String prompt, String defaultValue, int dialogWidth, boolean selectAllText,
                    ValueCallback callback, ValueDiscardCallback discardCallback) {
        if (display != null) {
            Window.alert("Another Ask For Value Dialog is opened!");
            return;
        }

        this.callback = callback;
        this.discardCallback = discardCallback;

        display = GWT.create(Display.class);

        display.asView().setTitle(title);
        display.getPromptLabel().setText(prompt);

        display.getTextField().setValue(defaultValue);

        display.getTextField().addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                checkYesButtonEnabled();
            }
        });
        checkYesButtonEnabled();

        display.getTextField().addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                if (event.getNativeKeyCode() == 13) {
                    if (display.getTextField().getValue() == null || display.getTextField().getValue().isEmpty()) {
                        return;
                    }

                    yesButtonClicked();
                }
            }
        });

        display.getYesButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                yesButtonClicked();
            }
        });

        display.getNoButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                noButtonClicked();
            }
        });

        display.getCancelButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                cancelButtonClicked();
            }
        });

        IDE.getInstance().openView(display.asView());
    }

    private void checkYesButtonEnabled() {
        if (display.getTextField().getValue() == null || display.getTextField().getValue().isEmpty()) {
            display.setYesButtonEnabled(false);
        } else {
            display.setYesButtonEnabled(true);
        }
    }

    public void ask(String title, String prompt, String defaultValue, int dialogWidth, ValueCallback callback,
                    ValueDiscardCallback discardCallback) {
        ask(title, prompt, defaultValue, dialogWidth, false, callback, discardCallback);
    }

    public void ask(String title, String prompt, String defaultValue, int dialogWidth, boolean selectAllText, ValueCallback callback) {
        ask(title, prompt, defaultValue, dialogWidth, selectAllText, callback, null);
    }

    public void ask(String title, String prompt, String defaultValue, int dialogWidth, ValueCallback callback) {
        ask(title, prompt, defaultValue, dialogWidth, false, callback, null);
    }

    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (event.getView() instanceof Display) {
            display = null;
        }
    }

    private void yesButtonClicked() {
        String value = display.getTextField().getValue();
        IDE.getInstance().closeView(display.asView().getId());
        callback.execute(value);
    }

    private void noButtonClicked() {
        IDE.getInstance().closeView(display.asView().getId());
        discardCallback.discard();
    }

    private void cancelButtonClicked() {
        IDE.getInstance().closeView(display.asView().getId());
        callback.execute(null);
    }

}
