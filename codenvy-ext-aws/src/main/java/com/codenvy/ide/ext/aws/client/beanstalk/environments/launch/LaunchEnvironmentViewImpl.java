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
package com.codenvy.ide.ext.aws.client.beanstalk.environments.launch;

import com.codenvy.ide.ext.aws.client.AWSLocalizationConstant;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.ui.Button;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * The implementation of {@link LaunchEnvironmentView}.
 *
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
@Singleton
public class LaunchEnvironmentViewImpl extends DialogBox implements LaunchEnvironmentView {
    interface LaunchEnvironmentViewImplUiBinder extends UiBinder<Widget, LaunchEnvironmentViewImpl> {
    }

    private static LaunchEnvironmentViewImplUiBinder uiBinder = GWT.create(LaunchEnvironmentViewImplUiBinder.class);

    @UiField
    TextBox envNameField;

    @UiField
    TextBox envDescriptionField;

    @UiField
    ListBox solutionStackField;

    @UiField
    ListBox versionsField;

    @UiField
    Button launchButton;

    @UiField
    Button cancelButton;

    @UiField(provided = true)
    AWSLocalizationConstant constant;

    private boolean isShown;

    private ActionDelegate delegate;

    /**
     * Create view.
     *
     * @param constant
     */
    @Inject
    protected LaunchEnvironmentViewImpl(AWSLocalizationConstant constant) {
        this.constant = constant;

        Widget widget = uiBinder.createAndBindUi(this);

        this.setText(constant.launchEnvironmentViewTitle());
        this.setWidget(widget);
    }

    /** {@inheritDoc} */
    @Override
    public String getEnvName() {
        return envNameField.getText();
    }

    /** {@inheritDoc} */
    @Override
    public String getEnvDescription() {
        return envDescriptionField.getText();
    }

    /** {@inheritDoc} */
    @Override
    public String getSolutionStack() {
        return solutionStackField.getItemText(solutionStackField.getSelectedIndex());
    }

    /** {@inheritDoc} */
    @Override
    public void setSolutionStackValues(JsonArray<String> values) {
        solutionStackField.clear();

        for (int i = 0; i < values.size(); i++) {
            solutionStackField.addItem(values.get(i));
        }
    }

    /** {@inheritDoc} */
    @Override
    public String getVersionField() {
        return versionsField.getItemText(versionsField.getSelectedIndex());
    }

    /** {@inheritDoc} */
    @Override
    public void setVersionValues(JsonArray<String> values, String selectedValue) {
        versionsField.clear();
        int indexToSelect = 0;

        for (int i = 0; i < values.size(); i++) {
            versionsField.addItem(values.get(i));
            if (selectedValue.equals(values.get(i))) {
                indexToSelect = i;
            }

        }

        versionsField.setSelectedIndex(indexToSelect);
    }

    /** {@inheritDoc} */
    @Override
    public void enableLaunchButton(boolean enabled) {
        launchButton.setEnabled(enabled);
    }

    /** {@inheritDoc} */
    @Override
    public void focusInEnvNameField() {
        envNameField.setFocus(true);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isShown() {
        return isShown;
    }

    /** {@inheritDoc} */
    @Override
    public void showDialog() {
        this.isShown = true;
        this.center();
        this.show();
    }

    /** {@inheritDoc} */
    @Override
    public void close() {
        this.isShown = false;
        this.hide();
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    @UiHandler("launchButton")
    public void onLaunchButtonClicked(ClickEvent event) {
        delegate.onLaunchButtonClicked();
    }

    @UiHandler("cancelButton")
    public void onCancelButtonClicked(ClickEvent event) {
        delegate.onCancelButtonClicked();
    }

    @UiHandler("envNameField")
    public void onNameFieldValueChanged(KeyUpEvent event) {
        delegate.onNameFieldValueChanged();
    }
}
