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

import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.ext.cloudbees.client.CloudBeesClientService;
import com.codenvy.ide.ext.cloudbees.client.CloudBeesLocalizationConstant;
import com.codenvy.ide.ext.cloudbees.client.marshaller.CloudBeesAccountUnmarshaller;
import com.codenvy.ide.ext.cloudbees.client.marshaller.CloudBeesUserUnmarshaller;
import com.codenvy.ide.ext.cloudbees.dto.client.DtoClientImpls;
import com.codenvy.ide.ext.cloudbees.shared.CloudBeesAccount;
import com.codenvy.ide.ext.cloudbees.shared.CloudBeesUser;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import static com.codenvy.ide.api.notification.Notification.Type.ERROR;
import static com.codenvy.ide.api.notification.Notification.Type.INFO;

/**
 * Presenter for creating user account on CloudBees.
 *
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Jul 10, 2012 5:20:35 PM anya $
 */
@Singleton
public class CreateAccountPresenter implements CreateAccountView.ActionDelegate {
    private CreateAccountView             view;
    private EventBus                      eventBus;
    private CloudBeesClientService        service;
    private CloudBeesLocalizationConstant constant;
    private NotificationManager           notificationManager;

    /**
     * Create presenter.
     *
     * @param view
     * @param eventBus
     * @param service
     * @param notificationManager
     */
    @Inject
    protected CreateAccountPresenter(CreateAccountView view, EventBus eventBus, CloudBeesLocalizationConstant constant,
                                     CloudBeesClientService service, NotificationManager notificationManager) {
        this.view = view;
        this.view.setDelegate(this);
        this.eventBus = eventBus;
        this.constant = constant;
        this.service = service;
        this.notificationManager = notificationManager;
    }

    /** Show dialog. */
    public void showDialog() {
        view.setAccountName("");
        view.setCompany("");
        view.setEmail("");
        view.setError("");

        showNewUserElement(false);

        view.focusDomainField();
        view.setEnableCreateButton(false);

        view.showDialog();
    }

    /**
     * Shows new user's elements.
     *
     * @param isShown
     */
    private void showNewUserElement(boolean isShown) {
        view.setCreateNewUser(isShown);
        view.setUserName("");
        view.setEnableUserName(isShown);
        view.setLastName("");
        view.setEnableLastName(isShown);
        view.setFirstName("");
        view.setEnableFirstName(isShown);
        view.setPassword("");
        view.setEnablePassword(isShown);
        view.setConfirmPassword("");
        view.setEnableConfirmPassword(isShown);
    }

    /** {@inheritDoc} */
    @Override
    public void onCreateClicked() {
        checkBeforeCreate();
    }

    /** Perform checking fields before account creation. */
    private void checkBeforeCreate() {
        boolean isCreateNew = view.isCreateNewUser();
        boolean accountNameIsCorrect = view.getAccountName() != null && view.getAccountName().length() >= 3;
        boolean passwordIsCorrect = view.getPassword() != null && view.getPassword().length() >= 8 && view.getPassword().length() <= 40;
        boolean passwordMatch = view.getPassword().equals(view.getConfirmPassword());

        view.setError("");
        if (!accountNameIsCorrect) {
            view.setError(constant.createAccountShortDomain());
        } else if (passwordIsCorrect && isCreateNew) {
            view.setError(constant.createAccountPasswordsDoNotMatch());
        } else if (!passwordIsCorrect && isCreateNew) {
            view.setError(constant.createAccountShortPassword());
        }

        if ((isCreateNew && passwordIsCorrect && accountNameIsCorrect && passwordMatch) || (!isCreateNew && accountNameIsCorrect)) {
            createAccount();
        }
    }

    /** Create new account. */
    private void createAccount() {
        DtoClientImpls.CloudBeesAccountImpl account = DtoClientImpls.CloudBeesAccountImpl.make();
        account.setName(view.getAccountName());
        account.setCompany(view.getCompany());

        CloudBeesAccountUnmarshaller unmarshaller = new CloudBeesAccountUnmarshaller();

        try {
            service.createAccount(account, new AsyncRequestCallback<CloudBeesAccount>(unmarshaller) {
                @Override
                protected void onSuccess(CloudBeesAccount result) {
                    Notification notification = new Notification(constant.createAccountSuccess(result.getName()), INFO);
                    notificationManager.showNotification(notification);
                    addUserToAccount(result);
                }

                @Override
                protected void onFailure(Throwable exception) {
                    Notification notification = new Notification(exception.getMessage(), ERROR);
                    notificationManager.showNotification(notification);
                    eventBus.fireEvent(new ExceptionThrownEvent(exception));
                }
            });
        } catch (RequestException e) {
            Notification notification = new Notification(e.getMessage(), ERROR);
            notificationManager.showNotification(notification);
            eventBus.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /**
     * Add user to specified account.
     *
     * @param account
     */
    private void addUserToAccount(CloudBeesAccount account) {
        boolean isExisting = !view.isCreateNewUser();

        DtoClientImpls.CloudBeesUserImpl user = DtoClientImpls.CloudBeesUserImpl.make();
        user.setEmail(view.getEmail());
        if (!isExisting) {
            user.setName(view.getUserName());
            user.setFirst_name(view.getFirstName());
            user.setLast_name(view.getLastName());
            user.setPassword(view.getPassword());
            user.setRole("admin");
        }

        CloudBeesUserUnmarshaller unmarshaller = new CloudBeesUserUnmarshaller();

        try {
            service.addUserToAccount(account.getName(), user, isExisting, new AsyncRequestCallback<CloudBeesUser>(unmarshaller) {
                @Override
                protected void onSuccess(CloudBeesUser result) {
                    Notification notification = new Notification(constant.addUserSuccess(result.getEmail()), ERROR);
                    notificationManager.showNotification(notification);
                    view.close();
                }

                @Override
                protected void onFailure(Throwable exception) {
                    Notification notification = new Notification(exception.getMessage(), ERROR);
                    notificationManager.showNotification(notification);
                    eventBus.fireEvent(new ExceptionThrownEvent(exception));
                }
            });
        } catch (RequestException e) {
            Notification notification = new Notification(e.getMessage(), ERROR);
            notificationManager.showNotification(notification);
            eventBus.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onCancelClicked() {
        view.close();
    }

    /** {@inheritDoc} */
    @Override
    public void onValueChanged() {
        view.setEnableCreateButton(isRequiredFieldsFullFilled());
    }

    /**
     * Check whether necessary fields are fullfilled.
     *
     * @return if <code>true</code> all necessary fields are fullfilled
     */
    private boolean isRequiredFieldsFullFilled() {
        if (view.isCreateNewUser()) {
            return (view.getEmail() != null && !view.getEmail().isEmpty() && view.getAccountName() != null &&
                    !view.getAccountName().isEmpty() && view.getPassword() != null && !view.getPassword().isEmpty()
                    && view.getConfirmPassword() != null && !view.getConfirmPassword().isEmpty() && view.getFirstName() != null &&
                    !view.getFirstName().isEmpty() && view.getLastName() != null && !view.getLastName().isEmpty() &&
                    view.getUserName() != null && !view.getUserName().isEmpty());
        } else {
            return (view.getEmail() != null && !view.getEmail().isEmpty() && view.getAccountName() != null &&
                    !view.getAccountName().isEmpty());
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onNewUserChanged() {
        showNewUserElement(view.isCreateNewUser());
        onValueChanged();
    }
}