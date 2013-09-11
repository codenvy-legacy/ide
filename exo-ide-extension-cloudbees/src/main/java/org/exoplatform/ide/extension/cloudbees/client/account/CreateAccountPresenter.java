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
package org.exoplatform.ide.extension.cloudbees.client.account;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.HasValue;
import com.google.web.bindery.autobean.shared.AutoBean;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.AutoBeanUnmarshaller;
import org.exoplatform.gwtframework.ui.client.api.TextFieldItem;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.cloudbees.client.CloudBeesClientService;
import org.exoplatform.ide.extension.cloudbees.client.CloudBeesExtension;
import org.exoplatform.ide.extension.cloudbees.shared.CloudBeesAccount;
import org.exoplatform.ide.extension.cloudbees.shared.CloudBeesUser;

/**
 * Presenter for creating user account on CloudBees.
 *
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Jul 10, 2012 5:20:35 PM anya $
 */
public class CreateAccountPresenter implements CreateAccountHandler, ViewClosedHandler {
    interface Display extends IsView {
        HasClickHandlers getCreateButton();

        HasClickHandlers getCancelButton();

        void enableCreateButton(boolean enabled);

        TextFieldItem getAccountNameField();

        TextFieldItem getUserNameField();

        TextFieldItem getCompanyField();

        TextFieldItem getFirstNameField();

        TextFieldItem getLastNameField();

        TextFieldItem getEmailField();

        TextFieldItem getPasswordField();

        TextFieldItem getConfirmPasswordField();

        HasValue<String> getDomainErrorLabel();

        HasValue<String> getErrorLabel();

        HasValue<Boolean> getCreateNewUserField();

        void showAdvancedData(boolean show);

        void focusDomainField();
    }

    private Display display;

    public CreateAccountPresenter() {
        IDE.getInstance().addControl(new CreateAccountControl());

        IDE.addHandler(CreateAccountEvent.TYPE, this);
        IDE.addHandler(ViewClosedEvent.TYPE, this);
    }

    public void bindDisplay() {
        display.getAccountNameField().addValueChangeHandler(new ValueChangeHandler<String>() {

            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                display.enableCreateButton(isRequiredFieldsFullFilled());
            }
        });

        display.getUserNameField().addValueChangeHandler(new ValueChangeHandler<String>() {

            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                display.enableCreateButton(isRequiredFieldsFullFilled());
            }
        });

        display.getEmailField().addValueChangeHandler(new ValueChangeHandler<String>() {

            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                display.enableCreateButton(isRequiredFieldsFullFilled());
            }
        });

        display.getFirstNameField().addValueChangeHandler(new ValueChangeHandler<String>() {

            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                display.enableCreateButton(isRequiredFieldsFullFilled());
            }
        });

        display.getLastNameField().addValueChangeHandler(new ValueChangeHandler<String>() {

            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                display.enableCreateButton(isRequiredFieldsFullFilled());
            }
        });

        display.getPasswordField().addValueChangeHandler(new ValueChangeHandler<String>() {

            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                display.enableCreateButton(isRequiredFieldsFullFilled());
            }
        });

        display.getConfirmPasswordField().addValueChangeHandler(new ValueChangeHandler<String>() {

            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                display.enableCreateButton(isRequiredFieldsFullFilled());
            }
        });

        display.getCancelButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                IDE.getInstance().closeView(display.asView().getId());
            }
        });

        display.getCreateButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                checkBeforeCreate();
            }
        });

        display.getCreateNewUserField().addValueChangeHandler(new ValueChangeHandler<Boolean>() {

            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                display.enableCreateButton(isRequiredFieldsFullFilled());
                display.showAdvancedData(event.getValue());
            }
        });
    }

    private boolean isRequiredFieldsFullFilled() {
        if (display.getCreateNewUserField().getValue()) {
            return (display.getEmailField().getValue() != null && !display.getEmailField().getValue().isEmpty()
                    && display.getAccountNameField().getValue() != null && !display.getAccountNameField().getValue().isEmpty()
                    && display.getPasswordField().getValue() != null && !display.getPasswordField().getValue().isEmpty()
                    && display.getConfirmPasswordField().getValue() != null
                    && !display.getConfirmPasswordField().getValue().isEmpty()
                    && display.getFirstNameField().getValue() != null && !display.getFirstNameField().getValue().isEmpty()
                    && display.getLastNameField().getValue() != null && !display.getLastNameField().getValue().isEmpty()
                    && display.getUserNameField().getValue() != null && !display.getUserNameField().getValue().isEmpty());
        } else {
            return (display.getEmailField().getValue() != null && !display.getEmailField().getValue().isEmpty()
                    && display.getAccountNameField().getValue() != null && !display.getAccountNameField().getValue().isEmpty());
        }
    }

    /** Perform checking fields before account creation. */
    private void checkBeforeCreate() {
        boolean isCreateNew = display.getCreateNewUserField().getValue();

        boolean accountNameIsCorrect =
                (display.getAccountNameField().getValue() != null && display.getAccountNameField().getValue().length() >= 3);
        boolean passwordIsCorrect =
                (display.getPasswordField().getValue() != null && display.getPasswordField().getValue().length() >= 8 && display





































                                                                                                                                 .getPasswordField()
                                                                                                                                 .getValue()
                                                                                                                                 .length() <=
                                                                                                                         40);
        boolean passwordMatch =
                (display.getPasswordField().getValue().equals(display.getConfirmPasswordField().getValue()));

        display.getDomainErrorLabel().setValue(
                accountNameIsCorrect ? "" : CloudBeesExtension.LOCALIZATION_CONSTANT.createAccountShortDomain());
        if (passwordIsCorrect && isCreateNew) {
            display.getErrorLabel().setValue(
                    passwordMatch ? "" : CloudBeesExtension.LOCALIZATION_CONSTANT.createAccountPasswordsDoNotMatch());
        } else if (!passwordIsCorrect && isCreateNew) {
            display.getErrorLabel().setValue(CloudBeesExtension.LOCALIZATION_CONSTANT.createAccountShortPassword());
        }

        if ((isCreateNew && passwordIsCorrect && accountNameIsCorrect && passwordMatch)
            || (!isCreateNew && accountNameIsCorrect)) {
            createAccount();
        }
    }

    /** Create new account. */
    private void createAccount() {
        CloudBeesAccount account = CloudBeesExtension.AUTO_BEAN_FACTORY.account().as();
        account.setName(display.getAccountNameField().getValue());
        account.setCompany(display.getCompanyField().getValue());
        AutoBean<CloudBeesAccount> accountBean = CloudBeesExtension.AUTO_BEAN_FACTORY.account();
        AutoBeanUnmarshaller<CloudBeesAccount> unmarshaller = new AutoBeanUnmarshaller<CloudBeesAccount>(accountBean);

        try {
            CloudBeesClientService.getInstance().createAccount(account,
                                                               new AsyncRequestCallback<CloudBeesAccount>(unmarshaller) {

                                                                   @Override
                                                                   protected void onSuccess(CloudBeesAccount result) {
                                                                       IDE.fireEvent(new OutputEvent(
                                                                               CloudBeesExtension.LOCALIZATION_CONSTANT
                                                                                                 .createAccountSuccess(result
                                                                                                                               .getName()),
                                                                               Type.INFO));
                                                                       addUserToAccount(result);
                                                                   }

                                                                   @Override
                                                                   protected void onFailure(Throwable exception) {
                                                                       IDE.fireEvent(new ExceptionThrownEvent(exception));
                                                                   }
                                                               });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /**
     * Add user to specified account.
     *
     * @param account
     */
    private void addUserToAccount(CloudBeesAccount account) {
        CloudBeesUser user = CloudBeesExtension.AUTO_BEAN_FACTORY.user().as();
        user.setEmail(display.getEmailField().getValue());
        if (display.getCreateNewUserField().getValue()) {
            user.setName(display.getUserNameField().getValue());
            user.setFirst_name(display.getFirstNameField().getValue());
            user.setLast_name(display.getLastNameField().getValue());
            user.setPassword(display.getPasswordField().getValue());
            user.setRole("admin");
        }

        AutoBean<CloudBeesUser> userBean = CloudBeesExtension.AUTO_BEAN_FACTORY.user();
        AutoBeanUnmarshaller<CloudBeesUser> unmarshaller = new AutoBeanUnmarshaller<CloudBeesUser>(userBean);
        boolean isExisting = !display.getCreateNewUserField().getValue();
        try {
            CloudBeesClientService.getInstance().addUserToAccount(account.getName(), user, isExisting,
                                                                  new AsyncRequestCallback<CloudBeesUser>(unmarshaller) {

                                                                      @Override
                                                                      protected void onSuccess(CloudBeesUser result) {
                                                                          IDE.fireEvent(new OutputEvent(
                                                                                  CloudBeesExtension.LOCALIZATION_CONSTANT
                                                                                                    .addUserSuccess(result
                                                                                                                            .getEmail()),
                                                                                  Type.INFO));
                                                                          IDE.getInstance().closeView(display.asView().getId());
                                                                      }

                                                                      @Override
                                                                      protected void onFailure(Throwable exception) {
                                                                          IDE.fireEvent(new ExceptionThrownEvent(exception));
                                                                      }
                                                                  });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /** @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api
     * .event.ViewClosedEvent) */
    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (event.getView() instanceof Display) {
            display = null;
        }
    }

    /** @see org.exoplatform.ide.extension.cloudbees.client.account.CreateAccountHandler#onCreateAccount(org.exoplatform.ide.extension
     * .cloudbees.client.account.CreateAccountEvent) */
    @Override
    public void onCreateAccount(CreateAccountEvent event) {
        if (display == null) {
            display = GWT.create(Display.class);
            bindDisplay();
            IDE.getInstance().openView(display.asView());
        }
        display.enableCreateButton(false);
        display.getDomainErrorLabel().setValue("");
        display.getErrorLabel().setValue("");
        display.focusDomainField();
    }
}
