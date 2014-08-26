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
package com.codenvy.ide.workspace;

import com.codenvy.ide.api.action.Constraints;
import com.codenvy.ide.api.mvp.Presenter;
import com.codenvy.ide.api.parts.PartPresenter;
import com.codenvy.ide.api.parts.PartStackType;
import com.codenvy.ide.api.parts.WorkspaceAgent;
import com.codenvy.ide.menu.MainMenuPresenter;
import com.codenvy.ide.toolbar.MainToolbar;
import com.codenvy.ide.toolbar.ToolbarPresenter;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

/**
 * Root Presenter that implements Workspace logic. Descendant Presenters are injected
 * via constructor and exposed to corresponding UI containers. It contains Menu,
 * Toolbar and WorkBench Presenter to expose their views into corresponding places and to
 * maintain their interactions.
 *
 * @author Nikolay Zamosenchuk
 */
@Singleton
public class WorkspacePresenter implements Presenter, WorkspaceView.ActionDelegate, WorkspaceAgent {
    private final WorkspaceView      view;
    private final MainMenuPresenter  menu;
    private final ToolbarPresenter   toolbarPresenter;
    private       WorkBenchPresenter workBenchPresenter;

    /**
     * Instantiates Presenter.
     *
     * @param view
     * @param menu
     * @param toolbarPresenter
     * @param genericPerspectiveProvider
     */
    @Inject
    protected WorkspacePresenter(WorkspaceView view,
                                 MainMenuPresenter menu,
                                 @MainToolbar ToolbarPresenter toolbarPresenter,
                                 Provider<WorkBenchPresenter> genericPerspectiveProvider) {
        super();
        this.view = view;
        this.view.setDelegate(this);
        this.toolbarPresenter = toolbarPresenter;
        this.menu = menu;
        this.workBenchPresenter = genericPerspectiveProvider.get();
    }

    /** {@inheritDoc} */
    @Override
    public void go(AcceptsOneWidget container) {
        menu.go(view.getMenuPanel());
        toolbarPresenter.go(view.getToolbarPanel());
        workBenchPresenter.go(view.getPerspectivePanel());
        container.setWidget(view);
    }

    /** {@inheritDoc} */
    @Override
    public void setActivePart(PartPresenter part) {
        workBenchPresenter.setActivePart(part);
    }

    /** {@inheritDoc} */
    @Override
    public void openPart(PartPresenter part, PartStackType type) {
        openPart(part, type, null);
    }

    /** {@inheritDoc} */
    @Override
    public void openPart(PartPresenter part, PartStackType type, Constraints constraint){
        workBenchPresenter.openPart(part, type, constraint);
    }

    /** {@inheritDoc} */
    @Override
    public void hidePart(PartPresenter part) {
        workBenchPresenter.hidePart(part);
    }

    /** {@inheritDoc} */
    @Override
    public void removePart(PartPresenter part) {
        workBenchPresenter.removePart(part);
    }

    /** {@inheritDoc} */
    @Override
    public void onUpdateClicked() {
        final String host = Window.Location.getParameter("h");
        final String port = Window.Location.getParameter("p");
        updateExtension(host, port);
    }

    /** Update already launched Codenvy extension. */
    private static native void updateExtension(String host, String port) /*-{
        $wnd.__gwt_bookmarklet_params = {server_url: 'http://' + host + ':' + port + '/', module_name: '_app'};
        var s = $doc.createElement('script');
        s.src = 'http://' + host + ':' + port + '/dev_mode_on.js';
        void($doc.getElementsByTagName('head')[0].appendChild(s));
    }-*/;


    /**
     * Sets whether 'Update extension' button is visible.
     *
     * @param visible
     *         <code>true</code> to show the button, <code>false</code> to hide it
     */
    public void setUpdateButtonVisibility(boolean visible) {
        view.setUpdateButtonVisibility(visible);
    }
}