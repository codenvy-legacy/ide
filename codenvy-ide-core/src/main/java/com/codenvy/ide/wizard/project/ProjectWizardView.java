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
package com.codenvy.ide.wizard.project;

import com.codenvy.ide.api.mvp.Presenter;
import com.codenvy.ide.api.mvp.View;
import com.google.inject.ImplementedBy;

/**
 * @author Evgen Vidolob
 */
@ImplementedBy(ProjectWizardViewImpl.class)
public interface ProjectWizardView extends View<ProjectWizardView.ActionDelegate>{

    void showPage(Presenter presenter);

    void showDialog();

    void setEnabledAnimation(boolean enabled);

    void close();

    void setNextButtonEnabled(boolean enabled);

    void setFinishButtonEnabled(boolean enabled);

    void setBackButtonEnabled(boolean enabled);

    void reset();

    void enableInput();

    void disableInput();

    void setName(String name);

    void setVisibility(boolean visible);

    void removeNameError();

    void showNameError();

    void focusOnName();

    void disableAllExceptName();

    public interface ActionDelegate{
        /** Performs any actions appropriate in response to the user having pressed the Next button */
        void onNextClicked();

        /** Performs any actions appropriate in response to the user having pressed the Back button */
        void onBackClicked();

        /** Performs any actions appropriate in response to the user having pressed the Finish button */
        void onSaveClicked();

        /** Performs any actions appropriate in response to the user having pressed the Cancel button */
        void onCancelClicked();

        void projectNameChanged(String name);

        void projectVisibilityChanged(Boolean publ);

        void projectDescriptionChanged(String projectDescriptionValue);
    }
}
