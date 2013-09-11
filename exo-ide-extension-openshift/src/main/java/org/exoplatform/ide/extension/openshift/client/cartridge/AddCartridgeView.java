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
package org.exoplatform.ide.extension.openshift.client.cartridge;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.SelectItem;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.extension.openshift.client.OpenShiftExtension;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class AddCartridgeView extends ViewImpl implements AddCartridgePresenter.Display {
    private static final String ID = "ideAddCartridgeView";

    private static final int WIDTH = 300;

    private static final int HEIGHT = 95;

    private static final String OK_BUTTON_ID = "ideAddCartridgeButtonId";

    private static final String CANCEL_BUTTON_ID = "ideAddCartridgeCancelButtonId";

    @UiField
    SelectItem cartridgesList;

    @UiField
    ImageButton okButton;

    @UiField
    ImageButton cancelButton;

    private static AddCartridgeViewUiBinder uiBinder = GWT.create(AddCartridgeViewUiBinder.class);

    interface AddCartridgeViewUiBinder extends UiBinder<Widget, AddCartridgeView> {
    }

    public AddCartridgeView() {
        super(ID, ViewType.MODAL, OpenShiftExtension.LOCALIZATION_CONSTANT.addCartridgeTitle(), null, WIDTH, HEIGHT, false);
        add(uiBinder.createAndBindUi(this));

        okButton.setId(OK_BUTTON_ID);
        cancelButton.setId(CANCEL_BUTTON_ID);
    }

    @Override
    public void setCartridgesList(String[] cartridges) {
        cartridgesList.setValueMap(cartridges);
    }

    @Override
    public HasValue<String> getCartridgeName() {
        return cartridgesList;
    }

    @Override
    public HasClickHandlers getOkButton() {
        return okButton;
    }

    @Override
    public HasClickHandlers getCancelButton() {
        return cancelButton;
    }
}
