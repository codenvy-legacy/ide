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
package com.codenvy.ide.wizard.project.name;

import com.codenvy.ide.api.mvp.View;
import com.google.inject.ImplementedBy;

/**
 * @author Evgen Vidolob
 */
@ImplementedBy(NamePageViewImpl.class)
public interface NamePageView extends View<NamePageView.ActionDelegate> {
    String getProjectName();

    void focusOnNameField();

    void setProjectName(String name);

    /** Set project's description. */
    void setProjectDescription(String projectDescription);

    boolean getProjectVisibility();

    public interface ActionDelegate{
        void projectNameChanged(String name);

        void onVisibilityChanged(boolean value);
    }
}
