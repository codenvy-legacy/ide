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
import org.eclipse.che.ide.api.mvp.View;

import javax.annotation.Nonnull;


/**
 * The view of {@link ToolbarPresenter}.
 *
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
public interface ToolbarView extends View<ToolbarView.ActionDelegate> {
    /** Needs for delegate some function into Toolbar view. */
    public interface ActionDelegate {
    }

    void setPlace(@Nonnull String place);

    void setLeftActionGroup(@Nonnull ActionGroup actionGroup);

    void setRightActionGroup(@Nonnull ActionGroup actionGroup);

    void setAddSeparatorFirst(boolean addSeparatorFirst);
}