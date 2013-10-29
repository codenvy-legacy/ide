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

import com.codenvy.ide.api.mvp.Presenter;
import com.google.gwt.resources.client.ImageResource;


/**
 * Interface of wizard page.
 *
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
@Deprecated
public interface WizardPagePresenter extends Presenter {
    /** Needs for delegate updateControls function into PagePresenter. */
    interface WizardUpdateDelegate {
        /** Updates wizard view components without content panel. */
        void updateControls();
    }

    /**
     * Sets new delegate
     *
     * @param delegate
     */
    void setUpdateDelegate(WizardUpdateDelegate delegate);

    /**
     * Performs any actions appropriate in response to the user
     * having pressed the Next button.
     *
     * @return the next page, or <code>null</code> if none
     */
    WizardPagePresenter flipToNext();

    /**
     * Performs any actions appropriate in response to the user
     * having pressed the Back button.
     *
     * @return the previous page, or <code>null</code> if none
     */
    WizardPagePresenter flipToPrevious();

    /**
     * Sets or clears the previous page.
     *
     * @param previous
     *         new previous page
     */
    void setPrevious(WizardPagePresenter previous);

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
     * Returns whether this page is complete or not.
     * This information is typically used by the wizard to decide
     * when it is okay to finish or to flip to the next page.
     *
     * @return <code>true</code> if this page is complete, and
     *         <code>false</code> otherwise
     */
    boolean isCompleted();

    /**
     * Returns wizard page caption.
     *
     * @return caption
     */
    String getCaption();

    /**
     * Returns notice of wizard's page. Notice is a text message
     * displayed on the top of the dialog, usually used to guide
     * user through the process of filling the wizard pages. It
     * displays notices, error prompts and etc.
     *
     * @return notice text if any or null
     */
    String getNotice();

    /**
     * Returns this wizard page's image.
     *
     * @return the image for this wizard page, or <code>null</code> if none
     */
    ImageResource getImage();

    /**
     * Performs any actions appropriate in response to the user
     * having pressed the Finish button.
     */
    void doFinish();

    /**
     * Performs any actions appropriate in response to the user
     * having pressed the Cancel button.
     */
    void doCancel();
}