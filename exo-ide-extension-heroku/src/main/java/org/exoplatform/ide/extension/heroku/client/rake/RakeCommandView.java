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
package org.exoplatform.ide.extension.heroku.client.rake;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.api.TextFieldItem;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.TextInput;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.extension.heroku.client.HerokuExtension;

/**
 * View for executing rake command. Must be pointed in Views.gwt.xml.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Jun 17, 2011 10:55:51 AM anya $
 */
public class RakeCommandView extends ViewImpl implements RakeCommandPresenter.Display {
    private static final String ID = "ideRakeCommandView";

    private static final int WIDTH = 480;

    private static final int HEIGHT = 120;

    private static final String RUN_BUTTON_ID = "ideRakeCommandViewRunButton";

    private static final String HELP_BUTTON_ID = "ideRakeCommandViewHelpButton";

    private static final String CLOSE_BUTTON_ID = "ideRakeCommandViewCloseButton";

    private static final String COMMAND_FIELD_ID = "ideRakeCommandViewCommandField";

    private static RakeCommandViewUiBinder uiBinder = GWT.create(RakeCommandViewUiBinder.class);

    interface RakeCommandViewUiBinder extends UiBinder<Widget, RakeCommandView> {
    }

    /** Rake command field. */
    @UiField
    TextInput commandField;

    /** Run rake command button. */
    @UiField
    ImageButton runButton;

    /** Get rake help button. */
    @UiField
    ImageButton helpButton;

    /** Close view button. */
    @UiField
    ImageButton closeButton;

    public RakeCommandView() {
        super(ID, ViewType.POPUP, HerokuExtension.LOCALIZATION_CONSTANT.rakeViewTitle(), null, WIDTH, HEIGHT, false);
        add(uiBinder.createAndBindUi(this));

        commandField.setName(COMMAND_FIELD_ID);
        runButton.setButtonId(RUN_BUTTON_ID);
        helpButton.setButtonId(HELP_BUTTON_ID);
        closeButton.setButtonId(CLOSE_BUTTON_ID);
    }

    /** @see org.exoplatform.ide.extension.heroku.client.rake.RakeCommandPresenter.Display#getCommandField() */
    @Override
    public TextFieldItem getCommandField() {
        return commandField;
    }

    /** @see org.exoplatform.ide.extension.heroku.client.rake.RakeCommandPresenter.Display#getRunButton() */
    @Override
    public HasClickHandlers getRunButton() {
        return runButton;
    }

    /** @see org.exoplatform.ide.extension.heroku.client.rake.RakeCommandPresenter.Display#getCloseButton() */
    @Override
    public HasClickHandlers getCloseButton() {
        return closeButton;
    }

    /** @see org.exoplatform.ide.extension.heroku.client.rake.RakeCommandPresenter.Display#getHelpButton() */
    @Override
    public HasClickHandlers getHelpButton() {
        return helpButton;
    }

    /** @see org.exoplatform.ide.extension.heroku.client.rake.RakeCommandPresenter.Display#enableRunButton(boolean) */
    @Override
    public void enableRunButton(boolean isEnabled) {
        runButton.setEnabled(isEnabled);
    }

    /** @see org.exoplatform.ide.extension.heroku.client.rake.RakeCommandPresenter.Display#focusInCommandField() */
    @Override
    public void focusInCommandField() {
        commandField.focus();
    }

}
