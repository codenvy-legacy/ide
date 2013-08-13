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