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
package com.codenvy.ide.ext.openshift.client.login;

import com.codenvy.ide.api.mvp.View;

/**
 * The view for {@link LoginPresenter}.
 *
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public interface LoginView extends View<LoginView.ActionDelegate> {
    /** Needs for delegate some function into Login view. */
    public interface ActionDelegate {
        /** Performs any actions appropriate in response to the user having pressed the Login button. */
        public void onLoginClicked();

        /** Performs any actions appropriate in response to the user having pressed the Cancel button. */
        public void onCancelClicked();

        /** Performs any actions appropriate in response to the user having changed something. */
        public void onValueChanged();
    }

    /**
     * Get email entered by user.
     *
     * @return String representation of email field
     */
    public String getEmail();

    /**
     * Set email which is stored in storage to show user who is loggined in current moment.
     *
     * @param email
     *         email value
     */
    public void setEmail(String email);

    /**
     * Get password value entered by user.
     *
     * @return String representation of password field
     */
    public String getPassword();

    /**
     * Set password field value.
     *
     * @param password
     *         password
     */
    public void setPassword(String password);

    /**
     * Set error message if login was failed.
     *
     * @param message
     *         error message
     */
    public void setError(String message);

    /**
     * Enable or disable login button if input field are correctly filled.
     *
     * @param isEnable
     *         true if login and password field are correctly filled
     */
    public void setEnableLoginButton(boolean isEnable);

    /** Focus to email field. */
    public void focusEmailField();

    /**
     * Is current windows showed.
     *
     * @return true - if window showed, otherwise - false
     */
    public boolean isShown();

    /** Close current window. */
    public void close();

    /** Show window. */
    public void showDialog();
}
