/*
 * Copyright (C) 2003-2012 eXo Platform SAS.
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
package com.codenvy.ide.workspace;

import com.codenvy.ide.api.mvp.Presenter;
import com.codenvy.ide.api.ui.workspace.PartPresenter;
import com.codenvy.ide.api.ui.workspace.PartStackType;
import com.codenvy.ide.api.ui.workspace.WorkspaceAgent;
import com.codenvy.ide.menu.MainMenuPresenter;
import com.codenvy.ide.toolbar.ToolbarPresenter;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;


/**
 * Root Presenter that implements Workspace logic. Descendant Presenters are injected via
 * constructor and exposed to corresponding UI containers.
 * It contains Menu, Toolbar and WorkBench Presenter to expose their views into corresponding places
 * and to maintain their interactions.
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a>
 */
@Singleton
public class WorkspacePresenter implements Presenter, WorkspaceView.ActionDelegate, WorkspaceAgent {
    private final WorkspaceView view;

    private final MainMenuPresenter menu;

    private WorkBenchPresenter workBenchPresenter;

    private final ToolbarPresenter toolbarPresenter;

    /**
     * Instantiates Presenter
     *
     * @param view
     * @param menu
     * @param genericPerspectiveProvider
     */
    @Inject
    protected WorkspacePresenter(WorkspaceView view, MainMenuPresenter menu, ToolbarPresenter toolbarPresenter,
                                 Provider<WorkBenchPresenter> genericPerspectiveProvider) {
        super();
        this.view = view;
        this.toolbarPresenter = toolbarPresenter;
        this.view.setDelegate(this);
        this.menu = menu;

        this.workBenchPresenter = genericPerspectiveProvider.get();
    }

    /** {@inheritDoc} */
    @Override
    public void go(AcceptsOneWidget container) {
        // Expose Project Explorer into Tools Panel
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
        workBenchPresenter.openPart(part, type);
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

}
