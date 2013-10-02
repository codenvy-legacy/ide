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
package com.codenvy.ide.workspace;

import com.codenvy.ide.api.mvp.Presenter;
import com.codenvy.ide.api.ui.workspace.PartPresenter;
import com.codenvy.ide.api.ui.workspace.PartStackType;
import com.codenvy.ide.api.ui.workspace.WorkspaceAgent;
import com.codenvy.ide.menu.MainMenuPresenter;
import com.codenvy.ide.notification.NotificationManagerImpl;
import com.codenvy.ide.toolbar.MainToolbar;
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

    private final NotificationManagerImpl notificationManager;

    /**
     * Instantiates Presenter
     *
     * @param view
     * @param menu
     * @param genericPerspectiveProvider
     */
    @Inject
    protected WorkspacePresenter(WorkspaceView view, MainMenuPresenter menu, @MainToolbar ToolbarPresenter toolbarPresenter,
                                 Provider<WorkBenchPresenter> genericPerspectiveProvider, NotificationManagerImpl notificationManager) {
        super();
        this.view = view;
        this.view.setDelegate(this);
        this.toolbarPresenter = toolbarPresenter;
        this.menu = menu;
        this.notificationManager = notificationManager;

        this.workBenchPresenter = genericPerspectiveProvider.get();
    }

    /** {@inheritDoc} */
    @Override
    public void go(AcceptsOneWidget container) {
        // Expose Project Explorer into Tools Panel
        menu.go(view.getMenuPanel());
        toolbarPresenter.go(view.getToolbarPanel());
        workBenchPresenter.go(view.getPerspectivePanel());
        notificationManager.go(view.getStatusPanel());
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
