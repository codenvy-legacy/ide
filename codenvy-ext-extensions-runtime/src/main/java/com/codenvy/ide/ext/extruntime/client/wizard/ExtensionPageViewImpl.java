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
package com.codenvy.ide.ext.extruntime.client.wizard;

import com.codenvy.ide.ext.extruntime.client.ExtRuntimeResources;
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
 * The implementation of {@link ExtensionPageView}.
 * 
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: ExtensionPageViewImpl.java Jul 8, 2013 4:07:57 PM azatsarynnyy $
 */
@Singleton
public class ExtensionPageViewImpl extends Composite implements ExtensionPageView {
    interface ExtensionPageViewImplUiBinder extends UiBinder<Widget, ExtensionPageViewImpl> {
    }

    private static ExtensionPageViewImplUiBinder uiBinder = GWT.create(ExtensionPageViewImplUiBinder.class);

    @UiField
    TextBox                                      groupIdField;
    @UiField
    TextBox                                      artifactIdField;
    @UiField
    TextBox                                      versionField;
    @UiField(provided = true)
    final ExtRuntimeResources                    res;
    private ActionDelegate                       delegate;

    /**
     * Create view.
     * 
     * @param resources
     */
    @Inject
    protected ExtensionPageViewImpl(ExtRuntimeResources resources) {
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
    public void setGroupId(String groupId) {
        groupIdField.setText(groupId);
    }

    /** {@inheritDoc} */
    @Override
    public String getArtifactId() {
        return artifactIdField.getText();
    }

    /** {@inheritDoc} */
    @Override
    public void setArtifactId(String artifactId) {
        artifactIdField.setText(artifactId);
    }

    /** {@inheritDoc} */
    @Override
    public String getVersion() {
        return versionField.getText();
    }

    /** {@inheritDoc} */
    @Override
    public void setVersion(String version) {
        versionField.setText(version);
    }

    @UiHandler("groupIdField")
    void onGroupIdFieldChange(KeyUpEvent event) {
        delegate.onGroupIdChanged();
    }

    @UiHandler("artifactIdField")
    void onArtifactIdFieldKeyUp(KeyUpEvent event) {
        delegate.onArtifactIdChanged();
    }

    @UiHandler("versionField")
    void onVersionFieldKeyUp(KeyUpEvent event) {
        delegate.onVersionChanged();
    }
}
