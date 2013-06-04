/*
 * Copyright (C) 2013 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.codenvy.ide.ext.openshift.client.wizard;

import com.codenvy.ide.ext.openshift.client.OpenShiftResources;
import com.codenvy.ide.json.JsonArray;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
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
