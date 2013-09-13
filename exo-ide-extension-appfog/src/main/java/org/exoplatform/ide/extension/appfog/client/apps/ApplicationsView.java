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
package org.exoplatform.ide.extension.appfog.client.apps;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.TextInput;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.extension.appfog.client.AppfogExtension;
import org.exoplatform.ide.extension.appfog.shared.AppfogApplication;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class ApplicationsView extends ViewImpl implements ApplicationsPresenter.Display {
    interface ApplicationsViewUiBinder extends UiBinder<Widget, ApplicationsView> {
    }

    private static ApplicationsViewUiBinder uiBinder = GWT.create(ApplicationsViewUiBinder.class);


    private static final int HEIGHT = 300;

    private static final int WIDTH = 850;

    /** Close button. */
    @UiField
    ImageButton closeButton;

    @UiField
    ImageButton showButton;

    @UiField
    ApplicationsListGrid applicationsGrid;

    @UiField
    TextInput serverField;

    public ApplicationsView() {
        super(ID, ViewType.MODAL, AppfogExtension.LOCALIZATION_CONSTANT.appsViewTitle(), null, WIDTH, HEIGHT);
        add(uiBinder.createAndBindUi(this));
        serverField.setReadOnly(true);
    }

    /** @see org.exoplatform.ide.extension.cloudfoundry.client.apps.ApplicationsPresenter.Display#getCloseButton() */
    @Override
    public HasClickHandlers getCloseButton() {
        return closeButton;
    }

    /** @see org.exoplatform.ide.extension.cloudfoundry.client.apps.ApplicationsPresenter.Display#getAppsGrid() */
    @Override
    public ListGridItem<AppfogApplication> getAppsGrid() {
        return applicationsGrid;
    }

    /** @see org.exoplatform.ide.extension.cloudfoundry.client.apps.ApplicationsPresenter.Display#getActions() */
    @Override
    public HasApplicationsActions getActions() {
        return applicationsGrid;
    }

    /** @see org.exoplatform.ide.extension.cloudfoundry.client.apps.ApplicationsPresenter.Display#getServerSelectField() */
    @Override
    public HasValue<String> getServerSelectField() {
        return serverField;
    }

    /** @see org.exoplatform.ide.extension.cloudfoundry.client.apps.ApplicationsPresenter.Display#setServerValues(java.lang.String[]) */
    @Override
    public void setServerValue(String server) {
        serverField.setValue(server);
    }

    /** @see org.exoplatform.ide.extension.cloudfoundry.client.apps.ApplicationsPresenter.Display#getShowButton() */
    @Override
    public HasClickHandlers getShowButton() {
        return showButton;
    }

}
