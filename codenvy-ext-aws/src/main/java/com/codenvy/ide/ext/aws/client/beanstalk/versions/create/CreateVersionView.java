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
package com.codenvy.ide.ext.aws.client.beanstalk.versions.create;

import com.codenvy.ide.api.mvp.View;

/**
 * The view for {@link CreateVersionPresenter}.
 *
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public interface CreateVersionView extends View<CreateVersionView.ActionDelegate> {
    /** Interface which must implement presenter to process any actions. */
    interface ActionDelegate {
        /** Perform action when create button clicked. */
        void onCreateButtonClicked();

        /** Perform action when cancel button clicked. */
        void onCancelButtonClicked();

        /** Perform action when version field changed. */
        void onVersionLabelKeyUp();
    }

    /**
     * Get version for new application.
     *
     * @return version label for new application.
     */
    String getVersionLabel();

    /**
     * Get description for new application.
     *
     * @return description for new application.
     */
    String getDescription();

    /**
     * Get S3 Bucket in which application will be created.
     *
     * @return S3 Bucket id.
     */
    String getS3Bucket();

    /**
     * Get S3 Key in which application will be created.
     *
     * @return S3 Key id.
     */
    String getS3Key();

    /**
     * Enable or disable create button.
     *
     * @param enable
     *         true if enable.
     */
    void enableCreateButton(boolean enable);

    /** Set focus in version field. */
    void focusInVersionLabelField();

    /**
     * Return shown state for current window.
     *
     * @return true if shown, otherwise false.
     */
    boolean isShown();

    /** Shows current dialog. */
    void showDialog();

    /** Close current dialog. */
    void close();
}
