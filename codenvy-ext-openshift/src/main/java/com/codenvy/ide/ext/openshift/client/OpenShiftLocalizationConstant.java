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
package com.codenvy.ide.ext.openshift.client;

import com.google.gwt.i18n.client.Messages;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public interface OpenShiftLocalizationConstant extends Messages {
    /*
     * Cartridges
     */
    @Key("cartridgeSuccessfullyStarted")
    public String cartridgeSuccessfullyStarted(String cartridgeName);

    @Key("cartridgeSuccessfullyStopped")
    public String cartridgeSuccessfullyStopped(String cartridgeName);

    @Key("cartridgeSuccessfullyRestarted")
    public String cartridgeSuccessfullyRestarted(String cartridgeName);

    @Key("cartridgeSuccessfullyReloaded")
    public String cartridgeSuccessfullyReloaded(String cartridgeName);

    @Key("cartridgeSuccessfullyDeleted")
    public String cartridgeSuccessfullyDeleted(String cartridgeName);

    /*
     * Application list
     */
    @Key("applicationListViewTitle")
    public String applicationListViewTitle();

    @Key("applicationListViewLoginLabel")
    public String applicationListViewLoginLabel();

    @Key("applicationListViewLoginButton")
    public String applicationListViewLoginButton();

    @Key("applicationListViewDomainLabel")
    public String applicationListViewDomainLabel();

    @Key("applicationListViewDomainButton")
    public String applicationListViewDomainButton();

    @Key("applicationListViewCreateCartridgeButton")
    public String applicationListViewCreateCartridgeButton();

    @Key("applicationListViewCloseButton")
    public String applicationListViewCloseButton();

    /*
     * Application Info
     */
    @Key("applicationInfoViewTitle")
    public String applicationInfoViewTitle();

    @Key("applicationInfoViewNameField")
    public String applicationInfoViewNameField();

    @Key("applicationInfoViewTypeField")
    public String applicationInfoViewTypeField();

    @Key("applicationInfoViewPublicUrlField")
    public String applicationInfoViewPublicUrlField();

    @Key("applicationInfoViewGitUrlField")
    public String applicationInfoViewGitUrlField();

    @Key("applicationInfoViewCreationTimeField")
    public String applicationInfoViewCreationTimeField();

    @Key("applicationInfoViewCloseButton")
    public String applicationInfoViewCloseButton();

    @Key("applicationInfoViewPropertyNameColumn")
    public String applicationInfoViewPropertyNameColumn();

    @Key("applicationInfoViewPropertyValueColumn")
    public String applicationInfoViewPropertyValueColumn();

    /*
     * Cartridges
     */
    @Key("createCartridgeViewTitle")
    public String createCartridgeViewTitle();

    @Key("createCartridgeViewSuccessfullyAdded")
    public String createCartridgeViewSuccessfullyAdded(String cartridgeName, String appName);

    @Key("createCartridgeViewCreateButton")
    public String createCartridgeViewCreateButton();

    @Key("createCartridgeViewCancelButton")
    public String createCartridgeViewCancelButton();

    /*
     * Delete
     */
    @Key("deleteApplicationPrompt")
    public String deleteApplicationPrompt(String appName);

    @Key("deleteApplicationSuccessfullyDeleted")
    public String deleteApplicationSuccessfullyDeleted(String appName);

    /*
     * DomainView
     */
    @Key("changeDomainViewTitle")
    public String changeDomainViewTitle();

    @Key("changeDomainViewField")
    public String changeDomainViewField();

    @Key("changeDomainViewChangeButton")
    public String changeDomainViewChangeButton();

    @Key("changeDomainViewCancelButton")
    public String changeDomainViewCancelButton();

    @Key("changeDomainViewDeleteAppsMessage")
    public String changeDomainViewDeleteAppsMessage();

    @Key("changeDomainViewSuccessfullyChanged")
    public String changeDomainViewSuccessfullyChanged();

    @Key("changeDomainViewFailedChanged")
    public String changeDomainViewFailedChanged();

    @Key("changeDomainViewIncorrectDomainName")
    public String changeDomainViewIncorrectDomainName();

    /*
     * LoginView.
     */
    @Key("loginView.title")
    public String loginViewTitle();

    @Key("loginView.field.email")
    public String loginViewEmailField();

    @Key("loginView.field.password")
    public String loginViewPasswordField();

    @Key("loginView.button.login")
    public String loginViewButtonLogin();

    @Key("loginView.button.cancel")
    public String loginViewButtonCancel();

    @Key("loginViewErrorInvalidUserOrPassword")
    public String loginViewErrorInvalidUserOrPassword();

    @Key("loginViewSuccessfullyLogined")
    public String loginViewSuccessfullyLogined();

    /*
     * Messages
     */
    @Key("applicationAlreadyStarted")
    public String applicationAlreadyStarted(String appName);

    @Key("applicationWasNotStarted")
    public String applicationWasNotStarted(String appName);

    @Key("applicationStartedSuccessfully")
    public String applicationStartedSuccessfully(String appName);

    @Key("applicationAlreadyStopped")
    public String applicationAlreadyStopped(String appName);

    @Key("applicationStoppedSuccessfully")
    public String applicationStoppedSuccessfully(String appName);

    @Key("applicationWasNotStopped")
    public String applicationWasNotStopped(String appName);

    @Key("applicationRestarted")
    public String applicationRestarted(String appName);

    @Key("applicationCreatedSuccessfully")
    public String applicationCreatedSuccessfully(String appName, String url);

    @Key("applicationPublicKeyUpdateFailed")
    public String applicationPublicKeyUpdateFailed();

    @Key("applicationSourcePullingFailed")
    public String applicationSourcePullingFailed();

    /*
     * Project
     */
    @Key("projectViewTitle")
    public String projectViewTitle();

    @Key("projectViewTitlePropertiesApp")
    public String projectViewTitlePropertiesApp();

    /*
     * Creating
     */
    @Key("creatingApplicationStarted")
    public String creatingApplicationStarted(String appName);

    @Key("creatingApplicationFailed")
    public String creatingApplicationFailed(String appName);

    @Key("creatingApplicationFinished")
    public String creatingApplicationFinished(String appName);

}
