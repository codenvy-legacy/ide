/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.projectimporter.importerpage;

import com.codenvy.ide.CoreLocalizationConstant;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.inject.Inject;

import javax.annotation.Nonnull;

/**
 * @author Roman Nikitenko
 */
public class ZipImporterPageViewImpl extends Composite implements ZipImporterPageView {
    interface ZipImporterPageViewImplUiBinder extends UiBinder<DockLayoutPanel, ZipImporterPageViewImpl> {
    }

    private ActionDelegate delegate;

    @UiField
    SimplePanel basePagePanel;
    @UiField
    CheckBox    skipFirstLevel;
    @UiField(provided = true)
    final CoreLocalizationConstant locale;

    @Inject
    public ZipImporterPageViewImpl(ZipImporterPageViewImplUiBinder uiBinder,
                                   CoreLocalizationConstant locale) {
        this.locale = locale;
        initWidget(uiBinder.createAndBindUi(this));
    }

    /** {@inheritDoc} */
    @Override
    public boolean isSkipFirstLevelSelected() {
        return skipFirstLevel.getValue();
    }

    @UiHandler({"skipFirstLevel"})
    void skipFirstLevelHandler(ValueChangeEvent<Boolean> event) {
        delegate.skipFirstLevelChanged(skipFirstLevel.getValue());
    }

    /** {@inheritDoc} */
    @Override
    public void reset() {
        skipFirstLevel.setValue(false);
    }

    /** {@inheritDoc} */
    @Nonnull
    @Override
    public AcceptsOneWidget getBasePagePanel() {
        return basePagePanel;
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(@Nonnull ActionDelegate delegate) {
        this.delegate = delegate;
    }
}
