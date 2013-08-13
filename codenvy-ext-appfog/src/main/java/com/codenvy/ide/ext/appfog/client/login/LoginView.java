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
package com.codenvy.ide.ext.appfog.client.login;

import com.codenvy.ide.api.mvp.View;

/**
 * The view of {@link LoginPresenter}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public interface LoginView extends View<LoginView.ActionDelegate> {
    /** Needs for delegate some function into Login view. */
    public interface ActionDelegate {
        /** Performs any actions appropriate in response to the user having pressed the Login button. */
        void onLoginClicked();

        /** Performs any actions appropriate in response to the user having pressed the Cancel button. */
        void onCancelClicked();

        /** Performs any actions appropriate in response to the user having changed something. */
        void onValueChanged();
    }

    /**
     * Returns email.
     *
     * @return email.
     */
    String getEmail();

    /**
     * Sets email.
     *
     * @param email
     */
    void setEmail(String email);

    /**
     * Returns password.
     *
     * @return password
     */
    String getPassword();

    /**
     * Sets password.
     *
     * @param password
     */
    void setPassword(String password);

    /**
     * Returns target.
     *
     * @return target
     */
    String getTarget();

    /**
     * Sets target.
     *
     * @param target
     */
    void setTarget(String target);

    /**
     * Sets error message.
     *
     * @param message
     */
    void setError(String message);

    /**
     * Change the enable state of the login button.
     *
     * @param enabled
     *         <code>true</code> to enable the button, <code>false</code> to disable it
     */
    void setEnableLoginButton(boolean enabled);

    /** Give focus to login field. */
    void focusInEmailField();

    /**
     * Returns whether the view is shown.
     *
     * @return <code>true</code> if the view is shown, and
     *         <code>false</code> otherwise
     */
    boolean isShown();

    /** Close dialog. */
    void close();

    /** Show dialog. */
    void showDialog();
}