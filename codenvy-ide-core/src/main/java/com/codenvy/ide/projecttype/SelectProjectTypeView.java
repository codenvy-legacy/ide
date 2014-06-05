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
package com.codenvy.ide.projecttype;

import com.codenvy.api.project.shared.dto.ProjectTypeDescriptor;
import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.collections.Array;

import javax.validation.constraints.NotNull;

/**
 * Interface for project type selection view.
 *
 * @author <a href="mailto:ashumilova@codenvy.com">Ann Shumilova</a>
 */
public interface SelectProjectTypeView extends View<SelectProjectTypeView.ActionDelegate> {
    /** Needs for delegate some function into SelectProjectType view. */
    public interface ActionDelegate {
        /** Performs any actions appropriate in response to the user having pressed the Ok button. */
        void onOkClicked();

        /** Performs any actions appropriate in response to the user having pressed the Cancel button. */
        void onCancelClicked();
    }

    /**
     * Sets the value of the project's type field label.
     *
     * @param label
     */
    void setLabel(@NotNull String label);

    /**
     * Sets project types.
     *
     * @param types
     */
    void setTypes(@NotNull Array<ProjectTypeDescriptor> types);

    /** Clear project types. */
    void clearTypes();

    /**
     * Returns chosen project type descriptor.
     *
     * @return chosen {@link ProjectTypeDescriptor}
     */
    ProjectTypeDescriptor getSelectedProjectType();

    /** Close dialog. */
    void close();

    /** Show dialog. */
    void showDialog();
}
