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
package com.codenvy.ide.ext.aws.client.beanstalk.update;

import com.codenvy.ide.api.mvp.View;

/**
 * The view for {@link DescriptionUpdatePresenter}.
 *
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public interface DescriptionUpdateView extends View<DescriptionUpdateView.ActionDelegate> {
    /** Interface which must implement presenter to process any actions. */
    public interface ActionDelegate {
        /** Perform action when update button clicked. */
        void onUpdateClicked();

        /** Perform action when cancel button clicked. */
        void onCancelClicked();

        /** Perform action when description field changed value. */
        void onDescriptionFieldChangedValue();
    }

    /**
     * Get description value for the application.
     *
     * @return description for the application.
     */
    String getDescriptionValue();

    /**
     * Enable or disable update button.
     *
     * @param enable
     *         true if enable.
     */
    void enableUpdateButton(boolean enable);

    /** Set focus in description field. */
    void focusDescriptionField();

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
