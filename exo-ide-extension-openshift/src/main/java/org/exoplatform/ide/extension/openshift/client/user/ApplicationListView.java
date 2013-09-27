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
package org.exoplatform.ide.extension.openshift.client.user;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.TextInput;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.extension.openshift.client.OpenShiftExtension;
import org.exoplatform.ide.extension.openshift.client.info.ApplicationInfoListGrid;
import org.exoplatform.ide.extension.openshift.client.info.Property;
import org.exoplatform.ide.extension.openshift.shared.AppInfo;
import org.exoplatform.ide.extension.openshift.shared.OpenShiftEmbeddableCartridge;

import java.util.ArrayList;
import java.util.Collections;

/**
 * view for showing user's information.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Jun 14, 2011 4:25:33 PM anya $
 */
public class ApplicationListView extends ViewImpl implements ApplicationListPresenter.Display {
    public static final String ID = "ideUserInfoView";

    private static final int HEIGHT = 420;

    private static final int WIDTH = 700;

    private static final String LOGIN_FIELD_ID = "ideUserInfoViewLoginField";

    private static final String DOMAIN_FIELD_ID = "ideUserInfoViewDomainField";

    private static final String ADD_CARTRIDGE_BUTTON_ID = "ideAddCartridgeButton";

    private static UserInfoViewUiBinder uiBinder = GWT.create(UserInfoViewUiBinder.class);

    interface UserInfoViewUiBinder extends UiBinder<Widget, ApplicationListView> {
    }

    /** User's login field. */
    @UiField
    TextInput loginField;

    /** User's domain. */
    @UiField
    TextInput domainField;

    /** Grid with user's applications. */
    @UiField
    ApplicationGrid applicationGrid;

    /** Application's properties. */
    @UiField
    ApplicationInfoListGrid applicationInfoGrid;

    /** Ok button. */
    @UiField
    ImageButton okButton;

    /** Switch account button */
    @UiField
    ImageButton switchAccount;

    /** Change namespace button */
    @UiField
    ImageButton changeNamespace;

    /** Cartridge list grid */
    @UiField
    CartridgeGrid cartridgeGrid;

    /** Add new cartridge button */
    @UiField
    ImageButton addCartridgeButton;

    public ApplicationListView() {
        super(ID, ViewType.MODAL, OpenShiftExtension.LOCALIZATION_CONSTANT.userInfoViewTitle(), null, WIDTH, HEIGHT, false);
        add(uiBinder.createAndBindUi(this));
        domainField.setName(DOMAIN_FIELD_ID);
        domainField.setHeight("22px");
        loginField.setName(LOGIN_FIELD_ID);
        loginField.setHeight("22px");
        addCartridgeButton.setId(ADD_CARTRIDGE_BUTTON_ID);
    }

    /** @see ApplicationListPresenter.Display#getOkButton() */
    @Override
    public HasClickHandlers getOkButton() {
        return okButton;
    }

    /** @see ApplicationListPresenter.Display#getLoginField() */
    @Override
    public HasValue<String> getLoginField() {
        return loginField;
    }

    /** @see ApplicationListPresenter.Display#getDomainField() */
    @Override
    public HasValue<String> getDomainField() {
        return domainField;
    }

    /** @see ApplicationListPresenter.Display#getApplicationInfoGrid() */
    @Override
    public ListGridItem<Property> getApplicationInfoGrid() {
        return applicationInfoGrid;
    }

    /** @see ApplicationListPresenter.Display#getApplicationGrid() */
    @Override
    public ListGridItem<AppInfo> getApplicationGrid() {
        return applicationGrid;
    }

    /**
     * @see ApplicationListPresenter.Display#addDeleteButtonSelectionHandler(com.google.gwt
     *      .event.logical.shared.SelectionHandler)
     */
    @Override
    public void addDeleteButtonSelectionHandler(SelectionHandler<AppInfo> handler) {
        applicationGrid.addDeleteButtonSelectionHandler(handler);
    }

    /** @see ApplicationListPresenter.Display#clearApplicationInfo() */
    @Override
    public void clearApplicationInfo() {
        applicationInfoGrid.setValue(new ArrayList<Property>());
    }

    @Override
    public CartridgeGrid getCartridgesGrid() {
        return cartridgeGrid;
    }

    @Override
    public void addDeleteCartridgeButtonSelectionHandler(SelectionHandler<OpenShiftEmbeddableCartridge> handler) {
        cartridgeGrid.addDeleteButtonSelectionHandler(handler);
    }

    @Override
    public void addStartCartridgeButtonSelectionHandler(SelectionHandler<OpenShiftEmbeddableCartridge> handler) {
        cartridgeGrid.addStartButtonSelectionHandler(handler);
    }

    @Override
    public void addStopCartridgeButtonSelectionHandler(SelectionHandler<OpenShiftEmbeddableCartridge> handler) {
        cartridgeGrid.addStopButtonSelectionHandler(handler);
    }

    @Override
    public void addRestartCartridgeButtonSelectionHandler(SelectionHandler<OpenShiftEmbeddableCartridge> handler) {
        cartridgeGrid.addRestartButtonSelectionHandler(handler);
    }

    @Override
    public void addReloadCartridgeButtonSelectionHandler(SelectionHandler<OpenShiftEmbeddableCartridge> handler) {
        cartridgeGrid.addReloadButtonSelectionHandler(handler);
    }

    @Override
    public HasClickHandlers getAddCartridgeButton() {
        return addCartridgeButton;
    }

    @Override
    public HasClickHandlers getSwitchAccountButton() {
        return switchAccount;
    }

    @Override
    public HasClickHandlers getChangeNamespaceButton() {
        return changeNamespace;
    }

    @Override
    public void setAddCartridgeButtonEnable(boolean isEnable) {
        addCartridgeButton.setEnabled(isEnable);
    }

    @Override
    public void clearCartridgesInfo() {
        cartridgeGrid.setValue(Collections.<OpenShiftEmbeddableCartridge>emptyList());
    }
}
