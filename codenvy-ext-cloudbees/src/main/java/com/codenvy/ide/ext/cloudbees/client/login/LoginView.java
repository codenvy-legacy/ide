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
package com.codenvy.ide.ext.cloudbees.client.login;

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
     * Sets error message.
     *
     * @param message
     */
    void setError(String message);

    /**
     * Change the enable state of the login button.
     *
     * @param enabled
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