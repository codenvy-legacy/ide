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
    /** Required for delegating update function in wizard model. */
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
     * Returns notice of wizard's page. Notice is a text message displayed on the top of the dialog, usually used to guide user through the
     * process of filling the wizard pages. It displays notices, error prompts and etc.
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
     * Returns whether this page can skip.
     * This option is usable in case page has just one choose item and user usually choose this item.
     * Other use case it is user page doesn't have a view but it have some logical. This option helps to execute this logical and don't
     * show a view.
     *
     * @return <code>true</code> if this page can skip, and <code>false</code> otherwise
     */
    boolean canSkip();

    /** Focus components on view. This option just for better usability. This means to select field that need to fill or etc. */
    void focusComponent();

    /**
     * Performs some actions which provides the page.
     *
     * @param callback
     */
    void commit(@NotNull CommitCallback callback);

    // TODO may be need rollback?...

    /** Store options which can add the page. */
    // TODO may be not need?...
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