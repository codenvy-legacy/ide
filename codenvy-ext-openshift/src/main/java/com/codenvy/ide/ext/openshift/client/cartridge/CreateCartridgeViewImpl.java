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
import com.codenvy.ide.json.JsonArray;
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

/**
 * The implementation of {@link CreateCartridgeView}.
 *
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

    /**
     * Create view.
     *
     * @param constant
     *         localized constants
     */
    @Inject
    protected CreateCartridgeViewImpl(OpenShiftLocalizationConstant constant) {
        this.constant = constant;

        Widget widget = uiBinder.createAndBindUi(this);

        this.setTitle(constant.createCartridgeViewTitle());
        this.setWidget(widget);
    }

    /** {@inheritDoc} */
    @Override
    public String getCartridgeName() {
        return cartridges.getValue(cartridges.getSelectedIndex());
    }

    /** {@inheritDoc} */
    @Override
    public void setCartridgesList(JsonArray<String> cartridgesList) {
        cartridges.clear();

        for (int i = 0; i < cartridgesList.size(); i++) {
            cartridges.addItem(cartridgesList.get(i));
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean isShown() {
        return isShown;
    }

    /** {@inheritDoc} */
    @Override
    public void close() {
        this.isShown = false;
        this.hide();
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
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    /** {@inheritDoc} */
    @UiHandler("btnCreate")
    public void onCreateButtonClick(ClickEvent event) {
        delegate.onCreateCartridgeClicked();
    }

    /** {@inheritDoc} */
    @UiHandler("btnCancel")
    public void onCancelButtonClick(ClickEvent event) {
        delegate.onCancelClicked();
    }
}
