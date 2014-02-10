/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 * [2012] - [2013] Codenvy, S.A. 
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
package com.codenvy.ide.api.ui.wizard;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

/**
 * The wizard contains wizard pages. It has info on the next or previous page. The wizard can disable a page if it is possible. It can
 * change the next page in case parameters for {@link WizardContext} has been changed. Usually every wizard has its own wizard context.
 * This wizard context contains all required attributes.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public interface Wizard {
    /** Required for delegating update function in a wizard model. */
    public interface UpdateDelegate {
        /** Performs some actions in response to a user's updating controls. */
        void updateControls();
    }

    /**
     * Sets update control delegate.
     *
     * @param delegate
     */
    void setUpdateDelegate(@NotNull UpdateDelegate delegate);

    /** @return wizard title */
    @NotNull
    String getTitle();

    /**
     * Performs some actions required for flipping to first page and returning to the first page of a wizard.
     *
     * @return first page
     */
    @NotNull
    WizardPage flipToFirst();

    /**
     * Performs actions required for flipping to next page and return next page of wizard.
     *
     * @return if the wizard has next page, it returns next page, otherwise returns <code>null</code>.
     */
    @Nullable
    WizardPage flipToNext();

    /**
     * Performs some actions required for jumping to the previous page and returning to the previous page of a wizard.
     *
     * @return if the wizard has previous page, it returns to the previous page, otherwise return <code>null</code>.
     */
    @Nullable
    WizardPage flipToPrevious();

    /**
     * Returns whether the wizard has the next page.
     * The result of this method is typically used by the wizard container to enable or disable the Next button.
     *
     * @return <code>true</code> if the wizard has next page, and <code>false</code> otherwise
     */
    boolean hasNext();

    /**
     * Returns whether the wizard has previous page.
     * The result of this method is typically used by the wizard container to enable or disable the Back button.
     *
     * @return <code>true</code> if the wizard has previous page, and <code>false</code> otherwise
     */
    boolean hasPrevious();

    /**
     * Returns whether the wizard could be finished without further user interaction.
     * The result of this method is typically used by the wizard container to enable or disable the Finish button.
     *
     * @return <code>true</code> if the wizard could be finished, and <code>false</code> otherwise
     */
    boolean canFinish();

    /** Performs some actions in response to a user's click on Finish button. */
    void onFinish();
}