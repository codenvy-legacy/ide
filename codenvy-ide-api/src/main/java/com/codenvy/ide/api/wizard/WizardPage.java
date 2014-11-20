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
package com.codenvy.ide.api.wizard;

import com.codenvy.ide.api.mvp.Presenter;
import com.google.gwt.resources.client.ImageResource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.codenvy.ide.api.wizard.Wizard.UpdateDelegate;

/**
 * The general interface of wizard page.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public interface WizardPage extends Presenter {
    /** Required for delegating update function in a wizard model. */
    public interface CommitCallback {
        /** Call when commit operations complete successfully. */
        void onSuccess();

        /**
         * Call when commit operations complete failure.
         *
         * @param exception
         *         exception that happened
         */
        void onFailure(@Nonnull Throwable exception);
    }

    /**
     * Sets update control delegate.
     *
     * @param delegate
     */
    void setUpdateDelegate(@Nonnull UpdateDelegate delegate);

    /** @return wizard page caption */
    @Nullable
    String getCaption();

    /**
     * Returns notice of a wizard's page. Notice is a text message displayed on the top of the dialog, usually used to guide a user through
     * the process of filling the wizard pages. It displays notices, error prompts etc.
     *
     * @return notice text if has some or null
     */
    @Nullable
    String getNotice();

    /** @return the image for this wizard page, or <code>null</code> if none */
    @Nullable
    ImageResource getImage();

    /**
     * Returns whether this page is complete or not.
     * This information is typically used by the wizard to decide when it is okay to finish or to flip to the next page.
     *
     * @return <code>true</code> if this page is complete, and <code>false</code> otherwise
     */
    boolean isCompleted();

    /**
     * Returns whether this page can be skipped.
     * This option is usable in case a page has just one item to choose from and a user usually chooses this item.
     * Another example is a user's page having no view but possessing some logic. This option helps execute this logic and skip
     * showing a view.
     *
     * @return <code>true</code> if this page can be skipped, and <code>false</code> otherwise
     */
    boolean canSkip();

    /**
     * Returns whether this page is in context.
     * This option is usable in case a page is available for current parameters. This means commit operation will execute on this page.
     *
     * @return <code>true</code> if this page is in context, and <code>false</code> otherwise
     */
    boolean inContext();

    /** Focus components on view. This option is just for a better usability, i.e. selecting a field that needs to be filled etc. */
    void focusComponent();

    /**
     * Performs some actions which provide the page.
     *
     * @param callback
     */
    void commit(@Nonnull CommitCallback callback);

    /** Store options which can add the page. */
    void storeOptions();

    /** Remove options which can provide the page. */
    void removeOptions();

    /**
     * Set wizard context for current wizard.
     *
     * @param wizardContext
     */
    void setContext(@Nonnull WizardContext wizardContext);
}