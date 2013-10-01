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

/** @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a> */
public interface WizardModel {

    public interface UpdateDelegate {

        void updateControls();
    }

    /**
     * Sets new delegate
     *
     * @param delegate
     */
    void setUpdateDelegate(UpdateDelegate delegate);

    String getTitle();

    WizardPage getFirst();

    WizardPage flipToNext();

    WizardPage flipToPrevious();

    /**
     * Returns whether this wizard has the next page.
     * The result of this method is typically used by the wizard container to enable
     * or disable the Next button.
     *
     * @return <code>true</code> if the wizard has the next page, and
     *         <code>false</code> otherwise
     */
    boolean hasNext();

    /**
     * Returns whether this wizard has the previous page.
     * The result of this method is typically used by the wizard container to enable
     * or disable the Back button.
     *
     * @return <code>true</code> if the wizard has the previous page, and
     *         <code>false</code> otherwise
     */
    boolean hasPrevious();

    /**
     * Returns whether this wizard could be finished without further user
     * interaction.
     * The result of this method is typically used by the wizard container to enable
     * or disable the Finish button.
     *
     * @return <code>true</code> if the wizard could be finished, and
     *         <code>false</code> otherwise
     */
    boolean canFinish();

    // TODO may be need to remove
    void onCancel();

    void onFinish();
}