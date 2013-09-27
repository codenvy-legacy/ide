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
package org.exoplatform.ide.extension.openshift.client.project;

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
import org.exoplatform.ide.extension.openshift.client.OpenShiftClientBundle;
import org.exoplatform.ide.extension.openshift.client.OpenShiftExtension;

/**
 * View for managing application, deployed on OpenShift. View must be pointed in <b>Views.gwt.xml</b>
 *
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Dec 8, 2011 9:40:02 AM anya $
 */
public class OpenShiftProjectView extends ViewImpl implements OpenShiftProjectPresenter.Display {
    private static final int WIDTH = 580;

    private static final int HEIGHT = 270;

    private static final String ID = "ideOpenShiftProjectView";

    private static final String DELETE_BUTTON_ID = "ideOpenShiftProjectViewDeleteButton";

    private static final String PREVIEW_BUTTON_ID = "ideOpenShiftProjectViewPreviewButton";

    private static final String INFO_BUTTON_ID = "ideOpenShiftProjectViewInfoButton";

    private static final String CLOSE_BUTTON_ID = "ideOpenShiftProjectViewCloseButton";

    private static final String NAME_FIELD_ID = "ideOpenShiftProjectViewNameField";

    private static final String URL_FIELD_ID = "ideOpenShiftProjectViewUrlField";

    private static final String TYPE_FIELD_ID = "ideOpenShiftProjectViewTypeField";

    private static final String START_BUTTON_ID = "ideOpenShiftProjectViewStartButton";

    private static final String STOP_BUTTON_ID = "ideOpenShiftProjectViewStopButton";

    private static final String RESTART_BUTTON_ID = "ideOpenShiftProjectViewRestartButton";

    private static final String STATUS_LABEL_ID = "ideOpenShiftProjectViewStatusLabel";

    private static OpenShiftProjectViewUiBinder uiBinder = GWT.create(OpenShiftProjectViewUiBinder.class);

    @UiField
    Button deleteButton;

    @UiField
    Button previewButton;

    @UiField
    ImageButton infoButton;

    @UiField
    ImageButton closeButton;

    @UiField
    ImageButton startButton;

    @UiField
    ImageButton stopButton;

    @UiField
    ImageButton restartButton;

    @UiField
    Label statusField;

    @UiField
    TextInput nameField;

    @UiField
    Anchor urlField;

    @UiField
    Label typeField;

    interface OpenShiftProjectViewUiBinder extends UiBinder<Widget, OpenShiftProjectView> {
    }

    public OpenShiftProjectView() {
        super(ID, ViewType.MODAL, OpenShiftExtension.LOCALIZATION_CONSTANT.manageProjectViewTitle(), new Image(
                OpenShiftClientBundle.INSTANCE.openShiftControl()), WIDTH, HEIGHT);
        add(uiBinder.createAndBindUi(this));

        deleteButton.getElement().setId(DELETE_BUTTON_ID);
        previewButton.getElement().setId(PREVIEW_BUTTON_ID);
        closeButton.setButtonId(CLOSE_BUTTON_ID);
        infoButton.setButtonId(INFO_BUTTON_ID);
        nameField.setName(NAME_FIELD_ID);
        urlField.setName(URL_FIELD_ID);
        typeField.setID(TYPE_FIELD_ID);
        startButton.setButtonId(START_BUTTON_ID);
        stopButton.setButtonId(STOP_BUTTON_ID);
        restartButton.setButtonId(RESTART_BUTTON_ID);
        statusField.setID(STATUS_LABEL_ID);
    }

    /** @see org.exoplatform.ide.extension.cloudbees.client.project.CloudBeesProjectPresenter.Display#getCloseButton() */
    @Override
    public HasClickHandlers getCloseButton() {
        return closeButton;
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

    @Override
    public HasClickHandlers getPreviewButton() {
        return previewButton;
    }

    @Override
    public HasValue<String> getApplicationType() {
        return typeField;
    }

    @Override
    public HasClickHandlers getStartButton() {
        return startButton;
    }

    @Override
    public HasClickHandlers getStopButton() {
        return stopButton;
    }

    @Override
    public HasClickHandlers getRestartButton() {
        return restartButton;
    }

    @Override
    public void setControlsActivity(boolean active) {
        if (active) {
            startButton.setEnabled(false);
            stopButton.setEnabled(true);
            restartButton.setEnabled(true);
            statusField.setValue("STARTED");
        } else {
            startButton.setEnabled(true);
            stopButton.setEnabled(false);
            restartButton.setEnabled(false);
            statusField.setValue("STOPPED");
        }
    }
}
