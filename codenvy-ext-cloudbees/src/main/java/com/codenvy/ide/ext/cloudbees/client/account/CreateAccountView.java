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
package com.codenvy.ide.ext.cloudbees.client.account;

import com.codenvy.ide.api.mvp.View;

/**
 * The view of {@link CreateAccountPresenter}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public interface CreateAccountView extends View<CreateAccountView.ActionDelegate> {
    /** Needs for delegate some function into CreateApplication view. */
    public interface ActionDelegate {
        /** Performs any actions appropriate in response to the user having pressed the Create button. */
        void onCreateClicked();

        /** Performs any actions appropriate in response to the user having pressed the Cancel button. */
        void onCancelClicked();

        /** Performs any actions appropriate in response to the user having changed something. */
        void onValueChanged();

        /** Performs any actions appropriate in response to the user having changed new user. */
        void onNewUserChanged();
    }

    /** @return account name */
    String getAccountName();

    /**
     * Sets account name.
     *
     * @param accountName
     */
    void setAccountName(String accountName);

    /** @return user's name */
    String getUserName();

    /**
     * Sets user's name.
     *
     * @param userName
     */
    void setUserName(String userName);

    /** @return company */
    String getCompany();

    /**
     * Sets company.
     *
     * @param company
     */
    void setCompany(String company);

    /** @return first name */
    String getFirstName();

    /**
     * Sets first name.
     *
     * @param firstName
     */
    void setFirstName(String firstName);

    /** @return last name */
    String getLastName();

    /**
     * Sets last name.
     *
     * @param lastName
     */
    void setLastName(String lastName);

    /** @return email */
    String getEmail();

    /**
     * Sets email.
     *
     * @param email
     */
    void setEmail(String email);

    /** @return password */
    String getPassword();

    /**
     * Sets password.
     *
     * @param password
     */
    void setPassword(String password);

    /** @return confirm password */
    String getConfirmPassword();

    /**
     * Sets confirm password.
     *
     * @param confirmPassword
     */
    void setConfirmPassword(String confirmPassword);

    /** @return error */
    String getError();

    /**
     * Sets error message.
     *
     * @param error
     */
    void setError(String error);

    /**
     * Returns whether need to create new user.
     *
     * @return <code>true</code> if need to create new user, and <code>false</code> otherwise
     */
    Boolean isCreateNewUser();

    /**
     * Sets whether need to create new user.
     *
     * @param createNewUser
     *         <code>true</code> need to create new user, <code>false</code> otherwise
     */
    void setCreateNewUser(boolean createNewUser);

    /**
     * Sets whether Create button is enabled.
     *
     * @param enabled
     *         <code>true</code> to enable the button, <code>false</code> to disable it
     */
    void setEnableCreateButton(boolean enabled);

    /**
     * Sets whether user name field is enabled.
     *
     * @param enabled
     *         <code>true</code> to enable the field, <code>false</code> to disable it
     */
    void setEnableUserName(boolean enabled);

    /**
     * Sets whether first name field is enabled.
     *
     * @param enabled
     *         <code>true</code> to enable the field, <code>false</code> to disable it
     */
    void setEnableFirstName(boolean enabled);

    /**
     * Sets whether last name field is enabled.
     *
     * @param enabled
     *         <code>true</code> to enable the field, <code>false</code> to disable it
     */
    void setEnableLastName(boolean enabled);

    /**
     * Sets whether password field is enabled.
     *
     * @param enabled
     *         <code>true</code> to enable the field, <code>false</code> to disable it
     */
    void setEnablePassword(boolean enabled);

    /**
     * Sets whether confirm password field is enabled.
     *
     * @param enabled
     *         <code>true</code> to enable the field, <code>false</code> to disable it
     */
    void setEnableConfirmPassword(boolean enabled);

    /** Sets focus in the domain field. */
    void focusDomainField();

    /** Close dialog. */
    void close();

    /** Show dialog. */
    void showDialog();
}