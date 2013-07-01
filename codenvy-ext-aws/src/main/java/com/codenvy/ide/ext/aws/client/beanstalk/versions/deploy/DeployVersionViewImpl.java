/*
 * Copyright (C) 2013 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
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
