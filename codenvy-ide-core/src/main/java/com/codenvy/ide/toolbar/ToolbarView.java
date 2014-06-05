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
package com.codenvy.ide.toolbar;

import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.api.ui.action.ActionGroup;

import javax.validation.constraints.NotNull;


/**
 * The view of {@link ToolbarPresenter}.
 *
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
public interface ToolbarView extends View<ToolbarView.ActionDelegate> {
    /** Needs for delegate some function into Toolbar view. */
    public interface ActionDelegate {
    }

    void setPlace(@NotNull String place);

    void setActionGroup(@NotNull ActionGroup actionGroup);

    void setAddSeparatorFirst(boolean addSeparatorFirst);
}