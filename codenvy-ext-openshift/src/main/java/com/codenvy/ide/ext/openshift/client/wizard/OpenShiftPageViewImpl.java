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
package com.codenvy.ide.ext.openshift.client.wizard;

import com.codenvy.ide.ext.openshift.client.OpenShiftResources;
import com.codenvy.ide.json.JsonArray;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * The implementation for {@link OpenShiftPageView}.
 *
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
@Singleton
public class OpenShiftPageViewImpl extends Composite implements OpenShiftPageView {

    interface OpenShiftPageViewImplUiBinder extends UiBinder<Widget, OpenShiftPageViewImpl> {
    }

    private static OpenShiftPageViewImplUiBinder uiBinder = GWT.create(OpenShiftPageViewImplUiBinder.class);

    @UiField
    TextBox applicationName;

    @UiField
    ListBox applicationType;

    @UiField
    CheckBox useAutoScaling;

    @UiField(provided = true)
    final OpenShiftResources resources;

    private ActionDelegate delegate;

    /**
     * Create view.
     *
     * @param resources
     *         image resources
     */
    @Inject
    protected OpenShiftPageViewImpl(OpenShiftResources resources) {
        this.resources = resources;

        initWidget(uiBinder.createAndBindUi(this));
    }

    /** {@inheritDoc} */
    @Override
    public String getName() {
        return applicationName.getText();
    }

    /** {@inheritDoc} */
    @Override
    public void setName(String applicationName) {
        this.applicationName.setText(applicationName);
    }

    /** {@inheritDoc} */
    @Override
    public boolean getScalingValue() {
        return useAutoScaling.getValue();
    }

    /** {@inheritDoc} */
    @Override
    public String getApplicationType() {
        return applicationType.getValue(applicationType.getSelectedIndex());
    }

    /** {@inheritDoc} */
    @Override
    public void setApplicationTypes(JsonArray<String> applicationTypes) {
        for (int i = 0; i < applicationTypes.size(); i++) {
            applicationType.addItem(applicationTypes.get(i));
        }
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }
}
