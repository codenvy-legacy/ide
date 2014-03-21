/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 *  [2012] - [2013] Codenvy, S.A. 
 *  All Rights Reserved.
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
