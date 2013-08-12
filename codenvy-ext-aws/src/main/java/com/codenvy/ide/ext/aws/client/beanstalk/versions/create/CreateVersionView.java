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
