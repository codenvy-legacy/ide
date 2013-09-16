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
package org.exoplatform.ide.extension.appfog.client.info;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.extension.appfog.client.AppfogExtension;

/**
 * Application information view.
 *
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class ApplicationInfoView extends ViewImpl implements ApplicationInfoPresenter.Display {
    public static final String ID = "ideAppfogApplicationInfoView";

    public static final String APPLICATION_URIS_ID = "ideAppfogAppUrisGridView";

    public static final String APPLICATION_SERVICES_ID = "ideAppfogAppServicesGridView";

    public static final String APPLICATION_ENVIRONMENTS_ID = "ideAppfogAppEnvironmentsGridView";

    private static final int HEIGHT = 390;

    private static final int WIDTH = 500;

    private static ApplicationInfoViewUiBinder uiBinder = GWT.create(ApplicationInfoViewUiBinder.class);

    /** Ok button. */
    @UiField
    ImageButton okButton;

    @UiField
    Label nameLabel;

    @UiField
    Label stateLabel;

    @UiField
    Label instancesLabel;

    @UiField
    Label versionLabel;

    @UiField
    ApplicationStringGrid applicationUrisGrid;

    @UiField
    ApplicationStringGrid applicationServicesGrid;

    @UiField
    ApplicationStringGrid applicationEnvironmentsGrid;

    @UiField
    Label diskLabel;

    @UiField
    Label memoryLabel;

    @UiField
    Label stackLabel;

    @UiField
    Label modelLabel;

    interface ApplicationInfoViewUiBinder extends UiBinder<Widget, ApplicationInfoView> {
    }

    public ApplicationInfoView() {
        super(ID, ViewType.MODAL, AppfogExtension.LOCALIZATION_CONSTANT.applicationInfoTitle(), null, WIDTH, HEIGHT);
        add(uiBinder.createAndBindUi(this));
        applicationUrisGrid.setID(APPLICATION_URIS_ID);
        applicationUrisGrid.addColumn(AppfogExtension.LOCALIZATION_CONSTANT.appInfoUris());
        applicationServicesGrid.setID(APPLICATION_SERVICES_ID);
        applicationServicesGrid.addColumn(AppfogExtension.LOCALIZATION_CONSTANT.appInfoServices());
        applicationEnvironmentsGrid.setID(APPLICATION_ENVIRONMENTS_ID);
        applicationEnvironmentsGrid.addColumn(AppfogExtension.LOCALIZATION_CONSTANT.appInfoEnvironments());
    }

    @Override
    public HasClickHandlers getOkButton() {
        return okButton;
    }

    @Override
    public void setName(String text) {
        nameLabel.setText(text);
    }

    /** @see org.exoplatform.ide.extension.cloudfoundry.client.info.ApplicationInfoPresenter.Display#setState(java.lang.String) */
    @Override
    public void setState(String text) {
        stateLabel.setText(text);
    }

    /** @see org.exoplatform.ide.extension.cloudfoundry.client.info.ApplicationInfoPresenter.Display#setInstances(java.lang.String) */
    @Override
    public void setInstances(String text) {
        instancesLabel.setText(text);
    }

    /** @see org.exoplatform.ide.extension.cloudfoundry.client.info.ApplicationInfoPresenter.Display#setVersion(java.lang.String) */
    @Override
    public void setVersion(String text) {
        versionLabel.setText(text);
    }

    /** @see org.exoplatform.ide.extension.cloudfoundry.client.info.ApplicationInfoPresenter.Display#getApplicationUrisGrid() */
    @Override
    public ListGridItem<String> getApplicationUrisGrid() {
        return applicationUrisGrid;
    }

    /** @see org.exoplatform.ide.extension.cloudfoundry.client.info.ApplicationInfoPresenter.Display#getApplicationServicesGrid() */
    @Override
    public ListGridItem<String> getApplicationServicesGrid() {
        return applicationServicesGrid;
    }

    /** @see org.exoplatform.ide.extension.cloudfoundry.client.info.ApplicationInfoPresenter.Display#getApplicationEnvironmentsGrid() */
    @Override
    public ListGridItem<String> getApplicationEnvironmentsGrid() {
        return applicationEnvironmentsGrid;
    }

    /** @see org.exoplatform.ide.extension.cloudfoundry.client.info.ApplicationInfoPresenter.Display#setDisk(java.lang.String) */
    @Override
    public void setDisk(String text) {
        diskLabel.setText(text);
    }

    /** @see org.exoplatform.ide.extension.cloudfoundry.client.info.ApplicationInfoPresenter.Display#setMemory(java.lang.String) */
    @Override
    public void setMemory(String text) {
        memoryLabel.setText(text);
    }

    /** @see org.exoplatform.ide.extension.cloudfoundry.client.info.ApplicationInfoPresenter.Display#setStack(java.lang.String) */
    @Override
    public void setStack(String text) {
        stackLabel.setText(text);
    }

    /** @see org.exoplatform.ide.extension.cloudfoundry.client.info.ApplicationInfoPresenter.Display#setModel(java.lang.String) */
    @Override
    public void setModel(String text) {
        modelLabel.setText(text);
    }
}
