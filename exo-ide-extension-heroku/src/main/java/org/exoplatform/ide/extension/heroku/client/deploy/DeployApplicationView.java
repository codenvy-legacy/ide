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
package org.exoplatform.ide.extension.heroku.client.deploy;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.component.TextInput;

/**
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: DeployApplicationView.java Dec 5, 2011 1:58:14 PM vereshchaka $
 */
public class DeployApplicationView extends Composite implements DeployApplicationPresenter.Display {
    interface DeployApplicationViewUiBinder extends UiBinder<Widget, DeployApplicationView> {
    }

    private static DeployApplicationViewUiBinder uiBinder = GWT.create(DeployApplicationViewUiBinder.class);

    @UiField
    TextInput nameField;

    /** Application URL field. */
    @UiField
    TextInput remoteNameField;

    public DeployApplicationView() {
        super();
        initWidget(uiBinder.createAndBindUi(this));
        setHeight("150px");
    }

    /** @see org.exoplatform.ide.extension.cloudfoundry.client.deploy.DeployApplicationPresenter.Display#getApplicationNameField() */
    @Override
    public HasValue<String> getApplicationNameField() {
        return nameField;
    }

    /** @see org.exoplatform.ide.extension.cloudfoundry.client.deploy.DeployApplicationPresenter.Display#getRemoteNameField() */
    @Override
    public HasValue<String> getRemoteNameField() {
        return remoteNameField;
    }

    /** @see org.exoplatform.ide.extension.cloudfoundry.client.deploy.DeployApplicationPresenter.Display#getView() */
    @Override
    public Composite getView() {
        return this;
    }

}
