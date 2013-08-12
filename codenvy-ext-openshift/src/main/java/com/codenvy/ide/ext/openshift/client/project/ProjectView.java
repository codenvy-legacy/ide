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
package com.codenvy.ide.ext.openshift.client.project;

import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.ext.openshift.shared.AppInfo;

/**
 * The view of {@link ProjectPresenter}.
 *
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public interface ProjectView extends View<ProjectView.ActionDelegate> {
    /** Needs for delegate some function into Project view. */
    public interface ActionDelegate {
        /** Performs any actions appropriate in response to the user having pressed the Close button. */
        public void onCloseClicked();

        /** Performs any actions appropriate in response to the user having pressed the Start application button. */
        public void onStartApplicationClicked(AppInfo application);

        /** Performs any actions appropriate in response to the user having pressed the Stop application button. */
        public void onStopApplicationClicked(AppInfo application);

        /** Performs any actions appropriate in response to the user having pressed the Restart application button. */
        public void onRestartApplicationClicked(AppInfo application);

        /** Performs any actions appropriate in response to the user having pressed the Show application properties button. */
        public void onShowApplicationPropertiesClicked(AppInfo application);

        /** Performs any actions appropriate in response to the user having pressed the Delete application button. */
        public void onDeleteApplicationDeleted(AppInfo application);
    }

    /**
     * Set application health.
     *
     * @param health
     *         two possibility values - STARTED/STOPPED
     */
    public void setApplicationHealth(String health);

    /**
     * Is current windows showed.
     *
     * @return true - if window showed, otherwise - false
     */
    public boolean isShown();

    /** Close current window. */
    public void close();

    /** Show window. */
    public void showDialog(AppInfo application);
}
