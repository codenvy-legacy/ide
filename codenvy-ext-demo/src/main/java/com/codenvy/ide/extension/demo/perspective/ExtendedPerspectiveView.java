/*
 * Copyright (C) 2003-2013 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package com.codenvy.ide.extension.demo.perspective;

import com.codenvy.ide.api.ui.perspective.PerspectiveView;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;


/**
 * Demo for Perspective API, that allows to create custom branded perspectives
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a>
 */
public class ExtendedPerspectiveView extends Composite implements
                                                       PerspectiveView<ExtendedPerspectiveView.ExtendedPerspectiveActionDelegate> {

    private static ExtendedPerspectiveViewUiBinder uiBinder = GWT.create(ExtendedPerspectiveViewUiBinder.class);

    interface ExtendedPerspectiveViewUiBinder extends UiBinder<Widget, ExtendedPerspectiveView> {
    }

    /** Extended Action Delegate that provides a way to get Events from cusom Perspective elements. */
    interface ExtendedPerspectiveActionDelegate extends PerspectiveView.ActionDelegate {
        public void onGoogleAccountClick();
    }

    @UiField
    SplitLayoutPanel splitPanel;

    @UiField
    SimplePanel editorPanel;

    @UiField
    SimplePanel navPanel;

    @UiField
    SimplePanel infoPanel;

    @UiField
    SimplePanel toolPanel;

    @UiField
    FlowPanel extPanel;

    private ExtendedPerspectiveActionDelegate delegate;

    /**
     * Because this class has a default constructor, it can
     * be used as a binder template. In other words, it can be used in other
     * *.ui.xml files as follows:
     * <ui:UiBinder xmlns:ui="urn:ui:com.google.gwt.uibinder"
     * xmlns:g="urn:import:**user's package**">
     * <g:**UserClassName**>Hello!</g:**UserClassName>
     * </ui:UiBinder>
     * Note that depending on the widget that is used, it may be necessary to
     * implement HasHTML instead of HasText.
     */
    public ExtendedPerspectiveView() {
        initWidget(uiBinder.createAndBindUi(this));
        // set editor panel's parent a CSS class "ide-editor-ares" to have a wide shadow effect
        splitPanel.getWidgetContainerElement(editorPanel).addClassName("ide-editor-area");
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ExtendedPerspectiveActionDelegate delegate) {
        this.delegate = delegate;
    }

    /** {@inheritDoc} */
    @Override
    public AcceptsOneWidget getEditorPanel() {
        return editorPanel;
    }

    /** {@inheritDoc} */
    @Override
    public AcceptsOneWidget getNavigationPanel() {
        return navPanel;
    }

    /** {@inheritDoc} */
    @Override
    public AcceptsOneWidget getInformationPanel() {
        return infoPanel;
    }

    /** {@inheritDoc} */
    @Override
    public AcceptsOneWidget getToolPanel() {
        return toolPanel;
    }

    /**
     * Handle button events
     *
     * @param event
     */
    @UiHandler({"googAccout", "manageApps"})
    void onGoogAccClick(ClickEvent event) {
        if (delegate != null) {
            delegate.onGoogleAccountClick();
        }
    }
}
