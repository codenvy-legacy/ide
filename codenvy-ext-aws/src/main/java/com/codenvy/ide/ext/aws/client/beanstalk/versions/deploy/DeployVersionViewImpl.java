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
package com.codenvy.ide.ext.aws.client.beanstalk.versions.deploy;

import com.codenvy.ide.ext.aws.client.AWSLocalizationConstant;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.ui.Button;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
@Singleton
public class DeployVersionViewImpl extends DialogBox implements DeployVersionView {
    interface DeployVersionViewImplUiBinder extends UiBinder<Widget, DeployVersionViewImpl> {}

    private static DeployVersionViewImplUiBinder uiBinder = GWT.create(DeployVersionViewImplUiBinder.class);

    @UiField
    RadioButton deployToNewEnvironment;

    @UiField
    RadioButton deployToExistingEnvironment;

    @UiField
    ListBox environmentsField;

    @UiField
    Button deployButton;

    @UiField
    Button cancelButton;

    @UiField(provided = true)
    AWSLocalizationConstant constant;

    private boolean isShown;

    private ActionDelegate delegate;

    @Inject
    protected DeployVersionViewImpl(AWSLocalizationConstant constant) {
        this.constant = constant;

        Widget widget = uiBinder.createAndBindUi(this);

        this.setText(constant.deployVersionViewTitle());
        this.setWidget(widget);
    }

    @Override
    public boolean getNewEnvironmentMode() {
        return deployToNewEnvironment.getValue();
    }

    @Override
    public boolean getExistingEnvironmentMode() {
        return deployToExistingEnvironment.getValue();
    }

    @Override
    public String getEnvironmentsField() {
        return environmentsField.getItemText(environmentsField.getSelectedIndex());
    }

    @Override
    public void setEnvironmentsValues(JsonArray<String> values) {
        environmentsField.clear();

        for (int i = 0; i < values.size(); i++) {
            environmentsField.addItem(values.get(i));
        }
    }

    @Override
    public void enableEnvironmentsField(boolean value) {
        environmentsField.setEnabled(value);
    }

    @Override
    public void enableDeployButton(boolean enable) {
        deployButton.setEnabled(enable);
    }

    @Override
    public boolean isShown() {
        return isShown;
    }

    @Override
    public void showDialog() {
        this.isShown = true;
        this.center();
        this.show();
    }

    @Override
    public void close() {
        this.isShown = false;
        this.hide();
    }

    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    @UiHandler("deployButton")
    public void onDeployButtonClicked(ClickEvent event) {
        delegate.onDeployButtonClicked();
    }

    @UiHandler("cancelButton")
    public void onCancelButtonClicked(ClickEvent event) {
        delegate.onCancelButtonClicked();
    }

    @UiHandler("deployToNewEnvironment")
    public void onDeployToNewEnvironmentClicked(ClickEvent event) {
        delegate.onNewEnvironmentModeClicked();
    }

    @UiHandler("deployToExistingEnvironment")
    public void deployToExistingEnvironmentClicked(ClickEvent event) {
        delegate.onExistingEnvironmentModeClicked();
    }
}
