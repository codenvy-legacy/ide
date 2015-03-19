/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide.toolbar;

import org.eclipse.che.ide.api.action.ActionGroup;
import org.eclipse.che.ide.api.action.ActionPlaces;
import org.eclipse.che.ide.api.mvp.Presenter;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;


/**
 * Manages Toolbar items, changes item state and other.
 *
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
public class ToolbarPresenter implements Presenter, ToolbarView.ActionDelegate {
    private ToolbarView view;

    /**
     * Create presenter.
     *
     * @param view
     */
    @Inject
    public ToolbarPresenter(ToolbarView view) {
        this.view = view;
        this.view.setDelegate(this);
        this.view.setAddSeparatorFirst(true);
        this.view.setPlace(ActionPlaces.MAIN_TOOLBAR);
    }

    public void bindMainGroup(ActionGroup group) {
        view.setLeftActionGroup(group);
    }

    public void bindRightGroup(ActionGroup group) {
        view.setRightActionGroup(group);
    }

    /** {@inheritDoc} */
    @Override
    public void go(AcceptsOneWidget container) {
        container.setWidget(view);
    }
}