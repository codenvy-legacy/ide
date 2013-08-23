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
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.api.TextFieldItem;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.Label;
import org.exoplatform.gwtframework.ui.client.component.TextInput;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class AskForValueView extends ViewImpl implements org.exoplatform.ide.client.dialogs.AskForValueDialog.Display {

    private static final String ID = "ideAskForValueView";

    private static AskForValueViewUiBinder uiBinder = GWT.create(AskForValueViewUiBinder.class);

    interface AskForValueViewUiBinder extends UiBinder<Widget, AskForValueView> {
    }

    @UiField
    Label promptLabel;

    @UiField
    TextInput textField;

    @UiField
    ImageButton yesButton;

    @UiField
    ImageButton noButton;

    @UiField
    ImageButton cancelButton;

    public AskForValueView() {
        super(ID, "modal", "view title", new Image(IDEImageBundle.INSTANCE.about()), 450, 170);
        setCloseOnEscape(true);
        add(uiBinder.createAndBindUi(this));
    }

    @Override
    public HasText getPromptLabel() {
        return promptLabel;
    }

    @Override
    public TextFieldItem getTextField() {
        return textField;
    }

    @Override
    public HasClickHandlers getYesButton() {
        return yesButton;
    }

    @Override
    public HasClickHandlers getNoButton() {
        return noButton;
    }

    @Override
    public HasClickHandlers getCancelButton() {
        return cancelButton;
    }

    @Override
    public void setNoButtonEnabled(boolean enabled) {
        noButton.setEnabled(enabled);
    }

    @Override
    public void setYesButtonEnabled(boolean enabled) {
        yesButton.setEnabled(enabled);
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                textField.focus();
                textField.selectAll();
            }
        });
    }

}
