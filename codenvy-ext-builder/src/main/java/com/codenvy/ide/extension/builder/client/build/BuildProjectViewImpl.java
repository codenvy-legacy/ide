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
package com.codenvy.ide.extension.builder.client.build;

import com.codenvy.ide.api.parts.PartStackUIResources;
import com.codenvy.ide.api.parts.base.BaseView;
import com.codenvy.ide.extension.builder.client.BuilderResources;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * The implementation of {@link BuildProjectView}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@Singleton
public class BuildProjectViewImpl extends BaseView<BuildProjectView.ActionDelegate> implements BuildProjectView {
    private static BuildProjectViewImplUiBinder uiBinder = GWT.create(BuildProjectViewImplUiBinder.class);

    interface BuildProjectViewImplUiBinder extends UiBinder<Widget, BuildProjectViewImpl> {
    }

    @UiField
    Button    btnClearOutput;
    @UiField
    FlowPanel output;

    /**
     * Create view.
     *
     * @param resources
     * @param partStackUIResources
     */
    @Inject
    protected BuildProjectViewImpl(BuilderResources resources, PartStackUIResources partStackUIResources) {
        super(partStackUIResources);
        container.add(uiBinder.createAndBindUi(this));

        btnClearOutput.setHTML(new Image(resources.clearOutput()).toString());
    }

    /** {@inheritDoc} */
    @Override
    public void showMessageInOutput(String text) {
        output.add(new HTML(text));
    }

    /** {@inheritDoc} */
    @Override
    public void clearOutput() {
        output.clear();
    }

    /** {@inheritDoc} */
    @Override
    public void setClearOutputButtonEnabled(boolean isEnabled) {
        btnClearOutput.setEnabled(isEnabled);
    }

    @UiHandler("btnClearOutput")
    void onBtnCancelClick(ClickEvent event) {
        delegate.onClearOutputClicked();
    }
}