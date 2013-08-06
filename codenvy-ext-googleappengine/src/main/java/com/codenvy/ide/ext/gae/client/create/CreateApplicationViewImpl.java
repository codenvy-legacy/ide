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
package com.codenvy.ide.ext.gae.client.create;

import com.codenvy.ide.ext.gae.client.GAELocalization;
import com.codenvy.ide.ui.Button;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
@Singleton
public class CreateApplicationViewImpl extends DialogBox implements CreateApplicationView {
    interface CreateApplicationViewImplUiBinder extends UiBinder<Widget, CreateApplicationViewImpl> {}

    private static CreateApplicationViewImplUiBinder uiBinder = GWT.create(CreateApplicationViewImplUiBinder.class);

    @UiField
    Button createButton;

    @UiField
    Button deployButton;

    @UiField
    Button cancelButton;

    @UiField
    Label instructionLabel;

    @UiField(provided = true)
    GAELocalization constant;

    private boolean isShown;

    private ActionDelegate delegate;

    @Inject
    public CreateApplicationViewImpl(GAELocalization constant) {
        this.constant = constant;

        Widget widget = uiBinder.createAndBindUi(this);

        this.setText(constant.createApplicationViewTitle());
        this.setWidget(widget);
    }

    @Override
    public void enableDeployButton(boolean enable) {
        deployButton.setEnabled(enable);
    }

    @Override
    public void enableCreateButton(boolean enable) {
        createButton.setEnabled(enable);
    }

    @Override
    public void setUserInstruction(String userInstruction) {
        instructionLabel.setText(userInstruction);
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
        delegate.onDeployApplicationButtonClicked();
    }

    @UiHandler("createButton")
    public void onCreateButtonClicked(ClickEvent event) {
        delegate.onCreateApplicationButtonClicked();
    }

    @UiHandler("cancelButton")
    public void onCancelButtonClicked(ClickEvent event) {
        delegate.onCancelButtonClicked();
    }
}
