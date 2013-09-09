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
package org.exoplatform.ide.extension.cloudbees.client.project;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.Label;
import org.exoplatform.gwtframework.ui.client.component.TextInput;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.extension.cloudbees.client.CloudBeesClientBundle;
import org.exoplatform.ide.extension.cloudbees.client.CloudBeesExtension;

/**
 * View for managing project, deployed on CloudBees. View must be pointed in <b>Views.gwt.xml</b>.
 *
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Dec 5, 2011 12:33:20 PM anya $
 */
public class CloudBeesProjectView extends ViewImpl implements CloudBeesProjectPresenter.Display {
    private static final int WIDTH = 610;

    private static final int HEIGHT = 260;

    private static final String ID = "ideCloudBeesProjectView";

    private static final String DELETE_BUTTON_ID = "ideCloudBeesProjectViewDeleteButton";

    private static final String UPDATE_BUTTON_ID = "ideCloudBeesProjectViewUpdateButton";

    private static final String INFO_BUTTON_ID = "ideCloudBeesProjectViewInfoButton";

    private static final String CLOSE_BUTTON_ID = "ideCloudBeesProjectViewCloseButton";

    private static final String NAME_FIELD_ID = "ideCloudBeesProjectViewNameField";

    private static final String URL_FIELD_ID = "ideCloudBeesProjectViewUrlField";

    private static final String STATUS_FIELD_ID = "ideCloudBeesProjectViewStatusField";

    private static final String INSTANCES_FIELD_ID = "ideCloudBeesProjectViewInstancesField";

    private static CloudBeesProjectViewUiBinder uiBinder = GWT.create(CloudBeesProjectViewUiBinder.class);

    @UiField
    Button deleteButton;

    @UiField
    Button updateButton;

    @UiField
    ImageButton infoButton;

    @UiField
    ImageButton closeButton;

    @UiField
    TextInput nameField;

    @UiField
    Anchor urlField;

    @UiField
    Label statusField;

    @UiField
    Label instancesField;

    interface CloudBeesProjectViewUiBinder extends UiBinder<Widget, CloudBeesProjectView> {
    }

    public CloudBeesProjectView() {
        super(ID, ViewType.MODAL, CloudBeesExtension.LOCALIZATION_CONSTANT.manageProjectTitle(), new Image(
                CloudBeesClientBundle.INSTANCE.cloudBees()), WIDTH, HEIGHT);
        add(uiBinder.createAndBindUi(this));

        deleteButton.getElement().setId(DELETE_BUTTON_ID);
        updateButton.getElement().setId(UPDATE_BUTTON_ID);
        closeButton.setButtonId(CLOSE_BUTTON_ID);
        infoButton.setButtonId(INFO_BUTTON_ID);
        nameField.setName(NAME_FIELD_ID);
        urlField.setName(URL_FIELD_ID);
        statusField.setID(STATUS_FIELD_ID);
        instancesField.setID(INSTANCES_FIELD_ID);
    }

    /** @see org.exoplatform.ide.extension.cloudbees.client.project.CloudBeesProjectPresenter.Display#getCloseButton() */
    @Override
    public HasClickHandlers getCloseButton() {
        return closeButton;
    }

    /** @see org.exoplatform.ide.extension.cloudbees.client.project.CloudBeesProjectPresenter.Display#getUpdateButton() */
    @Override
    public HasClickHandlers getUpdateButton() {
        return updateButton;
    }

    /** @see org.exoplatform.ide.extension.cloudbees.client.project.CloudBeesProjectPresenter.Display#getDeleteButton() */
    @Override
    public HasClickHandlers getDeleteButton() {
        return deleteButton;
    }

    /** @see org.exoplatform.ide.extension.cloudbees.client.project.CloudBeesProjectPresenter.Display#getApplicationName() */
    @Override
    public HasValue<String> getApplicationName() {
        return nameField;
    }

    /** @see org.exoplatform.ide.extension.cloudbees.client.project.CloudBeesProjectPresenter.Display#getApplicationStatus() */
    @Override
    public HasValue<String> getApplicationStatus() {
        return statusField;
    }

    /** @see org.exoplatform.ide.extension.cloudbees.client.project.CloudBeesProjectPresenter.Display#getApplicationInstances() */
    @Override
    public HasValue<String> getApplicationInstances() {
        return instancesField;
    }

    /** @see org.exoplatform.ide.extension.cloudbees.client.project.CloudBeesProjectPresenter.Display#setApplicationURL(java.lang.String) */
    @Override
    public void setApplicationURL(String URL) {
        urlField.setHref(URL);
        urlField.setText(URL);
    }

    /** @see org.exoplatform.ide.extension.cloudbees.client.project.CloudBeesProjectPresenter.Display#getInfoButton() */
    @Override
    public HasClickHandlers getInfoButton() {
        return infoButton;
    }
}
