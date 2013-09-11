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
package org.exoplatform.ide.extension.aws.client.beanstalk.create;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.api.TextFieldItem;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.SelectItem;
import org.exoplatform.gwtframework.ui.client.component.TextInput;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.extension.aws.client.AWSExtension;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Sep 17, 2012 3:32:44 PM anya $
 */
public class CreateApplicationView extends ViewImpl implements CreateApplicationPresenter.Display {

    private static final String ID = "ideCreateApplicationView";

    private static final int WIDTH = 555;

    private static final int HEIGHT = 315;

    private static final String NAME_FIELD_ID = "ideCreateApplicationViewNameField";

    private static final String DESCRIPTION_FIELD_ID = "ideCreateApplicationViewDescriptionField";

    private static final String S3_BUCKET_FIELD_ID = "ideCreateApplicationViewS3BucketField";

    private static final String S3_KEY_FIELD_ID = "ideCreateApplicationViewS3KeyField";

    private static final String ENV_NAME_FIELD_ID = "ideCreateApplicationViewEnvNameField";

    private static final String ENV_DESCRIPTION_FIELD_ID = "ideCreateApplicationViewEnvDescriptionField";

    private static final String SOLUTION_STACK_FIELD_ID = "ideCreateApplicationViewSolutionStackField";

    private static final String LAUNCH_ENV_FIELD_ID = "ideCreateApplicationViewLaunchEnvField";

    private static final String NEXT_BUTTON_ID = "ideCreateApplicationViewNextButton";

    private static final String BACK_BUTTON_ID = "ideCreateApplicationViewBackButton";

    private static final String FINISH_BUTTON_ID = "ideCreateApplicationViewFinishButton";

    private static final String CANCEL_BUTTON_ID = "ideCreateApplicationViewCancelButton";

    private static CreateApplicationViewUiBinder uiBinder = GWT.create(CreateApplicationViewUiBinder.class);

    interface CreateApplicationViewUiBinder extends UiBinder<Widget, CreateApplicationView> {
    }

    @UiField
    TextInput nameField;

    @UiField
    TextInput descriptionField;

    @UiField
    TextInput s3BucketField;

    @UiField
    TextInput s3KeyField;

    @UiField
    ImageButton nextButton;

    @UiField
    ImageButton backButton;

    @UiField
    ImageButton finishButton;

    @UiField
    ImageButton cancelButton;

    @UiField
    FlowPanel createApplicationStep;

    @UiField
    FlowPanel createEnvironmentStep;

    @UiField
    TextInput envNameField;

    @UiField
    TextInput envDescriptionField;

    @UiField
    SelectItem solutionStackField;

    @UiField
    CheckBox launchEnvironment;

    public CreateApplicationView() {
        super(ID, ViewType.MODAL, AWSExtension.LOCALIZATION_CONSTANT.createApplicationViewTitle(), null, WIDTH, HEIGHT);
        add(uiBinder.createAndBindUi(this));

        nameField.setName(NAME_FIELD_ID);
        descriptionField.setName(DESCRIPTION_FIELD_ID);
        s3BucketField.setName(S3_BUCKET_FIELD_ID);
        s3KeyField.setName(S3_KEY_FIELD_ID);

        envNameField.setName(ENV_NAME_FIELD_ID);
        envDescriptionField.setName(ENV_DESCRIPTION_FIELD_ID);
        solutionStackField.setName(SOLUTION_STACK_FIELD_ID);
        launchEnvironment.setName(LAUNCH_ENV_FIELD_ID);

        nextButton.setButtonId(NEXT_BUTTON_ID);
        backButton.setButtonId(BACK_BUTTON_ID);
        finishButton.setButtonId(FINISH_BUTTON_ID);
        cancelButton.setButtonId(CANCEL_BUTTON_ID);
    }

    /** @see org.exoplatform.ide.extension.aws.client.beanstalk.create.CreateApplicationPresenter.Display#getNameField() */
    @Override
    public TextFieldItem getNameField() {
        return nameField;
    }

    /** @see org.exoplatform.ide.extension.aws.client.beanstalk.create.CreateApplicationPresenter.Display#getDescriptionField() */
    @Override
    public TextFieldItem getDescriptionField() {
        return descriptionField;
    }

    /** @see org.exoplatform.ide.extension.aws.client.beanstalk.create.CreateApplicationPresenter.Display#getS3BucketField() */
    @Override
    public TextFieldItem getS3BucketField() {
        return s3BucketField;
    }

    /** @see org.exoplatform.ide.extension.aws.client.beanstalk.create.CreateApplicationPresenter.Display#getS3KeyField() */
    @Override
    public TextFieldItem getS3KeyField() {
        return s3KeyField;
    }

    /** @see org.exoplatform.ide.extension.aws.client.beanstalk.create.CreateApplicationPresenter.Display#getNextButton() */
    @Override
    public HasClickHandlers getNextButton() {
        return nextButton;
    }

    /** @see org.exoplatform.ide.extension.aws.client.beanstalk.create.CreateApplicationPresenter.Display#getCancelButton() */
    @Override
    public HasClickHandlers getCancelButton() {
        return cancelButton;
    }

    /** @see org.exoplatform.ide.extension.aws.client.beanstalk.create.CreateApplicationPresenter.Display#focusInApplicationNameField() */
    @Override
    public void focusInApplicationNameField() {
        nameField.setFocus(true);
    }

    /** @see org.exoplatform.ide.extension.aws.client.beanstalk.create.CreateApplicationPresenter.Display#showCreateApplicationStep() */
    @Override
    public void showCreateApplicationStep() {
        createEnvironmentStep.setVisible(false);
        createApplicationStep.setVisible(true);
        backButton.setVisible(false);
        finishButton.setVisible(false);
        nextButton.setVisible(true);
    }

    /** @see org.exoplatform.ide.extension.aws.client.beanstalk.create.CreateApplicationPresenter.Display#showCreateEnvironmentStep() */
    @Override
    public void showCreateEnvironmentStep() {
        createApplicationStep.setVisible(false);
        createEnvironmentStep.setVisible(true);
        backButton.setVisible(true);
        finishButton.setVisible(true);
        nextButton.setVisible(false);
    }

    /** @see org.exoplatform.ide.extension.aws.client.beanstalk.create.CreateApplicationPresenter.Display#getBackButton() */
    @Override
    public HasClickHandlers getBackButton() {
        return backButton;
    }

    /** @see org.exoplatform.ide.extension.aws.client.beanstalk.create.CreateApplicationPresenter.Display#getFinishButton() */
    @Override
    public HasClickHandlers getFinishButton() {
        return finishButton;
    }

    /** @see org.exoplatform.ide.extension.aws.client.beanstalk.create.CreateApplicationPresenter.Display#getEnvNameField() */
    @Override
    public TextFieldItem getEnvNameField() {
        return envNameField;
    }

    /** @see org.exoplatform.ide.extension.aws.client.beanstalk.create.CreateApplicationPresenter.Display#getEnvDescriptionField() */
    @Override
    public TextFieldItem getEnvDescriptionField() {
        return envDescriptionField;
    }

    /** @see org.exoplatform.ide.extension.aws.client.beanstalk.create.CreateApplicationPresenter.Display#getSolutionStackField() */
    @Override
    public HasValue<String> getSolutionStackField() {
        return solutionStackField;
    }

    /** @see org.exoplatform.ide.extension.aws.client.beanstalk.create.CreateApplicationPresenter.Display#getLaunchEnvField() */
    @Override
    public HasValue<Boolean> getLaunchEnvField() {
        return launchEnvironment;
    }

    /** @see org.exoplatform.ide.extension.aws.client.beanstalk.create.CreateApplicationPresenter.Display#enableCreateEnvironmentStep
     * (boolean) */
    @Override
    public void enableCreateEnvironmentStep(boolean enabled) {
        envNameField.setEnabled(enabled);
        envDescriptionField.setEnabled(enabled);
        solutionStackField.setEnabled(enabled);
    }

    /** @see org.exoplatform.ide.extension.aws.client.beanstalk.create.CreateApplicationPresenter.Display#setSolutionStackValues(java.lang
     * .String[]) */
    @Override
    public void setSolutionStackValues(String[] values) {
        solutionStackField.setValueMap(values);
    }

}
