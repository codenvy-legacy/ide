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
