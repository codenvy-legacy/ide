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
package com.codenvy.ide.ext.openshift.client.domain;

import com.codenvy.ide.ext.openshift.client.OpenShiftLocalizationConstant;
import com.codenvy.ide.ui.Button;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class CreateDomainViewImpl extends DialogBox implements CreateDomainView {

    interface CreateDomainViewImplUiBinder extends UiBinder<Widget, CreateDomainViewImpl> {
    }

    private static CreateDomainViewImplUiBinder uiBinder = GWT.create(CreateDomainViewImplUiBinder.class);

    @UiField
    TextBox domain;

    @UiField
    Button btnChange;

    @UiField
    Button btnCancel;

    @UiField
    Label errorLabel;

    @UiField(provided = true)
    final OpenShiftLocalizationConstant constant;

    private ActionDelegate delegate;

    private boolean isShown;

    @Inject
    protected CreateDomainViewImpl(OpenShiftLocalizationConstant constant) {
        this.constant = constant;

        Widget widget = uiBinder.createAndBindUi(this);

        this.setTitle(constant.changeDomainViewTitle());
        this.setWidget(widget);
    }

    @Override
    public String getDomain() {
        return domain.getText();
    }

    @Override
    public void setDomain(String domain) {
        this.domain.setText(domain);
    }

    @Override
    public void setError(String message) {
        errorLabel.setText(message);
    }

    @Override
    public void setEnableChangeDomainButton(boolean isEnable) {
        btnChange.setEnabled(isEnable);
    }

    @Override
    public void focusDomainField() {
        domain.setFocus(true);
    }

    @Override
    public boolean isShown() {
        return isShown;
    }

    @Override
    public void close() {
        this.isShown = false;
        this.hide();
    }

    @Override
    public void showDialog() {
        this.isShown = true;
        this.center();
        this.show();
    }

    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    @UiHandler("btnChange")
    public void onBtnChangeClick(ClickEvent event) {
        delegate.onDomainChangeClicked();
    }

    @UiHandler("btnCancel")
    public void onBtnCancelClick(ClickEvent event) {
        delegate.onCancelClicked();
    }

    @UiHandler("domain")
    public void onDomainKeyUp(KeyUpEvent event) {
        delegate.onValueChanged();
    }
}
