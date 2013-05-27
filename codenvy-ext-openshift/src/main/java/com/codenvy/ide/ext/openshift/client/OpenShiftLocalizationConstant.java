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
package com.codenvy.ide.ext.openshift.client;

import com.google.gwt.i18n.client.Messages;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public interface OpenShiftLocalizationConstant extends Messages {
   /*
    * Buttons.
    */

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

}
