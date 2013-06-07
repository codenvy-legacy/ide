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
