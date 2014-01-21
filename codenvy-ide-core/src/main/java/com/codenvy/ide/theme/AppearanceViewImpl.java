/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2014] Codenvy, S.A.
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
package com.codenvy.ide.theme;

import com.codenvy.ide.api.ui.theme.Theme;
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
    void handleSelectionChanged(ChangeEvent event){
        themeBox.getSelectedIndex();
        delegate.themeSelected(themeBox.getValue(themeBox.getSelectedIndex()));
    }

    interface AppearanceViewImplUiBinder
            extends UiBinder<FlowPanel, AppearanceViewImpl> {
    }
}