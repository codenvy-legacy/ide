/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2014] Codenvy, S.A.
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
