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
package org.exoplatform.ide.extension.aws.client.beanstalk.deploy;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.api.TextFieldItem;
import org.exoplatform.gwtframework.ui.client.component.SelectItem;
import org.exoplatform.gwtframework.ui.client.component.TextInput;

/**
 * @author <a href="mailto:azatsarynnyy@exoplatform.com">Artem Zatsarynnyy</a>
 * @version $Id: DeployApplicationView.java Sep 25, 2012 12:56:56 PM azatsarynnyy $
 */
public class DeployApplicationView extends Composite implements DeployApplicationPresenter.Display {
    interface DeployApplicationViewUiBinder extends UiBinder<Widget, DeployApplicationView> {
    }

    private static DeployApplicationViewUiBinder uiBinder = GWT.create(DeployApplicationViewUiBinder.class);

    @UiField
    TextInput nameField;

    @UiField
    TextInput envNameField;

    @UiField
    SelectItem solutionStackField;

    public DeployApplicationView() {
        super();
        initWidget(uiBinder.createAndBindUi(this));
        setHeight("260px");
    }

    /** @see org.exoplatform.ide.extension.aws.client.beanstalk.deploy.DeployApplicationPresenter.Display#getNameField() */
    @Override
    public TextFieldItem getNameField() {
        return nameField;
    }

    /** @see org.exoplatform.ide.extension.aws.client.beanstalk.deploy.DeployApplicationPresenter.Display#getEnvNameField() */
    @Override
    public TextFieldItem getEnvNameField() {
        return envNameField;
    }

    /** @see org.exoplatform.ide.extension.aws.client.beanstalk.deploy.DeployApplicationPresenter.Display#getSolutionStackField() */
    @Override
    public HasValue<String> getSolutionStackField() {
        return solutionStackField;
    }

    /** @see org.exoplatform.ide.extension.aws.client.beanstalk.deploy.DeployApplicationPresenter.Display#getView() */
    @Override
    public Composite getView() {
        return this;
    }

    /** @see org.exoplatform.ide.extension.aws.client.beanstalk.deploy.DeployApplicationPresenter.Display#setSolutionStackValues(java.lang
     * .String[]) */
    @Override
    public void setSolutionStackValues(String[] values) {
        solutionStackField.setValueMap(values);
    }

}
