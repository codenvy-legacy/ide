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
package com.codenvy.ide.ext.jenkins.client.build;

import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.api.parts.base.BaseActionDelegate;

/**
 * The view of {@link BuildApplicationView}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public interface BuildApplicationView extends View<BuildApplicationView.ActionDelegate> {
    /** Needs for delegate some function into BuildApplication view. */
    public interface ActionDelegate extends BaseActionDelegate {
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

    /**
     * Sets title.
     *
     * @param title
     */
    void setTitle(String title);
}