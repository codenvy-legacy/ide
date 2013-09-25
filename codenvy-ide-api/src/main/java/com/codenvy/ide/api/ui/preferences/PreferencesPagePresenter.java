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
package com.codenvy.ide.api.ui.preferences;

import com.codenvy.ide.api.mvp.Presenter;
import com.google.gwt.resources.client.ImageResource;


/**
 * Interface of preference page.
 * Describes main methods for all preference pages.
 *
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
public interface PreferencesPagePresenter extends Presenter {
    /** Needs for delegate updateControls function into PagePresenter. */
    interface DirtyStateListener {
        /** Updates preference view components without content panel. */
        void onDirtyChanged();
    }

    /**
     * Sets new delegate
     *
     * @param delegate
     */
    void setUpdateDelegate(DirtyStateListener delegate);

    /**
     * Performs any actions appropriate in response to the user
     * having pressed the Apply button.
     */
    void doApply();

    /**
     * Returns whether this page is changed or not.
     * This information is typically used by the preferences presenter to decide
     * when the information is changed.
     *
     * @return <code>true</code> if this page is changed, and
     *         <code>false</code> otherwise
     */
    boolean isDirty();

    /**
     * Return preference page's title. This title will be shown into list of preferences.
     *
     * @return
     */
    String getTitle();

    /**
     * Returns this preference page's icon. This icon will be shown into list of preferences.
     *
     * @return
     */
    ImageResource getIcon();
}