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
package com.codenvy.ide.ext.openshift.client.cartridge;

import com.codenvy.ide.ext.openshift.client.OpenShiftLocalizationConstant;
import com.codenvy.ide.ui.Button;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.List;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
@Singleton
public class CreateCartridgeViewImpl extends DialogBox implements CreateCartridgeView {

    interface CreateCartridgeViewImplUiBinder extends UiBinder<Widget, CreateCartridgeViewImpl> {
    }

    private CreateCartridgeViewImplUiBinder uiBinder = GWT.create(CreateCartridgeViewImplUiBinder.class);

    @UiField
    ListBox cartridges;

    @UiField
    Button btnCreate;

    @UiField
    Button btnCancel;

    @UiField(provided = true)
    final OpenShiftLocalizationConstant constant;

    private ActionDelegate delegate;

    private boolean isShown;

    @Inject
    protected CreateCartridgeViewImpl(OpenShiftLocalizationConstant constant) {
        this.constant = constant;

        Widget widget = uiBinder.createAndBindUi(this);

        this.setTitle(constant.createCartridgeViewTitle());
        this.setWidget(widget);
    }

    @Override
    public String getCartridgeName() {
        return cartridges.getValue(cartridges.getSelectedIndex());
    }

    @Override
    public void setCartridgesList(List<String> cartridgesList) {
        cartridges.clear();

        for (String cartridge : cartridgesList) {
            cartridges.addItem(cartridge);
        }
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

    @UiHandler("btnCreate")
    public void onCreateButtonClick(ClickEvent event) {
        delegate.onCreateCartridgeClicked();
    }

    @UiHandler("btnCancel")
    public void onCancelButtonClick(ClickEvent event) {
        delegate.onCancelClicked();
    }
}
