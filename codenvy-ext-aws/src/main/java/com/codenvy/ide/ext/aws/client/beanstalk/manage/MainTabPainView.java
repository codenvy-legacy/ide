/*
 * Copyright (C) 2013 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
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
