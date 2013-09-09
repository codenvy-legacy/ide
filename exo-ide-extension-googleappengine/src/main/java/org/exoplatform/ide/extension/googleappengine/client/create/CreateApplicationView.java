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
package org.exoplatform.ide.extension.googleappengine.client.create;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.Label;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.extension.googleappengine.client.GAEClientBundle;
import org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineExtension;

public class CreateApplicationView extends ViewImpl implements CreateApplicationPresenter.Display {
    private static final String ID = "exoCreateApplicationView";

    private static final String DEPLOY_BUTTON_ID = "exoCreateApplicationViewDeployButton";

    private static final String CANCEL_BUTTON_ID = "exoCreateApplicationViewCancelButton";

    private static final String TITLE = GoogleAppEngineExtension.GAE_LOCALIZATION.createApplicationViewTitle();

    private static final int WIDTH = 545;

    private static final int HEIGHT = 180;

    private static CreateApplicationViewUiBinder uiBinder = GWT.create(CreateApplicationViewUiBinder.class);

    interface CreateApplicationViewUiBinder extends UiBinder<Widget, CreateApplicationView> {
    }

    @UiField
    ImageButton deployButton;

    @UiField
    ImageButton cancelButton;

    @UiField
    ImageButton createButton;

    @UiField
    Label instructionLabel;

    public CreateApplicationView() {
        super(ID, ViewType.MODAL, TITLE, new Image(GAEClientBundle.INSTANCE.googleAppEngine()), WIDTH, HEIGHT);
        add(uiBinder.createAndBindUi(this));
        deployButton.setButtonId(DEPLOY_BUTTON_ID);
        cancelButton.setButtonId(CANCEL_BUTTON_ID);
        createButton.setImage(new Image(GAEClientBundle.INSTANCE.googleAppEngine()));
        createButton.setDisabledImage(new Image(GAEClientBundle.INSTANCE.googleAppEngineDisabled()));
        createButton.setText(GoogleAppEngineExtension.GAE_LOCALIZATION.createButton());
    }

    /** @see org.exoplatform.ide.extension.googleappengine.client.create.CreateApplicationPresenter.Display#getDeployButton() */
    @Override
    public HasClickHandlers getDeployButton() {
        return deployButton;
    }

    /** @see org.exoplatform.ide.extension.googleappengine.client.create.CreateApplicationPresenter.Display#getCancelButton() */
    @Override
    public HasClickHandlers getCancelButton() {
        return cancelButton;
    }

    @Override
    public HasClickHandlers getCreateButton() {
        return createButton;
    }

    @Override
    public void enableDeployButton(boolean enable) {
        deployButton.setEnabled(enable);
    }

    @Override
    public void enableCreateButton(boolean enable) {
        createButton.setEnabled(enable);
    }

    @Override
    public void setUserInstructions(String instructions) {
        instructionLabel.setValue(instructions);
    }
}
