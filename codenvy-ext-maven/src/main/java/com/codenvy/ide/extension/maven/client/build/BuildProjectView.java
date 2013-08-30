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
package com.codenvy.ide.extension.maven.client.build;

import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.api.parts.base.BaseActionDelegate;

/**
 * The view of {@link BuildProjectPresenter}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public interface BuildProjectView extends View<BuildProjectView.ActionDelegate> {
    /** Needs for delegate some function into BuildProject view. */
    public interface ActionDelegate extends BaseActionDelegate {
        /** Performs any actions appropriate in response to the user having pressed the ClearOutput button. */
        void onClearOutputClicked();
    }

    /**
     * Shows message.
     *
     * @param text
     */
    void showMessageInOutput(String text);

    /** Starts animation. */
    void startAnimation();

    /** Stops animation. */
    void stopAnimation();

    /** Clears output. */
    void clearOutput();

    /**
     * Sets whether ClearOutput button is enabled.
     *
     * @param isEnabled
     *         <code>true</code> to enable the button, <code>false</code> to disable it
     */
    void setClearOutputButtonEnabled(boolean isEnabled);

    /**
     * Sets title.
     *
     * @param title
     */
    void setTitle(String title);
}