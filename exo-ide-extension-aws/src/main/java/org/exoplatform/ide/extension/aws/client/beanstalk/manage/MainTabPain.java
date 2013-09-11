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
package org.exoplatform.ide.extension.aws.client.beanstalk.manage;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.Label;
import org.exoplatform.gwtframework.ui.client.component.TextInput;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: May 23, 2012 12:14:23 PM anya $
 */
public class MainTabPain extends Composite {

    private static MainTabPainUiBinder uiBinder = GWT.create(MainTabPainUiBinder.class);

    interface MainTabPainUiBinder extends UiBinder<Widget, MainTabPain> {
    }

    private static final String NAME_FIELD_ID = "ideMainTabPainNameField";

    private static final String DESCRIPTION_FIELD_ID = "ideMainTabPainDescriptionField";

    private static final String CREATION_DATE_FIELD_ID = "ideMainTabPainCreateDateField";

    private static final String UPDATED_DATE_FIELD_ID = "ideMainTabPainUpdatedDateField";

    private static final String EDIT_DESCRIPTION_BUTTON_ID = "ideMainTabPainEditDescriptionButton";

    private static final String DELETE_APPLICATION_BUTTON_ID = "ideMainTabPainDeleteApplicationButton";

    private static final String CREATE_VERSION_BUTTON_ID = "ideMainTabPainCreateVersionButton";

    private static final String LAUNCH_ENVIRONMENT_BUTTON_ID = "ideMainTabPainLaunchEnvButton";

    @UiField
    TextInput nameField;

    @UiField
    TextInput descriptionField;

    @UiField
    ImageButton editDescriptionButton;

    @UiField
    ImageButton deleteApplicationButton;

    @UiField
    ImageButton createVersionButton;

    @UiField
    ImageButton launchEnvironmentButton;

    @UiField
    Label creationDateField;

    @UiField
    Label updatedDateField;

    public MainTabPain() {
        initWidget(uiBinder.createAndBindUi(this));

        nameField.setName(NAME_FIELD_ID);
        descriptionField.setName(DESCRIPTION_FIELD_ID);
        creationDateField.setID(CREATION_DATE_FIELD_ID);
        updatedDateField.setID(UPDATED_DATE_FIELD_ID);

        editDescriptionButton.setButtonId(EDIT_DESCRIPTION_BUTTON_ID);
        deleteApplicationButton.setButtonId(DELETE_APPLICATION_BUTTON_ID);
        createVersionButton.setButtonId(CREATE_VERSION_BUTTON_ID);
        launchEnvironmentButton.setButtonId(LAUNCH_ENVIRONMENT_BUTTON_ID);
    }

    /** @return the nameField */
    public TextInput getNameField() {
        return nameField;
    }

    /** @return the descriptionField */
    public TextInput getDescriptionField() {
        return descriptionField;
    }

    /** @return the editDescriptionButton */
    public ImageButton getEditDescriptionButton() {
        return editDescriptionButton;
    }

    /** @return the deleteApplicationButton */
    public ImageButton getDeleteApplicationButton() {
        return deleteApplicationButton;
    }

    /** @return the creationDateField */
    public Label getCreationDateField() {
        return creationDateField;
    }

    /** @return the updatedDateField */
    public Label getUpdatedDateField() {
        return updatedDateField;
    }

    /** @return the createVersionButton */
    public ImageButton getCreateVersionButton() {
        return createVersionButton;
    }

    /** @return the launchEnvironmentButton */
    public ImageButton getLaunchEnvironmentButton() {
        return launchEnvironmentButton;
    }

}
