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
package org.exoplatform.ide.extension.googleappengine.client.deploy;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.component.TextInput;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Jun 20, 2012 11:56:01 AM anya $
 */
public class DeployApplicationView extends Composite implements DeployApplicationPresenter.Display {

    private static DeployApplicationViewUiBinder uiBinder = GWT.create(DeployApplicationViewUiBinder.class);

    interface DeployApplicationViewUiBinder extends UiBinder<Widget, DeployApplicationView> {
    }

    @UiField
    CheckBox useExistedField;

    @UiField
    TextInput applicationIdField;

    public DeployApplicationView() {
        super();
        initWidget(uiBinder.createAndBindUi(this));
        setHeight("160px");
    }

    /** @see org.exoplatform.ide.extension.googleappengine.client.deploy.DeployApplicationPresenter.Display#getApplicationIdField() */
    @Override
    public HasValue<String> getApplicationIdField() {
        return applicationIdField;
    }

    /** @see org.exoplatform.ide.extension.googleappengine.client.deploy.DeployApplicationPresenter.Display#getUseExisting() */
    @Override
    public HasValue<Boolean> getUseExisting() {
        return useExistedField;
    }

    /** @see org.exoplatform.ide.extension.googleappengine.client.deploy.DeployApplicationPresenter.Display#enableApplicationIdField
     * (boolean) */
    @Override
    public void enableApplicationIdField(boolean enable) {
        applicationIdField.setEnabled(enable);
    }

    /** @see org.exoplatform.ide.extension.googleappengine.client.deploy.DeployApplicationPresenter.Display#getView() */
    @Override
    public Composite getView() {
        return this;
    }
}
