/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 *  [2012] - [2013] Codenvy, S.A. 
 *  All Rights Reserved.
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
package com.codenvy.ide.ext.extensions.client.template.sample;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.ext.extensions.client.ExtRuntimeResources;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * The implementation of {@link CreateSampleCodenvyExtensionPageView}.
 *
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: CreateSampleCodenvyExtensionPageViewImpl.java Jul 8, 2013 4:07:57 PM azatsarynnyy $
 */
@Singleton
public class CreateSampleCodenvyExtensionPageViewImpl extends Composite implements CreateSampleCodenvyExtensionPageView {
    interface ExtensionPageViewImplUiBinder extends UiBinder<Widget, CreateSampleCodenvyExtensionPageViewImpl> {
    }

    private static ExtensionPageViewImplUiBinder uiBinder = GWT.create(ExtensionPageViewImplUiBinder.class);

    @UiField
    TextBox groupIdField;
    @UiField
    TextBox artifactIdField;
    @UiField
    TextBox versionField;
    @UiField(provided = true)
    final   ExtRuntimeResources res;
    private ActionDelegate      delegate;

    /**
     * Create view.
     *
     * @param resources
     */
    @Inject
    protected CreateSampleCodenvyExtensionPageViewImpl(ExtRuntimeResources resources) {
        this.res = resources;

        initWidget(uiBinder.createAndBindUi(this));
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    /** {@inheritDoc} */
    @Override
    public String getGroupId() {
        return groupIdField.getText();
    }

    /** {@inheritDoc} */
    @Override
    public void setGroupId(@NotNull String groupId) {
        groupIdField.setText(groupId);
    }

    /** {@inheritDoc} */
    @Override
    public String getArtifactId() {
        return artifactIdField.getText();
    }

    /** {@inheritDoc} */
    @Override
    public void setArtifactId(@NotNull String artifactId) {
        artifactIdField.setText(artifactId);
    }

    /** {@inheritDoc} */
    @Override
    public String getVersion() {
        return versionField.getText();
    }

    /** {@inheritDoc} */
    @Override
    public void setVersion(@NotNull String version) {
        versionField.setText(version);
    }

    @UiHandler(value = {"versionField", "artifactIdField", "groupIdField"})
    void onValueChanged(KeyUpEvent event) {
        delegate.onValueChanged();
    }
}
