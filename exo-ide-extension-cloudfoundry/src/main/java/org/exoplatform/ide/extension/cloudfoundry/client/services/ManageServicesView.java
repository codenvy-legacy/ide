package org.exoplatform.ide.extension.cloudfoundry.client.services;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryExtension;
import org.exoplatform.ide.extension.cloudfoundry.shared.ProvisionedService;

/**
 * View for managing CloudFoundry services.
 *
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Jul 16, 2012 4:35:17 PM anya $
 */
public class ManageServicesView extends ViewImpl implements ManageServicesPresenter.Display {

    private static final String ID = "ideManageServicesView";

    private static final int WIDTH = 740;

    private static final int HEIGHT = 300;

    private static final String DELETE_BUTTON_ID = "ideManageServicesViewDeleteButton";

    private static final String ADD_BUTTON_ID = "ideManageServicesViewAddButton";

    private static final String CANCEL_BUTTON_ID = "ideManageServicesViewCancelButton";

    private static BindServiceViewUiBinder uiBinder = GWT.create(BindServiceViewUiBinder.class);

    @UiField
    ImageButton deleteButton;

    @UiField
    ImageButton addButton;

    @UiField
    ImageButton cancelButton;

    @UiField
    ProvisionedServicesGrid servicesGrid;

    @UiField
    BoundedServicesGrid boundedServicesGrid;

    interface BindServiceViewUiBinder extends UiBinder<Widget, ManageServicesView> {
    }

    public ManageServicesView() {
        super(ID, ViewType.MODAL, CloudFoundryExtension.LOCALIZATION_CONSTANT.bindServiceViewTitle(), null, WIDTH,
              HEIGHT, true);
        add(uiBinder.createAndBindUi(this));

        addButton.setButtonId(ADD_BUTTON_ID);
        deleteButton.setButtonId(DELETE_BUTTON_ID);
        cancelButton.setButtonId(CANCEL_BUTTON_ID);
    }

    /** @see org.exoplatform.ide.extension.cloudfoundry.client.services.ManageServicesPresenter.Display#getAddButton() */
    @Override
    public HasClickHandlers getAddButton() {
        return addButton;
    }

    /** @see org.exoplatform.ide.extension.cloudfoundry.client.services.ManageServicesPresenter.Display#getDeleteButton() */
    @Override
    public HasClickHandlers getDeleteButton() {
        return deleteButton;
    }

    /** @see org.exoplatform.ide.extension.cloudfoundry.client.services.ManageServicesPresenter.Display#getCancelButton() */
    @Override
    public HasClickHandlers getCancelButton() {
        return cancelButton;
    }

    /** @see org.exoplatform.ide.extension.cloudfoundry.client.services.ManageServicesPresenter.Display#getProvisionedServicesGrid() */
    @Override
    public ListGridItem<ProvisionedService> getProvisionedServicesGrid() {
        return servicesGrid;
    }

    /** @see org.exoplatform.ide.extension.cloudfoundry.client.services.ManageServicesPresenter.Display#enableDeleteButton(boolean) */
    @Override
    public void enableDeleteButton(boolean enabled) {
        deleteButton.setEnabled(enabled);
    }

    /** @see org.exoplatform.ide.extension.cloudfoundry.client.services.ManageServicesPresenter.Display#getUnbindServiceHandler() */
    @Override
    public HasUnbindServiceHandler getUnbindServiceHandler() {
        return boundedServicesGrid;
    }

    /** @see org.exoplatform.ide.extension.cloudfoundry.client.services.ManageServicesPresenter.Display#getBoundedServicesGrid() */
    @Override
    public ListGridItem<String> getBoundedServicesGrid() {
        return boundedServicesGrid;
    }

    /** @see org.exoplatform.ide.extension.cloudfoundry.client.services.ManageServicesPresenter.Display#getBindServiceHandler() */
    @Override
    public HasBindServiceHandler getBindServiceHandler() {
        return servicesGrid;
    }

}
