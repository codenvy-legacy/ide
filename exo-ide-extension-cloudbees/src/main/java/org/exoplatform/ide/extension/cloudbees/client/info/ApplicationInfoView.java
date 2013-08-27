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
package org.exoplatform.ide.extension.cloudbees.client.info;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.extension.cloudbees.client.CloudBeesExtension;

import java.util.Map.Entry;

/**
 * Application information view.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Jun 1, 2011 2:57:22 PM anya $
 */
public class ApplicationInfoView extends ViewImpl implements ApplicationInfoPresenter.Display {
    public static final String ID = "ideCloudBeesApplicationInfoView";

    private static final int HEIGHT = 345;

    private static final int WIDTH = 460;

    private static ApplicationInfoViewUiBinder uiBinder = GWT.create(ApplicationInfoViewUiBinder.class);

    /** Ok button. */
    @UiField
    ImageButton okButton;

    /** Application's information grid. */
    @UiField
    ApplicationInfoGrid applicationInfoGrid;

    interface ApplicationInfoViewUiBinder extends UiBinder<Widget, ApplicationInfoView> {
    }

    public ApplicationInfoView() {
        super(ID, ViewType.MODAL, CloudBeesExtension.LOCALIZATION_CONSTANT.applicationInfoTitle(), null, WIDTH, HEIGHT);
        add(uiBinder.createAndBindUi(this));
    }

    /** @see org.exoplatform.ide.extension.heroku.client.info.ApplicationInfoPresenter.Display#getOkButton() */
    @Override
    public HasClickHandlers getOkButton() {
        return okButton;
    }

    /** @see org.exoplatform.ide.extension.cloudbees.client.info.ApplicationInfoPresenter.Display#getApplicationInfoGrid() */
    @Override
    public ListGridItem<Entry<String, String>> getApplicationInfoGrid() {
        return applicationInfoGrid;
    }
}
