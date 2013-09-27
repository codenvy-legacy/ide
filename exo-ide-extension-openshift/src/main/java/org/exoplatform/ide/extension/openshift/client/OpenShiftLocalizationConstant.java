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
package org.exoplatform.ide.extension.openshift.client;

import com.google.gwt.i18n.client.Messages;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Jun 6, 2011 11:16:02 AM anya $
 */
public interface OpenShiftLocalizationConstant extends Messages {
    /* Buttons: */
    @DefaultMessage("Cancel")
    @Key("cancelButton")
    String cancelButton();

    @DefaultMessage("Change")
    @Key("changeButton")
    String changeButton();

    @DefaultMessage("Create")
    @Key("createButton")
    String createButton();

    @DefaultMessage("Login")
    @Key("loginButton")
    String loginButton();

    @DefaultMessage("Ok")
    @Key("okButton")
    String okButton();

    @Key("previewButton")
    String previewButton();

    @Key("deleteButton")
    String deleteButton();

    @Key("closeButton")
    String closeButton();

    /* Controls: */
    @DefaultMessage("PaaS/OpenShift/Create domain...")
    @Key("control.createDomain.id")
    String createDomainControlId();

    @DefaultMessage("Create domain...")
    @Key("control.createDomain.title")
    String createDomainControlTitle();

    @DefaultMessage("Create domain on OpenShift...")
    @Key("control.createDomain.prompt")
    String createDomainControlPrompt();

    /*
     * Create Application
     */
    @DefaultMessage("PaaS/OpenShift/Create application...")
    @Key("control.createApplication.id")
    String createApplicationControlId();

    @DefaultMessage("Create application...")
    @Key("control.createApplication.title")
    String createApplicationControlTitle();

    @DefaultMessage("Create application on OpenShift...")
    @Key("control.createApplication.prompt")
    String createApplicationControlPrompt();

    /*
     * Delete Application
     */
    @DefaultMessage("PaaS/OpenShift/Delete application...")
    @Key("control.deleteApplication.id")
    String deleteApplicationControlId();

    @DefaultMessage("Delete application...")
    @Key("control.deleteApplication.title")
    String deleteApplicationControlTitle();

    @DefaultMessage("Delete application on OpenShift...")
    @Key("control.deleteApplication.prompt")
    String deleteApplicationControlPrompt();

    /*
     * Application Info...
     */
    @DefaultMessage("PaaS/OpenShift/Application info...")
    @Key(" control.showApplicationInfo.id")
    String showApplicationInfoControlId();

    @DefaultMessage("Application info...")
    @Key("control.showApplicationInfo.title")
    String showApplicationInfoControlTitle();

    @DefaultMessage("Show application info...")
    @Key("control.showApplicationInfo.prompt")
    String showApplicationInfoControlPrompt();

    /*
     * Preview Application
     */
    @DefaultMessage("PaaS/OpenShift/Preview Application")
    @Key("control.previewApplication.id")
    String previewApplicationControlId();

    @DefaultMessage("Preview Application")
    @Key("control.previewApplication.title")
    String previewApplicationControlTitle();

    @DefaultMessage("Preview Application")
    @Key("control.previewApplication.prompt")
    String previewApplicationControlPrompt();

    /*
     * PaaS -> OpenShift
     */
    @DefaultMessage("PaaS/OpenShift")
    @Key("control.openshift.id")
    String openShiftControlId();

    @DefaultMessage("OpenShift")
    @Key("control.openshift.title")
    String openShiftControlTitle();

    @DefaultMessage("OpenShift")
    @Key("control.openshift.prompt")
    String openShiftControlPrompt();

    /*
     * User Info...
     */
    @DefaultMessage("PaaS/OpenShift/User info...")
    @Key("control.showUserInfo.id")
    String showUserInfoControlId();

    @DefaultMessage("User info...")
    @Key("control.showUserInfo.title")
    String showUserInfoControlTitle();

    @DefaultMessage("Show user info...")
    @Key("control.showUserInfo.prompt")
    String showUserInfoControlPrompt();

    /*
     * Update Public Key
     */
    @Key("control.updatePublicKey.id")
    String updatePublicKeyControlId();

    @Key("control.updatePublicKey.title")
    String updatePublicKeyControlTitle();

    @Key("control.updatePublicKey.prompt")
    String updatePublicKeyControlPrompt();

    /*
     * Switch account
     */
    @Key("control.switchAccount.id")
    String switchAccountControlId();

    @Key("control.switchAccount.switch.title")
    String switchAccountControlSwitchTitle();

    @Key("control.switchAccount.switch.prompt")
    String switchAccountControlSwitchPrompt();

    /*
     * Login view
     */
    @DefaultMessage("Log in OpenShift")
    @Key("loginView.title")
    String loginViewTitle();

    @DefaultMessage("Email:")
    @Key("loginView.field.email")
    String loginViewEmailField();

    @DefaultMessage("Password:")
    @Key("loginView.field.password")
    String loginViewPasswordField();

    /* Create domain view */
    @DefaultMessage("Create new domain")
    @Key("createDomainView.title")
    String createDomainViewTitle();

    @DefaultMessage("Enter domain name:")
    @Key("createDomainView.name.field")
    String createDomainViewNameField();

    // Create application view
    @DefaultMessage("Create new OpenShift application")
    @Key("createApplicationView.title")
    String createApplicationViewTitle();

    @DefaultMessage("Enter application name:")
    @Key("createApplicationView.name.field")
    String createApplicationViewNameField();

    @DefaultMessage("Application working directory:")
    @Key("createApplicationView.workdir.field")
    String createApplicationViewWorkDirField();

    @DefaultMessage("Choose application type:")
    @Key("createApplicationView.type.field")
    String createApplicationViewTypeField();

    /* User info view */
    @DefaultMessage("User information")
    @Key("userInfoView.title")
    String userInfoViewTitle();

    @DefaultMessage("Login")
    @Key("userInfoView.field.login")
    String userInfoViewLoginField();

    @DefaultMessage("Domain")
    @Key("userInfoView.field.domain")
    String userInfoViewDomainField();

    @DefaultMessage("Applications")
    @Key("userInfoView.applications")
    String userInfoViewApplications();

   /* Messages */

    /**
     * @param domainName
     *         domain name
     * @return {@link String}
     */
    @DefaultMessage("Domain {0} is successfully created.")
    @Key("createDomainSuccess")
    String createDomainSuccess(String domainName);

    @DefaultMessage("Application {0} is successfully created.")
    @Key("createApplicationSuccess")
    String createApplicationSuccess(String application);

    @DefaultMessage("Logged in OpenShift successfully.")
    @Key("loginSuccess")
    String loginSuccess();

    @DefaultMessage("Failed to log in OpenShift.")
    @Key("loginFailed")
    String loginFailed();

    @DefaultMessage("Please, select folder(place for OpenShift application) in browser tree.")
    @Key("selectFolder")
    String selectFolder();

    @DefaultMessage("Do you want to delete application <b>{0}</b> on OpenShift?")
    @Key("deleteApplication")
    String deleteApplication(String applicationName);

    @DefaultMessage("Delete OpenShift application")
    @Key("deleteApplicationTitle")
    String deleteApplicationTitle();

    @DefaultMessage("Application <b>{0}</b> is successfully deleted.")
    @Key("deleteApplicationSuccess")
    String deleteApplicationSuccess(String applicationName);

    @Key("creatingApplication")
    String creatingApplication();

    @Key("creatingProject")
    String creatingProject();

    @Key("deployToOpenShift")
    String deployToOpenShift();

    @Key("updatePublicKeySuccess")
    String updatePublicKeySuccess();

    @Key("updatePublicKeyFailed")
    String updatePublicKeyFailed();

    @Key("pullSourceFailed")
    String pullSourceFailed();

    @DefaultMessage("OpenShift application information")
    @Key("applicationInfoView.title")
    String applicationInfoViewTitle();

    @DefaultMessage("Property")
    @Key("applicationInfoGid.field.name")
    String applicationInfoGridNameField();

    @DefaultMessage("Value")
    @Key("applicationInfoGid.field.value")
    String applicationInfoGridValueField();

    /* Error */
    @Key("unmarshal.application.types.failed")
    String applicationTypesUnmarshallerFail();

    @Key("errorGettingCartridgesList")
    String errorGettingCartridgesList();

    /* Application properties */
    @DefaultMessage("Name")
    @Key("application.name")
    String applicationName();

    @DefaultMessage("Type")
    @Key("application.type")
    String applicationType();

    @DefaultMessage("Public URL")
    @Key("application.publicUrl")
    String applicationPublicUrl();

    @DefaultMessage("Git URL")
    @Key("application.gitUrl")
    String applicationGitUrl();

    @DefaultMessage("Creation time")
    @Key("application.createTime")
    String applicationCreationTime();

    @DefaultMessage("Express exit code : <b>{0}</b>")
    @Key("express.exit.code")
    String expressExitCode(String code);

    @DefaultMessage("Create OpenShift application {0} failed.")
    @Key("createApplicationFail")
    String createApplicationFail(String applicationName);

    @DefaultMessage("Create OpenShift domain {0} failed.")
    @Key("createDomainFail")
    String createDomainFail(String domainName);

    @DefaultMessage("Delete OpenShift application {0} failed.")
    @Key("deleteApplicationFail")
    String deleteApplicationFail(String applicationName);

    @DefaultMessage("Get application information failed.")
    @Key("getApplicationInfoFail")
    String getApplicationInfoFail();

    @DefaultMessage("Get user information failed.")
    @Key("getUserInfoFail")
    String getUserInfoFail();

    @Key("manage.project.view.title")
    String manageProjectViewTitle();

    @Key("manage.project.application.name")
    String manageProjectApplicationName();

    @Key("manage.project.application.type")
    String manageProjectApplicationType();

    @Key("manage.project.application.url")
    String manageProjectApplicationUrl();

    @Key("manage.project.application.properties")
    String manageProjectApplicationProperies();

    @Key("manage.project.application.actions")
    String manageProjectApplicationActions();

    //Application control
    @Key("application.start")
    String startApplication();

    @Key("application.stop")
    String stopApplication();

    @Key("application.restart")
    String restartApplication();

    @Key("application.status.label")
    String statusLabel();

    @Key("addCartridgeButton")
    String addCartridgeButton();

    @Key("addCartridgeTitle")
    String addCartridgeTitle();

    @Key("errorAddingCartridge")
    String errorAddingCartridge();

    @DefaultMessage("Delete OpenShift cartridge")
    @Key("deleteCartridgeTitle")
    String deleteCartridgeTitle();

    @DefaultMessage("Do you want to delete cartridge <b>{0}</b> on OpenShift?")
    @Key("deleteCartridge")
    String deleteCartridge(String applicationName);

    @Key("deleteCartridgeError")
    String deleteCartridgeError();

    @Key("sendEventFailed")
    String sendEventFailed(String event);

    @Key("changeNamespaceTitle")
    String changeNamespaceTitle();

    @Key("changeNamespacePrompt")
    String changeNamespacePrompt();

    @Key("removingApplicationsFailed")
    String removingApplicationsFailed();

    @Key("createAppForPropertiesView")
    String createAppForPropertiesView();

    @Key("createAppForCartridgesView")
    String createAppForCartridgesView();

    @Key("createAppForView")
    String createAppForView();
}
