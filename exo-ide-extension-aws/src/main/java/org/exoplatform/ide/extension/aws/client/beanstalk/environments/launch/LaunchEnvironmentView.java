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
package org.exoplatform.ide.extension.aws.client.beanstalk.environments.launch;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
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
 * @version $Id: Sep 21, 2012 3:59:12 PM anya $
 */
public class LaunchEnvironmentView extends ViewImpl implements LaunchEnvironmentPresenter.Display {
    private static final String ID = "ideLaunchEnvironmentView";

    private static final int WIDTH = 555;

    private static final int HEIGHT = 195;

    private static final String ENV_NAME_FIELD_ID = "ideLaunchEnvironmentViewEnvNameField";

    private static final String ENV_DESCRIPTION_FIELD_ID = "ideLaunchEnvironmentViewEnvDescriptionField";

    private static final String SOLUTION_STACK_FIELD_ID = "ideLaunchEnvironmentViewSolutionStackField";

    private static final String VERSIONS_FIELD_ID = "ideLaunchEnvironmentViewVersionsField";

    private static final String LAUNCH_BUTTON_ID = "ideLaunchEnvironmentViewLaunchButton";

    private static final String CANCEL_BUTTON_ID = "ideLaunchEnvironmentViewCancelButton";

    private static CreateEnvironmentViewUiBinder uiBinder = GWT.create(CreateEnvironmentViewUiBinder.class);

    interface CreateEnvironmentViewUiBinder extends UiBinder<Widget, LaunchEnvironmentView> {
    }

    @UiField
    TextInput envNameField;

    @UiField
    TextInput envDescriptionField;

    @UiField
    SelectItem solutionStackField;

    @UiField
    SelectItem versionsField;

    @UiField
    ImageButton launchButton;

    @UiField
    ImageButton cancelButton;

    public LaunchEnvironmentView() {
        super(ID, ViewType.MODAL, AWSExtension.LOCALIZATION_CONSTANT.launchEnvironmentViewTitle(), null, WIDTH, HEIGHT, false);
        add(uiBinder.createAndBindUi(this));

        envNameField.setName(ENV_NAME_FIELD_ID);
        envDescriptionField.setName(ENV_DESCRIPTION_FIELD_ID);
        solutionStackField.setName(SOLUTION_STACK_FIELD_ID);
        versionsField.setName(VERSIONS_FIELD_ID);
        launchButton.setButtonId(LAUNCH_BUTTON_ID);
        cancelButton.setButtonId(CANCEL_BUTTON_ID);
    }

    /** @see org.exoplatform.ide.extension.aws.client.beanstalk.environments.launch.LaunchEnvironmentPresenter.Display#getEnvNameField() */
    @Override
    public TextFieldItem getEnvNameField() {
        return envNameField;
    }

    /** @see org.exoplatform.ide.extension.aws.client.beanstalk.environments.launch.LaunchEnvironmentPresenter
     * .Display#getEnvDescriptionField() */
    @Override
    public TextFieldItem getEnvDescriptionField() {
        return envDescriptionField;
    }

    /** @see org.exoplatform.ide.extension.aws.client.beanstalk.environments.launch.LaunchEnvironmentPresenter
     * .Display#getSolutionStackField() */
    @Override
    public HasValue<String> getSolutionStackField() {
        return solutionStackField;
    }

    /** @see org.exoplatform.ide.extension.aws.client.beanstalk.environments.launch.LaunchEnvironmentPresenter
     * .Display#setSolutionStackValues(java.lang.String[]) */
    @Override
    public void setSolutionStackValues(String[] values) {
        solutionStackField.setValueMap(values);
    }

    /** @see org.exoplatform.ide.extension.aws.client.beanstalk.environments.launch.LaunchEnvironmentPresenter.Display#getLaunchButton() */
    @Override
    public HasClickHandlers getLaunchButton() {
        return launchButton;
    }

    /** @see org.exoplatform.ide.extension.aws.client.beanstalk.environments.launch.LaunchEnvironmentPresenter.Display#getCancelButton() */
    @Override
    public HasClickHandlers getCancelButton() {
        return cancelButton;
    }

    /** @see org.exoplatform.ide.extension.aws.client.beanstalk.environments.launch.LaunchEnvironmentPresenter.Display#enableLaunchButton
     * (boolean) */
    @Override
    public void enableLaunchButton(boolean enabled) {
        launchButton.setEnabled(enabled);
    }

    /** @see org.exoplatform.ide.extension.aws.client.beanstalk.environments.launch.LaunchEnvironmentPresenter
     * .Display#focusInEnvNameField() */
    @Override
    public void focusInEnvNameField() {
        envNameField.setFocus(true);
    }

    /** @see org.exoplatform.ide.extension.aws.client.beanstalk.environments.launch.LaunchEnvironmentPresenter.Display#getVersionField() */
    @Override
    public HasValue<String> getVersionField() {
        return versionsField;
    }

    /**
     * @see org.exoplatform.ide.extension.aws.client.beanstalk.environments.launch.LaunchEnvironmentPresenter.Display#setVersionValues
     * (java.lang.String[],
     *      java.lang.String)
     */
    @Override
    public void setVersionValues(String[] values, String selectedValue) {
        versionsField.setValueMap(values, selectedValue);
    }
}
