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
package com.codenvy.ide.ext.aws.client.login;

import com.codenvy.ide.api.mvp.View;

/**
 * The view for {@link LoginPresenter}.
 *
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public interface LoginView extends View<LoginView.ActionDelegate> {
    /** Needs for delegate some function into Login view. */
    public interface ActionDelegate {
        /** Performs any actions appropriate in response to the user having pressed the LogIn button. */
        void onLogInClicked();

        /** Performs any actions appropriate in response to the user having pressed the Cancel button. */
        void onCancelClicked();

        /** Performs any actions appropriate in response to the user having changed something. */
        void onValueChanged();
    }

    /**
     * Get access key from user input.
     *
     * @return access key.
     */
    String getAccessKey();

    /**
     * Get secret key from user input.
     *
     * @return secret key.
     */
    String getSecretKey();

    /**
     * Set error message if login is failed.
     *
     * @param result
     *         message with details.
     */
    void setLoginResult(String result);

    /** Focus into access key field. */
    void focusAccessKeyField();

    /**
     * Enable or disable login button.
     *
     * @param enable
     *         true if enable.
     */
    void enableLoginButton(boolean enable);

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
