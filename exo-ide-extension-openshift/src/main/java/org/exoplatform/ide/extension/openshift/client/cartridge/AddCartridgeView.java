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
