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
package org.exoplatform.ide.extension.aws.client.beanstalk.environments;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: May 23, 2012 12:14:23 PM anya $
 */
public class EnvironmentsTabPain extends Composite {
    private static final String MANAGE_APPLICATION_EDIT_CONFIGURATION_BUTTON_ID = "ideManageApplicationViewEditConfigurationButton";

    private static final String MANAGE_APPLICATION_RESTART_ENVIRONMENT_BUTTON_ID = "ideManageApplicationViewRestartEnvironmentButton";

    private static final String MANAGE_APPLICATION_REBUILD_ENVIRONMENT_BUTTON_ID = "ideManageApplicationViewRebuildEnvironmentButton";

    private static final String MANAGE_APPLICATION_TERMINATE_ENVIRONMENT_BUTTON_ID = "ideManageApplicationViewTerminateEnvironmentButton";

    private static final String MANAGE_APPLICATION_LOGS_ENVIRONMENT_BUTTON_ID = "ideManageApplicationViewLogsEnvironmentButton";

    private static EnvironmentsTabPainUiBinder uiBinder = GWT.create(EnvironmentsTabPainUiBinder.class);

    interface EnvironmentsTabPainUiBinder extends UiBinder<Widget, EnvironmentsTabPain> {
    }

    @UiField
    ImageButton editConfigurationButton;

    @UiField
    ImageButton restartEnvironmentButton;

    @UiField
    ImageButton rebuildEnvironmentButton;

    @UiField
    ImageButton terminateEnvironmentButton;

    @UiField
    ImageButton logsEnvironmentButton;

    @UiField
    EnvironmentsGrid environmentsGrid;

    public EnvironmentsTabPain() {
        initWidget(uiBinder.createAndBindUi(this));
        editConfigurationButton.setButtonId(MANAGE_APPLICATION_EDIT_CONFIGURATION_BUTTON_ID);
        restartEnvironmentButton.setButtonId(MANAGE_APPLICATION_RESTART_ENVIRONMENT_BUTTON_ID);
        rebuildEnvironmentButton.setButtonId(MANAGE_APPLICATION_REBUILD_ENVIRONMENT_BUTTON_ID);
        terminateEnvironmentButton.setButtonId(MANAGE_APPLICATION_TERMINATE_ENVIRONMENT_BUTTON_ID);
        logsEnvironmentButton.setButtonId(MANAGE_APPLICATION_LOGS_ENVIRONMENT_BUTTON_ID);
    }

    public HasClickHandlers getConfigurationButton() {
        return editConfigurationButton;
    }

    public HasClickHandlers getRestartButton() {
        return restartEnvironmentButton;
    }

    public HasClickHandlers getRebuildButton() {
        return rebuildEnvironmentButton;
    }

    public HasClickHandlers getTerminateButton() {
        return terminateEnvironmentButton;
    }

    public HasClickHandlers getLogsButton() {
        return logsEnvironmentButton;
    }

    /** @return the environmentsGrid */
    public EnvironmentsGrid getEnvironmentsGrid() {
        return environmentsGrid;
    }

    /** @param isEnable */
    public void setAllEnvironmentButtonsEnableState(boolean isEnable) {
        editConfigurationButton.setEnabled(isEnable);
        restartEnvironmentButton.setEnabled(isEnable);
        rebuildEnvironmentButton.setEnabled(isEnable);
        terminateEnvironmentButton.setEnabled(isEnable);
        logsEnvironmentButton.setEnabled(isEnable);
    }

}