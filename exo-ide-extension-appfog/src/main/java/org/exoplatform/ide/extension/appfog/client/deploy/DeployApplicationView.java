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
package org.exoplatform.ide.extension.appfog.client.deploy;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.component.ComboBoxField;
import org.exoplatform.gwtframework.ui.client.component.TextInput;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class DeployApplicationView extends Composite implements DeployApplicationPresenter.Display {
    interface DeployApplicationViewUiBinder extends UiBinder<Widget, DeployApplicationView> {
    }

    private static DeployApplicationViewUiBinder uiBinder = GWT.create(DeployApplicationViewUiBinder.class);

    @UiField
    TextInput targetField;

    @UiField
    TextInput nameField;

    @UiField
    ComboBoxField infraField;

    /** Application URL field. */
    @UiField
    TextInput urlField;

    public DeployApplicationView() {
        super();
        initWidget(uiBinder.createAndBindUi(this));
        setHeight("220px");
        targetField.setReadOnly(true);
    }

    /** @see org.exoplatform.ide.extension.cloudfoundry.client.deploy.DeployApplicationPresenter.Display#getNameField() */
    @Override
    public HasValue<String> getNameField() {
        return nameField;
    }

    /** @see org.exoplatform.ide.extension.cloudfoundry.client.deploy.DeployApplicationPresenter.Display#getUrlField() */
    @Override
    public HasValue<String> getUrlField() {
        return urlField;
    }

    /** @see org.exoplatform.ide.extension.cloudfoundry.client.deploy.DeployApplicationPresenter.Display#getServerField() */
    @Override
    public HasValue<String> getServerField() {
        return targetField;
    }

    @Override
    public HasValue<String> getInfraField() {
        return infraField;
    }

    /** @see org.exoplatform.ide.extension.cloudfoundry.client.deploy.DeployApplicationPresenter.Display#setServerValues(java.lang
     * .String[]) */
    @Override
    public void setServerValue(String server) {
        targetField.setValue(server);
    }

    @Override
    public void setInfraValues(String[] infras) {
        infraField.setValueMap(infras);
    }

    /** @see org.exoplatform.ide.extension.cloudfoundry.client.deploy.DeployApplicationPresenter.Display#getView() */
    @Override
    public Composite getView() {
        return this;
    }
}
