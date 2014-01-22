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
package org.exoplatform.ide.extension.cloudbees.client.list.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.extension.cloudbees.client.CloudBeesClientBundle;
import org.exoplatform.ide.extension.cloudbees.client.CloudBeesExtension;
import org.exoplatform.ide.extension.cloudbees.client.list.ApplicationListPresenter.Display;
import org.exoplatform.ide.extension.cloudbees.client.list.HasApplicationListActions;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: Sep 21, 2011 evgen $
 */
public class ApplicationListView extends ViewImpl implements Display {

    private static ApplicationListViewUiBinder uiBinder = GWT.create(ApplicationListViewUiBinder.class);

    interface ApplicationListViewUiBinder extends UiBinder<Widget, ApplicationListView> {
    }

    @UiField
    ImageButton okButton;

    @UiField
    ApplicationListGrid applicationGrid;

    public ApplicationListView() {
        super(ID, ViewType.POPUP, CloudBeesExtension.LOCALIZATION_CONSTANT.appListViewTitle(), new Image(
                CloudBeesClientBundle.INSTANCE.appList()), 775, 270);
        add(uiBinder.createAndBindUi(this));
    }

    /** @see org.exoplatform.ide.extension.cloudbees.client.list.ApplicationListPresenter.Display#getOkButton() */
    @Override
    public HasClickHandlers getOkButton() {
        return okButton;
    }

    /** @see org.exoplatform.ide.extension.cloudbees.client.list.ApplicationListPresenter.Display#getAppListGrid() */
    @Override
    public HasApplicationListActions getAppListGrid() {
        return applicationGrid;
    }

}