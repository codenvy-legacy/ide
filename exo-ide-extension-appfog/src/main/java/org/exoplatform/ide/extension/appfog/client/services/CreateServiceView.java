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
package org.exoplatform.ide.extension.appfog.client.services;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.SelectItem;
import org.exoplatform.gwtframework.ui.client.component.TextInput;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.extension.appfog.client.AppfogExtension;

import java.util.LinkedHashMap;

/**
 * View for creating new provisioned service.
 *
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class CreateServiceView extends ViewImpl implements CreateServicePresenter.Display {
    private static final String ID = "ideCreateServiceView";

    private static final int WIDTH = 470;

    private static final int HEIGHT = 150;

    private static final String CREATE_BUTTON_ID = "ideCreateServiceViewCreateButton";

    private static final String CANCEL_BUTTON_ID = "ideCreateServiceViewCancelButton";

    private static final String NAME_FIELD_ID = "ideCreateServiceViewNameField";

    private static final String SERVICES_FIELD_ID = "ideCreateServiceViewServicesField";

    private static CreateServiceViewUiBinder uiBinder = GWT.create(CreateServiceViewUiBinder.class);

    interface CreateServiceViewUiBinder extends UiBinder<Widget, CreateServiceView> {
    }

    @UiField
    ImageButton createButton;

    @UiField
    ImageButton cancelButton;

    @UiField
    TextInput nameField;

    @UiField
    SelectItem servicesField;

    public CreateServiceView() {
        super(ID, ViewType.MODAL, AppfogExtension.LOCALIZATION_CONSTANT.createServiceViewTitle(), null, WIDTH,
              HEIGHT, false);
        add(uiBinder.createAndBindUi(this));

        createButton.setButtonId(CREATE_BUTTON_ID);
        cancelButton.setButtonId(CANCEL_BUTTON_ID);
        nameField.setName(NAME_FIELD_ID);
        servicesField.setName(SERVICES_FIELD_ID);
    }

    /** @see org.exoplatform.ide.extension.cloudfoundry.client.services.CreateServicePresenter.Display#getSystemServicesField() */
    @Override
    public HasValue<String> getSystemServicesField() {
        return servicesField;
    }

    /** @see org.exoplatform.ide.extension.cloudfoundry.client.services.CreateServicePresenter.Display#getNameField() */
    @Override
    public HasValue<String> getNameField() {
        return nameField;
    }

    /** @see org.exoplatform.ide.extension.cloudfoundry.client.services.CreateServicePresenter.Display#getCreateButton() */
    @Override
    public HasClickHandlers getCreateButton() {
        return createButton;
    }

    /** @see org.exoplatform.ide.extension.cloudfoundry.client.services.CreateServicePresenter.Display#getCancelButton() */
    @Override
    public HasClickHandlers getCancelButton() {
        return cancelButton;
    }

    /** @see org.exoplatform.ide.extension.cloudfoundry.client.services.CreateServicePresenter.Display#setServices(java.util
     * .LinkedHashMap) */
    @Override
    public void setServices(LinkedHashMap<String, String> values) {
        servicesField.setValueMap(values);
    }
}
