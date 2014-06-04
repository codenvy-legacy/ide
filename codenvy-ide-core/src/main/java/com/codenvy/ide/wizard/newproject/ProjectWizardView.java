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
package com.codenvy.ide.wizard.newproject;

import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.collections.Array;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;


/**
 * Interface of Wizard view.
 *
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
public interface ProjectWizardView extends View<ProjectWizardView.ActionDelegate> {

    /**
     * Sets steps titles for wizard
     * @param stepsTitles
     */
    void setStepTitles(Array<String> stepsTitles);

    /**
     * Sets position of the 'step arrow'.
     *
     * @param position
     *         number of 'step tab' to set the arrow. Tab's number starts from zero.
     */
    void setStepArrowPosition(int position);

    /**
     * Sets whether Next button is visible.
     *
     * @param isVisible
     *         <code>true</code> to visible the button, <code>false</code>
     *         to disable it
     */
    void setNextButtonVisible(boolean isVisible);

    /**
     * Sets whether Next button is enabled.
     *
     * @param isEnabled
     *         <code>true</code> to enable the button, <code>false</code>
     *         to disable it
     */
    void setNextButtonEnabled(boolean isEnabled);

    /**
     * Sets whether Back button is visible.
     *
     * @param isVisible
     *         <code>true</code> to visible the button, <code>false</code>
     *         to disable it
     */
    void setBackButtonVisible(boolean isVisible);

    /**
     * Sets whether Finish button is enabled.
     *
     * @param isEnabled
     *         <code>true</code> to enable the button, <code>false</code>
     *         to disable it
     */
    void setFinishButtonEnabled(boolean isEnabled);

    void setTitle(String title);

    /**
     * Sets new caption of wizard's page
     *
     * @param caption
     */
    void setCaption(@NotNull String caption);

    /**
     * Sets new notice of wizard's page
     *
     * @param notice
     */
    void setNotice(@Nullable String notice);

    /**
     * Sets new image of wizard's page
     *
     * @param image
     */
    void setImage(@Nullable ImageResource image);

    /** Close wizard */
    void close();

    /** Show dialog */
    void showDialog();

    /**
     * Sets whether animation for change page is enabled.
     *
     * @param isEnabled
     *         <code>true</code> to enable the animation, <code>false</code>
     *         to disable it
     */
    void setEnabledAnimation(boolean isEnabled);

    /**
     * Returns place of main form where will be shown current wizard page.
     *
     * @return place of main form
     */
    AcceptsOneWidget getContentPanel();

    /** Needs for delegate some function into Wizard view. */
    public interface ActionDelegate {
        /** Performs any actions appropriate in response to the user having pressed the Next button */
        void onNextClicked();

        /** Performs any actions appropriate in response to the user having pressed the Back button */
        void onBackClicked();

        /** Performs any actions appropriate in response to the user having pressed the Finish button */
        void onFinishClicked();

        /** Performs any actions appropriate in response to the user having pressed the Cancel button */
        void onCancelClicked();
    }
}