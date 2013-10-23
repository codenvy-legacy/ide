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

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.annotations.Nullable;
import com.codenvy.ide.api.mvp.Presenter;
import com.google.gwt.resources.client.ImageResource;

import static com.codenvy.ide.api.ui.wizard.Wizard.UpdateDelegate;

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
        void onFailure(@NotNull Throwable exception);
    }

    /**
     * Sets update control delegate.
     *
     * @param delegate
     */
    void setUpdateDelegate(@NotNull UpdateDelegate delegate);

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
    void commit(@NotNull CommitCallback callback);

    /** Store options which can add the page. */
    void storeOptions();

    /** Remove options which can provide the page. */
    void removeOptions();

    /**
     * Set wizard context for current wizard.
     *
     * @param wizardContext
     */
    void setContext(@NotNull WizardContext wizardContext);
}