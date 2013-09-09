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

package org.exoplatform.gwtframework.ui.client.command.ui;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Command;

import org.exoplatform.gwtframework.ui.client.command.StatusTextControl;
import org.exoplatform.gwtframework.ui.client.command.StatusTextControlStateListener;
import org.exoplatform.gwtframework.ui.client.component.TextButton;
import org.exoplatform.gwtframework.ui.client.component.Toolbar;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class TextButtonControl extends TextButton implements StatusTextControlStateListener {

    private HandlerManager eventBus;

    private StatusTextControl statusTextControl;

    private Toolbar toolbar;

    public TextButtonControl(HandlerManager eventBus, StatusTextControl statusTextControl, Toolbar toolbar) {
        super(statusTextControl.getText());

        getElement().setAttribute("control-id", statusTextControl.getId());

        this.eventBus = eventBus;
        this.statusTextControl = statusTextControl;
        this.toolbar = toolbar;

        setTitle(statusTextControl.getPrompt());

        if (statusTextControl.getEvent() != null) {
            setCommand(textButtonCommand);
        }

        if (statusTextControl.getSize() >= 0) {
            setWidth(statusTextControl.getSize());
        }

        setTextAlignment(statusTextControl.getTextAlignment());
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        getParent().setVisible(statusTextControl.isVisible());
        statusTextControl.getStateListeners().add(this);
    }

    @Override
    protected void onDetach() {
        super.onDetach();
        statusTextControl.getStateListeners().remove(this);
    }

    public void updateControlEnabling(boolean enabled) {
    }

    public void updateControlVisibility(boolean visible) {
        getParent().setVisible(visible);
        toolbar.hideDuplicatedDelimiters();

        if (statusTextControl.getEvent() != null) {
            setCommand(textButtonCommand);
        } else {
            setCommand(null);
        }
    }

    public void updateControlPrompt(String prompt) {
        setTitle(prompt);
    }

    public void updateControlIcon(String icon) {
    }

    public void updateStatusText(String text) {
        setText(text);
    }

    private Command textButtonCommand = new Command() {
        public void execute() {
            if (statusTextControl.getEvent() != null) {
                eventBus.fireEvent(statusTextControl.getEvent());
            }
        }
    };

}
