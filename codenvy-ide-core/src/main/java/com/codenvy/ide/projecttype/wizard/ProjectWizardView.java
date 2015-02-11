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
package com.codenvy.ide.projecttype.wizard;

import com.codenvy.ide.api.mvp.Presenter;
import com.codenvy.ide.api.mvp.View;
import com.google.inject.ImplementedBy;

import javax.annotation.Nullable;

/**
 * @author Evgen Vidolob
 * @author Oleksii Orel
 */
@ImplementedBy(ProjectWizardViewImpl.class)
public interface ProjectWizardView extends View<ProjectWizardView.ActionDelegate> {

    void showPage(Presenter presenter);

    void showDialog(boolean isCreatingNewProject);

    void setRAMRequired(int amountOfRAM);

    void setRAMAvailable(int amountOfRAM);

    void setBuilderEnvironmentConfig(@Nullable String text);

    void setRunnerEnvironmentConfig(String text);

    void setLoaderVisibility(boolean visible);

    void close();

    void setNextButtonEnabled(boolean enabled);

    void setFinishButtonEnabled(boolean enabled);

    void setPreviousButtonEnabled(boolean enabled);

    public interface ActionDelegate {
        /** Performs any actions appropriate in response to the user having pressed the Next button */
        void onNextClicked();

        /** Performs any actions appropriate in response to the user having pressed the Back button */
        void onBackClicked();

        /** Performs any actions appropriate in response to the user having pressed the Create button */
        void onSaveClicked();

        /** Performs any actions appropriate in response to the user having pressed the Cancel button */
        void onCancelClicked();
    }
}
