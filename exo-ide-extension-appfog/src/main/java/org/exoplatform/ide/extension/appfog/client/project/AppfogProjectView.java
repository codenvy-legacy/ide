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
package org.exoplatform.ide.extension.appfog.client.project;

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
import org.exoplatform.ide.extension.appfog.client.AppfogClientBundle;
import org.exoplatform.ide.extension.appfog.client.AppfogExtension;

/**
 * View for managing project, deployed on Appfog. View must be pointed in Views.gwt.xml
 *
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class AppfogProjectView extends ViewImpl implements AppfogProjectPresenter.Display {
    private static final int WIDTH = 600;

    private static final int HEIGHT = 385;

    private static final String ID = "ideAppfogProjectView";

    private static final String DELETE_BUTTON_ID = "ideAppfogProjectViewDeleteButton";

    private static final String UPDATE_BUTTON_ID = "ideAppfogProjectViewUpdateButton";

    private static final String LOGS_BUTTON_ID = "ideAppfogProjectViewLogsButton";

    private static final String SERVICES_BUTTON_ID = "ideAppfogProjectViewServicesButton";

    private static final String INFO_BUTTON_ID = "ideAppfogProjectViewInfoButton";

    private static final String CLOSE_BUTTON_ID = "ideAppfogProjectViewCloseButton";

    private static final String START_BUTTON_ID = "ideAppfogProjectViewStartButton";

    private static final String STOP_BUTTON_ID = "ideAppfogProjectViewStopButton";

    private static final String RESTART_BUTTON_ID = "ideAppfogProjectViewRestartButton";

    private static final String EDIT_MEMORY_BUTTON_ID = "ideAppfogProjectViewEditMemoryButton";

    private static final String EDIT_INSTANCES_BUTTON_ID = "ideAppfogProjectViewEditInstancesButton";

    private static final String EDIT_URL_BUTTON_ID = "ideAppfogProjectViewEditUrlButton";

    private static final String NAME_FIELD_ID = "ideAppfogProjectViewNameField";

    private static final String URL_FIELD_ID = "ideAppfogProjectViewUrlField";

    private static final String MODEL_FIELD_ID = "ideAppfogProjectViewModelField";

    private static final String INSTANCES_FIELD_ID = "ideAppfogProjectViewInstancesField";

    private static final String MEMORY_FIELD_ID = "ideAppfogProjectViewMemoryField";

    private static final String STACK_FIELD_ID = "ideAppfogProjectViewStackField";

    private static final String INFRA_FIELD_ID = "ideAppfogProjectViewInfraField";

    private static final String STATUS_FIELD_ID = "ideAppfogProjectViewStatusField";

    private static AppfogProjectViewUiBinder uiBinder = GWT.create(AppfogProjectViewUiBinder.class);

    interface AppfogProjectViewUiBinder extends UiBinder<Widget, AppfogProjectView> {
    }

    @UiField
    Button deleteButton;

    @UiField
    Button updateButton;

    @UiField
    Button logsButton;

    @UiField
    Button servicesButton;

    @UiField
    ImageButton infoButton;

    @UiField
    ImageButton closeButton;

    @UiField
    TextInput nameField;

    @UiField
    Anchor urlField;

    @UiField
    Label modelField;

    @UiField
    Label infraField;

    @UiField
    Label stackField;

    @UiField
    Label statusField;

    @UiField
    TextInput instancesField;

    @UiField
    TextInput memoryField;

    @UiField
    ImageButton startButton;

    @UiField
    ImageButton stopButton;

    @UiField
    ImageButton restartButton;

    @UiField
    ImageButton editUrlsButton;

    @UiField
    ImageButton editMemoryButton;

    @UiField
    ImageButton editInstancesButton;

    @UiField
    Anchor showUrisAnchor;

    public AppfogProjectView() {
        super(ID, ViewType.MODAL, AppfogExtension.LOCALIZATION_CONSTANT.manageProjectViewTitle(), new Image(
                AppfogClientBundle.INSTANCE.appfog()), WIDTH, HEIGHT);
        add(uiBinder.createAndBindUi(this));

        deleteButton.getElement().setId(DELETE_BUTTON_ID);
        updateButton.getElement().setId(UPDATE_BUTTON_ID);
        logsButton.getElement().setId(LOGS_BUTTON_ID);
        servicesButton.getElement().setId(SERVICES_BUTTON_ID);
        closeButton.setButtonId(CLOSE_BUTTON_ID);
        infoButton.setButtonId(INFO_BUTTON_ID);

        startButton.setButtonId(START_BUTTON_ID);
        stopButton.setButtonId(STOP_BUTTON_ID);
        restartButton.setButtonId(RESTART_BUTTON_ID);

        editInstancesButton.setButtonId(EDIT_INSTANCES_BUTTON_ID);
        editMemoryButton.setButtonId(EDIT_MEMORY_BUTTON_ID);
        editUrlsButton.setButtonId(EDIT_URL_BUTTON_ID);

        nameField.setName(NAME_FIELD_ID);
        urlField.setName(URL_FIELD_ID);
        modelField.setID(MODEL_FIELD_ID);
        infraField.setID(INFRA_FIELD_ID);
        stackField.setID(STACK_FIELD_ID);
        instancesField.setName(INSTANCES_FIELD_ID);
        memoryField.setName(MEMORY_FIELD_ID);
        statusField.setID(STATUS_FIELD_ID);
    }

    /** @see org.exoplatform.ide.extension.cloudfoundry.client.project.CloudFoundryProjectPresenter.Display#getCloseButton() */
    @Override
    public HasClickHandlers getCloseButton() {
        return closeButton;
    }

    /** @see org.exoplatform.ide.extension.cloudfoundry.client.project.CloudFoundryProjectPresenter.Display#getUpdateButton() */
    @Override
    public HasClickHandlers getUpdateButton() {
        return updateButton;
    }

    @Override
    public HasClickHandlers getLogsButton() {
        return logsButton;
    }

    /** @see org.exoplatform.ide.extension.cloudfoundry.client.project.CloudFoundryProjectPresenter.Display#getDeleteButton() */
    @Override
    public HasClickHandlers getDeleteButton() {
        return deleteButton;
    }

    /** @see org.exoplatform.ide.extension.cloudfoundry.client.project.CloudFoundryProjectPresenter.Display#getInfoButton() */
    @Override
    public HasClickHandlers getInfoButton() {
        return infoButton;
    }

    /** @see org.exoplatform.ide.extension.cloudfoundry.client.project.CloudFoundryProjectPresenter.Display#getApplicationName() */
    @Override
    public HasValue<String> getApplicationName() {
        return nameField;
    }

    @Override
    public void setApplicationURL(String url) {
        if (url != null) {
            url = (url.startsWith("http://")) ? url : "http://" + url;
        }
        urlField.setHref(url);
        urlField.setText(url);
    }

    @Override
    public HasValue<String> getApplicationModel() {
        return modelField;
    }

    @Override
    public HasValue<String> getApplicationInfra() {
        return infraField;
    }

    @Override
    public HasValue<String> getApplicationStack() {
        return stackField;
    }

    @Override
    public HasValue<String> getApplicationInstances() {
        return instancesField;
    }

    @Override
    public HasValue<String> getApplicationMemory() {
        return memoryField;
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
    public HasClickHandlers getEditMemoryButton() {
        return editMemoryButton;
    }

    @Override
    public HasClickHandlers getEditURLButton() {
        return editUrlsButton;
    }

    @Override
    public HasClickHandlers getEditInstancesButton() {
        return editInstancesButton;
    }

    @Override
    public void setStartButtonEnabled(boolean enabled) {
        startButton.setEnabled(enabled);
    }

    @Override
    public void setStopButtonEnabled(boolean enabled) {
        stopButton.setEnabled(enabled);
    }

    @Override
    public void setRestartButtonEnabled(boolean enabled) {
        restartButton.setEnabled(enabled);
    }

    @Override
    public HasValue<String> getApplicationStatus() {
        return statusField;
    }

    @Override
    public HasClickHandlers getServicesButton() {
        return servicesButton;
    }

    @Override
    public HasClickHandlers getShowUrisAnchor() {
        return showUrisAnchor;
    }

    @Override
    public void setUrisPopupVisible(boolean visible) {
        showUrisAnchor.setVisible(visible);
    }
}
