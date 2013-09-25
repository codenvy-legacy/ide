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
package com.codenvy.ide.ext.aws.client.beanstalk.manage;

import com.codenvy.ide.api.mvp.View;

/**
 * The view for {@link MainTabPainPresenter}.
 *
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public interface MainTabPainView extends View<MainTabPainView.ActionDelegate> {
    /** Interface which must implement presenter to process any actions. */
    interface ActionDelegate {
        /** Perform action when edit description button clicked. */
        void onEditDescriptionButtonClicked();

        /** Perform action when delete application button clicked. */
        void onDeleteApplicationButtonClicked();

        /** Perform action when new version button clicked. */
        void onCreateNewVersionButtonClicked();

        /** Perform action when new environment button clicked. */
        void onLaunchNewEnvironmentButtonClicked();
    }

    /**
     * Set application name.
     *
     * @param applicationName
     *         application name.
     */
    void setApplicationName(String applicationName);

    /**
     * Set description for the application.
     *
     * @param description
     *         description for the application.
     */
    void setDescription(String description);

    /**
     * Set creation date for the application.
     *
     * @param date
     *         creation date.
     */
    void setCreationDate(String date);

    /**
     * Set update date for the application.
     *
     * @param date
     *         update date.
     */
    void setUpdateDate(String date);
}
