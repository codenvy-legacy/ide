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
package com.codenvy.ide.ext.cloudbees.client;

import com.google.gwt.i18n.client.Messages;

/**
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: CloudBeesLocalizationConstant.java Jun 23, 2011 10:12:47 AM vereshchaka $
 */
public interface CloudBeesLocalizationConstant extends Messages {
    /*
     * Buttons.
     */
    @Key("button.create")
    String createButton();

    @Key("button.cancel")
    String cancelButton();

    @Key("button.ok")
    String okButton();

    @Key("button.close")
    String closeButton();

    /*
     * Controls.
     */
    @Key("control.cloudbees.id")
    String cloudBeesControlId();

    @Key("control.cloudbees.title")
    String cloudBeesControlTitle();

    @Key("control.cloudbees.prompt")
    String cloudBeesControlPrompt();

    @Key("control.initializeApp.id")
    String initializeAppControlId();

    @Key("control.initializeApp.title")
    String initializeAppControlTitle();

    @Key("control.initializeApp.prompt")
    String initializeAppControlPrompt();

    @Key("control.appInfo.id")
    String applicationInfoControlId();

    @Key("control.appInfo.title")
    String applicationInfoControlTitle();

    @Key("control.appInfo.prompt")
    String applicationInfoControlPrompt();

    @Key("control.deleteApp.id")
    String deleteApplicationControlId();

    @Key("control.deleteApp.title")
    String deleteApplicationControlTitle();

    @Key("control.deleteApp.prompt")
    String deleteApplicationControlPrompt();

    @Key("control.updateApp.id")
    String updateApplicationControlId();

    @Key("control.updateApp.title")
    String updateApplicationControlTitle();

    @Key("control.updateApp.prompt")
    String updateApplicationControlPrompt();

    @Key("control.appList.id")
    String controlAppListId();

    @Key("control.appList.title")
    String controlAppListTitle();

    @Key("control.appList.prompt")
    String controlAppListPrompt();

    @Key("control.createAccount.id")
    String controlCreateAccountId();

    @Key("control.createAccount.title")
    String controlCreateAccountTitle();

    @Key("control.createAccount.prompt")
    String controlCreateAccountPrompt();

    @Key("control.switchAccount.id")
    String controlSwitchAccountId();

    @Key("control.switchAccount.title")
    String controlSwitchAccountTitle();

    @Key("control.switchAccount.prompt")
    String controlSwitchAccountPrompt();

    /*
     * LoginView.
     */
    @Key("login.title")
    String loginViewTitle();

    @Key("login.field.email")
    String loginViewEmailField();

    @Key("login.field.password")
    String loginViewPasswordField();

    /*
     * ApplicationNameView.
     */
    @Key("appName.title")
    String appNameTitle();

    @Key("appName.field.domain")
    String appNameDomainField();

    @Key("appName.field.name")
    String appNameFieldName();

    @Key("appName.field.id")
    String appNameIdField();

    /*
     * Messages
     */
    @Key("loginSuccess")
    String loginSuccess();

    @Key("loginFailed")
    String loginFailed();

    @Key("creatingProject")
    String creatingProject();

    @Key("deployToCloudBees")
    String deployToCloudBees();

    @Key("creatingApplication")
    String creatingApplication();

    @Key("creatingApplicationFinished")
    String creatingApplicationFinished(String application);

    /*
     * DeployApplicationPresenter
     */
    @Key("deployApplication.deployedSuccess")
    String deployApplicationSuccess();

    @Key("deployApplication.appInfo")
    String deployApplicationInfo();

    @Key("deployApplication.failure")
    String deployApplicationFailureMessage();

    /*
     * ApplicationInfoView
     */
    @Key("appInfo.title")
    String applicationInfoTitle();

    /*
     * ApplicationInfoPresenter
     */
    @Key("appInfo.listGrid.id")
    String applicationInfoListGridId();

    @Key("appInfo.listGrid.title")
    String applicationInfoListGridTitle();

    @Key("appInfo.listGrid.serverPool")
    String applicationInfoListGridServerPool();

    @Key("appInfo.listGrid.status")
    String applicationInfoListGridStatus();

    @Key("appInfo.listGrid.container")
    String applicationInfoListGridContainer();

    @Key("appInfo.listGrid.idleTimeout")
    String applicationInfoListGridIdleTimeout();

    @Key("appInfo.listGrid.maxMemory")
    String applicationInfoListGridMaxMemory();

    @Key("appInfo.listGrid.securityMode")
    String applicationInfoListGridSecurityMode();

    @Key("appInfo.listGrid.clusterSize")
    String applicationInfoListGridClusterSize();

    @Key("appInfo.listGrid.url")
    String applicationInfoListGridUrl();

    /*
     * ApplicationInfoGrid
     */
    @Key("appInfo.listGrid.field.name")
    String applicationInfoListGridNameField();

    @Key("appInfo.listGrid.field.value")
    String applicationInfoListGridValueField();

    /*
     * DeleteApplicationEvent
     */
    @Key("deleteApplication.title")
    String deleteApplicationTitle();

    @Key("askForDeleteApplication")
    String deleteApplicationQuestion(String applicationTitle);

    @Key("applicationDeleted")
    String applicationDeletedMsg(String applicationTitle);

    /*
     * UpdateApplicationPresenter
     */
    @Key("applicationUpdated")
    String applicationUpdatedMsg(String applicationTitle);

    @Key("appUpdate.askForMsg.title")
    String updateAppAskForMsgTitle();

    @Key("appUpdate.askForMsg.text")
    String updateAppAskForMsgText();

    @Key("appUpdate.selectFolderToUpdate")
    String selectFolderToUpdate();

    /*
     * AppListView
     */
    @Key("appList.title")
    String appListViewTitle();

    @Key("appList.grid.name")
    String appListName();

    @Key("appList.grid.status")
    String appListStatus();

    @Key("appList.grid.url")
    String appListUrl();

    @Key("appList.grid.instance")
    String appListInstance();

    @Key("appList.grid.info")
    String appListInfo();

    @Key("appList.grid.delete")
    String appListDelete();

    /*
     * Manage Project View
     */
    @Key("manageProject.title")
    String manageProjectTitle();

    @Key("manageProject.update")
    String manageProjectUpdateButton();

    @Key("manageProject.delete")
    String manageProjectDeleteButton();

    @Key("manageProject.application.url")
    String manageProjectApplicationUrl();

    @Key("manageProject.application.status")
    String manageProjectApplicationStatus();

    @Key("manageProject.application.instances")
    String manageProjectApplicationInstances();

    @Key("manageProject.application.actions")
    String manageProjectApplicationActions();

    /*
     * Create account view.
     */
    @Key("create.account.view.title")
    String createAccountViewTitle();

    @Key("create.account.email.field")
    String createAccountEmailField();

    @Key("create.account.company.field")
    String createAccountCompanyField();

    @Key("create.account.first.name.field")
    String createAccountFirstNameField();

    @Key("create.account.last.name.field")
    String createAccountLastNameField();

    @Key("create.account.password.field")
    String createAccountPasswordField();

    @Key("create.account.confirm.password.field")
    String createAccountConfirmPasswordField();

    @Key("create.account.domain.field")
    String createAccountDomainField();

    @Key("create.account.user.name.field")
    String createAccountUserNameField();

    @Key("create.account.optional")
    String createAccountOptional();

    @Key("create.account.passwords.do.not.match")
    String createAccountPasswordsDoNotMatch();

    @Key("create.account.short.password")
    String createAccountShortPassword();

    @Key("create.account.short.domain")
    String createAccountShortDomain();

    @Key("create.account.empty.required.fields")
    String createAccountEmptyRequiredFields();

    @Key("create.account.success")
    String createAccountSuccess(String account);

    @Key("add.user.success")
    String addUserSuccess(String user);

    @Key("create.account.create.new.user")
    String createAccountCreateNewUser();
}