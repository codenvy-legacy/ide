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
package com.codenvy.ide.theme;

import com.codenvy.ide.api.theme.Theme;
import com.codenvy.ide.collections.Array;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Evgen Vidolob
 */
public class AppearanceViewImpl implements AppearanceView {

    private static AppearanceViewImplUiBinder ourUiBinder = GWT.create(AppearanceViewImplUiBinder.class);
    private final FlowPanel rootElement;
    @UiField
    ListBox themeBox;
    private ActionDelegate delegate;

    public AppearanceViewImpl() {
        rootElement = ourUiBinder.createAndBindUi(this);

    }

    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    public Widget asWidget() {
        return rootElement;
    }

    @Override
    public void setThemes(Array<Theme> themes, String currentThemeId) {
        themeBox.clear();

        for (Theme t : themes.asIterable()) {
            themeBox.addItem(t.getDescription(), t.getId());
            if (t.getId().equals(currentThemeId)) {
                themeBox.setSelectedIndex(themes.indexOf(t));
            }
        }

    }

    @UiHandler("themeBox")
    void handleSelectionChanged(ChangeEvent event) {
        themeBox.getSelectedIndex();
        delegate.themeSelected(themeBox.getValue(themeBox.getSelectedIndex()));
    }

    interface AppearanceViewImplUiBinder
            extends UiBinder<FlowPanel, AppearanceViewImpl> {
    }
}