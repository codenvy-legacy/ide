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
package com.codenvy.ide.ext.aws.client.beanstalk.versions.delete;

import com.codenvy.ide.api.mvp.View;

/**
 * The view for {@link DeleteVersionView}.
 *
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public interface DeleteVersionView extends View<DeleteVersionView.ActionDelegate> {
    /** Interface which must implement presenter to process any actions. */
    interface ActionDelegate {
        /** Perform action when delete button clicked. */
        void onDeleteButtonCLicked();

        /** Perform action when cancel button clicked. */
        void onCancelButtonClicked();
    }

    /**
     * Set delete question.
     *
     * @param question
     *         delete question.
     */
    void setDeleteQuestion(String question);

    /**
     * Include deletion of S3 Bundle.
     *
     * @return true if deletion of S3 Bundle is included.
     */
    boolean getDeleteS3Bundle();

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
